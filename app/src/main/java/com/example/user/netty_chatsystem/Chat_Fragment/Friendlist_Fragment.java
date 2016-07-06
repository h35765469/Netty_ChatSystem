package com.example.user.netty_chatsystem.Chat_Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.user.netty_chatsystem.Addfriend_Activity;
import com.example.user.netty_chatsystem.Character_Activity;
import com.example.user.netty_chatsystem.ChangePassword_Activity;
import com.example.user.netty_chatsystem.Chat_Activity;
import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.CustomBaseAdapter;
import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.CustomBaseAdapter_horizontal;
import com.example.user.netty_chatsystem.Chat_Listview_Friendlist.RowItem;
import com.example.user.netty_chatsystem.R;
import com.example.user.netty_chatsystem.Search_Activity;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/3/12.
 */
public class Friendlist_Fragment extends BaseFragment {
    public static final String[] titles = new String[] { "Strawberry" , "banana" , "apple" , "pitch"};


    public static final Integer[] images = { R.drawable.bomb , R.drawable.bomb_clock , R.drawable.avatar , R.drawable.line};

    public String [] Id_array = {"123" , "456" , "a2131464@yahoo.com.tw" , "gg"};

    //更改在設定中觀察者的眼睛圖示
    public int eyechange_count = 0;

