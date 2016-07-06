package com.example.user.netty_chatsystem.Chat_DrawBoard;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.user.netty_chatsystem.R;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;

/**
 * Created by user on 2016/4/5.
 */

public class PageTwoFragment extends Fragment
{

    private SweetSheet mSweetSheet2;
    private RelativeLayout rl;
    private ImageView cameraboard_camera_imageview , cameraboard_effect_imageview,cameraboard_save_imageview , cameraboard_send_imageview;
    private ImageView cameraboard_no_imageview , cameraboard_word_imageview,cameraboard_pencil_imageview, cameraboard_back_imageview;
    boolean isBtnLongPressed = false;

    DrawBoardFragement drawBoardFragement;
    FragmentManager manager;
    FragmentTransaction transaction;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pagetwo_fragment, container, false);
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();
        drawBoardFragement = new DrawBoardFragement();

        initUI(rootView);

        return rootView;
    }

    public void initUI(View rootView){


        //下排按鈕
        cameraboard_camera_imageview = (ImageView)rootView.findViewById(R.id.cameraboard_camera_imageview);
        cameraboard_effect_imageview = (ImageView)rootView.findViewById(R.id.cameraboard_effect_imageview);
        cameraboard_save_imageview = (ImageView)rootView.findViewById(R.id.cameraboard_save_imageview);
        cameraboard_send_imageview = (ImageView)rootView.findViewById(R.id.cameraboard_send_imageview);

        //上排按鈕
        cameraboard_no_imageview = (ImageView)rootView.findViewById(R.id.cameraboard_no_imageview);
        cameraboard_back_imageview = (ImageView)rootView.findViewById(R.id.cameraboard_back_imageview);
        cameraboard_pencil_imageview = (ImageView)rootView.findViewById(R.id.cameraboard_pencil_imageview);
        cameraboard_word_imageview = (ImageView)rootView.findViewById(R.id.cameraboard_word_imageview);

        cameraboard_camera_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.replace(R.id.cameraContainer, drawBoardFragement);
                transaction.commit();
            }
        });

        cameraboard_camera_imageview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isBtnLongPressed = true;
                //不引發按一下事件
                return true;
            }
        });

        cameraboard_camera_imageview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (isBtnLongPressed) {
                        cameraboard_save_imageview.setVisibility(View.VISIBLE);
                        cameraboard_effect_imageview.setVisibility(View.VISIBLE);
                        cameraboard_send_imageview.setVisibility(View.VISIBLE);
                        cameraboard_no_imageview.setVisibility(View.VISIBLE);
                        cameraboard_camera_imageview.setVisibility(View.INVISIBLE);

                    }
                }
                return false;
            }
        });

        cameraboard_effect_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cameraboard_save_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cameraboard_send_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cameraboard_no_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraboard_no_imageview.setVisibility(View.INVISIBLE);
                cameraboard_back_imageview.setVisibility(View.INVISIBLE);
                cameraboard_pencil_imageview.setVisibility(View.INVISIBLE);
                cameraboard_word_imageview.setVisibility(View.INVISIBLE);
                cameraboard_effect_imageview.setVisibility(View.INVISIBLE);
                cameraboard_save_imageview.setVisibility(View.INVISIBLE);
                cameraboard_camera_imageview.setVisibility(View.VISIBLE);
                cameraboard_send_imageview.setVisibility(View.INVISIBLE);
            }
        });

        cameraboard_back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cameraboard_pencil_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cameraboard_word_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    private void setupViewpager() {


        mSweetSheet2 = new SweetSheet(rl);

        //从menu 中设置数据源
        mSweetSheet2.setMenuList(R.menu.menu_sweet);
        mSweetSheet2.setDelegate(new ViewPagerDelegate());
        mSweetSheet2.setBackgroundEffect(new DimEffect(0.5f));
        mSweetSheet2.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
            @Override
            public boolean onItemClick(int position, MenuEntity menuEntity1) {

                Toast.makeText(getActivity(), menuEntity1.title + "  " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }
}
