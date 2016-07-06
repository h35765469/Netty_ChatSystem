package com.example.user.netty_chatsystem.Chat_Listview_Friendlist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.netty_chatsystem.Chat_Activity;
import com.example.user.netty_chatsystem.Friendlist_Activity;
import com.example.user.netty_chatsystem.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by user on 2016/3/3.
 */
public class CustomBaseAdapter extends BaseAdapter {
    Context context;
    List<RowItem>rowItems;
    ViewHolder holder = null;


    public CustomBaseAdapter(Context context, List<RowItem> items) {
        this.context = context;
        this.rowItems = items;
    }

    private class ViewHolder{
        de.hdodenhof.circleimageview.CircleImageView imageView;
        TextView txtTitle ;
    }

    public View getView(int position , View convertView , ViewGroup parent){

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.friend_profile,null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView)convertView.findViewById(R.id.title_text);
            holder.imageView = (de.hdodenhof.circleimageview.CircleImageView)convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        RowItem rowItem = (RowItem)getItem(position);

        holder.txtTitle.setText(rowItem.getTitle());
        holder.imageView.setImageResource(rowItem.getImageId());


        return convertView;
    }



    @Override
    public int getCount(){
        return rowItems.size();
    }

    @Override
    public Object getItem(int position){
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position){
        return rowItems.indexOf(getItem(position));
    }
}
