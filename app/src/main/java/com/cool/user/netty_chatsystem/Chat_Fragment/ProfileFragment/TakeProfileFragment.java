package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.OpenCameraFragment;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.R;

import java.io.File;

/**
 * Created by user on 2017/3/6.
 */
public class TakeProfileFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_takeprofile, container, false);
        ImageView profilePreviewImg = (ImageView)rootView.findViewById(R.id.profilePreviewImg);
        ImageView takeProfileImg = (ImageView)rootView.findViewById(R.id.takeProfileImg);
        ImageView backImg = (ImageView)rootView.findViewById(R.id.backImg);


        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
        if(sharePreferenceManager.getProfileName().length() > 0){
            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File file = new File(directory.getAbsolutePath(), sharePreferenceManager.getProfileName());
            if(file.exists()) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                profilePreviewImg.setLayoutParams(layoutParams);
                com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.ImageLoader.Builder builder = new com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.ImageLoader.Builder(getActivity());
                builder.load(file.getAbsolutePath()).build().into(profilePreviewImg);
            }
        }

        takeProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack("backFragment");
                Bundle bundle = new Bundle();
                bundle.putInt("whichFragment", R.id.profileContainer);
                OpenCameraFragment openCameraFragment = new OpenCameraFragment();
                openCameraFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.profileContainer, openCameraFragment);
                fragmentTransaction.commit();
            }
        });

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });


        return rootView;
    }
}
