package com.example.user.netty_chatsystem.Chat_Sqlite_ChatHistory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        String sql =  "create table stu_table(id INTEGER PRIMARY KEY AUTOINCREMENT , from_id varchar(50) , to_id varchar(50) , content varchar(500))" ;
        //輸出創建數據庫的日誌信息
        Log.i(TAG, "create Database------------->");
        //execSQL函數用於執行SQL語句
        db.execSQL(sql);
    }

    //當更新數據庫的時候執行該方法
    public  void  onUpgrade(SQLiteDatabase db,  int  oldVersion,  int  newVersion) {
        //輸出更新數據庫的日誌信息
        Log.i(TAG, "update Database------------->");
    }
}
