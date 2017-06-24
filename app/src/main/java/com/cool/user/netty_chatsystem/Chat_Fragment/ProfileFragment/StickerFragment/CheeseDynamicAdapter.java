package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment;

/**
 * Author: alex askerov
 * Date: 9/9/13
 * Time: 10:52 PM
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment.DynamicGrid.BaseDynamicGridAdapter;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.List;

/**
 * Author: alex askerov
 * Date: 9/7/13
 * Time: 10:56 PM
 */
public class CheeseDynamicAdapter extends BaseDynamicGridAdapter {
    List<StickerData>imageItems;
    DisplayImageOptions options;

    public CheeseDynamicAdapter(Context context, List<StickerData> imageItems, int columnCount) {
        super(context, imageItems, columnCount);
        this.imageItems = imageItems;
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
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
    public View getView(int position, View convertView, ViewGroup parent) {
        CheeseViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.resource_stickers_gridview, null);
            holder = new CheeseViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CheeseViewHolder) convertView.getTag();
        }
        holder.build(imageItems.get(position).getStickerPath());

        return convertView;
    }

    private class CheeseViewHolder {
        private ImageView image;

        private CheeseViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.item_img);
        }

        void build(String content) {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
            ImageLoader.getInstance()
                    .displayImage("file://" + mediaStorageDir.getPath() + File.separator + content + ".jpg", image, options, new SimpleImageLoadingListener() {
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
        }
    }

    public void deleteImageItems(StickerData stickerData){
        imageItems.remove(stickerData);
    }
}