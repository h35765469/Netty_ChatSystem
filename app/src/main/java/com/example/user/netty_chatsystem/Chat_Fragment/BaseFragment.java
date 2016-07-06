package com.example.user.netty_chatsystem.Chat_Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 2016/3/11.
 */
public abstract class BaseFragment extends Fragment {
    public Context mcontext;
    private View mview;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mcontext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,Bundle savedInstanceState) {
        mview = initView(inflater);
        return mview;
    }
    /**
     * 初始化界面
     */

    public abstract View initView(LayoutInflater inflater);

    /**
     * 初始化數據
     */
    public abstract void initData(Bundle savedInstanceState);

}

