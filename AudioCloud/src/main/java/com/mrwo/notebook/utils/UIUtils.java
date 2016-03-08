package com.mrwo.notebook.utils;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

import com.mrwo.notebook.application.BaseApplication;

public class UIUtils {

	public static Context getContext() {
		return BaseApplication.getContext();
	}

	public static Handler getHandler() {
		return BaseApplication.getHandler();
	}

	public static Thread getMainThread() {
		return BaseApplication.getMainThread();
	}

	public static int getMainThreadId() {
		return BaseApplication.getMainThreadId();
	}
	
	//xml-->view
	public static View inflate(int layoutId){
		return View.inflate(getContext(), layoutId, null);
	}
	
	//获取String
	public static String getString(int stringId){
		return getResource().getString(stringId);
	}
	
	//获取Drawable
	public static Drawable getDrawable(int drawableId){
		return getResource().getDrawable(drawableId);
	}
	//获得资源文件夹
	public static Resources getResource() {
		return getContext().getResources();
	}
	
	//获得StringArray数组
	public static String[] getStringArray(int stringArrayId){
		return getResource().getStringArray(stringArrayId);
	}
	
	// 获取颜色
	public static int getColor(int id) {
		return getContext().getResources().getColor(id);
	}
	
	//dip-->px    dip = px/像素密度
	public static int dip2px(int dip){
		float density = getResource().getDisplayMetrics().density;
		int px =  (int) (dip*density+0.5);
		return px;
	}
	//px-->dip    dip = px/像素密度
	public static int px2dip(int px){
		float density = getResource().getDisplayMetrics().density;
		int dip =  (int) (px/density+0.5);
		return dip;
	}
	
	//判断当前类运行的线程是否是主线程
	public static boolean isRunInMainThread(){
		return android.os.Process.myTid() == getMainThreadId();
	}
	
	//通过消息机制将数据返回到主线程中，然后填充UI
	public static void runInMainThread(Runnable runnable){
		if(isRunInMainThread()){
			//如果当前任务就是在主线程中
			runnable.run();
		}else{
			//如果当前任务不在主线程中，通过handler发送消息
			getHandler().post(runnable);
		}
	}
	
	//根据颜色选择器的id，获取颜色选择器对象的方法
	public static ColorStateList getColorStateList(int mTabTextColorResId){
		return getResource().getColorStateList(mTabTextColorResId);
	}
	
}
