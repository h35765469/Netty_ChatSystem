package com.example.user.netty_chatsystem.Chat_Listview_Friendlist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.user.netty_chatsystem.R;

import java.util.List;

/**
 * Created by user on 2016/3/9.
 */
public class CustomBaseAdapter_horizontal extends BaseAdapter {
    Context context;
    List<RowItem> rowItems;

    public CustomBaseAdapter_horizontal(Context context, List<RowItem> items) {
        this.context = context;
        this.rowItems = items;
    }

    private class ViewHolder{
        de.hdodenhof.circleimageview.CircleImageView imageView;
    }

    public View getView(int position , View convertView , ViewGroup parent){
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.resource_friendlist_listview_horizontal,null);
            holder = new ViewHolder();
            holder.imageView = (de.hdodenhof.circleimageview.CircleImageView)convertView.findViewById(R.id.profile_image_horizontal);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        RowItem rowItem = (RowItem)getItem(position);

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
