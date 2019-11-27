package com.ub.techexcel.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.kloudsync.techexcel2.config.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectService {

    /**
     * 判断网络是否连接
     *
     * @param ctx
     * @return
     */
    public static boolean isNetWorkConnected(Context ctx) {
        if (ctx != null) {
            ConnectivityManager cm = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nwi = cm.getActiveNetworkInfo();
            if (nwi != null) {
                return nwi.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getTypeName().equals("WIFI")) {
            return true;
        }
        return false;
    }

    /**
     * 以get方式发送URL请求（httpurlconnection）
     *
     * @param path
     * @return
     */
    public static JSONObject getIncidentData(String path) {
        JSONObject jsonObject = new JSONObject();
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
             conn.addRequestProperty("UserToken", AppConfig.SACN_USER_TOKEN);
            // conn.addRequestProperty("LanguageID", AppConfig.LANGUAGEID + "");
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                String json = StringUtils.inputStreamTString(is);

                jsonObject = new JSONObject(json);
                is.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }/**
     * 以get方式发送URL请求（httpurlconnection）
     *
     * @param path
     * @return
     */
    public static JSONObject getIncidentDataattachment(String path) {
        JSONObject jsonObject = new JSONObject();
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
             conn.addRequestProperty("UserToken", AppConfig.UserToken);
            // conn.addRequestProperty("LanguageID", AppConfig.LANGUAGEID + "");
            conn.setRequestMethod("DELETE");
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                String json = StringUtils.inputStreamTString(is);

                jsonObject = new JSONObject(json);
                is.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public static JSONObject getRequestWithoutToken(String path) {
        JSONObject jsonObject = new JSONObject();
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            // conn.addRequestProperty("LanguageID", AppConfig.LANGUAGEID + "");
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                String json = StringUtils.inputStreamTString(is);

                jsonObject = new JSONObject(json);
                is.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 通過httpget获取网络数据
     *
     * @param path
     * @return
     */
    public static JSONObject getIncidentbyHttpGet(String path) {
        JSONObject jsonObject = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.addRequestProperty("UserToken", AppConfig.UserToken);
            // conn.addRequestProperty("LanguageID", AppConfig.LANGUAGEID + "");
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                String json = StringUtils.inputStreamTString(is);
                jsonObject = new JSONObject(json);
                is.close();
                conn.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // 利用http发送数据到服务器（addincident）
    public static JSONObject submitDataByJson(String path, JSONObject jsonObject) {

        Log.e("check_url","url:" + path);

        JSONObject responsejson = new JSONObject();
        // 把JSON数据转换成String类型使用输出流向服务器写
        try {
            URL url2 = new URL(path);
            String content = String.valueOf(jsonObject);

            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("UserToken", AppConfig.UserToken);
            // connection.addRequestProperty("LanguageID", AppConfig.LANGUAGEID
            // + "");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String json = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(json);
                is.close();
                connection.disconnect();

                Log.e("response","url:" + url2 + ": response:" + responsejson);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responsejson;
    }

    public static JSONObject submitDataByJsonNoToken(String path,
                                                     JSONObject jsonObject) {
        JSONObject responsejson = new JSONObject();
        /* 把JSON数据转换成String类型使用输出流向服务器写 */
        Log.e("path", path);
        try {
            URL url2 = new URL(path);
            String content = String.valueOf(jsonObject);
            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
            // connection.addRequestProperty("UserToken", AppConfig.UserToken);
            // connection.addRequestProperty("LanguageID", AppConfig.LANGUAGEID
            // + "");
            connection.setDoOutput(true);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String json = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(json);
                responsejson.put("code",code);
                is.close();
                connection.disconnect();
            }else {
                responsejson.put("code",code);
                responsejson.put("ErrorMessage","network error ,error code:" + code);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            try {
                responsejson.put("code",100);
                responsejson.put("ErrorMessage","error code ：100，exception error，" + e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return responsejson;
    }


    // 利用http发送数据到服务器（addincident）
    public static JSONObject submitDataByJsonLive(String path, JSONObject jsonObject) {
        JSONObject responsejson = null;

        try {
            URL url2 = new URL(path);
            String content = String.valueOf(jsonObject);
            HttpURLConnection connection = (HttpURLConnection) url2
                    .openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("Authorization", "Bearer " +AppConfig.liveToken);
            connection.setRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash," +
                    " application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Charset", "utf-8");
            OutputStream os = connection.getOutputStream();
            os.write(content.getBytes());
            os.close();
            int code = connection.getResponseCode();
            Log.e("code", code + "");
            if (code == 200) {
                InputStream is = connection.getInputStream();
                String str = StringUtils.inputStreamTString(is);
                responsejson = new JSONObject(str);
                is.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responsejson;

    }
}
