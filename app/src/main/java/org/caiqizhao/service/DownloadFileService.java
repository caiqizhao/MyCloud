package org.caiqizhao.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.caiqizhao.mycloud.R;

import org.caiqizhao.activity.MainActivity;
import org.caiqizhao.entity.Directory;
import org.caiqizhao.entity.UserFile;
import org.caiqizhao.fragments.FileFragment;
import org.caiqizhao.util.FileTransferListener;
import org.caiqizhao.util.VariableUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadFileService extends Service {
    private  DownloadTask downloadTask;

    public static List<UserFile> downloadUserFileList = new ArrayList<UserFile>();

    public static List<UserFile> successUserFileList = new ArrayList<UserFile>();

    private UserFile userFile;

    private FileTransferListener listener = new FileTransferListener() {
        @Override
        public void onProgress(int progress) {
            Notification notification = getNotification(userFile.getFile_name()+"  正在下载...",progress);
            if (notification!=null)
                getNotificationManager().notify(1,notification);
        }

        @Override
        public void onSucceess() {
            downloadTask = null;
            stopForeground(true);
            Notification notification = getNotification(userFile.getFile_name()+"  下载完成",-1);
            if (notification!=null)
                getNotificationManager().notify(1,notification);
            downloadUserFileList.remove(userFile);
            if(isSuccess(userFile))
                successUserFileList.add(userFile);
            userFile = null;
            if(!downloadUserFileList.isEmpty()){
                mbinder.startDownload();
            }
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            Notification notification = getNotification(userFile.getFile_name()+"  下载失败",-1);
            if (notification!=null)
                getNotificationManager().notify(1,notification);
            downloadUserFileList.remove(userFile);
            userFile = null;
            if(!downloadUserFileList.isEmpty()){
                mbinder.startDownload();
            }
        }

        @Override
        public void onPaused() {
            downloadTask = null;
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            downloadUserFileList.remove(userFile);
            userFile = null;
            if(!downloadUserFileList.isEmpty()){
                mbinder.startDownload();
            }
        }
    };


    private DownloadFileBinder mbinder = new DownloadFileBinder();

    private Notification getNotification(String s, int progress) {
        Intent intent = new Intent(FileFragment.context,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(FileFragment.context,0,intent,0);
        Notification.Builder notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(1+"","文件下载通知", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(FileFragment.context,s)
                    .setChannelId(1+"")
                    .setContentTitle(s)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                    .setContentIntent(pi);
            if(progress>=0){
                notification.setContentText(progress+"%");
                notification.setProgress(100,progress,false);
            }
            notificationManager.notify(1,notification.build());
            return null;
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(FileFragment.context,s);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
            builder.setContentIntent(pi);
            builder.setContentTitle(s);
            if(progress>=0){
                builder.setContentText(progress+"%");
                builder.setProgress(100,progress,false);
            }
            return builder.build();
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mbinder;
    }

    public class DownloadFileBinder extends Binder {
        public void startDownload(){
            if(downloadTask == null&&!downloadUserFileList.isEmpty()){
                userFile = downloadUserFileList.get(0);
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(userFile);
                Notification notification = getNotification(userFile.getFile_name()+"正在下载",0);
                if(notification!=null)
                    startForeground(1,notification);
            }
        }

        public void pauseDownload(){
            if(downloadTask!=null){
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            if (downloadTask!=null){
                downloadTask.cancelDownload();
            }
            if(userFile!=null){
                String fileName = userFile.getFile_name();
                String directory = MainActivity.user_file.getPath();
                File file = new File(directory,fileName);
                if(file.exists()){
                    file.delete();
                }
                getNotificationManager().cancel(1);
                stopForeground(true);


            }
        }
    }

    private boolean isSuccess(UserFile userFile){
        for (UserFile userFile1:successUserFileList){
            if(userFile1.getDir_id()==userFile.getDir_id()&&userFile1.getFile_name().equals(userFile.getFile_name()))
                return false;
        }

        return true;
    }
}
