package com.kloudsync.techexcel2.tool;

import android.content.Context;
import android.text.TextUtils;

import com.kloudsync.techexcel2.config.AppConfig;


public class UserData {

    public static String getUserToken(Context context){
        String userToken = AppConfig.UserToken;
        if(TextUtils.isEmpty(userToken)){
            userToken = context.getSharedPreferences(AppConfig.LOGININFO,
                    Context.MODE_PRIVATE).getString("UserToken","");
        }
        return userToken;
    }
}
