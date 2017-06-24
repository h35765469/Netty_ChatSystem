package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment.PreviewFragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/12/7.
 */
public class PreviewFragment extends Fragment{

    // Declare Variables
    ViewPager viewPager;
    PagerAdapter adapter;
    ImageView backImg;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.preview_fragment, container, false);
        ImageView backImg = (ImageView)rootView.findViewById(R.id.backImg);

        int currentPosition = getArguments().getInt("currentPosition");
        ArrayList<String>stickerArrayList = loadStickerInSqlite();

        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager)rootView.findViewById(R.id.previewPager);
        // Pass results to ViewPagerAdapter Class
        adapter = new PreviewPagerAdapter(getActivity(), stickerArrayList);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(currentPosition);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return rootView;
    }


    private ArrayList<String> loadStickerInSqlite(){
        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Sticker", new String[]{"id", "content"}, "id", null, null, null, null);
        ArrayList<String>stickers = new ArrayList<>();
        while (cursor.moveToNext()){
            HashMap<String, Object> map = new HashMap<String, Object>();
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
            String sticker = cursor.getString(cursor.getColumnIndex("content"));
            stickers.add(sticker);
        }

        //關閉數據庫
        db.close();
        cursor.close();

        return stickers;
    }
}
