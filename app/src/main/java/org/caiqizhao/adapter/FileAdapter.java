package org.caiqizhao.adapter;


import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caiqizhao.mycloud.R;

import org.caiqizhao.activity.UpdataUserActivity;
import org.caiqizhao.entity.FileCloud;
import org.caiqizhao.entity.UserFile;
import org.caiqizhao.fragments.BottomHomePageBarFragment;
import org.caiqizhao.fragments.FileFragment;
import org.caiqizhao.service.DeleteFileService;
import org.caiqizhao.service.DownloadFileService;
import org.caiqizhao.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    public static int sum = 0;
    private static List<ViewHolder> holders = new ArrayList<ViewHolder>();

    private List<FileCloud> fileClouds;

    public FileAdapter(List<FileCloud> fileClouds) {
        this.fileClouds = fileClouds;
    }

    @NonNull
    @Override
    public FileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_file_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FileAdapter.ViewHolder holder, int position) {
        final FileCloud fileCloud = fileClouds.get(position);

        holder.file_tupian.setImageResource(fileCloud.getFile_tupian());
        holder.file_name.setText(fileCloud.getFile_name());
        holder.file_data.setText(fileCloud.getFile_data());
        if (fileCloud.directory!=null){
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileFragment.dir = fileCloud.directory;
                    Message message = new Message();
                    message.what = 0x001;
                    BottomHomePageBarFragment.handler.sendMessage(message);
                }
            });
            holder.select_file.setChecked(false);
            holder.select_file.setEnabled(false);
        }else {
            holder.view.setClickable(false);
            holder.select_file.setBackgroundResource(R.drawable.danxuan);
            holder.select_file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.select_file.isChecked()) {
                        sum++;
                        FileFragment.toolbar.setTitle("已选中"+sum+"个文件");
                        if(FileFragment.istoolbar) {
                            FileFragment.istoolbar = false;
                            LocalBroadcastManager localBroadcastManager =
                                    LocalBroadcastManager.getInstance(FileFragment.context);
                            Intent intent = new Intent("com.example.mycloud.UPDATA_BAR");
                            localBroadcastManager.sendBroadcast(intent);
                            ((AppCompatActivity) FileFragment.context).supportInvalidateOptionsMenu();
                        }
                        holders.add(holder);
                    }
                    if (!holder.select_file.isChecked()){
                        sum--;
                        if(sum==0){
                            ShutDownCheckNox();
                            FileFragment.toolbar.setTitle("文件管理");
                            FileFragment.istoolbar = true;
                            LocalBroadcastManager localBroadcastManager =
                                    LocalBroadcastManager.getInstance(FileFragment.context);
                            Intent intent = new Intent("com.example.mycloud.UPDATA_BAR");
                            localBroadcastManager.sendBroadcast(intent);
                            ((AppCompatActivity) FileFragment.context).supportInvalidateOptionsMenu();
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return fileClouds.size();
    }

    public UserFile isUpdateFileName() {
        if(holders.size()>1) {
            sum = 0;
            ToastUtil.showToast(FileFragment.context, "一次只能更改一个文件名");
            ShutDownCheckNox();
            FileFragment.toolbar.setTitle("文件管理");
            FileFragment.istoolbar = true;
            LocalBroadcastManager localBroadcastManager =
                    LocalBroadcastManager.getInstance(FileFragment.context);
            Intent intent = new Intent("com.example.mycloud.UPDATA_BAR");
            localBroadcastManager.sendBroadcast(intent);
            ((AppCompatActivity) FileFragment.context).supportInvalidateOptionsMenu();
            return null;
        }
        else {
            sum = 0;
            FileFragment.toolbar.setTitle("文件管理");
            FileFragment.istoolbar = true;
            LocalBroadcastManager localBroadcastManager =
                    LocalBroadcastManager.getInstance(FileFragment.context);
            Intent intent = new Intent("com.example.mycloud.UPDATA_BAR");
            localBroadcastManager.sendBroadcast(intent);
            ((AppCompatActivity) FileFragment.context).supportInvalidateOptionsMenu();
            UserFile userFile = fileClouds.get(holders.get(0).getAdapterPosition()).userFile;
            ShutDownCheckNox();
            return userFile;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView file_tupian;
        TextView file_name;
        TextView file_data;
        CheckBox select_file;
        View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            file_tupian = itemView.findViewById(R.id.file_tupiao);
            file_name = itemView.findViewById(R.id.file_name);
            file_data = itemView.findViewById(R.id.file_size);
            select_file = itemView.findViewById(R.id.select_file);
        }
    }


    /**
     * 取消所有选中文件
     */
    public static void ShutDownCheckNox(){
        for (ViewHolder holder:holders){
            holder.select_file.setChecked(false);
        }
        holders.clear();
    }


    public void DeleteFile(){
        sum = 0;
        for(ViewHolder holder :holders)
            DeleteFileService.userFileList.add(fileClouds.get(holder.getAdapterPosition()).userFile);
        ShutDownCheckNox();
        FileFragment.toolbar.setTitle("文件管理");
        FileFragment.istoolbar = true;
        LocalBroadcastManager localBroadcastManager =
                LocalBroadcastManager.getInstance(FileFragment.context);
        Intent intent = new Intent("com.example.mycloud.UPDATA_BAR");
        localBroadcastManager.sendBroadcast(intent);
        ((AppCompatActivity) FileFragment.context).supportInvalidateOptionsMenu();

    }

    public void DownloadFile(){
        sum = 0;
        for(ViewHolder holder :holders)
            DownloadFileService.downloadUserFileList.add(fileClouds.get(holder.getAdapterPosition()).userFile);
        ShutDownCheckNox();
        FileFragment.toolbar.setTitle("文件管理");
        FileFragment.istoolbar = true;
        LocalBroadcastManager localBroadcastManager =
                LocalBroadcastManager.getInstance(FileFragment.context);
        Intent intent = new Intent("com.example.mycloud.UPDATA_BAR");
        localBroadcastManager.sendBroadcast(intent);
        ((AppCompatActivity) FileFragment.context).supportInvalidateOptionsMenu();

    }
}
