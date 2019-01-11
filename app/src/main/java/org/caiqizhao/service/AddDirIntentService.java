package org.caiqizhao.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.example.caiqizhao.mycloud.Main;
import com.google.gson.Gson;

import org.caiqizhao.entity.Directory;
import org.caiqizhao.entity.User;
import org.caiqizhao.entity.UserFile;
import org.caiqizhao.fragments.BottomHomePageBarFragment;
import org.caiqizhao.fragments.FileFragment;
import org.caiqizhao.util.VariableUtil;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AddDirIntentService extends IntentService {

    public AddDirIntentService() {
        super("AddDirIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        String dir = intent.getStringExtra("dir");
        Directory directory = new Gson().fromJson(dir,Directory.class);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("dir",dir)
                .build();
        Request request = new Request.Builder()
                .url(VariableUtil.Service_IP +"addDir")
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String str = response.body().string();
            if(!str.equals("")&&str!=null){
                Message message = new Message();
                message.what = 0x001;
                System.out.println(str);
                directory.setDir_id(Integer.parseInt(str));
                Directory.user_dir_list.add(directory);
                BottomHomePageBarFragment.handler.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
