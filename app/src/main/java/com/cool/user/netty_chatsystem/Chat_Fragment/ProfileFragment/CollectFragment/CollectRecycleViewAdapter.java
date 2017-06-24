package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment;

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
 * Created by user on 2017/2/17.
 */
public class CollectRecycleViewAdapter extends RecyclerView.Adapter<CollectRecycleViewAdapter.ViewHolder> {
    Context context;
    ArrayList<CollectData> collectDataArrayList;
    private DisplayImageOptions options;
    private static ClickListener clickListener;;

    public CollectRecycleViewAdapter(Context context,  ArrayList<CollectData> collectDataArrayList) {
        this.context = context;
        this.collectDataArrayList = collectDataArrayList;
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
    public CollectRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resource_collect_recycleview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ImageLoader.getInstance()
                .displayImage(Config.SERVER_ADDRESS + collectDataArrayList.get(position).getCollectContent() + ".jpg", viewHolder.collectContentImg, options, new SimpleImageLoadingListener() {
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

        viewHolder.collectOwnerNameTxt.setText(collectDataArrayList.get(position).getCollectNickName());
    }

    @Override
    public int getItemCount() {
        return collectDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView collectContentImg;
        TextView collectOwnerNameTxt;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            collectContentImg = (ImageView)view.findViewById(R.id.myContentImg);
            collectOwnerNameTxt = (TextView)view.findViewById(R.id.collectOwnerNameTxt);
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
