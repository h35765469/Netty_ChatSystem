package com.example.user.netty_chatsystem.Chat_Fragment.ProfileFragment;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.user.netty_chatsystem.Chat_Fragment.CameraFragment.OpenCameraFragment;
import com.example.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.example.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.delete_color)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();


        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory.getAbsolutePath(), sharePreferenceManager.getProfileName());
        com.example.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.ImageLoader.Builder builder = new com.example.user.netty_chatsystem.Chat_Fragment.CameraFragment.internal.utils.ImageLoader.Builder(getActivity());
        builder.load(file.getAbsolutePath()).build().into(profilePreviewImg);

        takeProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.allContainer, new OpenCameraFragment());
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
