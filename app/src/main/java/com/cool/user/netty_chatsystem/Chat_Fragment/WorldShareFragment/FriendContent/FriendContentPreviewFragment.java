package com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.FriendContent;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.CollectFragment.CollectData;
import com.cool.user.netty_chatsystem.Chat_Fragment.UItraViewPager.UltraViewPager;
import com.cool.user.netty_chatsystem.Chat_Fragment.UItraViewPager.UltraViewPagerView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.BubbleEffect.BubbleView;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.Chat_AnimationElement.HeartEffect.BezierEvaluator;
import com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.ProfilePreviewFragment;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_MySQL.DBConnector;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_biz.entity.Message_entity;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.Chat_server.dto.MessageDTO;
import com.cool.user.netty_chatsystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by user on 2017/2/27.
 */
public class FriendContentPreviewFragment extends Fragment{
    String loginId;
    int position;
    int ScreenHeight;
    int ScreenWidth;
    ArrayList<CollectData> collectDataArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_friendcontentpreview , container, false);

        //螢幕的寬高
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        ScreenHeight = dm.heightPixels;
        ScreenWidth = dm.widthPixels;

        final Bundle bundle = getArguments();
        position = bundle.getInt("position");
        final ArrayList<FriendContentData> friendContentArrayList = bundle.getParcelableArrayList("friendContentArrayList");
        collectDataArrayList = loadCollectInSqlite();//載入收集資料

        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.logo_red)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
        loginId = sharePreferenceManager.getLoginId();

        UltraViewPagerView ultraViewPagerView = (UltraViewPagerView)rootView.findViewById(R.id.ultra_viewpager);
        ultraViewPagerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ultraViewPagerView.setScrollMode(UltraViewPager.ScrollMode.VERTICAL);
        FriendContentPreviewPagerViewAdapter adapter = new FriendContentPreviewPagerViewAdapter(loginId, ScreenWidth, ScreenHeight,
                                                                                                friendContentArrayList, getActivity(), ultraViewPagerView, collectDataArrayList);
        ultraViewPagerView.setAdapter(adapter);
        ultraViewPagerView.setCurrentItem(position);
        ultraViewPagerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                deleteFinishReadDataInRemoteMySql(friendContentArrayList.get(position).getContent());//看過即刪除
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //deleteFinishReadDataInRemoteMySql(friendContentArrayList.get(position).getContent());

        return rootView;
    }


    private void deleteFinishReadDataInRemoteMySql(String content){

        ArrayList<NameValuePair>params = new ArrayList<>();
        params.add(new BasicNameValuePair("readerid", loginId));
        params.add(new BasicNameValuePair("content", content));
        try{
            String result = DBConnector.executeQuery("",Config.FRIENDCONTENT_DELETE_URL, params);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //從sqlite載入收集資料
    private ArrayList<CollectData> loadCollectInSqlite(){
        //以下為從sqlite載回收集資料
        MyDBHelper dbHelper =  new  MyDBHelper(getActivity() , "Chat.db" , null , 1 );
        //得到一個可寫的數據庫
        SQLiteDatabase db =dbHelper.getReadableDatabase();

        ArrayList<CollectData> collectDataArrayList = new ArrayList<>();

        Cursor cursor = db.query("Collect" ,  new  String[]{"id", "content"},  "isdelete=?" , new String[]{"0"}, null, null, null );
        while (cursor.moveToNext()) {
            CollectData collectData = new CollectData();
            collectData.setCollectId(cursor.getString(cursor.getColumnIndex("id")));
            collectData.setCollectContent(cursor.getString(cursor.getColumnIndex("content")));
            collectDataArrayList.add(collectData);
        }

        cursor.close();
        db.close();

        return collectDataArrayList;
    }

}
