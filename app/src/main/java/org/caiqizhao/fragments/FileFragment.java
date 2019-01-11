package org.caiqizhao.fragments;



import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import com.example.caiqizhao.mycloud.R;
import com.google.gson.Gson;
import com.leon.lfilepickerlibrary.LFilePicker;

import org.caiqizhao.activity.MainActivity;
import org.caiqizhao.entity.Directory;
import org.caiqizhao.entity.FileCloud;
import org.caiqizhao.entity.User;
import org.caiqizhao.entity.UserFile;
import org.caiqizhao.adapter.FileAdapter;
import org.caiqizhao.service.AddDirIntentService;
import org.caiqizhao.service.DeleteFileService;
import org.caiqizhao.service.UpdateFileNameService;
import org.caiqizhao.service.UploadFileIntentService;
import org.caiqizhao.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户的文件管理列表
 */
public class FileFragment extends Fragment {

    public static Directory dir = null;

    public static Context context;
    private List<FileCloud> fileClouds = new ArrayList<FileCloud>();
    private View view;
    public static Toolbar toolbar;
    public static Boolean istoolbar = true;
    public static Handler handler;
    private FileAdapter fileAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        FileFragment.context = context;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new MessageUtile();
        init();
    }

    private void init() {
        if(!Directory.user_dir_list.isEmpty()){
            if(dir == null) {
                for (Directory directory: Directory.user_dir_list) {
                    if (directory.getDir_parent() == null || directory.getDir_parent() == 0) {
                        FileCloud fileCloud = new FileCloud(directory.getDir_name(), R.drawable.dir, "");
                        fileCloud.directory = directory;
                        fileClouds.add(fileCloud);
                    }
                }
            }else {
                for (Directory directory : Directory.user_dir_list) {
                    if (dir.getDir_id() == directory.getDir_parent()) {
                        FileCloud fileCloud = new FileCloud(directory.getDir_name(), R.drawable.dir, "");
                        fileCloud.directory = directory;
                        fileClouds.add(fileCloud);
                    }
                }
            }
        }

        if(!UserFile.user_file_list.isEmpty()){
            if(dir == null) {
                for (UserFile userFile : UserFile.user_file_list) {
                    if (userFile.getDir_id() == null || userFile.getDir_id() == 0) {
                        FileCloud fileCloud = new FileCloud(userFile.getFile_name(), R.drawable.file,
                                userFile.getFile_date() + "  " + userFile.getFile_size());
                        fileCloud.userFile = userFile;
                        fileClouds.add(fileCloud);
                    }
                }
            }else {
                for (UserFile userFile : UserFile.user_file_list) {
                    if (dir.getDir_id() == userFile.getDir_id()) {
                        FileCloud fileCloud = new FileCloud(userFile.getFile_name(), R.drawable.file,
                                userFile.getFile_date() + "  " + userFile.getFile_size());
                        fileCloud.userFile = userFile;
                        fileClouds.add(fileCloud);
                    }
                }
            }
        }

        RecyclerView file_list = view.findViewById(R.id.file_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        file_list.setLayoutManager(linearLayoutManager);
        fileAdapter = new FileAdapter(fileClouds);
        file_list.setAdapter(fileAdapter);

        toolbar = view.findViewById(R.id.file_toolbar);
        ((AppCompatActivity)context).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        if(dir!=null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setTitle(dir.getDir_name());

        }
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if(istoolbar) {
            inflater.inflate(R.menu.main_menu, menu);

        }
        else {
            inflater.inflate(R.menu.cancel_select, menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.cancel:
                FileAdapter.sum = 0;
                FileAdapter.ShutDownCheckNox();
                FileFragment.istoolbar = true;
                ((AppCompatActivity) FileFragment.context).supportInvalidateOptionsMenu();
                toolbar.setTitle("文件管理器");
                LocalBroadcastManager localBroadcastManager =
                        LocalBroadcastManager.getInstance(FileFragment.context);
                Intent intent = new Intent("com.example.mycloud.UPDATA_BAR");
                localBroadcastManager.sendBroadcast(intent);
                break;
            case R.id.upload:
                new LFilePicker().withSupportFragment(FileFragment.this)
                        .withRequestCode(1000)
                        .withTitle("请选择上传的文件")
                        .withStartPath(android.os.Environment.getExternalStorageDirectory().getPath())
                        .withFileFilter(new String[]{".txt", "jpg",".png", ".docx",".pptx",".ppt",".pdf",".doc",".xml",".zip",".rar"})
                        .start();
                break;
            case R.id.create_dir:
                final EditText dirName = new EditText(getActivity());
                final AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(getActivity());
                inputDialog.setTitle("请输入文件夹名称");
                inputDialog.setView(dirName);
                inputDialog.setNegativeButton("取消",null);
                inputDialog.setPositiveButton("创建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isDir(dirName.getText().toString())){
                            Directory directory = new Directory();
                            if(dir==null){
                                directory.setUser_name(User.user.getUser_name());
                                directory.setDir_name(dirName.getText().toString());
                            }else {
                                directory.setUser_name(User.user.getUser_name());
                                directory.setDir_name(dirName.getText().toString());
                                directory.setDir_parent(dir.getDir_id());
                            }
                            Intent intent = new Intent(getActivity(),AddDirIntentService.class);
                            intent.putExtra("dir",new Gson().toJson(directory));
                            getActivity().startService(intent);
                        }else {
                            ToastUtil.showToast(getActivity(),"文件夹重名");
                        }
                    }
                });
                inputDialog.show();
                break;
            case android.R.id.home:
                if(dir.getDir_parent()==null||dir.getDir_parent()==0)
                    dir = null;
                else
                    dir = selectDir_parent(dir.getDir_parent());
                Message message = new Message();
                message.what = 0x001;
                BottomHomePageBarFragment.handler.sendMessage(message);
                break;


        }
        return true;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_file_list,container,false);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
                ArrayList<String> list = data.getStringArrayListExtra("paths");
                if(list!=null&&!list.isEmpty()) {
                    for (String s : list)
                        System.out.println(s);
                    Intent intent = new Intent(getActivity(),UploadFileIntentService.class);
                    intent.putStringArrayListExtra("file_path", list);
                    getActivity().startService(intent);

                }
            }
        }
    }


    private boolean isDir(String newDirName){
        if(dir == null){
            for (Directory directory : Directory.user_dir_list) {
                if ((directory.getDir_parent() == null || directory.getDir_parent() == 0)&&directory.getDir_name().equals(newDirName)) {
                    return false;
                }
            }
            return true;
        }else {
            for (Directory directory : Directory.user_dir_list) {
                if (dir.getDir_id() == directory.getDir_parent()&&directory.getDir_name().equals(newDirName)) {
                    return false;
                }
            }
            return true;
        }
    }

    private Directory selectDir_parent(int dir_parent){
        for (Directory directory : Directory.user_dir_list){
            if(directory.getDir_id()==dir_parent)
                return directory;
        }
        return null;
    }

    private class MessageUtile extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x001:
                    ((AppCompatActivity) FileFragment.context).supportInvalidateOptionsMenu();
                    toolbar.setTitle("文件管理器");
                    break;
                case 0x002:
                    init();
                    break;
                case 0x003:
                    fileAdapter.DeleteFile();
                    Intent intent = new Intent(getActivity(),DeleteFileService.class);
                    getActivity().startService(intent);
                    break;
                case 0x004:
                    final UserFile userFile;
                    if((userFile=fileAdapter.isUpdateFileName())!=null){
                        final EditText newFileName = new EditText(getActivity());
                        final AlertDialog.Builder inputDialog =
                                new AlertDialog.Builder(getActivity());
                        inputDialog.setTitle("请输入文件夹名称");
                        inputDialog.setView(newFileName);
                        inputDialog.setNegativeButton("取消",null);
                        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!newFileName.getText().toString().equals("")){
                                    Intent intent = new Intent(context,UpdateFileNameService.class);
                                    String fileName = userFile.getFile_name();
                                    userFile.setFile_name(newFileName.getText().toString());
                                    intent.putExtra("file",new Gson().toJson(userFile));
                                    intent.putExtra("fileName",fileName);
                                    context.startService(intent);
                                }
                            }
                        });
                        inputDialog.show();
                    }
                    break;
                case 0x005:
                    fileAdapter.DownloadFile();
                    Message message = new Message();
                    message.what = 0x001;
                    MainActivity.handler.sendMessage(message);
                    break;
            }
        }
    }
}
