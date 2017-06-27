package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2017/6/22.
 */

public class ProfileRecycleViewAdapter extends RecyclerView.Adapter<ProfileRecycleViewAdapter.PersonViewHolder> {
    String[] titleNames = {"我的驚喜", "我的收藏", "認證中", "新朋友", "設定"};
    String[] relativeColors = {"#FF0088", "#FF8800", "#7700FF","#FF3333","#888888" };
    int[] titleIcons = {R.drawable.profile_surprise, R.drawable.agenda, R.drawable.profile_certification, R.drawable.logo, R.drawable.profile_setting};
    private static ClickListener clickListener;;

    public class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView profileItemNameTxt;
        ImageView profileItemIconImg;
        RelativeLayout profileItemRelativeLayout;

        PersonViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cv = (CardView)itemView.findViewById(R.id.cv);
            profileItemNameTxt = (TextView)itemView.findViewById(R.id.profileItemNameTxt);
            profileItemIconImg = (ImageView)itemView.findViewById(R.id.profileItemIconImg);
            profileItemRelativeLayout = (RelativeLayout)itemView.findViewById(R.id.profileItemRelativeLayout);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    @Override
    public int getItemCount() {
        return titleNames.length;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resource_profile_recycleview, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int position) {
        personViewHolder.profileItemNameTxt.setText(titleNames[position]);
        personViewHolder.profileItemRelativeLayout.setBackgroundColor(Color.parseColor(relativeColors[position]));
        personViewHolder.profileItemIconImg.setImageResource(titleIcons[position]);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        ProfileRecycleViewAdapter.clickListener = clickListener;
    }
}
