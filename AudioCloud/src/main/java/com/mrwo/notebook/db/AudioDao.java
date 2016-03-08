package com.mrwo.notebook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mrwo.notebook.bean.AudioText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/6.
 */
public class AudioDao {

    private MySqliteOpenHelper helper;

    public AudioDao(Context context) {
        helper = new MySqliteOpenHelper(context);
    }

    //增加内容
    public boolean add(AudioText audioText){
        SQLiteDatabase db = helper.getReadableDatabase();
        //创建要存入的数据的值得对象
        ContentValues values = new ContentValues();
        values.put("title",audioText.title);
        values.put("content",audioText.content);
        values.put("date", audioText.date);
        long insert = db.insert("audio_text", null, values);

        //关闭数据库对象
        db.close();

        if(insert!=-1){
            return true;
        }else
            return false;
    }

    //根据id删除内容
    public int delete(String id){
        SQLiteDatabase db = helper.getReadableDatabase();
        int result = db.delete("audio_text", "_id = ?", new String[]{id});
        db.close();
        return result;
    }

    //更改内容
    public int update(AudioText audioText){
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",audioText.title);
        values.put("content",audioText.content);
        values.put("date", audioText.date);
        int result = db.update("audio_text", values, "_id = ?", new String[]{audioText.id});

        db.close();
        return result;
    }

    //查询所有
    public List<AudioText> query() {
        //创建一个用于存储数据的集合
        List<AudioText> audioTexts = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("select _id, title,content,date from audio_text", null);
        Cursor cursor = db.query("audio_text", new String[]{"_id", "title", "content", "date"}, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            AudioText audioText = null;
            while (cursor.moveToNext()) {
                audioText = new AudioText();

//                System.out.println("_id:"+cursor.getInt(0)+";title:"+cursor.getString(1)+";content:"+cursor.getString(2)+";date:"+cursor.getString(3));
                //获取数据
                audioText.id = cursor.getInt(0) + "";
                audioText.title = cursor.getString(1);
                audioText.content = cursor.getString(2);
                audioText.date = cursor.getString(3);
                //将对象放入集合中
                audioTexts.add(audioText);
            }
            cursor.close();
        }
        db.close();
        return audioTexts;
    }

    //根据时间查找单个
    public int queryByDate(String date) {
        int id = -1;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("audio_text",new String[]{"_id"},"date = ?", new String[]{date},null,null,null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToNext()) {
                //获取数据
                id = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return id;
    }

    //根据id查询单个
    public AudioText queryOne(int id) {
        //首先初始化一个AudioDao对象
        AudioText audioText = new AudioText();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("audio_text",new String[]{"_id","title","content","date"},"_id = ?", new String[]{id+""},null,null,null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToNext()) {
                //获取数据
                audioText.id = cursor.getInt(0) + "";
                audioText.title = cursor.getString(1);
                audioText.content = cursor.getString(2);
                audioText.date = cursor.getString(3);
            }
            cursor.close();
        }
        db.close();
        return audioText;
    }
}
