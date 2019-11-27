package com.kloudsync.techexcel2.start;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.kloudsync.techexcel2.config.AppConfig;
import com.ub.service.activity.NotifyActivity;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class StartUbao extends Activity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean isFirst;
    private boolean isLogIn;
    private String telephone;
    private String password;
    private int countrycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("DeviceId",getDeviceInfo(StartUbao.this)).commit();
        isFirst = sharedPreferences.getBoolean("isFirst", true);
        isLogIn = sharedPreferences.getBoolean("isLogIn", false);
        telephone = sharedPreferences.getString("telephone", null);
        AppConfig.UUID = sharedPreferences.getString("uuid", null);
        AppConfig.UserToken = sharedPreferences.getString("UserToken", null);
        AppConfig.UserName = sharedPreferences.getString("Name", null);
        AppConfig.UserID = sharedPreferences.getInt("UserID", 0) + "";
        AppConfig.DEVICE_ID = sharedPreferences.getString("DeviceId", null);
        Log.e("deviceID", AppConfig.DEVICE_ID);
//        Toast.makeText(getApplicationContext(),"device_id:" + AppConfig.DEVICE_ID,Toast.LENGTH_LONG).show();
        password = LoginGet.DecodeBase64Password(sharedPreferences.getString("password", ""));
        countrycode = sharedPreferences.getInt("countrycode", 86);
        AppConfig.LANGUAGEID = getLocaleLanguage();
        Log.e("haha", AppConfig.UUID + ":" + AppConfig.UserToken + ":" + AppConfig.UserID);

		/*Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
		finish();*/
		/*if (isLogIn) {
			LoginGet.LoginRequest(StartUbao.this, "+" + countrycode
					+ telephone, password, 1, sharedPreferences, editor);
		} else {
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			startActivity(intent);
			finish();
		}*/
        Intent intent = null;
        Log.e("tv_bind_user",sharedPreferences.getString("tv_bind_user",""));
        if (isFirst || TextUtils.isEmpty(AppConfig.UserToken)) {
            intent = new Intent(getApplicationContext(),
                    TvRegisterActivity.class);
        } else {
            AppConfig.BINDUSERID = sharedPreferences.getString("tv_bind_user","");
            if(TextUtils.isEmpty(AppConfig.BINDUSERID) || AppConfig.BINDUSERID.equals("0")){
                intent = new Intent(getApplicationContext(),
                        QrCodeActivity.class);
            }else {
                startWBService();
                intent = new Intent(this, NotifyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        }
        startActivity(intent);
        finish();
    }


    private void startWBService() {
        Intent service = new Intent(getApplicationContext(), SocketService.class);
        startService(service);
    }

    public static String getDeviceInfo(Context context) {

        try {
            String device_id = "";
            if(TextUtil.isEmpty(device_id)){
                device_id = android.os.Build.SERIAL;
            }

            if(TextUtil.isEmpty(device_id)){
                device_id = android.os.Build.FINGERPRINT;
            }

            if(TextUtils.isEmpty(device_id)){
                device_id = UUID.randomUUID() +"";
            }

            if(!TextUtils.isEmpty(device_id)){
                device_id = device_id.replaceAll("/","");
            }

            return device_id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getLocaleLanguage() {

        int language = sharedPreferences.getInt("language", -1);
        String mlanguage = getResources().getConfiguration().locale
                .getLanguage();
        String mcountry = getResources().getConfiguration().locale.getCountry();
        Log.e("嘿嘿嘿", mlanguage + ":" + mcountry);
        if (language != -1) {
            switch (language) {
                case 1:
                    updateLange(getApplicationContext(), Locale.ENGLISH);
                    break;
                case 2:
                    updateLange(getApplicationContext(), Locale.SIMPLIFIED_CHINESE);
                    break;
                default:
                    break;
            }
            return language;
        }
        if (mlanguage.equals("en")) {
            return 1;
        } else if (mlanguage.equals("zh")) {
            return 2;
        }/*else if(mlanguage.equals("ja")){
			return 4;
		}else if(mlanguage.equals("fr")){
			return 12;
		}*/
        return 1;

    }

    public static void updateLange(Context context, Locale locale) {
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        DisplayMetrics dm = res.getDisplayMetrics();
        res.updateConfiguration(config, dm);
    }

    public void onResume() {
        super.onResume();
//	    MobclickAgent.onPageStart("StartUbao");
//	    MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("StartUbao");
//	    MobclickAgent.onPause(this);
    }



}
