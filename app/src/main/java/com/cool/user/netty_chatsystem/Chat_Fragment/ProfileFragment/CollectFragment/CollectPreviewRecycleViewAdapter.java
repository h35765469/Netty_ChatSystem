package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.R;

import java.util.ArrayList;

/**
 * Created by user on 2017/6/24.
 */

public class CollectPreviewRecycleViewAdapter extends RecyclerView.Adapter<CollectPreviewRecycleViewAdapter.ViewHolder> {
    int screenWidth;
    int screenHeight;

    public CollectPreviewRecycleViewAdapter(){

    }

    public CollectPreviewRecycleViewAdapter(int screenWidth, int screenHeight, String[] collectUrlArray, ArrayList<CollectData> collectDataArrayList, ArrayList<String> friendArrayList){

    }

    @Override
    public CollectPreviewRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resource_collectpreview_pagerview, viewGroup, false);
        return new CollectPreviewRecycleViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

    }


    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout collectPreviewRelativeLayout;
        ImageView showCollectEffectImg, collectEffectImg;
        ImageView collectPreViewImg;
        ImageView deleteCollectImg;
        ImageView collectThinkImg;
        ImageView collectAddFriendImg;
        ImageView collectProfileImg;
        TextView collectNameText;
        TextView backTxt;

        public ViewHolder(View view) {
            super(view);
            collectPreviewRelativeLayout = (RelativeLayout)view.findViewById(R.id.collectPreviewRelativeLayout);
            collectPreViewImg = (ImageView)view.findViewById(R.id.collectPreViewImg);
            backTxt = (TextView)view.findViewById(R.id.backTxt);
            deleteCollectImg = (ImageView)view.findViewById(R.id.deleteCollectImg);
            collectThinkImg = (ImageView)view.findViewById(R.id.collectThinkImg);
            collectEffectImg = (ImageView)view.findViewById(R.id.collectEffectImg);
            collectAddFriendImg = (ImageView)view.findViewById(R.id.collectAddFriendImg);
            showCollectEffectImg = (ImageView)view.findViewById(R.id.showCollectEffectImg);
            collectProfileImg = (ImageView)view.findViewById(R.id.collectProfileImg);
            collectNameText = (TextView)view.findViewById(R.id.randomNameText);
        }

    }
}
