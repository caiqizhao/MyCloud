package org.caiqizhao.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.caiqizhao.mycloud.R;

import org.caiqizhao.activity.MainActivity;
import org.caiqizhao.adapter.FileAdapter;
import org.caiqizhao.adapter.TransferAdapter;
import org.caiqizhao.entity.User;
import org.caiqizhao.entity.UserFile;
import org.caiqizhao.service.DownloadFileService;
import org.caiqizhao.service.UploadFileIntentService;

import java.util.ArrayList;
import java.util.List;

import ru.bartwell.exfilepicker.ui.adapter.holder.UpFilesListHolder;


public class TransferFragment extends Fragment {

    private Context context;
    private View view;
    private static boolean isTransfer = true;

    public TransferFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isTransfer = true;
        init(DownloadFileService.downloadUserFileList);
        System.out.println(DownloadFileService.downloadUserFileList);
        view.findViewById(R.id.be_doing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTransfer = true;
                init(DownloadFileService.downloadUserFileList);
            }
        });
        view.findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isTransfer = false;
                List<UserFile> userFileList = new ArrayList<UserFile>();
                userFileList.clear();
                userFileList.addAll(DownloadFileService.successUserFileList);
                userFileList.addAll(UploadFileIntentService.finshUploadFile);
                init(userFileList);
            }
        });
    }

    private void init(List<UserFile> userFileList) {
        RecyclerView file_list = view.findViewById(R.id.transfer_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        file_list.setLayoutManager(linearLayoutManager);
        TransferAdapter transferAdapter = new TransferAdapter(userFileList);
        file_list.setAdapter(transferAdapter);
        if(isTransfer){
            registerForContextMenu(file_list);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transfer,container,false);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.canceled:
                Message message = new Message();
                message.what = 0x003;
                MainActivity.handler.sendMessage(message);
                break;
            case R.id.paused:
                break;
        }
        return super.onContextItemSelected(item);
    }

}
