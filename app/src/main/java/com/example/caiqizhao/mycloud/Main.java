package com.example.caiqizhao.mycloud;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidException;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.caiqizhao.activity.MainActivity;
import org.caiqizhao.activity.RegisterActivity;
import org.caiqizhao.entity.Directory;
import org.caiqizhao.entity.User;
import org.caiqizhao.entity.UserFile;
import org.caiqizhao.service.LoginService;
import org.caiqizhao.util.ToastUtil;
import org.caiqizhao.util.UsernameAndPasswordByIs;

import java.io.File;
import java.util.List;

import static org.caiqizhao.fragments.FileFragment.context;

public class Main extends AppCompatActivity {

    private Button go; //登陆

    private CheckBox login_no_password; //记住密码

    private TextView username,password; //账户密码输入框

    private TextView register,login_to_password; //注册以及忘记密码

    private String user_name=null,user_password=null;

    public static Handler handler; //消息接收

    public static File app_file;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        app_file = new File(android.os.Environment.getExternalStorageDirectory(),"MyCloud");
        if(!app_file.exists()) {
            app_file.mkdir();
        }

        go = findViewById(R.id.login_go);
        login_no_password = findViewById(R.id.login_no_password);
        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        register = findViewById(R.id.login_no_register);
        login_to_password = findViewById(R.id.login_to_password);

        go.setOnClickListener(new go_Click());

        register.setOnClickListener(new register_Click());

        handler = new MessageUtil();

        login_to_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, MainActivity.class);
                startActivity(intent);
            }
        });



    }

    /**
     * 注册按钮响应时间
     */
    private class register_Click implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Main.this, RegisterActivity.class);
            startActivity(intent);

        }
    }

    /**
     * 登陆按钮响应时间
     */
    private class go_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(UsernameAndPasswordByIs.checkusername(username.getText().toString())
                    && UsernameAndPasswordByIs.checkPassword(password.getText().toString())){
                Intent loginservice = new Intent(Main.this,LoginService.class);
                Bundle data = new Bundle();
                data.putString("username",username.getText().toString());
                data.putString("password",password.getText().toString());
                loginservice.putExtras(data);
                startService(loginservice);

            }else {

                ToastUtil.showToast(Main.this,"账户或密码格式不正确");
                username.setText("");
                password.setText("");
            }

        }
    }

    /**
     * 消息处理内部类
     */
    private class MessageUtil extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //接收数据
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String str = data.getString("login");
            if(msg.what == 0x003){
                JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
                JsonElement user = jsonObject.get("user");
                Gson gson = new Gson();
                User.user = gson.fromJson(user,User.class);
                JsonArray user_file = jsonObject.getAsJsonArray("user_file");
                if(user_file!=null){
                    UserFile.user_file_list = gson.fromJson(user_file,new TypeToken<List<UserFile>>(){}.getType());
                }
                JsonArray user_dir = jsonObject.getAsJsonArray("user_dir");
                System.out.println(user_dir);
                if(user_dir!=null){
                    Directory.user_dir_list = gson.fromJson(user_dir,new TypeToken<List<Directory>>(){}.getType());
                }
                Intent intent = new Intent(Main.this, MainActivity.class);
                startActivity(intent);
                Main.this.finish();
            }else{
                //获得消息中的数据并显示
                ToastUtil.showToast(Main.this,str);
            }

        }
    }

//    /**
//     * 暂停活动时保存信息
//     */
//    @Override
//    protected void onPause() {
//        super.onPause();
//        user_name = username.getText().toString();
//        user_password = password.getText().toString();
//    }
//
//    /**
//     * 停止活动时保存信息
//     */
//    @Override
//    protected void onStop() {
//        super.onStop();
//        user_name = username.getText().toString();
//        user_password = password.getText().toString();
//    }
//
//    /**
//     * 恢复信息
//     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(user_name!=null&&user_name.equals("")){
//            username.setText(user_name);
//        }
//        if(user_password!=null&&user_password.equals("")){
//            password.setText(user_password);
//        }
//    }

}

