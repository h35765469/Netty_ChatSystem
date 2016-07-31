package com.example.user.netty_chatsystem.Chat_Listview_Friendlist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.netty_chatsystem.R;

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

    public void addMessage(RowItem rowItem){
        rowItems.add(0,rowItem);
    }

    public boolean inMessage(RowItem rowItem){
       return rowItems.contains(rowItem);
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
