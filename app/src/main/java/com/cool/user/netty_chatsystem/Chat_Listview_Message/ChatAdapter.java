package com.cool.user.netty_chatsystem.Chat_Listview_Message;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_Fragment.ChatFragment.ChatFragment;
import com.cool.user.netty_chatsystem.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

/**
 * Created by user on 2016/2/24.
 */
public class ChatAdapter extends BaseAdapter {

    private final List<ChatMessage> chatMessages;
    private Activity context;

    public ChatAdapter(Activity context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ChatMessage chatMessage = getItem(position);

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.list_item_chat_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean myMsg = chatMessage.getIsme() ;//Just a dummy check
        //to simulate whether it me or other sender
        setAlignment(holder, myMsg, chatMessage, position);

        /*if(chatMessage.getMessage() != null) {
            holder.txtMessage.setText(chatMessage.getMessage());
        }

        if(chatMessage.getFilePath() !=null) {
            if(chatMessage.getIsEffect()){
                Picasso.with(context).load(R.drawable.gift).into(holder.picMessage);
            }else{
                Picasso.with(context).load(new File(chatMessage.getFilePath())).into(holder.picMessage);
            }
        }else{
            holder.picMessage.setImageBitmap(null);
        }

        if(position == getCount() - 1) {
            if(chatMessage.getIsme()) {
                if (chatMessage.getIsRead() == 1) {
                    holder.txtInfo.setText("已讀");
                } else {
                    holder.txtInfo.setText("");
                }
            }else{
                holder.txtInfo.setText("");
            }
        }else{
            holder.txtInfo.setText("");
        }*/


        return convertView;
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe, ChatMessage chatMessage, int position) {
        if (!isMe) {
            //holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);


            /*LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);*/

            /*layoutParams = (LinearLayout.LayoutParams)holder.nameText.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.nameText.setLayoutParams(layoutParams);
            holder.nameText.setTextColor(Color.BLUE);
            holder.nameText.setText("朋友");*/

            /*layoutParams = (LinearLayout.LayoutParams) holder.picMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.picMessage.setLayoutParams(layoutParams);*/
            holder.myself.setVisibility(View.GONE);
            holder.other.setVisibility(View.VISIBLE);
            holder.friendChatTxt.setVisibility(View.GONE);
            holder.friendChatImg.setVisibility(View.GONE);
            if(chatMessage.getFilePath() !=null) {
                holder.friendChatImg.setVisibility(View.VISIBLE);
                if(chatMessage.getIsEffect()){
                    Picasso.with(context).load(R.drawable.gift).into(holder.friendChatImg);
                }else{
                    Picasso.with(context).load(new File(chatMessage.getFilePath())).into(holder.friendChatImg);
                }
            }else {
                holder.friendChatTxt.setVisibility(View.VISIBLE);
                holder.friendChatTxt.setText(chatMessage.getMessage());
                holder.otherChatTxt.setText(chatMessage.getMessage());
            }

        } else {
            //holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            /*LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);*/

            /*layoutParams = (LinearLayout.LayoutParams)holder.nameText.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.nameText.setLayoutParams(layoutParams);
            holder.nameText.setTextColor(Color.RED);
            holder.nameText.setText("我");*/

            /*layoutParams = (LinearLayout.LayoutParams) holder.picMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.picMessage.setLayoutParams(layoutParams);*/
            holder.other.setVisibility(View.GONE);
            holder.myself.setVisibility(View.VISIBLE);
            holder.myChatTxt.setVisibility(View.GONE);
            holder.myChatImg.setVisibility(View.GONE);
            if(chatMessage.getFilePath() !=null) {
                holder.myChatImg.setVisibility(View.VISIBLE);
                if(chatMessage.getIsEffect()){
                    holder.myChatImg.setMaxHeight(40);
                    holder.myChatImg.setMaxWidth(40);
                    Picasso.with(context).load(R.drawable.gift).into(holder.myChatImg);
                }else{
                    Picasso.with(context).load(new File(chatMessage.getFilePath())).into(holder.myChatImg);
                }
            }else {
                holder.myChatTxt.setVisibility(View.VISIBLE);
                holder.myChatTxt.setText(chatMessage.getMessage());

            }

            if(position == getCount() - 1) {
                if (chatMessage.getIsRead() == 1) {
                    holder.alreadyReadTxt.setVisibility(View.VISIBLE);
                } else {
                    holder.alreadyReadTxt.setVisibility(View.GONE);
                }
            }else{
                holder.alreadyReadTxt.setVisibility(View.GONE);
            }

        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        //holder.nameText = (TextView)v.findViewById(R.id.nameText);
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        holder.picMessage = (ImageView)v.findViewById(R.id.picMessage);

        holder.myself = (RelativeLayout)v.findViewById(R.id.myself);
        holder.other = (RelativeLayout)v.findViewById(R.id.other);
        holder.myselfChatTxt = (TextView)v.findViewById(R.id.myselfChatTxt);
        holder.otherChatTxt = (TextView)v.findViewById(R.id.otherChatTxt);

        holder.myChatTxt = (TextView)v.findViewById(R.id.myChatTxt);
        holder.friendChatTxt = (TextView)v.findViewById(R.id.friendChatTxt);
        holder.alreadyReadTxt = (TextView)v.findViewById(R.id.alreadyReadTxt);
        holder.myChatImg = (ImageView)v.findViewById(R.id.myChatImg);
        holder.friendChatImg = (ImageView)v.findViewById(R.id.friendChatImg);
        return holder;
    }

    private static class ViewHolder {
        //public TextView nameText;
        public TextView txtMessage;
        public TextView txtInfo;
        public ImageView picMessage;
        public LinearLayout content;
        public LinearLayout contentWithBG;

        public RelativeLayout myself;
        public RelativeLayout other;
        public TextView myselfChatTxt;
        public TextView otherChatTxt;

        public TextView myChatTxt;
        public TextView friendChatTxt;
        public TextView alreadyReadTxt;
        public ImageView myChatImg;
        public ImageView friendChatImg;

    }
}
