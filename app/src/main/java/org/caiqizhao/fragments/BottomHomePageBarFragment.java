package org.caiqizhao.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.caiqizhao.mycloud.R;


public class BottomHomePageBarFragment extends Fragment {

    public static Context context;
    private View view;
    public static Handler handler;
    private boolean isFilePage = true;
    public BottomHomePageBarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BottomHomePageBarFragment.context = context;
    }


    /**
     * 底部状态栏响应事件
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.file_home:
                    isFilePage = true;
                    replaceFragment(new FileFragment());
                    return true;
                case R.id.transfer_home:
                    isFilePage = false;
                    replaceFragment(new TransferFragment());
                    return true;
                case R.id.user_home:
                    isFilePage = false;
                    replaceFragment(new ConfigUser());
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new MessgaeUtil();
        replaceFragment(new FileFragment());
        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.home_page);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bottom_home_page,container,false);
        return view;

    }

    /**
     * 变更fragment
     * @param fragment
     */
    public void replaceFragment(Fragment fragment){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.change_layout,fragment);
        transaction.commit();
    }

    public class MessgaeUtil extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x001:
                    if (isFilePage)
                        replaceFragment(new FileFragment());
                    break;
            }
        }
    }
}

