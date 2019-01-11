package org.caiqizhao.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.caiqizhao.mycloud.R;

import org.caiqizhao.activity.MainActivity;
import org.caiqizhao.adapter.FileAdapter;
import org.caiqizhao.service.DownloadFileService;


public class BottomFileOperationFragment extends Fragment {
    public static Context context;
    private View view;

    public BottomFileOperationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BottomFileOperationFragment.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.file_bar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bottom_file,container,false);
        return view;
    }

    /**
     * 底部状态栏响应事件
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Message message;
            switch (item.getItemId()) {
                case R.id.file_download:
                    message = new Message();
                    message.what = 0x005;
                    FileFragment.handler.sendMessage(message);
                    return true;
                case R.id.file_new_name:
                    message = new Message();
                    message.what=0x004;
                    FileFragment.handler.sendMessage(message);
                    return true;
                case R.id.file_delete:
                    message = new Message();
                    message.what=0x003;
                    FileFragment.handler.sendMessage(message);
                    return true;
            }
            return false;
        }
    };
}
