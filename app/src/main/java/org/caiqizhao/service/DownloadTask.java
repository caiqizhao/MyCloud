package org.caiqizhao.service;

import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Message;
import android.webkit.DownloadListener;

import org.caiqizhao.activity.MainActivity;
import org.caiqizhao.entity.User;
import org.caiqizhao.entity.UserFile;
import org.caiqizhao.fragments.FileFragment;
import org.caiqizhao.util.FileTransferListener;
import org.caiqizhao.util.ToastUtil;
import org.caiqizhao.util.VariableUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<UserFile,Integer,Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private FileTransferListener listener;

    private boolean isCanceled = false;
    private boolean isPaused = false;
    private long lastProgress;


    public DownloadTask(FileTransferListener listener){
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(UserFile... userFiles) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try{
            long downloadedLength = 0;
            UserFile userFile = userFiles[0];
            String fileName = userFile.getFile_name();
            String directory = MainActivity.user_file.getPath();
            file = new File(directory,fileName);
            //若文件存在，记录已经下载文件的长短
            if(file.exists()){
                downloadedLength = file.length();
            }
            //向服务器发送请求，获取完整文件的大小
            long contenttLength = grtContentLength(userFile.getFile_name(),userFile.getDir_id());
            if(contenttLength==0) {
                //若为0下载失败
                return TYPE_FAILED;
            }else if (contenttLength == downloadedLength){
                //本地文件大小等于服务器文件等大小则不需要重复下载。断点续传
                Message message = new Message();
                Bundle data = new Bundle();
                data.putString("message","文件已存在");
                message.setData(data);
                message.what=0x002;
                MainActivity.handler.sendMessage(message);
                return TYPE_SUCCESS;
            }

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("filename", userFile.getFile_name())
                    .add("username", userFile.getUser_name())
                    .add("dir_id",userFile.getDir_id()+"")
                    .build();
            Request request = new Request.Builder()
                    .addHeader("RANGE","bytes="+downloadedLength+"-") //记录本地以有的长短。只需获取未下载部分
                    .url(VariableUtil.Service_IP +"downloadfile")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            if(response!=null){
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file,"rw");
                savedFile.seek(downloadedLength);
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b))!=-1){
                    //以下判断为判断用户是否取消或暂停下载
                    if(isCanceled){
                        return TYPE_CANCELED;
                    }else if(isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total+=len;
                        savedFile.write(b,0,len);
                        int progress = (int)((total+downloadedLength)*100/contenttLength);
                        //更新文件下载进度条
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (is != null)
                    is.close();
                if(savedFile!=null)
                    savedFile.close();
                if (isCanceled&&file!=null)
                    file.delete();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    /**
     * 更新下载进度
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress = values[0];
        if(progress>lastProgress){
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }


    /**
     * 下载结果处理
     * @param integer
     */
    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer){
            case TYPE_SUCCESS:
                listener.onSucceess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
                default:
                    break;
        }
    }

    public void pauseDownload(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCanceled = true;
    }

    /**
     * 得到下载文件的总长度
     * @param file_name
     * @return
     * @throws IOException
     */
    private long grtContentLength(String file_name,int dir_id) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("filename", file_name)
                .add("dir_id",dir_id+"")
                .add("username", User.user.getUser_name())
                .build();
        Request request = new Request.Builder()
                .url(VariableUtil.Service_IP +"downloadfile")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if(response!=null&&response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

}
