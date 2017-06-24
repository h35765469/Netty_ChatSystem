package com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.MyLetterContentFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.R;

import java.util.ArrayList;

/**
 * Created by user on 2016/12/23.
 */
public class LetterContentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Bitmap> letterContentArrayList;

    public LetterContentAdapter(Context context , ArrayList<Bitmap>letterContentArrayList){
        this.context = context;
        this.letterContentArrayList = letterContentArrayList;
    }

    @Override
    public View getView(int position , View convertView , ViewGroup parent){
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.resource_lettercontent_listview,null);
            holder = new ViewHolder();

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        //holder.ownerNameText = (TextView)convertView.findViewById(R.id.ownerNameText);
        holder.letterContentImg = (ImageView)convertView.findViewById(R.id.friendContentImg);
        holder.letterContentImg.setImageBitmap((Bitmap)getItem(position));

        return convertView;
    }

    private class ViewHolder{
        ImageView letterContentImg;
        //TextView ownerNameText;
    }

    @Override
    public int getCount(){
        return letterContentArrayList.size();
    }

    @Override
    public Object getItem(int position){
        return letterContentArrayList.get(position);
    }

    @Override
    public long getItemId(int position){
        return letterContentArrayList.indexOf(getItem(position));
    }


}
