package com.mrwo.notebook.utils;

import android.content.Context;
import android.widget.Toast;

import com.mrwo.notebook.R;

/**
 * Created by Administrator on 2015/11/29.
 */
public class ToastUtil {
    public static void show(Context context,String string){
        Toast.makeText(context, string,Toast.LENGTH_SHORT).show();
    }
}
