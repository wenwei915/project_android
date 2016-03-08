package com.mrwo.notebook.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by Administrator on 2015/11/29.
 */
public class CacheFileUtils {
    // 从缓存中获取数据的方法
    public static String getDataFromLocal(String params) {
        BufferedReader reader = null;
        File cacheDir = UIUtils.getContext().getCacheDir();
        // getKey()+index+getParams()，唯一性
        File file = new File(cacheDir, params);
        try {
            reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            // 读取第一行数据，获取有效时间戳，判断读取这个缓存的时候，缓存的数据是否还有效
            long currentTime = System.currentTimeMillis();
            String string = reader.readLine();
            long validTime = Long.valueOf(string);
            if (currentTime < validTime) {
                // 数据有效，往后读取
                String temp = null;
                StringBuffer sb = new StringBuffer();
                while ((temp = reader.readLine()) != null) {
                    sb.append(temp);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
        }
        return null;
    }

    // 写数据到缓存的方法
    public static void writeToLocal(String result, String params) {
        BufferedWriter writer = null;
        File cacheDir = UIUtils.getContext().getCacheDir();
        File file = new File(cacheDir, params);
        try {
            writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));

            long validTime = System.currentTimeMillis() + 30 * 60 * 1000;
            writer.write(validTime + "\r\n");
            writer.write(result.toCharArray());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
        }
    }
}
