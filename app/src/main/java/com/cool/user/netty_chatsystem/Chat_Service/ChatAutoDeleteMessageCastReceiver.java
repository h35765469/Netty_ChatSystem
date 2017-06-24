package com.cool.user.netty_chatsystem.Chat_Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory.MyDBHelper;

import java.io.File;

/**
 * Created by user on 2017/2/26.
 */
public class ChatAutoDeleteMessageCastReceiver extends BroadcastReceiver {
    // 接收廣播後執行這個方法
    // 第一個參數Context物件，用來顯示訊息框、啟動服務
    // 第二個參數是發出廣播事件的Intent物件，可以包含資料
    @Override
    public void onReceive(Context context, Intent intent) {
        MyDBHelper dbHelper = new MyDBHelper(context, "Chat.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Message", null, null);
        db.delete("MessageOrder", null, null);
        db.close();

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("chatDir", Context.MODE_PRIVATE);
        deleteDirectory(directory);//清除資料夾
    }

    //清掉資料夾
    public void deleteDirectory( File dir )
    {

        if ( dir.isDirectory() )
        {
            String [] children = dir.list();
            for ( int i = 0 ; i < children.length ; i ++ )
            {
                File child =    new File( dir , children[i] );
                if(child.isDirectory()){
                    deleteDirectory( child );
                    child.delete();
                }else{
                    child.delete();

                }
            }
            dir.delete();
        }
    }
}
