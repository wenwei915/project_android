package com.mrwo.notebook.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
	private static SharedPreferences sharedPreferences;

	/**
	 * 读取boolean标识的方法
	 * @param content   上写文
	 * @param key       节点的名称
	 * @param defValue  默认值
	 * @return          返回的值
	 */
	public static boolean getBoolean(Context context,String key,boolean defvalue){
		if(sharedPreferences==null){
			sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sharedPreferences.getBoolean(key, false);
	} 
	/**
	 * 读取boolean标识的方法
	 * @param content   上写文
	 * @param key       节点的名称
	 * @param defValue  默认值
	 */
	public static void setBoolean(Context context,String key,boolean value){
		if(sharedPreferences==null){
			sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putBoolean(key, value).commit();
	} 
	/**
	 * 读取boolean标识的方法
	 * @param content   上写文
	 * @param key       节点的名称
	 * @param defValue  默认值
	 * @return          返回的值
	 */
	public static String getString(Context context,String key,String defvalue){
		if(sharedPreferences==null){
			sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sharedPreferences.getString(key, defvalue);
	} 
	/**
	 * 读取boolean标识的方法
	 * @param content   上写文
	 * @param key       节点的名称
	 * @param defValue  默认值
	 */
	public static void setString(Context context,String key,String value){
		if(sharedPreferences==null){
			sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putString(key, value).commit();
	}

	/**
	 * 从sp中移除指定节点
	 * @param ctx	上下文环境
	 * @param key	需要移除节点的名称
	 */
	public static void remove(Context ctx, String key) {
		if(sharedPreferences == null){
			sharedPreferences = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().remove(key).commit();
	}
}