package com.kloudsync.techexcel2.start;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.tool.DensityUtil;
import com.kloudsync.techexcel2.tool.Md5Tool;
import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.ub.service.activity.KloudWebClientManager;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Hashtable;
import java.util.UUID;

public class QrCodeActivity extends AppCompatActivity implements View.OnClickListener {
    //hahahalalalala
    private ImageView img_qr;
    private TextView tv_login;
    private RelativeLayout rl_login;
    private static FrameLayout fl_main;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    Intent service;
    public static QrCodeActivity instance = null;
    private boolean flag_lg;
    private TextView refreshQrcodeText;
    private RelativeLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        instance = this;
        StartWBService();
        findView();
        initView();
        initUpdate();

    }

    private void StartWBService() {

        service = new Intent(getApplicationContext(), SocketService.class);
        startService(service);
    }

    private void findView() {
        img_qr = (ImageView) findViewById(R.id.img_qr);
        tv_login = (TextView) findViewById(R.id.tv_login);
        rl_login = (RelativeLayout) findViewById(R.id.rl_login);
        refreshLayout = (RelativeLayout)findViewById(R.id.rl_refresh);
        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        refreshQrcodeText = (TextView) findViewById(R.id.txt_refresh_qrcode);
    }

    private void initView() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String uuid = sharedPreferences.getString("uuid", null);
        String tvToken = sharedPreferences.getString("UserToken",null);
        Log.e("QrcodeActivity","tvToken:" + tvToken);
        Bitmap bitmap = null;
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        width = width > height ? height / 2 : width / 2;
        width -= DensityUtil.dp2px(this, 60);
        try {
            //+ "###Kloudsync_TV" + tvToken
            bitmap = createQRCode(uuid + "###Kloudsync_TV" + tvToken, width);
            Log.e("QrcodeActivity","QRCode_info:" + uuid + "###Kloudsync_TV" + tvToken);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        img_qr.setImageBitmap(bitmap);
        tv_login.setOnClickListener(this);
        refreshQrcodeText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                GoToLogin();
                break;
            case R.id.txt_refresh_qrcode:
                refresh();
                break;
            default:
                break;
        }
    }

    UUID uuid;
    private JSONObject format(String name) {
        uuid = UUID.randomUUID();
        Log.e("uuid", uuid + "");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("TvID", uuid);
            jsonObject.put("TvName", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


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
                editor.putString("uuid", uuid + "");
                editor.commit();
                KloudWebClientManager.getDefault(getApplicationContext(),new URI(AppConfig.COURSE_SOCKET + "/" + AppConfig.UserToken + "/" + 3 + "/" +  AppConfig.DEVICE_ID)).reconnect();
                initView();
                Toast.makeText(getApplicationContext(),"refresh success",Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "refresh failed", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConfig.SUCCESS:
                    String result = (String) msg.obj;
                    mJsonTV(result);
                    break;
                case AppConfig.FAILED:
//                    result = (String) msg.obj;
//                    if(TextUtils.isEmpty(result)){
//                        result = "network error ,refresh failed";
//                    }
                    Toast.makeText(getApplicationContext(),"refresh failed",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    private void refresh(){
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String name = sharedPreferences.getString("Name","");
        if(TextUtils.isEmpty(name)){
            Toast.makeText(getApplicationContext(),"please register again",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,TvRegisterActivity.class));
            finish();
        }else {
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
                                msg.what = AppConfig.FAILED;
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

    }



    private void GoToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xFFFFFFFF;

    public Bitmap createQRCode(String str, int widthAndHeight) throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                } else {
                    pixels[y * width + x] = WHITE;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static void ShowGone() {
//        fl_main.setVisibility(View.VISIBLE);
    }

    boolean isRefresh = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
            flag_lg = true;
            isRefresh = false;
            rl_login.setBackgroundResource(R.drawable.bg_input);
            refreshLayout.setBackground(null);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            flag_lg = false;
            isRefresh = false;
            rl_login.setBackground(null);
            refreshLayout.setBackground(null);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (flag_lg) {
                GoToLogin();
            }else if(isRefresh){
                refresh();
            }
            return false;
        }else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            flag_lg = false;
            isRefresh = true;
            refreshLayout.setBackgroundResource(R.drawable.bg_input);
            rl_login.setBackground(null);
            return false;
        }if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
            flag_lg = true;
            isRefresh = false;
            refreshLayout.setBackground(null);
            rl_login.setBackgroundResource(R.drawable.bg_input);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KillService();
        KillFile();
    }

    private void KillFile() {
        FileUtils fileUtils = new FileUtils(getApplicationContext());
        fileUtils.deleteFile();
        fileUtils.deleteFile2();
    }

    private void KillService() {
        stopService(service);
    }

    ProgressDialog progressDialog;
    private void initUpdate() {
        // TODO Auto-generated method stub
        new PgyUpdateManager.Builder()
                .setForced(false)                //设置是否强制更新,非自定义回调更新接口此方法有用
                .setUserCanRetry(false)         //失败后是否提示重新下载，非自定义下载 apk 回调此方法有用
                .setDeleteHistroyApk(false)     // 检查更新前是否删除本地历史 Apk
                .setUpdateManagerListener(new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        //没有更新是回调此方法
                        Log.e("pgyer", "there is no new version");
                    }

                    @Override
                    public void onUpdateAvailable(final AppBean appBean) {
                        //有更新是回调此方法
                        Log.e("pgyer", "there is new version can update"
                                + "new versionCode is " + appBean.getVersionCode());

                        //调用以下方法，DownloadFileListener 才有效；如果完全使用自己的下载方法，不需要设置DownloadFileListener
//
                        new AlertDialog.Builder(QrCodeActivity.this)
                                .setTitle(getResources().getString(R.string.update))
                                .setMessage(getResources().getString(R.string.update_message))
                                .setPositiveButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(
                                        getResources().getString(R.string.update),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                PgyUpdateManager.downLoadApk(appBean.getDownloadURL());
                                            }
                                        }).show();
                    }

                    @Override
                    public void checkUpdateFailed(Exception e) {
                        //更新检测失败回调
                        Log.e("pgyer", "check update failed ", e);

                    }
                })

                //注意 ：下载方法调用 PgyUpdateManager.downLoadApk(appBean.getDownloadURL()); 此回调才有效
                .setDownloadFileListener(new DownloadFileListener() {   // 使用蒲公英提供的下载方法，这个接口才有效。
                    @Override
                    public void downloadFailed() {
                        //下载失败
                        Log.e("pgyer", "download apk failed");
                        if (progressDialog != null) {
                            progressDialog.cancel();
                            progressDialog = null;
                        }

                    }

                    @Override
                    public void downloadSuccessful(Uri uri) {
                        Log.e("pgyer", "download apk failed");
                        if (progressDialog != null) {
                            progressDialog.cancel();
                            progressDialog = null;
                        }
                        PgyUpdateManager.installApk(uri);  // 使用蒲公英提供的安装方法提示用户 安装apk

                    }

                    @Override
                    public void onProgressUpdate(Integer... integers) {
                        Log.e("pgyer", "update download apk progress : " + integers[0]);
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(QrCodeActivity.this);
                            progressDialog.setProgressStyle(1);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage(getResources().getString(R.string.downloading));
                            progressDialog.show();
                        } else {
                            if (integers != null && integers.length > 0) {
                                progressDialog.setProgress(integers[0]);
                            }

                        }
                    }
                }).register();
    }


}