    //更改最愛朋友的按鈕
    private int favorite_count = 0;




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_friendlist_titlebar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.activity_friendlist_,null);

        final ImageView Friendlist_search_imageview = (ImageView)view.findViewById(R.id.friendlist_search_imageview);
        final ImageView Friendlist_character_imageview = (ImageView)view.findViewById(R.id.friendlist_character_imageview);
        final ImageView Friendlist_addfriend_imageview = (ImageView)view.findViewById(R.id.friendlist_addfriend_imageview);

        final ListView Friendlist_listview = (ListView)view.findViewById(R.id.friendlist_listview);
        final TwoWayView favorite_listview = (TwoWayView)view.findViewById(R.id.lvItems);

        List<RowItem> rowItems;

        rowItems = new ArrayList<RowItem>();
        for(int i = 0 ; i < titles.length ; i++){
            RowItem item = new RowItem(images[i], titles[i],Id_array[i]);
            rowItems.add(item);
        }
        CustomBaseAdapter adapter = new CustomBaseAdapter(getActivity(),rowItems);
        CustomBaseAdapter_horizontal adapter_horizontal = new CustomBaseAdapter_horizontal(getActivity(),rowItems);

        Friendlist_listview.setAdapter(adapter);
        favorite_listview.setAdapter(adapter_horizontal);

        //註冊character的按鈕監聽
        Friendlist_character_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Character_Activity().mViewPager.setCurrentItem(1);
            }
        });


        //啟動增加好友的頁面
        Friendlist_addfriend_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(getActivity(), Addfriend_Activity.class);
                startActivity(it);
            }
        });

        //啟動搜尋好友的功能
        Friendlist_search_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(getActivity(), Search_Activity.class);
                startActivity(it);
            }
        });

        //啟動Friendlist_listview的按鈕監聽器
        Friendlist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog = new Dialog(getActivity(), R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_friendlist_dialog);

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.2f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();

                //進入虛擬人物房間
                ImageView roomicon_imageview = (ImageView) dialog.findViewById(R.id.roomicon_imageview);
                roomicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent();
                        it.setClass(getActivity(), ChangePassword_Activity.class);
                        startActivity(it);
                    }
                });

                //進入好友聊天視窗
                ImageView messageicon_imageview = (ImageView) dialog.findViewById(R.id.messageicon_imageview);
                messageicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("friend_id",Id_array[position]);
                        Intent it = new Intent();
                        it.putExtras(bundle);
                        it.setClass(getActivity(), Chat_Activity.class);
                        getActivity().startActivity(it);
                        dialog.dismiss();
                    }
                });

                //進入好友設定視窗
                ImageView settingicon_image = (ImageView) dialog.findViewById(R.id.settingicon_imageview);
                settingicon_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog setting_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                        setting_dialog.setContentView(R.layout.resource_friendlist_settinglist_dialog);

                        //設定dialog_setiing上按鈕的功能
                        Assign_settingdialog(setting_dialog);

                        // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                        lp.dimAmount = 0.2f;
                        setting_dialog.getWindow().setAttributes(lp);
                        setting_dialog.show();
                    }
                });

                //添加好友為我的最愛
                final ImageView favorite_imageview = (ImageView) dialog.findViewById(R.id.favorite_imageview);
                favorite_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (favorite_count == 0) {
                            favorite_imageview.setImageResource(R.drawable.candy);
                            favorite_count = 1;
                        } else {
                            favorite_imageview.setImageResource(R.drawable.candy_red);
                            favorite_count = 0;
                        }
                    }
                });
            }
        });


        //啟動favorite_listview的按鈕監聽器
        favorite_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(getActivity(),R.style.selectorDialog);
                dialog.setContentView(R.layout.resource_friendlist_dialog);

                // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
                lp.dimAmount=0.2f;
                dialog.getWindow().setAttributes(lp);
                dialog.show();

                //進入虛擬人物房間
                ImageView roomicon_imageview = (ImageView)dialog.findViewById(R.id.roomicon_imageview);
                roomicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent();
                        it.setClass(getActivity(), ChangePassword_Activity.class);
                        startActivity(it);
                    }
                });

                //進入好友聊天視窗
                ImageView messageicon_imageview = (ImageView)dialog.findViewById(R.id.messageicon_imageview);
                messageicon_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("friend_id","123");
                        Intent it = new Intent();
                        it.putExtras(bundle);
                        it.setClass(getActivity(),Chat_Activity.class);
                        getActivity().startActivity(it);
                        dialog.dismiss();
                    }
                });

                //進入好友設定視窗
                ImageView settingicon_image = (ImageView)dialog.findViewById(R.id.settingicon_imageview);
                settingicon_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog setting_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                        setting_dialog.setContentView(R.layout.resource_friendlist_settinglist_dialog);

                        //設定dialog_setiing上按鈕的功能
                        Assign_settingdialog(setting_dialog);

                        // 由程式設定 Dialog 視窗外的明暗程度, 亮度從 0f 到 1f
                        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
                        lp.dimAmount=0.2f;
                        setting_dialog.getWindow().setAttributes(lp);
                        setting_dialog.show();
                    }
                });

                //添加好友為我的最愛
                final ImageView favorite_imageview = (ImageView)dialog.findViewById(R.id.favorite_imageview);
                favorite_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(favorite_count == 0){
                            favorite_imageview.setImageResource(R.drawable.candy);
                            favorite_count = 1;
                        }else{
                            favorite_imageview.setImageResource(R.drawable.candy_red);
                            favorite_count = 0;
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState){

    }


    //設定dialog_setiing上按鈕的功能
    public void Assign_settingdialog(final Dialog setting_dialog){
        ImageView editnameicon_imageview = (ImageView)setting_dialog.findViewById(R.id.editnameicon_imageview);
        ImageView viewericon_imageview = (ImageView)setting_dialog.findViewById(R.id.viewericon_imageview);
        ImageView blockadeicon_imageview = (ImageView)setting_dialog.findViewById(R.id.blockadeicon_imageview);
        ImageView deleteicon_imageview = (ImageView)setting_dialog.findViewById(R.id.deleteicon_imageview);


        editnameicon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_dialog.dismiss();
                final Dialog editname_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                editname_dialog.setContentView(R.layout.resource_friendlist_settinglist_editname_dialog);
                editname_dialog.show();

                ImageView  editname_yes_imageview = (ImageView)editname_dialog.findViewById(R.id.editname_yes_imageview);
                ImageView editname_no_imageview = (ImageView)editname_dialog.findViewById(R.id.editname_no_imageview);

                editname_yes_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editname_dialog.dismiss();
                    }
                });

                editname_no_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editname_dialog.dismiss();
                    }
                });
            }
        });

        viewericon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_dialog.dismiss();
                final Dialog viewer_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                viewer_dialog.setContentView(R.layout.resource_friendlist_settinglist_viewer_dialog);
                viewer_dialog.show();

                final ImageView eye_imageview = (ImageView)viewer_dialog.findViewById(R.id.eye_imageview);
                eye_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(eyechange_count == 0){
                            eye_imageview.setImageResource(R.drawable.medical_open);
                            eyechange_count = 1;
                        }
                        else{
                            eye_imageview.setImageResource(R.drawable.medical_close);
                            eyechange_count = 0;
                        }
                    }
                });
            }
        });

        blockadeicon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_dialog.dismiss();
                final Dialog blockade_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                blockade_dialog.setContentView(R.layout.resource_friendlist_settinglist_blockade_dialog);
                blockade_dialog.show();

                ImageView blockade_yes_imageview = (ImageView)blockade_dialog.findViewById(R.id.blockade_yes_imageview);
                ImageView blockade_no_imageview = (ImageView)blockade_dialog.findViewById(R.id.blockade_no_imageview);

                blockade_yes_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blockade_dialog.dismiss();
                    }
                });

                blockade_no_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blockade_dialog.dismiss();
                    }
                });
            }
        });

        deleteicon_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_dialog.dismiss();
                final Dialog delete_dialog = new Dialog(getActivity(), R.style.selectorDialog);
                delete_dialog.setContentView(R.layout.resource_friendlist_settinglist_delete_dialog);
                delete_dialog.show();

                ImageView delete_yes_imageview = (ImageView)delete_dialog.findViewById(R.id.delete_yes_imageview);
                ImageView delete_no_imageview = (ImageView)delete_dialog.findViewById(R.id.delete_no_imageview);

                delete_yes_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete_dialog.dismiss();
                    }
                });

                delete_no_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete_dialog.dismiss();
                    }
                });
            }
        });
    }
}
