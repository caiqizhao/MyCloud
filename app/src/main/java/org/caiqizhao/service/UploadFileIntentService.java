package org.caiqizhao.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.example.caiqizhao.mycloud.R;
import com.google.gson.Gson;

import org.caiqizhao.activity.MainActivity;
import org.caiqizhao.entity.User;
import org.caiqizhao.entity.UserFile;
import org.caiqizhao.fragments.BottomHomePageBarFragment;
import org.caiqizhao.fragments.FileFragment;
import org.caiqizhao.util.FileSizeUtil;
import org.caiqizhao.util.VariableUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UploadFileIntentService extends IntentService {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static List<UserFile> finshUploadFile = new ArrayList<UserFile>();
    public UploadFileIntentService() {
        super("UploadFileIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        List<String>file_path = intent.getStringArrayListExtra("file_path");
        if(file_path!=null){
            for (String s:file_path){
               try {

                   //打开文件并获取得到文件读取缓冲流
                   File file = new File(s);
                   BufferedInputStream in = new BufferedInputStream(new FileInputStream(s));

                   ByteArrayOutputStream baos = new ByteArrayOutputStream();
                   byte[] buf = new byte[1024 * 4];

                   //读取文件
                   int size;
                   while ((size = in.read(buf)) != -1) {
                       //使用从套接字获得的输出流【发送】数据
                       baos.write(buf, 0, size);

                   }

                   //生成哈希值
                   byte data[] = baos.toByteArray();
                   byte hash[] = MessageDigest.getInstance("SHA-256").digest(data);
                   String file_hash = new BigInteger(1, hash).toString(16);
                   UserFile userFile = new UserFile();
                   userFile.setFile_name(file.getName());
                   userFile.setUser_name(User.user.getUser_name());
                   userFile.setFile_hash(file_hash);
                   userFile.setFile_date(simpleDateFormat.format(new Date()));
                   userFile.setFile_size(FileSizeUtil.FormetFileSize(file.length()));
                   if(FileFragment.dir!=null)
                       userFile.setDir_id(FileFragment.dir.getDir_id());
                   updateNotification(userFile.getFile_name()+"  文件开始上传");
                   OkHttpClient client = new OkHttpClient();
                   RequestBody requestBody = new FormBody.Builder()
                           .add("file_json",new Gson().toJson(userFile))
                           .build();
                   Request request = new Request.Builder()
                           .url(VariableUtil.Service_IP + "uploadFileHASH")
                           .post(requestBody)
                           .build();
                   Response response = client.newCall(request).execute();
                   String str = response.body().string();
                   System.out.println(str);
                   if(str.equals("1")){
                        response.close();
                        client = new OkHttpClient();
                        requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("file_json",new Gson().toJson(userFile))
                                .addFormDataPart("file",file.getName(),
                                        RequestBody.create(MediaType.parse("multipart/form-data"),file))
                                .build();
                        request = new Request.Builder()
                               .url(VariableUtil.Service_IP + "uploadFile")
                               .post(requestBody)
                               .build();
                        client.newCall(request).execute();
                       UserFile.user_file_list.add(userFile);
                       finshUploadFile.add(userFile);
                       updateNotification(userFile.getFile_name()+"  文件上传成功");
                   }else if(str.equals("0")){
                       UserFile.user_file_list.add(userFile);
                       finshUploadFile.add(userFile);
                       updateNotification(userFile.getFile_name()+"  文件上传成功");
                   }
                   Message message = new Message();
                   message.what = 0x001;
                   BottomHomePageBarFragment.handler.sendMessage(message);
               }catch (Exception e){
                   e.printStackTrace();
               }
            }
        }
    }

    private void updateNotification(String s){
        stopForeground(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(2+"","文件下载通知", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(FileFragment.context,s)
                    .setChannelId(2+"")
                    .setContentTitle("文件上传")
                    .setContentText(s)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));

        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(FileFragment.context,s);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
            builder.setContentTitle("文件上传");
            builder.setContentText(s);
        }
        notificationManager.notify(1,notification.build());
    }


}
