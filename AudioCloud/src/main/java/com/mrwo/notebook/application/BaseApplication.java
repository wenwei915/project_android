package com.mrwo.notebook.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by Administrator on 2015/11/29.
 */
public class BaseApplication extends Application {
    private static Context context;
    private static Handler handler;
    private static Thread mainThread;
    private static int mainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化常用的对象
        context = getApplicationContext();
        handler = new Handler();
        //获取主线程对象
        mainThread = Thread.currentThread();
        //获取主线程id，获取当前类的线程id（Application运行在主线程中）
        mainThreadId = android.os.Process.myTid();
    }

    public static Context getContext(){
        return context;
    }

    public static Handler getHandler(){
        return handler;
    }

    public static Thread getMainThread() {
        return mainThread;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }
}
