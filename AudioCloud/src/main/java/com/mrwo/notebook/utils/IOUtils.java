package com.mrwo.notebook.utils;

import com.lidroid.xutils.util.LogUtils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
	/** 关闭流 */
	public static boolean close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				LogUtils.e(String.valueOf(e));
			}
		}
		return true;
	}
}
