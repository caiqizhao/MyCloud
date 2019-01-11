package org.caiqizhao.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.example.caiqizhao.mycloud.Main;
import com.example.caiqizhao.mycloud.R;

import org.caiqizhao.entity.User;
import org.caiqizhao.fragments.BottomFileOperationFragment;
import org.caiqizhao.fragments.BottomHomePageBarFragment;
import org.caiqizhao.fragments.FileFragment;
import org.caiqizhao.service.DownloadFileService;
import org.caiqizhao.service.UploadFileIntentService;
import org.caiqizhao.util.ToastUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    public static File user_file;
    private UpdataBar updataBar;

    public static Handler handler;

    public DownloadFileService.DownloadFileBinder downloadFileBinder ;

    private  ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadFileBinder = (DownloadFileService.DownloadFileBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new MessageUtil();
        Intent intent = new Intent(this,DownloadFileService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
        setContentView(R.layout.activity_main);
        replaceFragment(new BottomHomePageBarFragment());
        user_file = new File(Main.app_file.getPath(),User.user.getUser_name());
        if(!user_file.exists()) {
            user_file.mkdir();
        }


        //注册广播接收
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.mycloud.UPDATA_BAR");
        updataBar = new UpdataBar();
        localBroadcastManager.registerReceiver(updataBar,intentFilter);


    }

    /**
     * 变更fragment
     * @param fragment
     */
    public void replaceFragment(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.status_bar,fragment);
        transaction.commit();
    }

    /**
     * 监听广播用文件管理底部状态栏的变化
     */
    public  class UpdataBar extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!FileFragment.istoolbar){
                replaceFragment(new BottomFileOperationFragment());
            }else {
                replaceFragment(new BottomHomePageBarFragment());
            }
        }
    }


    public class MessageUtil extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x001:
                    if (downloadFileBinder!=null)
                        downloadFileBinder.startDownload();
                    break;
                case 0x002:
                    ToastUtil.showToast(MainActivity.this,msg.getData().getString("message"));
                    break;
                case 0x003:
                    if(downloadFileBinder!=null)
                        downloadFileBinder.cancelDownload();
                    break;
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

}
