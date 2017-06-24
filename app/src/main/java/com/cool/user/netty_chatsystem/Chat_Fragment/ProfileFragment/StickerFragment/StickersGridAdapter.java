package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cool.user.netty_chatsystem.R;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/27.
 */
public class StickersGridAdapter extends BaseAdapter {

    private ArrayList<Bitmap>stickers;
    private int pageNumber;
    Context context;
    KeyClickListener keyClickListener;

    public interface KeyClickListener{
        public void keyClickedIndex(Bitmap index);
    }

    public StickersGridAdapter(Context context, ArrayList<Bitmap>stickers, int pageNumber, KeyClickListener keyClickListener){
        this.context = context;
        this.stickers = stickers;
        this.pageNumber = pageNumber;
        this.keyClickListener = keyClickListener;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.resource_stickers_item, null);
        }

        final Bitmap bitmap = stickers.get(position);

        ImageView img = (ImageView)v.findViewById(R.id.item);
        img.setImageBitmap(bitmap);

        return v;
    }

    @Override
    public int getCount(){
        return stickers.size();
    }

    @Override
    public Bitmap getItem(int position){
        return stickers.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


}
