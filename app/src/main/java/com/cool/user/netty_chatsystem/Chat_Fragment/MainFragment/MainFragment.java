package com.cool.user.netty_chatsystem.Chat_Fragment.MainFragment;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.CameraFragment.OpenCameraFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.EnterFragment.LoginFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.FriendListFragment.RootFriendFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.LockableViewPager;
import com.cool.user.netty_chatsystem.Chat_Fragment.MainFragment.ntb.NavigationTabBar;
import com.cool.user.netty_chatsystem.Chat_Fragment.MessageListFragment.RootMessageListFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.RootProfileFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.SearchFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ViewPagerAdapter;
import com.cool.user.netty_chatsystem.Chat_Fragment.WhiteBoardFragment.fragment.WhiteBoardFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.WorldShareFragment.RootShareContentFragment;
import com.cool.user.netty_chatsystem.Chat_Instruction.MyIntro;
import com.cool.user.netty_chatsystem.Chat_Service.ChatService;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2016/11/10.
 */
public class MainFragment extends Fragment {
    public static LockableViewPager fragmentPager;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private LoginFragment loginFragment = new LoginFragment();


    //**以下相關函式皆為通知MessageListFragment更改介面元件使用
    public static ChangeMessageListListener mChangeMessageListListener;

    public interface ChangeMessageListListener{
        public void onChangeMessageListEvent(String from , String content);
    }
    //****************************************************************


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, final Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_template, container, false);
        fragmentPager = (LockableViewPager)rootView.findViewById(R.id.fragmentPager);

        // Session class instance
        // Session Manager Class
        final SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());

        // get user data from session
        HashMap<String, String> user = sharePreferenceManager.getUserDetails();

        // name
        final String username = user.get(SharePreferenceManager.KEY_NAME);


        // password
        final String password = user.get(SharePreferenceManager.KEY_EMAIL);

        //nickname
        final String nickName = sharePreferenceManager.getNickName();


        final int mainFragmentIndex = sharePreferenceManager.getMainFragmentIndex();


        final Bundle bundle = getArguments();//從SharePerferenceManager來的為了點擊上方提醒欄後可以直接開啟聊天室

        openConnection(username, password, nickName);

        /*final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar)rootView.findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.logo),
                        Color.parseColor(colors[0]))
                        .title("Heart")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.questionmark),
                        Color.parseColor(colors[0]))
                        .title("Cup")
                        .build()
        );

        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.friendlist_icon),
                        Color.parseColor(colors[0]))
                        .title("Diploma")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.rsz_chaticon_white),
                        Color.parseColor(colors[0]))
                        .title("Flag")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setModelIndex(mainFragmentIndex);

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.setBehaviorEnabled(false);*/


        if(bundle != null){//此bundle為從sharePreferenceManager而來要進行點擊notification後可進入頁面
            if(bundle.getInt("loginFirst") == 1){
                mFragmentList.add(new RootProfileFragment());
                mFragmentList.add(new RootShareContentFragment());
                mFragmentList.add(new RootFriendFragment());
                mFragmentList.add(new RootMessageListFragment());
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mFragmentList);
                fragmentPager.setAdapter(viewPagerAdapter);
                fragmentPager.setCurrentItem(1);

            }else{
                RootMessageListFragment rootMessageListFragment = new RootMessageListFragment();
                RootProfileFragment rootProfileFragment = new RootProfileFragment();
                if(bundle.getInt("notificationType") == 0) {
                    rootMessageListFragment.setArguments(bundle);
                }else if(bundle.getInt("notificationType") ==1){
                    rootProfileFragment.setArguments(bundle);
                }
                mFragmentList.add(rootProfileFragment);
                mFragmentList.add(new RootShareContentFragment());
                mFragmentList.add(new RootFriendFragment());
                mFragmentList.add(rootMessageListFragment);
                if(bundle.getInt("notificationType") == 0) {
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mFragmentList);
                    fragmentPager.setAdapter(viewPagerAdapter);
                    fragmentPager.setCurrentItem(3);
                }else if(bundle.getInt("notificationType") ==1){
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mFragmentList);
                    fragmentPager.setAdapter(viewPagerAdapter);
                    fragmentPager.setCurrentItem(0);
                }
            }
        }else {
            mFragmentList.add(new RootProfileFragment());
            mFragmentList.add(new RootShareContentFragment());
            mFragmentList.add(new RootFriendFragment());
            mFragmentList.add(new RootMessageListFragment());
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mFragmentList);
            fragmentPager.setAdapter(viewPagerAdapter);
            fragmentPager.setCurrentItem(1);
        }

        /*if(bundle != null){
            if(bundle.getInt("loginFirst") == 1){
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, mFragmentList.get(mainFragmentIndex));
                fragmentTransaction.commit();

            }else{
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if(bundle.getInt("notificationType") == 0) {
                    fragmentTransaction.replace(R.id.fragmentContainer, mFragmentList.get(3));
                    bundle.putInt("notificationType", -1);
                }else if(bundle.getInt("notificationType") ==1){
                    fragmentTransaction.replace(R.id.fragmentContainer, mFragmentList.get(0));
                    bundle.putInt("notificationType", -1);
                }else{
                    fragmentTransaction.replace(R.id.fragmentContainer, mFragmentList.get(mainFragmentIndex));
                }
                fragmentTransaction.commit();
            }
        }else{
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, mFragmentList.get(mainFragmentIndex));
            fragmentTransaction.commit();
        }*/




        /*navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
                sharePreferenceManager.saveMainFragmentIndex(index);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, mFragmentList.get(index));
                fragmentTransaction.commit();
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }


        });*/

        /*ImageView cameraImg = (ImageView)rootView.findViewById(R.id.cameraImg);
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                WhiteBoardFragment whiteBoardFragment = new WhiteBoardFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putInt("whichFragment", 2);
                whiteBoardFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.allContainer, new OpenCameraFragment());
                fragmentTransaction.addToBackStack("backFragment");
                fragmentTransaction.commit();
            }
        });*/
        return rootView;
    }

    private void openConnection(String username, String password, String nickName){
        try {
            Intent startIntent =  new  Intent(getActivity() , ChatService.class );
            startIntent.putExtra("username" , username);
            startIntent.putExtra("password", password);
            startIntent.putExtra("nickName", nickName);
            System.out.println("mainFragment openConnection out");
            if(!username.equals("")) {
                if(!isMyServiceRunning(ChatService.class)){
                    System.out.println("mainFragment openConnection in");

                    getActivity().startService(startIntent);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
