package com.kloudsync.techexcel2.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel2.meeting.model.MeetingPage;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TvFileCache{
    private final SharedPreferences cachePreference;
    private static TvFileCache instance;
    Gson gson;

    private TvFileCache(Context context) {
        cachePreference = context.getSharedPreferences("kloud_tv_file_cache", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized TvFileCache getInstance(Context context) {
        if (instance == null) {
            instance = new TvFileCache(context);
        }
        return instance;
    }


    public void cacheFile(MeetingPage page) {

        if (page == null || TextUtils.isEmpty(page.getPageUrl())) {
            return;
        }
        Map map = getPageMap();
        if(map == null){
            return;
        }
        map.put(page.getPageUrl(),page);
        cachePreference.edit().putString("page_map", new Gson().toJson(map)).commit();
    }

    public void removeFile(String url) {

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Map map = getPageMap();
        if(map == null){
            return;
        }
        map.remove(url);
        cachePreference.edit().putString("page_map", new Gson().toJson(map)).commit();
    }

    private Map<String,MeetingPage> getPageMap() {
        String json = cachePreference.getString("page_map", "");
        if (TextUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        return gson.fromJson(json, new TypeToken<Map<String,MeetingPage>>() {
        }.getType());
    }

    public MeetingPage getPageCache(String url){
        Map<String,MeetingPage> map = getPageMap();
        Log.e("TvFileCache","getPageCache, map:" + map);
        if(map != null){
            return map.get(url);
        }
        return null;
    }

    public boolean containFile(String url){
        return getPageMap().containsKey(url);
    }


    public void clear() {
        cachePreference.edit().putString("page_map", "").commit();
    }


}
