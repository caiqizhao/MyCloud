package org.caiqizhao.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import com.google.gson.Gson;

import org.caiqizhao.entity.UserFile;
import org.caiqizhao.fragments.BottomHomePageBarFragment;
import org.caiqizhao.fragments.FileFragment;
import org.caiqizhao.util.VariableUtil;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UpdateFileNameService extends Service {
    public UpdateFileNameService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String file = intent.getStringExtra("file");
        final String fileName = intent.getStringExtra("fileName");
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("file",file)
                        .add("fileName",fileName)
                        .build();
                Request request = new Request.Builder()
                        .url(VariableUtil.Service_IP +"updateFileName")
                        .post(requestBody)
                        .build();
                try {
                    client.newCall(request).execute();
                    Message message = new Message();
                    message.what= 0x001;
                    BottomHomePageBarFragment.handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return START_STICKY;
    }
}
