package com.cool.user.netty_chatsystem.ChatListViewAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
 * Created by user on 2016/3/3.
 */
public class FriendListAdapter extends BaseAdapter {
    Context context;
    List<FriendRowItem>friendRowItems;
    ViewHolder holder = null;
    final DisplayImageOptions options;
    Typeface font;


    public FriendListAdapter(Context context, List<FriendRowItem> friendRowItems) {
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
        font= Typeface.createFromAsset(context.getAssets(),"fonts/fontawesome-webfont.ttf");
    }

    private class ViewHolder{
        de.hdodenhof.circleimageview.CircleImageView imageView;
        TextView txtTitle ;
        TextView infoText;
        TextView favorite ;
    }

    public View getView(int position , View convertView , ViewGroup parent){

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.friend_profile,null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView)convertView.findViewById(R.id.title_text);
            holder.imageView = (de.hdodenhof.circleimageview.CircleImageView)convertView.findViewById(R.id.profileImg);
            holder.infoText = (TextView)convertView.findViewById(R.id.infoText);
            holder.favorite = (TextView)convertView.findViewById(R.id.favorite);
            holder.favorite.setTypeface(font);
            holder.favorite.setText("\uf08a ");


            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        FriendRowItem friendRowItem = (FriendRowItem)getItem(position);


        holder.txtTitle.setText(friendRowItem.getFriendName());
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory.getAbsolutePath(), friendRowItem.getAvatarName());
        if(friendRowItem.getAvatarName().length() > 0) {
            //Picasso.with(context).load(new File(mediaStorageDir.getPath() + File.separator + friendRowItem.getAvatarName() + ".jpg")).into(holder.imageView);
            ImageLoader.getInstance()
                    .displayImage("File://" + file.getAbsolutePath(), holder.imageView, options, new SimpleImageLoadingListener() {
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
