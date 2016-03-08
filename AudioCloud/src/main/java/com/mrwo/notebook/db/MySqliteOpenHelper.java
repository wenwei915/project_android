package com.mrwo.notebook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/12/6.
 */
public class MySqliteOpenHelper extends SQLiteOpenHelper {
    public MySqliteOpenHelper(Context context) {
        super(context, "audio.db", null, 1);
    }

    //数据库第一次被创建时被调用，适合用于表结构的初始化
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table audio_text (_id integer primary key autoincrement,title varchar(200),content varchar(200),date varchar(200))");
    }

    //数据库版本号发生改变时才会执行, 适合做表结构的修改
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
