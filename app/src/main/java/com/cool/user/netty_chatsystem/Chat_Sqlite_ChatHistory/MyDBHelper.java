package com.cool.user.netty_chatsystem.Chat_Sqlite_ChatHistory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2016/7/1.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    private  static  final  String TAG =  "TestSQLite" ;
    public  static  final  int  VERSION =  1 ;

    //必須要有構造函數
    public  MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                       int  version) {
        super (context, name, factory, version);
    }

    // 當第一次創建數據庫的時候，調用該方法
    public  void  onCreate(SQLiteDatabase db) {
    }

    //當更新數據庫的時候執行該方法
    public  void  onUpgrade(SQLiteDatabase db,  int  oldVersion,  int  newVersion) {
    }
}
