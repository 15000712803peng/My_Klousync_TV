package com.kloudsync.techexcel2.start;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.techexcel.service.ConnectService;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.tool.SoftInputUtils;
import com.kloudsync.techexcel2.ui.MainActivity;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends Activity {

    private TextView tv_cphone, tv_login, tv_atjoin, tv_fpass;
    private EditText et_telephone, et_password;
    private FrameLayout fl_login;
    private RelativeLayout rl_login;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String telephone;
    private String password;
    public static LoginActivity instance = null;

    private boolean flag;
    private boolean flag_cp;
    private int flag_se;
    private TextView socketInfoText;
    private static final int CODE_TOKEN_EXPIRED = -1401;
    String deviceId;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConfig.SUCCESS:
                    String result = (String) msg.obj;
//					mJsonTV(result);
//                    finish();
                    break;
                case AppConfig.FAILED:
                    if(msg.arg1 == CODE_TOKEN_EXPIRED){
                        refreshAndLogin();
                    }
                    result = (String) msg.obj;
//                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance = this;
        initView();
        IntentFilter filter = new IntentFilter();
        filter.addAction("socket_info");
//        expiredRefreshAndLogin();
        registerReceiver(socketInfoReceiver,filter);

    }

    private void initView() {
        tv_cphone = (TextView) findViewById(R.id.tv_cphone);
        tv_login = (TextView) findViewById(R.id.tv_login);
        tv_atjoin = (TextView) findViewById(R.id.tv_atjoin);
        tv_fpass = (TextView) findViewById(R.id.tv_fpass);
        et_telephone = (EditText) findViewById(R.id.et_telephone);
        et_password = (EditText) findViewById(R.id.et_password);
        fl_login = (FrameLayout) findViewById(R.id.fl_login);
        rl_login = (RelativeLayout) findViewById(R.id.rl_login);
        tv_login.setEnabled(false);
//        tv_login.setText(getVersion());
        editListener();
        setEditChangeInput();
        getSP();
        tv_login.setOnClickListener(new myOnClick());
        tv_atjoin.setOnClickListener(new myOnClick());
        tv_fpass.setOnClickListener(new myOnClick());
        tv_cphone.setOnClickListener(new myOnClick());
        socketInfoText = findViewById(R.id.txt_socket_info);
//        et_telephone.setOnClickListener(new myOnClick());
//        et_password.setOnClickListener(new myOnClick());

        SoftInputUtils.hideSoftInput(LoginActivity.this);
    }

    private void editListener() {
        et_telephone.setOnKeyListener(Key_listener());
        et_password.setOnKeyListener(Key_listener());
    }


    @NonNull
    private View.OnKeyListener Key_listener() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String t1 = et_telephone.getText().toString();
                    String t2 = et_password.getText().toString();
                    Log.e("haha", "KEYCODE_ENTER" + flag);
                    switch (v.getId()) {
                        case R.id.et_telephone:
                            et_telephone.setSelection(t2.length());
                            break;
                        case R.id.et_password:
                            if (!TextUtils.isEmpty(t1) && !TextUtils.isEmpty(t2)) {
                                if (flag) {
                                    doLogin();
                                }
                                flag = !flag;
                            }
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        };
    }

    private void getSP() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        telephone = sharedPreferences.getString("telephone", null);
        password = LoginGet.DecodeBase64Password(sharedPreferences.getString("password", ""));
        AppConfig.COUNTRY_CODE = sharedPreferences.getInt("countrycode", 86);
        et_telephone.setText(telephone);
        et_password.setText(password);
        tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);

    }

    private void setEditChangeInput() {
        et_telephone.addTextChangedListener(new myTextWatch());
        et_password.addTextChangedListener(new myTextWatch());

    }

    /**
     * 　　* 获取版本号
     * <p>
     * 　　* @return 当前应用的版本号
     * <p>
     */
    public String getVersion() {
        try {
            return Build.VERSION.SDK_INT + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


    protected class myTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @SuppressLint("NewApi")
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (et_password.getText().length() > 0
                    && et_telephone.getText().length() > 0) {
                tv_login.setAlpha(1.0f);
                tv_login.setEnabled(true);
            } else {
                tv_login.setAlpha(0.6f);
                tv_login.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

    }

    protected class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_login:
//                    GetLogin();
//                    refreshAndLogin();
                    doLogin();
                    break;
                case R.id.tv_atjoin:
                    GoToSign();
                    break;
                case R.id.tv_fpass:
                    GoToForget();
                    break;
                case R.id.tv_cphone:
                    GotoChangeCode();
                    break;
                /*case R.id.et_password:
                    flag_se = 1;
                    ChangeSelect();
                    break;
                case R.id.et_telephone:
                    flag_se = 0;
                    ChangeSelect();
                    break;*/
                default:
                    break;
            }

        }

    }


    public void GotoChangeCode() {
        tv_cphone.setBackgroundColor(getResources().getColor(R.color.green));
        Intent intent = new Intent(getApplicationContext(), ChangeCountryCode.class);
        String code = tv_cphone.getText().toString();
        code = code.replaceAll("\\+", "");
        AppConfig.COUNTRY_CODE = Integer.parseInt(code);
        startActivityForResult(intent, RegisterActivity.CHANGE_COUNTRY_CODE);
        overridePendingTransition(R.anim.tran_in4, R.anim.tran_out4);
    }


    private void doLogin() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_login.setEnabled(false);
                fl_login.setVisibility(View.VISIBLE);
            }
        });

        telephone = et_telephone.getText().toString();
        password = et_password.getText().toString();
        editor.putString("telephone", telephone);
        editor.putString("password", LoginGet.getBase64Password(password));
        editor.putInt("countrycode", AppConfig.COUNTRY_CODE);
        editor.commit();
        telephone = tv_cphone.getText().toString() + telephone;
        GoToSendMessage();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!instance.isFinishing()) {
                    tv_login.setEnabled(true);
                    fl_login.setVisibility(View.GONE);
                }
            }
        }, 5000);
    }

    private void GoToSendMessage() {

        final JSONObject jsonobject = format();
        AppConfig.UserToken = sharedPreferences.getString("UserToken", null);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Log.e("BindTVAuthentication", jsonobject.toString() + "");
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC + "TV/BindTVWithAuthentication",
                            jsonobject);
                    Log.e("BindTVAuthentication", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.SUCCESS;
                        msg.obj = responsedata.toString();
                    } else {
                        msg.what = AppConfig.FAILED;
                        msg.arg1 = Integer.parseInt(retcode);
                        msg.obj = responsedata.getString("ErrorMessage");
                    }

                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("TvID", sharedPreferences.getString("DeviceId", null));
            jsonObject.put("UserName", telephone);
            jsonObject.put("Password", password);
            jsonObject.put("EncryptOption", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void GoToForget() {
        Intent intent = new Intent(LoginActivity.this,
                ForgetPasswordActivity.class);
        startActivity(intent);
    }

    public void GoToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void GoToSign() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RegisterActivity.CHANGE_COUNTRY_CODE:
                tv_cphone.setText("+" + AppConfig.COUNTRY_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
            if (!flag_cp) {
            /*if(flag_se < 2) {
                flag_se++;
            }
            ChangeSelect();*/
                if (et_telephone.isFocusable()) {
                    et_telephone.setFocusable(false);
                    et_password.setFocusable(true);
                } else if (et_password.isFocusable()) {
                    et_password.setFocusable(false);
                    rl_login.setBackgroundResource(R.drawable.bg_input);
                }
            }
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (!flag_cp) {
            /*if(flag_se > 0){
                flag_se--;
            }
            ChangeSelect();*/
                if (et_password.isFocusable()) {
                    et_telephone.setFocusable(true);
                } else {
                    et_password.setFocusable(true);
                }
                rl_login.setBackground(null);
            }
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            tv_cphone.setBackgroundColor(getResources().getColor(R.color.green));
            flag_cp = true;
            et_telephone.setFocusable(false);
            et_password.setFocusable(false);
            rl_login.setBackground(null);
            Log.e("haha", "KEYCODE_DPAD_LEFT");
            return false;

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            tv_cphone.setBackground(null);
            flag_cp = false;
            et_telephone.setFocusable(true);
            et_password.setFocusable(true);
            Log.e("haha", "KEYCODE_DPAD_RIGHT");
            return false;

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (!et_telephone.isFocusable() && !et_password.isFocusable()
                    && !flag_cp) {
//                GetLogin();
//                refreshAndLogin();
                doLogin();
            } else if (flag_cp) {
                GotoChangeCode();
            }
            /*if(2 == flag_se){
                GetLogin();
            }*/
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void ChangeSelect() {
        switch (flag_se) {
            case 0:
                et_telephone.setSelection(0);
                rl_login.setBackground(null);
                break;
            case 1:
                et_password.setSelection(0);
                rl_login.setBackground(null);
                break;
            case 2:
                et_telephone.setFocusable(false);
                et_password.setFocusable(false);
                rl_login.setBackgroundResource(R.drawable.bg_input);
                break;
        }
    }

    public void onResume() {
        super.onResume();
//	    MobclickAgent.onPageStart("LoginActivity");
//	    MobclickAgent.onResume(this);       //统计时长
    }

    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("LoginActivity");
//	    MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(socketInfoReceiver);
    }

    BroadcastReceiver socketInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("LoginActivity","onReceive");
            String msg = intent.getStringExtra("socket_info");
            if(!TextUtils.isEmpty(msg)){
                socketInfoText.setText(msg);
            }

        }
    };

    private class RefreshData{
        public String newToken;
    }

    private void refreshAndLogin(){

        String tvId = sharedPreferences.getString("DeviceId","");
        if(TextUtils.isEmpty(tvId)){
            tvId = getDeviceInfo(this);
        }
        final String id = tvId;

        final RefreshData refreshData = new RefreshData();
        Observable.just(id).observeOn(Schedulers.io()).map(new Function<String, RefreshData>() {
            @Override
            public RefreshData apply(final String id) throws Exception {
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
                                          sharedPreferences.edit().putString("UserToken",data.getString("UserToken"));
                                          AppConfig.UserToken = data.getString("UserToken");
                                          refreshData.newToken = AppConfig.UserToken;
                                      }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                return refreshData;
            }

        }).doOnNext(new Consumer<RefreshData>() {
            @Override
            public void accept(RefreshData refreshData) throws Exception {
                Log.e("expired","step two");
                if(!TextUtils.isEmpty(refreshData.newToken)){
                    //
                    doLogin();
                }
            }
        }).subscribe();
//        Observable.just()
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


}
