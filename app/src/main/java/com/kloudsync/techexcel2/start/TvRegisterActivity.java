package com.kloudsync.techexcel2.start;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.techexcel.service.ConnectService;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.config.AppConfig;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TvRegisterActivity extends AppCompatActivity {

    private EditText et_room;
    private TextView tv_register;
    private TextView tv_error;
    private RelativeLayout rl_login;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean flag;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConfig.SUCCESS:
                    String result = (String) msg.obj;
                    tv_error.setVisibility(View.GONE);
                    mJsonTV(result);
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    if(TextUtils.isEmpty(result)){
                        result = "network error ,register failed";
                    }
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(result);
                    break;
                case -10002:
                    refreshAndLogin();
                    break;
                default:
                    break;
            }
        }
    };



    private void mJsonTV(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String RetCode = obj.getString("RetCode");
            String ErrorMessage = obj.getString("ErrorMessage");
            String DetailMessage = obj.getString("DetailMessage");
            if (RetCode.equals(AppConfig.RIGHT_RETCODE)) {
                JSONObject RetData = obj.getJSONObject("RetData");
                String UserToken = RetData.getString("UserToken");
                int UserID = RetData.getInt("UserID");
                String Name = RetData.getString("Name");
                AppConfig.UserToken = UserToken;
                AppConfig.UserID = UserID + "";
                AppConfig.UserName = Name + "";
                AppConfig.UUID = uuid + "";
                editor.putInt("UserID", UserID);
                editor.putBoolean("isFirst", false);
                editor.putString("UserToken", UserToken);
                editor.putString("Name", Name);
//                editor.putString("uuid", uuid + "");
                editor.commit();
                Intent intent = new Intent(this, QrCodeActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvreguster);
        findView();
        initView();
    }

    private void findView() {
        et_room = (EditText) findViewById(R.id.et_room);
        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_error = (TextView) findViewById(R.id.tv_error);
        rl_login = (RelativeLayout) findViewById(R.id.rl_login);
    }

    private void initView() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editListener();
        tv_register.setOnClickListener(new MyOnClick());
    }

    private void editListener() {
        et_room.setOnKeyListener(Key_listener());
    }

    @NonNull
    private View.OnKeyListener Key_listener() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String t1 = et_room.getText().toString();
                    Log.e("haha", "KEYCODE_ENTER" + flag);
                    if (!TextUtils.isEmpty(t1)) {
                        if (flag) {
                            SignUP();
                        }
                        flag = !flag;
                    }
                }
                return false;
            }
        };
    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_register:
                    SignUP();
                    break;
                default:
                    break;
            }
        }
    }

    private void SignUP() {
        String name = et_room.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), getString(R.string.RoomName_Null), Toast.LENGTH_SHORT).show();
            return;
        }
        final JSONObject jsonobject = format(name);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJsonNoToken(
                            AppConfig.URL_PUBLIC + "TV/Register",
                            jsonobject);
                    Log.e("TV_Register", jsonobject.toString() + "");
                    Log.e("TV_Register", responsedata.toString() + "");

                    Message msg = new Message();
                    if(responsedata.getInt("code") == 200){
                        String retcode = responsedata.getString("RetCode");
                        if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                            msg.what = AppConfig.SUCCESS;
                            msg.obj = responsedata.toString();
                        } else {
                            msg.what = Integer.parseInt(retcode);
                            msg.obj = responsedata.getString("ErrorMessage");
                        }
                    }else {
                        msg.what = AppConfig.FAILED;
                        msg.obj = responsedata.getString("ErrorMessage");
                    }
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    UUID uuid;

    private JSONObject format(String name) {
        uuid = UUID.randomUUID();
        Log.e("uuid", uuid + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("TvID", sharedPreferences.getString("DeviceId", null));
            jsonObject.put("TvName", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void refreshAndLogin(){

        String tvId = sharedPreferences.getString("DeviceId","");
        if(TextUtils.isEmpty(tvId)){
            tvId = getDeviceInfo(this);
        }
        final String id = tvId;


        Observable.just(id).observeOn(Schedulers.io()).map(new Function<String, Object>() {
            @Override
            public Object apply(final String id) throws Exception {
                ServiceInterfaceTools.getinstance().refreshTvToken(AppConfig.URL_PUBLIC + "TV/RefreshToken", id, new ServiceInterfaceTools.OnJsonResponseReceiver() {
                    @Override
                    public void jsonResponse(JSONObject jsonResponse) {
                        if(jsonResponse != null){
                            try {
                                int retCode = jsonResponse.getInt("RetCode");
                                Log.e("expired","step one");
                                if(retCode == 0){
//                                    refreshData.newToken = "";
                                    JSONObject data = jsonResponse.getJSONObject("RetData");
                                    if(data != null && data.has("UserToken")){
                                        sharedPreferences.edit().putString("UserToken",data.getString("UserToken")).commit();
                                        AppConfig.UserToken = data.getString("UserToken");
                                        goToQrcodeActivity();

                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                return id;
            }

        }).subscribe();
//        Observable.just()
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
            et_room.setFocusable(false);
            rl_login.setBackgroundResource(R.drawable.bg_input);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            et_room.setFocusable(true);
            rl_login.setBackground(null);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (!et_room.isFocusable()) {
                SignUP();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
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

    private void goToQrcodeActivity() {
        getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE).edit().putString("tv_bind_user", "").commit();
        AppConfig.BINDUSERID = "";
        Intent intent = new Intent(this, QrCodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
        startActivity(intent);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);
    }

}
