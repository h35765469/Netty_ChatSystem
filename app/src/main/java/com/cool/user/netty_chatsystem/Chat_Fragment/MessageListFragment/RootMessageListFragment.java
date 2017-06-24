package com.cool.user.netty_chatsystem.Chat_Fragment.MessageListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2016/9/14.
 */
public class RootMessageListFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        Bundle bundle = getArguments();//從MainFragment來的為了點擊上方提醒欄後可以直接開啟聊天室
        Messagelist_Fragment messagelist_fragment = new Messagelist_Fragment();
        if(bundle != null){
            messagelist_fragment.setArguments(bundle);
        }
        View rootView = inflater.inflate(R.layout.fragment_rootmessagelist , container, false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.replace(R.id.fragmentContainer, messagelist_fragment);
        fragmentTransaction.replace(R.id.messageListContainer, messagelist_fragment);
        fragmentTransaction.commit();

        return rootView;
    }
}
