package org.caiqizhao.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import com.example.caiqizhao.mycloud.Main;

import org.caiqizhao.activity.UpdataUserActivity;
import org.caiqizhao.fragments.ConfigUser;
import org.caiqizhao.util.PasswordSHA1Util;
import org.caiqizhao.util.VariableUtil;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateUserService extends Service {
    Thread update_user;
    public UpdateUserService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Bundle data = intent.getExtras();
        final String username = data.getString("username");
        final String password = data.getString("password");
        final int code = data.getInt("code");
        switch (code){
            case 0:
                update_user = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("username",username)
                                .add("password",password)
                                .add("code",code+"")
                                .build();
                        Request request = new Request.Builder()
                                .url(VariableUtil.Service_IP +"updateuser")
                                .post(requestBody)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String str = response.body().string();

                            if(str.equals("升级成功")){
                                Message message = new Message();
                                message.what = 0x001;
                                ConfigUser.handler.sendMessage(message);
                            }else {
                                Message message = new Message();
                                message.what = 0x002;
                                ConfigUser.handler.sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stopSelf();
                    }
                });
                break;
            case 1:
                update_user = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = data.getString("name");
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("username",username)
                                .add("password",password)
                                .add("code",code+"")
                                .add("name",name)
                                .build();
                        Request request = new Request.Builder()
                                .url(VariableUtil.Service_IP +"updateuser")
                                .post(requestBody)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String str = response.body().string();
                            if(str.equals("修改昵称成功")){
                                Message message = new Message();
                                message.what = 0x001;
                                Bundle data = new Bundle();
                                data.putString("name",name);
                                message.setData(data);
                                UpdataUserActivity.handler.sendMessage(message);
                            }else {
                                Message message = new Message();
                                message.what = 0x002;
                                UpdataUserActivity.handler.sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stopSelf();
                    }
                });
                break;
            case 2:
                update_user = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String new_password = data.getString("newpassword");
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("username",username)
                                .add("password",password)
                                .add("code",code+"")
                                .add("newpassword",PasswordSHA1Util.generateSHA1(new_password))
                                .build();
                        Request request = new Request.Builder()
                                .url(VariableUtil.Service_IP +"updateuser")
                                .post(requestBody)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String str = response.body().string();
                            if(str.equals("修改密码成功")){
                                Message message = new Message();
                                message.what = 0x003;
                                Bundle data = new Bundle();
                                data.putString("password",new_password);
                                message.setData(data);
                                UpdataUserActivity.handler.sendMessage(message);
                            }else {
                                Message message = new Message();
                                message.what = 0x004;
                                UpdataUserActivity.handler.sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stopSelf();
                    }
                });
                break;

        }
        update_user.start();
        return START_REDELIVER_INTENT;
    }


}
