package com.mrwo.notebook.utils;

import com.lidroid.xutils.BitmapUtils;

/**
 * BitmapUtils的一个工具类
 * @author Wenwei
 *
 */
public class BitmapHelper {

	private static BitmapUtils bitmapUtils = null;
	public static BitmapUtils getBitmapUtils(){
		if(bitmapUtils==null){
			bitmapUtils = new BitmapUtils(UIUtils.getContext());
		}
		return bitmapUtils;
	}
}
