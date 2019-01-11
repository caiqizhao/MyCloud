package org.caiqizhao.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caiqizhao.mycloud.R;

import org.caiqizhao.entity.UserFile;

import java.util.ArrayList;
import java.util.List;

public class TransferAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    List<UserFile > userFileList = new ArrayList<UserFile>();

    public UserFile getUserFile() {
        return userFile;
    }

    public void setUserFile(UserFile userFile) {
        this.userFile = userFile;
    }

    private UserFile userFile = null;



    public TransferAdapter(List<UserFile> userFileList) {
        this.userFileList = userFileList;
    }

    @NonNull
    @Override
    public FileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_file_item,parent,false);
        FileAdapter.ViewHolder holder = new FileAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FileAdapter.ViewHolder holder, int position) {
        UserFile userFile = userFileList.get(position);
        holder.file_tupian.setImageResource(R.drawable.file);
        holder.file_name.setText(userFile.getFile_name());
        holder.file_data.setText(userFile.getFile_size()+"  "+userFile.getFile_date());
        final int i = holder.getAdapterPosition();
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setUserFile(userFileList.get(i));
                //mPostion = getmPostion();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return userFileList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView file_tupian;
        TextView file_name;
        TextView file_data;
        View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            file_tupian = itemView.findViewById(R.id.file_tupiao);
            file_name = itemView.findViewById(R.id.file_name);
            file_data = itemView.findViewById(R.id.file_size);
        }
    }
}
