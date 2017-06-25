package com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.FriendContent;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by user on 2017/6/25.
 */

public class FriendContentRecycleViewAdapter extends RecyclerView.Adapter<FriendContentRecycleViewAdapter.ViewHolder>{
    private ArrayList<FriendContentData> friendContentArrayList;
    private Context context;
    private DisplayImageOptions options;
    private static ClickListener clickListener;


    public FriendContentRecycleViewAdapter(Context context, ArrayList<FriendContentData> friendContentArrayList) {
        this.context = context;
        this.friendContentArrayList = friendContentArrayList;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.delete_color)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resource_friendcontent_recycleview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ImageLoader.getInstance()
                .displayImage(Config.SERVER_ADDRESS + friendContentArrayList.get(position).getContent() + ".jpg", viewHolder.friendContentImg, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                    }
                });

        viewHolder.friendNameTxt.setText(friendContentArrayList.get(position).getOwnerNickName());
    }

    @Override
    public int getItemCount() {
        return friendContentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView friendContentImg;
        TextView friendNameTxt;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            friendContentImg = (ImageView)view.findViewById(R.id.friendContentImg);
            friendNameTxt = (TextView)view.findViewById(R.id.friendNameTxt);
        }

        @Override
        public void onClick(View v){
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
