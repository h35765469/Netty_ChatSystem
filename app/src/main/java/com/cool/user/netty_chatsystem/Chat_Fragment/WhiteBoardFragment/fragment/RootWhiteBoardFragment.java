package com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2016/10/30.
 */
public class RootWhiteBoardFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.rootwhiteboard_fragment, container, false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.rootWhiteBoardContainer, new WhiteBoardFragment());
        fragmentTransaction.commit();

        return rootView;
    }
}
