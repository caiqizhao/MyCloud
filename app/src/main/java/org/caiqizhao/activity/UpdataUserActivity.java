package org.caiqizhao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.caiqizhao.mycloud.Main;
import com.example.caiqizhao.mycloud.R;

import org.caiqizhao.entity.User;
import org.caiqizhao.service.UpdateUserService;
import org.caiqizhao.util.PasswordSHA1Util;
import org.caiqizhao.util.ToastUtil;
import org.caiqizhao.util.VerityCode;

public class UpdataUserActivity extends AppCompatActivity {

    private LinearLayout updata_name_button;
    private LinearLayout updata_password_button;
    private ImageButton return_main_activity;
    private TextView uer_name ,user_id;
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updata_user);
        init();
    }

    private void init() {
        //创建消息接收机制
        handler = new MessageUtil();
        user_id = findViewById(R.id.user_id);
        user_id.setText(User.user.getUser_name());

        //用户名更改
        updata_name_button = findViewById(R.id.upadta_name_button);
        uer_name = findViewById(R.id.user_name);
        uer_name.setText(User.user.getName());
        updata_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText newname = new EditText(UpdataUserActivity.this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(UpdataUserActivity.this);
                inputDialog.setTitle("请输入新的用户昵称");
                inputDialog.setView(newname);
                inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!newname.getText().toString().trim().equals("")) {
                            Intent update_name_service = new Intent(UpdataUserActivity.this,UpdateUserService.class);
                            Bundle data = new Bundle();
                            data.putInt("code",1);
                            data.putString("username",User.user.getUser_name());
                            data.putString("password",User.user.getUser_password());
                            data.putString("name",newname.getText().toString());
                            update_name_service.putExtras(data);
                            startService(update_name_service);

                        }
                    }
                });
                inputDialog.show();
            }
        });


        //更改密码
        updata_password_button = findViewById(R.id.updata_password_button);
        updata_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText passwod = new EditText(UpdataUserActivity.this);
                passwod.setHint("请输入旧密码");
                final EditText newpassword = new EditText(UpdataUserActivity.this);
                newpassword.setHint("请输入新密码");
                final EditText newpassword_agin = new EditText(UpdataUserActivity.this);
                newpassword_agin.setHint("请再次输入新密码");
                final AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(UpdataUserActivity.this);
                inputDialog.setTitle("更改密码");
                inputDialog.setView(passwod);
                inputDialog.setView(newpassword);
                inputDialog.setView(newpassword_agin);
                inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password_str = passwod.getText().toString();
                        String newpassword_str = newpassword.getText().toString();
                        String newpassword_agin_str = newpassword_agin.getText().toString();
                        if(password_str.trim().equals("")||
                                newpassword_str.trim().equals("")||
                                newpassword_agin_str.trim().equals("")){
                            ToastUtil.showToast(UpdataUserActivity.this,"密码编辑框不能为空白");
                        }else {
                            if((PasswordSHA1Util.generateSHA1(password_str)).equals(User.user.getUser_password())){
                                if(newpassword_str.equals(newpassword_agin_str)){
                                    Intent intent = new Intent(UpdataUserActivity.this,UpdateUserService.class);
                                    Bundle data = new Bundle();
                                    data.putInt("code",2);
                                    data.putString("username",User.user.getUser_name());
                                    data.putString("password",User.user.getUser_password());
                                    data.putString("newpassword",newpassword_agin_str);
                                    intent.putExtras(data);
                                    startService(intent);
                                }else {
                                    ToastUtil.showToast(UpdataUserActivity.this,"两次输入密码不一致");
                                }
                            }else {
                                ToastUtil.showToast(UpdataUserActivity.this,"旧密码不正确");
                            }
                        }
                    }
                });
                inputDialog.show();
            }
        });

        //返回按钮设置
        return_main_activity = findViewById(R.id.return_main_activity);
        return_main_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdataUserActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    /**
     * 消息处理内部类
     */
    private class MessageUtil extends Handler {
        AlertDialog.Builder normalDialog = null;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle daata = msg.getData();
            switch (msg.what){
                case 0x003:
                    normalDialog = new AlertDialog.Builder(UpdataUserActivity.this);
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("密码修改成功!!");
                    normalDialog.setPositiveButton("确定",null);
                    User.user.setUser_password(daata.getString("password"));
                    normalDialog.show();
                    break;
                case 0x004:
                    normalDialog = new AlertDialog.Builder(UpdataUserActivity.this);
                    normalDialog.setTitle("警告");
                    normalDialog.setMessage("密码修改失败!!");
                    normalDialog.setPositiveButton("确定",null);
                    normalDialog.show();
                    break;
                case 0x001:
                    User.user.setName(daata.getString("name"));
                    uer_name.setText(User.user.getName());
                    LocalBroadcastManager localBroadcastManager =
                            LocalBroadcastManager.getInstance(UpdataUserActivity.this);
                    Intent intent = new Intent("com.example.mycloud.UPDATA_USER");
                    intent.putExtra("name",User.user.getName());
                    localBroadcastManager.sendBroadcast(intent);
                    break;
            }

        }
    }
}
