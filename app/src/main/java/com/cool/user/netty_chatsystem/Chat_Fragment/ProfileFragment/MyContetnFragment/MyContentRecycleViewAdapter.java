package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.MyContetnFragment;

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
 * Created by user on 2017/2/22.
 */
public class MyContentRecycleViewAdapter extends RecyclerView.Adapter<MyContentRecycleViewAdapter.ViewHolder> {
    private ArrayList<MyContentData>myContentDataArrayList;
    private Context context;
    private DisplayImageOptions options;
    private static ClickListener clickListener;


    public MyContentRecycleViewAdapter(Context context, ArrayList<MyContentData> myContentDataArrayList) {
        this.context = context;
        this.myContentDataArrayList = myContentDataArrayList;
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
    public MyContentRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resource_mycontent_item_recycle, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ImageLoader.getInstance()
                .displayImage(Config.SERVER_ADDRESS + myContentDataArrayList.get(position).getContent() + ".jpg", viewHolder.myContentImg, options, new SimpleImageLoadingListener() {
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

        viewHolder.collectCountTxt.setText(String.valueOf(myContentDataArrayList.get(position).getCollectCount()));
        viewHolder.unReadCountTxt.setText(String.valueOf(myContentDataArrayList.get(position).getUnReadCount()));
    }

    @Override
    public int getItemCount() {
        return myContentDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView myContentImg;
        ImageView collectIconImg;
        ImageView unReadIconImg;
        TextView collectCountTxt;
        TextView unReadCountTxt;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            myContentImg = (ImageView)view.findViewById(R.id.myContentImg);
            collectIconImg = (ImageView)view.findViewById(R.id.collectIconImg);
            unReadIconImg = (ImageView)view.findViewById(R.id.unReadIconImg);
            collectCountTxt = (TextView)view.findViewById(R.id.collectCountTxt);
            unReadCountTxt = (TextView)view.findViewById(R.id.unReadCountTxt);
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
