package com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.PersonalSettingFragment;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cool.user.netty_chatsystem.Chat_Client.handler.Client_UserHandler;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.PersonalSettingFragment.BlockFriendFragment.BlockFriendFragment;
import com.cool.user.netty_chatsystem.Chat_Fragment.ProfileFragment.PersonalSettingFragment.ChangeNotificationFragment.ChangeNotificationFragment;
import com.cool.user.netty_chatsystem.Chat_MySQL.Config;
import com.cool.user.netty_chatsystem.Chat_Service.ChatService;
import com.cool.user.netty_chatsystem.Chat_Sharepreference.SharePreferenceManager;
import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;
import com.cool.user.netty_chatsystem.Chat_core.connetion.IMConnection;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Commands;
import com.cool.user.netty_chatsystem.Chat_core.protocol.Handlers;
import com.cool.user.netty_chatsystem.Chat_core.transport.Header;
import com.cool.user.netty_chatsystem.Chat_core.transport.IMResponse;
import com.cool.user.netty_chatsystem.R;

import java.io.File;

/**
 * Created by user on 2016/9/14.
 */
public class PersonalSettingFragment extends Fragment {
    String[] settings = {"訊息通知", "封鎖名單", "登出"};

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.personalsetting_fragment, container, false);
        ListView personSettingListView = (ListView)rootView.findViewById(R.id.personSettingListView);
        TextView backTxt = (TextView)rootView.findViewById(R.id.backTxt);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");//設定back的按紐
        backTxt.setTypeface(font);
        backTxt.setText("\uf060");
        backTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        ArrayAdapter<String>arrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.resource_personalsetting_listview,R.id.settingText,settings);
        personSettingListView.setAdapter(arrayAdapter);
        personSettingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        fragmentTransaction.replace(R.id.profileContainer, new ChangeNotificationFragment());
                        fragmentTransaction.commit();
                        break;
                    /*case 1: {
                        fragmentTransaction.replace(R.id.allContainer, new ChangePasswordFragment());
                        fragmentTransaction.commit();
                    }
                    break;*/
                    case 1: {
                        fragmentTransaction.replace(R.id.profileContainer, new BlockFriendFragment());
                        fragmentTransaction.commit();
                    }
                    break;
                    /*case 3:
                        break;*/
                    case 2: {
                        logOut();
                    }
                    break;
                }
            }
        });
        return rootView;
    }

    private void logOut(){
        clearAllSqlite();//清除所有本地資料庫
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
        deleteDir(mediaStorageDir);//刪除所有檔案資料夾
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        deleteDir(directory);
        directory = cw.getDir("chatDir", Context.MODE_PRIVATE);
        deleteDir(directory);
        // Session Manager Class
        SharePreferenceManager sharePreferenceManager = new SharePreferenceManager(getActivity());
        sharePreferenceManager.saveRestart(false);

        //close the service
        Intent stopIntent = new Intent(getActivity(), ChatService.class);
        getActivity().stopService(stopIntent);
        //getActivity().unbindService(serviceConnection);

        IMConnection connection = Client_UserHandler.getConnection();
        if(connection != null) {
            IMResponse resp = new IMResponse();
            Header header = new Header();
            header.setHandlerId(Handlers.MESSAGE);
            header.setCommandId(Commands.USER_LOGOUT_REQUEST);
            resp.setHeader(header);
            connection.sendResponse(resp);
        }
        // Clear the session data
        // This will clear all session data and
        // redirect user to LoginActivity
        sharePreferenceManager.logoutUser();
    }

    //清除所有sqlite
    public void clearAllSqlite(){
        MyDBHelper dbHelper = new MyDBHelper(getActivity(), "Chat.db", null, 1);
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + "MessageOrder");
        db.execSQL("DROP TABLE IF EXISTS " + "User");
        db.execSQL("DROP TABLE IF EXISTS " + "Sticker");
        db.execSQL("DROP TABLE IF EXISTS " + "Message");
        db.execSQL("DROP TABLE IF EXISTS " + "Collect");
        db.execSQL("DROP TABLE IF EXISTS " + "Friend");
        db.close();
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }


}
