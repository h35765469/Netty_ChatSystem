package com.cool.user.netty_chatsystem.Chat_Fragment.SendContentFragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.R;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by user on 2016/12/4.
 */
public class SendCustomAdapter extends BaseAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private ArrayList<SendData> mData = new ArrayList<SendData>();
    private String[] selectArray;
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater layoutInflater;
    ViewHolder viewHolder;
    private Context context;

    public SendCustomAdapter(Context context){
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;


    }

    public void addItem(final SendData item){
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addHeaderItem(final SendData item){
        mData.add(item);
        sectionHeader.add(mData.size()-1);
        notifyDataSetChanged();
    }

    public void setSelectArrayLength(int arrayLength){
        selectArray = new String[arrayLength];
    }

    public ArrayList<SendData>getmData(){
        return mData;
    }

    @Override
    public int getItemViewType(int position){
        return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public SendData getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        viewHolder = null;
        int rowType = getItemViewType(position);

        if(convertView == null){
            viewHolder = new ViewHolder();
            switch(rowType){
                case TYPE_ITEM: {
                    convertView = layoutInflater.inflate(R.layout.resource_sendcontent_item_listview, null);
                    viewHolder.nameText = (TextView) convertView.findViewById(R.id.nameText);
                    viewHolder.buttonImg = (ImageView) convertView.findViewById(R.id.buttonImg);
                    break;
                }
                case TYPE_HEADER: {
                    convertView = layoutInflater.inflate(R.layout.resource_sendcontent_header_listview, null);
                    viewHolder.nameText = (TextView) convertView.findViewById(R.id.headerText);
                    break;
                }
            }
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.nameText.setText(mData.get(position).getName());
        if(position != 0){
            if(position != 3){
                if(position == 1){
                    viewHolder.buttonImg.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), R.drawable.circle, 100, 100));
                }else if(position == 2){
                    viewHolder.buttonImg.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), R.drawable.logo, 100, 100));
                }/*else if(position == 3){
                    viewHolder.buttonImg.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), R.drawable.smile_gray, 100, 100));
                }*/
                else{
                    viewHolder.buttonImg.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), R.drawable.circle_gray, 100, 100));
                }
            }
        }

        return convertView;
    }

    class ViewHolder{
        public TextView nameText;
        public ImageView buttonImg;
    }

    @Override
    public int getCount(){
        return mData.size();
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth){

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;


            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }

        }
        return  inSampleSize;
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        //Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
