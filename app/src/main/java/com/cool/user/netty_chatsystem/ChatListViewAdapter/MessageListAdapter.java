package com.cool.user.netty_chatsystem.ChatListViewAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by user on 2017/3/27.
 */
public class MessageListAdapter extends BaseAdapter{
    Context context;
    List<FriendRowItem> friendRowItems;
    ViewHolder holder = null;
    final DisplayImageOptions options;


    public MessageListAdapter(Context context, List<FriendRowItem> friendRowItems) {
        this.context = context;
        this.friendRowItems = friendRowItems;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.logo_red)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtTitle ;
        TextView infoText;
    }

    public View getView(int position , View convertView , ViewGroup parent){

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.resource_messagelist_profile,null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView)convertView.findViewById(R.id.title_text);
            holder.imageView = (ImageView)convertView.findViewById(R.id.profileImg);
            holder.infoText = (TextView)convertView.findViewById(R.id.infoText);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        FriendRowItem friendRowItem = (FriendRowItem)getItem(position);


        holder.txtTitle.setText(friendRowItem.getFriendName());
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
        if(friendRowItem.getAvatarName() != null) {
            //Picasso.with(context).load(new File(mediaStorageDir.getPath() + File.separator + friendRowItem.getAvatarName() + ".jpg")).into(holder.imageView);
            ImageLoader.getInstance()
                    .displayImage("File://" + mediaStorageDir.getPath() + File.separator + friendRowItem.getAvatarName() + ".jpg", holder.imageView, options, new SimpleImageLoadingListener() {
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

        }else if(friendRowItem.getFromMessagelist()){
            holder.imageView.setImageBitmap(friendRowItem.getAvatar());
        }
        else{
            Picasso.with(context).load(R.drawable.logo_red).into(holder.imageView);
        }

        holder.infoText.setText(friendRowItem.getContent());


        return convertView;
    }

    public void addMessage(FriendRowItem friendRowItem){
        friendRowItems.add(0, friendRowItem);
    }

    public void removeMessage(FriendRowItem friendRowItem){
        friendRowItems.remove(friendRowItem);
        addMessage(friendRowItem);
    }

    public boolean inMessage(FriendRowItem friendRowItem){
        for(FriendRowItem ri : friendRowItems) {
            if(ri.getFriendId() != null && ri.getFriendId().equals(friendRowItem.getFriendId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCount(){
        return friendRowItems.size();
    }

    @Override
    public FriendRowItem getItem(int position){
        return friendRowItems.get(position);
    }

    @Override
    public long getItemId(int position){
        return friendRowItems.indexOf(getItem(position));
    }

    public void addItem(FriendRowItem friendRowItem){
        friendRowItems.add(friendRowItem);
    }

    public void removeItem(FriendRowItem friendRowItem){
        friendRowItems.remove(friendRowItem);
    }

    public void removePosition(int position){
        friendRowItems.remove(position);
    }

    public int getItemIndex(FriendRowItem friendRowItem){
        return friendRowItems.indexOf(friendRowItem);
    }
}
