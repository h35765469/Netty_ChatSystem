package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.MyContetnFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_Fragment.ChatFragment.Constants;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by user on 2017/3/1.
 */
public class MyContentImageAdapter extends BaseAdapter {
    private static final String[] IMAGE_URLS = Constants.IMAGES;

    private ArrayList<MyContentData> myContentDataArrayList;

    private LayoutInflater inflater;

    private DisplayImageOptions options;

    public MyContentImageAdapter(Context context, ArrayList<MyContentData> myContentDataArrayList) {
        inflater = LayoutInflater.from(context);
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
    public int getCount() {
        return myContentDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.resource_lettercontent_listview , parent, false);
            holder = new ViewHolder();
            assert view != null;
            holder.myContentImg = (ImageView) view.findViewById(R.id.myContentImg);
            holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            holder.collectCountTxt = (TextView)view.findViewById(R.id.collectCountTxt);
            holder.unReadCountText = (TextView)view.findViewById(R.id.unReadCountText);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }



        ImageLoader.getInstance()
                .displayImage(Config.SERVER_ADDRESS + myContentDataArrayList.get(position).getContent() + ".jpg", holder.myContentImg, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.progressBar.setProgress(0);
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        holder.progressBar.setProgress(Math.round(100.0f * current / total));
                    }
                });

        holder.collectCountTxt.setText(String.valueOf(myContentDataArrayList.get(position).getCollectCount()));
        holder.unReadCountText.setText(String.valueOf(myContentDataArrayList.get(position).getUnReadCount()));
        return view;
    }


    class ViewHolder {
        ImageView myContentImg;
        ProgressBar progressBar;
        TextView collectCountTxt;
        TextView unReadCountText;
    }
}
