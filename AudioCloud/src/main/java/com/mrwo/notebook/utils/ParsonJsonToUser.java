package com.mrwo.notebook.utils;

import com.mrwo.notebook.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/12/4.
 */
public class ParsonJsonToUser {

    public static User parson(String result) {
        User user = new User();
        try {
            JSONObject object = new JSONObject(result);
            user.email = object.getString("email");
            user.nick = object.getString("nick");
            user.describe = object.getString("describe");
            user.sex = object.getString("sex");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
