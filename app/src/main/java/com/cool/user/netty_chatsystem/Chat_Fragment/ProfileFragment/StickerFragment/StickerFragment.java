package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment.DynamicGrid.DynamicGridView;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.StickerFragment.PreviewFragment.PreviewFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Utils.BitmapUtils;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.fragment.WhiteBoardFragment;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/11/26.
 */
public class StickerFragment extends Fragment {
    private DynamicGridView gridView = null;
    private int stickerCount = 0;
    private int mode = 0;
    private ArrayList<StickerData>stickers = new ArrayList<>();
    private ArrayList<StickerData>selectStickers = new ArrayList<>();
    private CheeseDynamicAdapter cheeseDynamicAdapter;
    private String username, loginId;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.sticker_fragment, container, false);
        gridView = (DynamicGridView)rootView.findViewById(R.id.stickerGridView);
        ImageView goBackProfileImg = (ImageView)rootView.findViewById(R.id.goBackProfileImg);
        final TextView deleteStickerText = (TextView)rootView.findViewById(R.id.deleteStickerText);
        final TextView stickerTitleText = (TextView)rootView.findViewById(R.id.stickerTitleText);
        final LinearLayout deleteStickerLayout = (LinearLayout)rootView.findViewById(R.id.deleteStickerLayout);
        //以下為重要的浮動按鈕的初始化-------------------------------------------------------------------------------------------------------------------------------
        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu)rootView.findViewById(R.id.multiple_actions);
        FloatingActionButton addStickerFab = (FloatingActionButton)rootView.findViewById(R.id.addStickerFab);
        final FloatingActionButton deleteStickerFab = (FloatingActionButton)rootView.findViewById(R.id.deleteStickerFab);
        //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        // Session class instance
        // Session Manager Class
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity().getApplicationContext());

        // get user data from session
        HashMap<String, String> user = sharePreferenceManager.getUserDetails();

        // name
        username = user.get(SharePreferenceManager.KEY_NAME);
        //logindId
        loginId = sharePreferenceManager.getLoginId();

        stickers.clear();
        loadStickerInSqlite();
        cheeseDynamicAdapter = new CheeseDynamicAdapter(getActivity(), stickers, 3);

        if(!stickers.isEmpty()) {
            gridView.setAdapter(cheeseDynamicAdapter);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                PreviewFragment previewFragment = new PreviewFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("currentPosition",position);
                previewFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.allContainer, previewFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        gridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
                if(selectStickers.contains(stickers.get(position))){
                    stickerCount--;
                    if(stickerCount == 0){
                        deleteStickerLayout.setVisibility(View.GONE);
                    }else{
                        deleteStickerText.setText("刪除(" + stickerCount + ")");
                    }
                    selectStickers.remove(stickers.get(position));
                }else{
                    if(stickerCount == 0){
                        deleteStickerLayout.setVisibility(View.VISIBLE);
                    }
                    stickerCount++;
                    deleteStickerText.setText("刪除(" + stickerCount + ")");
                    selectStickers.add(stickers.get(position));
                }
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {

            }
        });



        goBackProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mode 0 : 正常摸式 1 : 刪除模式
                if (mode == 0) {
                    if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } else if (mode == 1) {
                    mode = 0;
                    stickerTitleText.setText("Sticker");
                    gridView.stopEditMode();
                    menuMultipleActions.setVisibility(View.VISIBLE);
                    deleteStickerLayout.setVisibility(View.GONE);
                    stickerCount = 0;
                    selectStickers.clear();
                }
            }
        });

        //選擇後會跑出來的刪除按鈕(以textview來表示)
        deleteStickerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStickerLayout.setVisibility(View.GONE);
                for(int i = 0 ; i < selectStickers.size(); i++){
                    stickers.remove(selectStickers.get(i));
                    cheeseDynamicAdapter.deleteImageItems(selectStickers.get(i));
                    cheeseDynamicAdapter.remove(selectStickers.get(i));
                    deleteStickerFile(selectStickers.get(i).getStickerPath());
                    deleteStickerInSqlite(selectStickers.get(i).getStickerPath());
                    new deleteStickerInRemoteMySql(selectStickers.get(i).getStickerPath()).execute();
                }
                stickerCount = 0;
                selectStickers.clear();
                cheeseDynamicAdapter.notifyDataSetChanged();
            }
        });

        //浮動按鈕中的填加新貼圖的按鈕
        //addStickerFab.setIcon(R.drawable.);
        addStickerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("whichFragment",3);
                WhiteBoardFragment whiteBoardFragment = new WhiteBoardFragment();
                whiteBoardFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.allContainer, whiteBoardFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //浮動按鈕中刪除貼圖的按鈕
        deleteStickerFab.setIcon(R.drawable.sticker_garbagecan);
        deleteStickerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.startEditMode();
                menuMultipleActions.setVisibility(View.GONE);
                mode = 1;
                stickerTitleText.setText("刪除");
                menuMultipleActions.toggle();
            }
        });



        return rootView;
    }


    //載入在sqlite裡貼圖資料
    private void loadStickerInSqlite(){
        //以下為從sqlite載回聊天紀錄
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Sticker", new String[]{"content"}, "isdelete=?", new String[]{"0"}, null, null, null);
        while (cursor.moveToNext()){
            HashMap<String, Object> map = new HashMap<String, Object>();
            stickers.add(new StickerData(cursor.getString(cursor.getColumnIndex("content"))));
        }

        //關閉數據庫
        db.close();
        cursor.close();
    }


    //刪除全部在sqlite裡sticker資料
    private void deleteStickerInSqlite(String content){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("isdelete",1);
        db.update("Sticker", cv, "content=?", new String[]{content});
        db.close();
    }

    //刪除sticker的檔案
    private void deleteStickerFile(String fileName){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
        File file = new File (mediaStorageDir.getPath() + File.separator + fileName + ".jpg");
        Boolean isDelete = file.delete();
    }


    //存入改變後的sticker陣列到sqlite裡
    private void saveStickerInSqlite(){
        //創建MyDBHelper對象
        MyDBHelper dbHelper = new MyDBHelper(getActivity() , "Chat.db", null, 1);
        //得到一個可讀的SQLiteDatabase對象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        for(int i = 0 ; i < stickers.size() ; i++) {
            //生成ContentValues​​對象 //key:列名，value:想插入的值
            ContentValues cv = new ContentValues();
            //往ContentValues​​對象存放數據，鍵-值對模式
            cv.put("id",i);
            cv.put("sticker", stickers.get(i).getStickerPath());


            //調用insert方法，將數據插入數據庫
            db.insert("Sticker", null, cv);
        }
        //關閉數據庫
        db.close();
    }



    //從sd卡裡獲取圖片
    public Bitmap getSDCardPhoto(String path) {
        File file = new File(path);
        if (file.exists()) {
            return BitmapUtils.decodeSampleBitMapFromFile(getActivity(), path, 0.5f);
        } else {
            return null;
        }
    }

    //刪除遠端的sticker data
    class deleteStickerInRemoteMySql extends AsyncTask<String, String, Void>
    {

        String content;
        InputStream is = null ;
        public deleteStickerInRemoteMySql(String content){
            this.content = content;
        }
        protected void onPreExecute()
        {
        }
        @Override
        protected Void doInBackground(String... params)
        {
            try
            {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("ownerid", loginId));
                nameValuePairs.add(new BasicNameValuePair("content", content));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Config.STICKER_DELETE_URL);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            }
            catch(Exception e){
                Log.e("log_tag", "Error in http connection" + e.toString());
            }

            return null;
        }
        protected void postExecute(Void v)
        {
            Toast.makeText(getActivity(), "Updation Successfull", Toast.LENGTH_LONG).show();

        }
    }

}
