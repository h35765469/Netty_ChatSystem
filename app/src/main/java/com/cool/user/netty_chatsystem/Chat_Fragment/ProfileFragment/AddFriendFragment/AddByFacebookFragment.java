package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.AddFriendFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2016/11/5.
 */
public class AddByFacebookFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.addbyfacebook_fragment, container, false);

        return rootView;
    }

}
