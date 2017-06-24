package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.PersonalSettingFragment.ChangeNotificationFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_Service.NotificationData;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.R;

/**
 * Created by user on 2017/1/8.
 */
public class ChangeNotificationAdapter extends BaseAdapter {
    private Context context;
    private String username;
    private LayoutInflater layoutInflater;
        private String[] notificationArray = {"通知", "聲音", "震動", "閃光"};

    private ViewHolder viewHolder;

    private class ViewHolder{
        TextView notificationNameTxv;
        ImageView changeNotificationImg;
    }

    public ChangeNotificationAdapter(Context context){
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return notificationArray.length;
    }

    @Override
    public Object getItem(int position){
        return notificationArray[position];
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView != null){
            viewHolder = (ViewHolder)convertView.getTag();
        }else{
            convertView = layoutInflater.inflate(R.layout.resource_changenotification_listview,null);
            viewHolder = new ViewHolder();
            viewHolder.notificationNameTxv = (TextView)convertView.findViewById(R.id.notificationNameTxv);
            viewHolder.changeNotificationImg = (ImageView)convertView.findViewById(R.id.changeNotificationImg);
            convertView.setTag(viewHolder);
        }

        final SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(context);
        final NotificationData notificationData = sharePreferenceManager.loadNotification();
        System.out.println("fucknotification " + notificationData.getNotification() + "," + notificationData.getSound() + "," + notificationData.getVibrate() + "," + notificationData.getLed());
        viewHolder.notificationNameTxv.setText(notificationArray[position]);
        switch(position){
            case 0 :{
                if(notificationData.getNotification() == 1){
                    viewHolder.changeNotificationImg.setImageResource(R.drawable.alarm_blue);
                }else{
                    viewHolder.changeNotificationImg.setImageResource(R.drawable.alarm);
                }
                break;
            }
            case 1 :{
                if(notificationData.getSound() == 1){
                    viewHolder.changeNotificationImg.setImageResource(R.drawable.alarm_blue);
                }
                else{
                    viewHolder.changeNotificationImg.setImageResource(R.drawable.alarm);
                }
                break;
            }
            case 2 :{
                if(notificationData.getVibrate() == 1){
                    viewHolder.changeNotificationImg.setImageResource(R.drawable.alarm_blue);
                }
                else{
                    viewHolder.changeNotificationImg.setImageResource(R.drawable.alarm);
                }
                break;
            }
            case 3 :{
                if(notificationData.getLed() == 1){
                    viewHolder.changeNotificationImg.setImageResource(R.drawable.alarm_blue);
                }
                else{
                    viewHolder.changeNotificationImg.setImageResource(R.drawable.alarm);
                }
                break;
            }
        }

         /*notificationType
        0 : 通知
        1 : 聲音
        2 : 震動
        3 : led
        condition
        0 : 關閉
        1 : 開啟
    */
        viewHolder.changeNotificationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0: {
                        if (notificationData.getNotification() == 1) {
                            sharePreferenceManager.updateNotification(0,0);
                        } else {
                            sharePreferenceManager.updateNotification(0,1);
                        }
                        break;
                    }
                    case 1: {
                        if (notificationData.getSound() == 1) {
                            sharePreferenceManager.updateNotification(1,0);
                        } else {
                            sharePreferenceManager.updateNotification(1,1);
                        }
                        break;
                    }
                    case 2: {
                        if (notificationData.getVibrate() == 1) {
                            sharePreferenceManager.updateNotification(2,0);
                        } else {
                            sharePreferenceManager.updateNotification(2,1);
                        }
                        break;
                    }
                    case 3: {
                        if (notificationData.getLed() == 1) {
                            sharePreferenceManager.updateNotification(3,0);
                        } else {
                            sharePreferenceManager.updateNotification(3,1);
                        }
                        break;
                    }
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
