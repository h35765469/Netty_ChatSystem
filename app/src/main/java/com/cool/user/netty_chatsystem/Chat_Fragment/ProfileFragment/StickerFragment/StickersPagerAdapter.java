package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cool.user.netty_chatsystem.Chat_Fragment.ChatFragment.Constants;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/27.
 */
public class StickersPagerAdapter extends PagerAdapter{
    ArrayList<Bitmap> stickers;
    ArrayList<String>stickerString;
    private static final int NUMBER_OF_STICKERS_PER_PAGE = 5;
    Activity activity;
    StickersGridAdapter.KeyClickListener keyClickListener;

    public StickersPagerAdapter(Activity activity, ArrayList<Bitmap>stickers, StickersGridAdapter.KeyClickListener keyClickListener, ArrayList<String>stickerString){
        this.stickers = stickers;
        this.activity = activity;
        this.keyClickListener = keyClickListener;
        this.stickerString = stickerString;
    }

    @Override
    public int getCount(){
        return (int)Math.ceil((double)stickers.size()/(double)NUMBER_OF_STICKERS_PER_PAGE);
    }

    @Override
    public Object  instantiateItem(View collection, int position){

        View layout = activity.getLayoutInflater().inflate(R.layout.resource_stickers_grid,null);

        int initialPosition = position * NUMBER_OF_STICKERS_PER_PAGE;
        final ArrayList<Bitmap> stickersInPage = new ArrayList<>();

        for(int i = initialPosition; i < initialPosition + NUMBER_OF_STICKERS_PER_PAGE && i < stickers.size(); i++){
            stickersInPage.add(stickers.get(i));
        }

        GridView grid = (GridView)layout.findViewById(R.id.stickers_grid);
        StickersGridAdapter adapter = new StickersGridAdapter(activity.getApplicationContext(), stickersInPage, position, keyClickListener);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity));
        grid.setAdapter(new ImageAdapter(activity.getApplicationContext(), stickerString));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                keyClickListener.keyClickedIndex(stickersInPage.get(position));
            }
        });

        ((ViewPager)collection).addView(layout);

        return layout;
    }

    @Override
    public void destroyItem(View collection, int position, Object view){
        ((ViewPager)collection).removeView((View)view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return view == object;
    }

    private static class ImageAdapter extends BaseAdapter {

        private static final String[] IMAGE_URLS = Constants.IMAGES;

        private ArrayList<String>stickerString;

        private LayoutInflater inflater;

        private DisplayImageOptions options;

        ImageAdapter(Context context, ArrayList<String>stickerString) {
            inflater = LayoutInflater.from(context);
            this.stickerString = stickerString;

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_background)
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
            return stickerString.size();
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
                view = inflater.inflate(R.layout.resource_stickers_item , parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.item);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            ImageLoader.getInstance()
                    .displayImage("file://" + stickerString.get(position), holder.imageView, options, new SimpleImageLoadingListener() {
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

            return view;
        }
    }

    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }
}
