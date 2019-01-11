package org.caiqizhao.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.caiqizhao.mycloud.Main;
import com.example.caiqizhao.mycloud.R;

import org.caiqizhao.activity.MycloudVersion;
import org.caiqizhao.activity.UpdataUserActivity;
import org.caiqizhao.entity.User;
import org.caiqizhao.service.UpdateUserService;


public class ConfigUser extends Fragment {
    private View view;
    private ProgressBar config_usersize_progress;
    private TextView config_usersize_text,config_username;
    private LinearLayout config_gupgrade,config_shezhi,config_help,config_exit;

    private  View.OnClickListener onClickListener = new SelectOnClick();

    private UpdataUserFilter updataUserFilter;

    private  Context context;

    public static Handler handler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view =inflater.inflate(R.layout.fragment_config_user,container,false);
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        User user = User.user;

        handler = new MessageUtil();

        //初始化用户名
        config_username = view.findViewById(R.id.config_username);
        config_username.setText(user.getName());

        //初始化进度条
        config_usersize_progress = view.findViewById(R.id.config_usersize_progress);
        config_usersize_progress.setMax(Integer.parseInt(user.getUser_size()));
        config_usersize_progress.setProgress((int) Double.parseDouble(user.getFile_size()));

        //初始化文件内存栏
        config_usersize_text = view.findViewById(R.id.config_usersize_text);
        config_usersize_text.setText((double)Math.round(Double.parseDouble(user.getFile_size())/1024*100)/100+"MB/"+
                Integer.parseInt(User.user.getUser_size())/1024+"MB   "+
                (double) Math.round((Double.parseDouble(user.getFile_size())/Double.parseDouble(user.getUser_size()))*10000)/10000*100+"%");


        //初始化fragment选项点击事件
        //内存控件升级
        config_gupgrade = view.findViewById(R.id.config_gupgrade);
        config_gupgrade.setOnClickListener(onClickListener);

        //用户设置选项点击事件
        config_shezhi = view.findViewById(R.id.config_shezhi);
        config_shezhi.setOnClickListener(onClickListener);


        //帮助信息点击栏
        config_help = view.findViewById(R.id.config_help);
        config_help.setOnClickListener(onClickListener);

        //用户注销点击栏
        config_exit = view.findViewById(R.id.config_exit);
        config_exit.setOnClickListener(onClickListener);

        //注册广播接收
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.mycloud.UPDATA_USER");
        updataUserFilter = new UpdataUserFilter();
        localBroadcastManager.registerReceiver(updataUserFilter,intentFilter);

    }


    private class SelectOnClick implements View.OnClickListener{
        AlertDialog.Builder normalDialog;
        Intent intent;
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.config_gupgrade:
                    //请求扩增用户存储空间
                    normalDialog = new AlertDialog.Builder(v.getContext());
                    if(!User.user.getIs_user().equals("1")){
                        normalDialog.setTitle("提示");
                        normalDialog.setMessage("您暂时没有可升级的控件！请保持使用!!");
                        normalDialog.setPositiveButton("确定",null);
                        normalDialog.show();
                    }else {
                        intent = new Intent(context,UpdateUserService.class);
                        Bundle data = new Bundle();
                        data.putInt("code",0);
                        data.putString("username",User.user.getUser_name());
                        data.putString("password",User.user.getUser_password());
                        intent.putExtras(data);
                        context.startService(intent);
                    }

                    break;
                case R.id.config_shezhi:
                    intent = new Intent(context,UpdataUserActivity.class);
                    startActivity(intent);
                    break;
                case R.id.config_help:
                    intent = new Intent(context, MycloudVersion.class);
                    startActivity(intent);
                    break;
                case R.id.config_exit:
                    normalDialog = new AlertDialog.Builder(v.getContext());
                    normalDialog.setTitle("警告");
                    normalDialog.setMessage("确定退出账户");
                    normalDialog.setNegativeButton("取消",null);
                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            intent = new Intent(context, Main.class);
                            startActivity(intent);
                            ((Activity)context).finish();
                        }
                    });
                    normalDialog.show();
                    break;


            }
        }
    }


    /**
     * 监听广播用户名称的变化，并将其显示在相应的TextView中
     */
    public  class UpdataUserFilter extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            config_username.setText(intent.getStringExtra("name"));
        }
    }

    /**
     * 消息处理内部类
     */
    @SuppressLint("HandlerLeak")
    private class MessageUtil extends Handler {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AlertDialog.Builder normalDialog = null;
            switch (msg.what){
                case 0x001:
                    System.out.println("成功升级");
                    normalDialog = new AlertDialog.Builder(config_gupgrade.getContext());
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("存储空间升级成功!!");
                    config_usersize_progress.setMax(1024*1024*2);
                    User.user.setIs_user("0");
                    User.user.setUser_size(1024*1024*2+"");
                    config_usersize_text.setText((double)Math.round(
                            Double.parseDouble(User.user.getFile_size())/1024
                                    *100)/100+ "MB/" + Integer.parseInt(User.user.getUser_size())/1024+"MB   "+
                            (double) Math.round(
                                    (Double.parseDouble(User.user.getFile_size())/Double.parseDouble(User.user.getUser_size()))
                                            *10000)/10000
                                    *100+"%");
                    normalDialog.setPositiveButton("确定",null);
                    normalDialog.show();
                    break;
                case 0x002:
                    System.out.println("升级失败");
                    normalDialog = new AlertDialog.Builder(config_gupgrade.getContext());
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("您暂时没有可升级的控件！请保持使用!!");
                    normalDialog.setPositiveButton("确定",null);
                    normalDialog.show();
                    break;
            }


        }
    }
}
