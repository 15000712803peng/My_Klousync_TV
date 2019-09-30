package com.ub.service.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.dialog.plugin.SingleCallActivity2;
import com.kloudsync.techexcel2.info.Customer;
import com.kloudsync.techexcel2.start.LoginActivity;
import com.kloudsync.techexcel2.start.QrCodeActivity;
import com.kloudsync.techexcel2.tool.Md5Tool;
import com.ub.techexcel.bean.NotifyBean;
import com.ub.techexcel.tools.SpliteSocket;
import com.ub.techexcel.tools.Tools;

import org.chromium.base.ThreadUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;

/**
 * Created by wang on 2017/9/1.
 * //    private String uri = "ws://123.127.97.142:9733/MeetingServer/websocket";
 * //    private String uri = "ws://ub.servicewise.net.cn:8080/MeetingServer/websocket";
 * //    private String uri = "wss://pt.techexcel.com:8443/MeetingServer/websocket";
 * //    private String uri = "ws://pt.techexcel.com:8080/MeetingServer/websocket";
 * //    private String uri = "wss://peertime.cn:8443/MeetingServer/websocket";
 * //    private String uri = "ws://wss.peertime.cn:8080/MeetingServer/websocket";
 * //    uri = "ws://wss.peertime.cn:8080/MeetingServer/websocket";
 * uri = "wss://wss.peertime.cn:8443/MeetingServer/websocket";
 */

public class SocketService extends Service implements KloudWebClientManager.OnMessageArrivedListener {
    private String uri;
    private WebSocketClient mWebSocketClient;
    private Timer timer;
    KloudWebClientManager kloudWebClientManager;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        AppConfig.UserToken = sharedPreferences.getString("UserToken", null);
        AppConfig.DEVICE_ID = sharedPreferences.getString("DeviceId", null);
        try {
            kloudWebClientManager = KloudWebClientManager.getDefault(this, new URI(AppConfig.COURSE_SOCKET + "/" + AppConfig.UserToken + "/" + 3 + "/" + AppConfig.DEVICE_ID));
            kloudWebClientManager.setOnMessageArrivedListener(this);
            kloudWebClientManager.connect();
            kloudWebClientManager.startHeartBeat();
            AppConfig.webSocketClient = kloudWebClientManager.getKloudWebClient();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 获得socket返回的action
     */
    private String getRetCodeByReturnData2(String str, String returnData) {
        if (!TextUtils.isEmpty(returnData)) {
            try {
                JSONObject jsonObject = new JSONObject(returnData);
                if (jsonObject.has(str)) {
                    return jsonObject.getString(str) + "";
                } else {
                    return "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    boolean flag;

    @Override
    public void onDestroy() {
        if (kloudWebClientManager != null) {
            kloudWebClientManager.release();
            kloudWebClientManager = null;
        }
        super.onDestroy();
    }

    /**
     * 登录判读是否已读
     *
     * @param meetingId
     */
    private void notifyleave(String meetingId) {
        String meetid = "";
        boolean status;
        if (!TextUtils.isEmpty(meetingId)) {
            int i = meetingId.indexOf("&");
            meetid = meetingId.substring(0, i);
            status = meetingId.substring(i + 1).equals("0") ? false : true;
        } else {
            return;
        }
        boolean isExist = false;
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (AppConfig.progressCourse.get(i).getMeetingId().equals(meetid)) {
                AppConfig.progressCourse.get(i).setStatus(status);
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            NotifyBean notifyBean = new NotifyBean();
            notifyBean.setMeetingId(meetid);
            notifyBean.setStatus(status);
            AppConfig.progressCourse.add(notifyBean);
        }
        Intent intent = new Intent();
        intent.setAction(getResources().getString(R.string.Receive_Course));
        sendBroadcast(intent);
    }


    private void notifyend(String meetingId) {
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (AppConfig.progressCourse.get(i).getMeetingId().equals(meetingId)) {
                AppConfig.progressCourse.remove(i);
                Intent removeintent = new Intent();
                removeintent.setAction(getResources().getString(R.string.Receive_Course));
                sendBroadcast(removeintent);
                break;
            }
        }
    }

    @Override
    public void onMessage(String message) {
        String msg = Tools.getFromBase64(message);
        Log.e("socket服务器返回结果", msg);
        String actionString = getRetCodeByReturnData2("action", msg);
        if (TextUtils.isEmpty(actionString)) {
            return;
        }
        switch (actionString) {
            case "LOGIN":
                handleLoginMessage(msg);
                break;
            case "REMOVE_JOIN_MEETING_NOTICE":
                handleRemoveJoinMeetingMessage(msg);
                break;
            case "UPDATE_TO_JOIN_MEETING_READ_STATUS":
                handleUpdateMeetingStatusMessage(msg);
                break;
            case "END_MEETING":
                handleEndMeetingMessage(msg);
                break;
            case "BIND_TV":
                handleBindTVMessage(msg);
                break;
            case "BIND_TV_JOIN_MEETING":
                handleBindTVJoinMeetingMessage(msg);
                break;
            case "BIND_TV_LEAVE_MEETING":
                break;
            case "SEND_MESSAGE":
                handleSendMessage(msg);
                break;
            case "HELLO":
                handleHeartMessage(msg);
                break;
            case "DISABLE_TV_FOLLOW":
                handleDisableTvFollow(msg);
                break;
            case "ENABLE_TV_FOLLOW":
                 handleEnableTvFollow(msg);
                break;
            case "ENABLE_FOLLOW_ON_DOC_MODE":
                handleEnableFollowOnDocMode(msg);
                break;
            case "LEAVE_MEETING":
                break;

        }

        Intent intent = new Intent();
        intent.setAction("com.cn.socket");
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }



    private void handleLoginMessage(String msg) {
        String tojoinmeeting = getRetCodeByReturnData2("toJoinMeeting", msg);
        String retCode = getRetCodeByReturnData2("retCode", msg);
        if (!retCode.equals("1")) {
            final String error = getRetCodeByReturnData2("ErrorMessage", msg);
        }
        if (!TextUtils.isEmpty(tojoinmeeting)) {
            String[] ss = tojoinmeeting.split(",");
            for (String s : ss) {
                notifyleave(s);
            }
        }
        if (sharedPreferences != null) {
            AppConfig.UUID = sharedPreferences.getString("uuid", null);
            AppConfig.UserName = sharedPreferences.getString("Name", null);
            AppConfig.UserID = sharedPreferences.getInt("UserID", 0) + "";
        }
    }

    private void handleRemoveJoinMeetingMessage(String msg) {
        String meetingids = getRetCodeByReturnData2("meetingIds", msg);
        String[] meetingArray = meetingids.split(",");
        for (String s : meetingArray) {
            for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
                if (AppConfig.progressCourse.get(i).getMeetingId().equals(s)) {
                    AppConfig.progressCourse.remove(i);
                    i--;
                }
            }
        }
        Intent intent = new Intent();
        intent.setAction(getResources().getString(R.string.Receive_Course));
        sendBroadcast(intent);
    }

    private void followUser(final String meetingId, final int type) {
        Log.e("SocketService","follow user");
        Intent intent = new Intent(getBaseContext(), NotifyActivity.class);
        intent.putExtra("meetingId", meetingId);
        intent.putExtra("enter", true);
        intent.putExtra("type", type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

    private void handleUpdateMeetingStatusMessage(String msg) {
        String meetingids = getRetCodeByReturnData2("meetingIds", msg);
        String[] meetingid = meetingids.split(",");
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            NotifyBean notifyBean = AppConfig.progressCourse.get(i);
            for (String ss : meetingid) {
                if (notifyBean.getMeetingId().equals(ss)) {
                    notifyBean.setStatus(true);
                    break;
                }
            }
        }
        Intent intent = new Intent();
        intent.setAction(getResources().getString(R.string.Receive_Course));
        sendBroadcast(intent);
    }

    private void handleEndMeetingMessage(String msg) {
        notifyend(getRetCodeByReturnData2("meetingId", msg));
    }

    String meetingId = null;
    int type = -1;
    private void handleBindTVJoinMeetingMessage(final String msg) {
         meetingId = null;
         type = -1;
        try {
            JSONObject jsonObject = new JSONObject(msg);
            JSONObject d = jsonObject.getJSONObject("retData");
            if(d.has("meetingId")){
                meetingId = d.getString("meetingId");
            }
            if (d.has("type")) {
                type = d.getInt("type");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent ii = new Intent();
        ii.setAction("com.cn.socket");
        ii.putExtra("message", "START_JOIN_MEETING");
        ii.putExtra("meeting_id",meetingId);
        ii.putExtra("meeting_type",type);
        sendBroadcast(ii);
//        if (WatchCourseActivity3.isMeetingStarted) {
//            //TV已经在会议里面
//            Log.e("-------------","meeting started return");
//            return;
//        }
        delayOption(new Runnable() {
            @Override
            public void run() {
                if (WatchCourseActivity3.watch3instance || WatchCourseActivity2.watch2instance || SyncRoomActivity.watchSyncroomInstance) {

                } else {
                    if (!TextUtils.isEmpty(AppConfig.BINDUSERID)) {


                        followUser(meetingId,type);

                    }

                }
            }
        });


    }

    private void handleEnableTvFollow(String msg) {
        if (WatchCourseActivity3.isMeetingStarted) {
            //TV已经在会议里面
            return;
        }
        if (WatchCourseActivity3.watch3instance || WatchCourseActivity2.watch2instance || SyncRoomActivity.watchSyncroomInstance) {
//            sendLeaveMeetingMessage();
            Log.e("SocketService","refresh meeting in handleEnableTvFollow");
            refreshMeeting(msg);
        }else {
            if (!TextUtils.isEmpty(AppConfig.BINDUSERID)) {
                String meetingId = null;
                int type = -1;
                int deviceType = -1;
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    JSONObject d = jsonObject.getJSONObject("retData");
                    if(d.has("meetingId")){
                        meetingId = d.getString("meetingId");
                    }
                    if (d.has("type")) {
                        type = d.getInt("type");
                    }
                    if(d.has("tvOwnerDeviceType")){
                        deviceType = d.getInt("tvOwnerDeviceType");
                    }
                    sendBindStatusMessage(deviceType);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                followUser(meetingId,type);

            }
        }


    }

    private void refreshMeeting(String msg) {

        if (!TextUtils.isEmpty(AppConfig.BINDUSERID)) {
            String meetingId = null;
            int type = 0;
            try {
                JSONObject jsonObject = new JSONObject(msg);
                JSONObject d = jsonObject.getJSONObject("retData");
                if(d.has("meetingId")){
                    meetingId = d.getString("meetingId");
                }
                if (d.has("type")) {
                    type = d.getInt("type");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent refreshIntent = new Intent("com.kloudsync.techexcel2.refresh_meeting");
            refreshIntent.putExtra("meeting_id",meetingId);
            refreshIntent.putExtra("meeting_type",type);
            sendBroadcast(refreshIntent);
        }
    }

    private void handleBindTVMessage(String msg) {

        String bindUserId = null;
        String meetingId = null;
        String attachmentId = null;
        int deviceType = -1;
        int type = 0;
        try {
            JSONObject xx = new JSONObject(msg);
            JSONObject d = xx.getJSONObject("retData");
            bindUserId = d.getString("bindUserId");
            updateBindUser(bindUserId);
            if (d.has("meetingId")) {
                meetingId = d.getString("meetingId");
            }
            if (d.has("attachmentId")) {
                attachmentId = d.getString("attachmentId");
            }
            if (d.has("type")) {
                type = d.getInt("type");
            }
            if(d.has("tvOwnerDeviceType")){
                deviceType = d.getInt("tvOwnerDeviceType");
            }
            if (attachmentId != null && attachmentId.equals("0")) {
                if (meetingId.contains(",")) {
                    attachmentId = meetingId.substring(0, meetingId.lastIndexOf(","));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("SocketService", "json exception:" + e);
        }
        Log.e("abc", meetingId + ":   " + bindUserId + ":  " + attachmentId);


        Intent intent = new Intent(QrCodeActivity.instance, NotifyActivity.class);
        if (!TextUtils.isEmpty(meetingId)) {
            intent.putExtra("meetingId", meetingId);
            intent.putExtra("attachmentId", attachmentId);
            intent.putExtra("enter", true);
            intent.putExtra("type", type);
            intent.putExtra("device_type",deviceType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("SocketService", "start activity");

            startActivity(intent);
            if (QrCodeActivity.instance != null && !QrCodeActivity.instance.isFinishing()) {
                QrCodeActivity.instance.ShowGone();
            }
        } else {
            intent.putExtra("enter", false);
            intent.putExtra("meetingId", meetingId);
            intent.putExtra("attachmentId", attachmentId);
            intent.putExtra("device_type",deviceType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("SocketService", "start activity");
            startActivity(intent);
            if (QrCodeActivity.instance != null && !QrCodeActivity.instance.isFinishing()) {
                QrCodeActivity.instance.ShowGone();
            }

        }
    }

    private void handleSendMessage(String msg) {
        String d = getRetCodeByReturnData2("data", msg);
        try {
            final JSONObject jsonObject = new JSONObject(Tools.getFromBase64(d));
            if (jsonObject.getInt("actionType") == 1 || jsonObject.getInt("actionType") == 3) { // 旧的课程 邀请学生上课
                if ((!WatchCourseActivity2.watch2instance) && (!WatchCourseActivity3.watch3instance)) {
                    Intent intent = new Intent(SocketService.this, AlertDialogActivity.class);
                    intent.putExtra("jsonObject", jsonObject.toString());
                    intent.putExtra("isNewCourse", 0);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else if (jsonObject.getInt("actionType") == 10) { // 新的课程
                if ((!WatchCourseActivity2.watch2instance) && (!WatchCourseActivity3.watch3instance)) {
                    Intent intent = new Intent(SocketService.this, AlertDialogActivity.class);
                    intent.putExtra("jsonObject", jsonObject.toString());
                    intent.putExtra("isNewCourse", 1);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else if (jsonObject.getInt("actionType") == 4) { //语音
                Customer cus = new Customer();
                cus.setUserID(jsonObject.getString("sourceUserId"));
                cus.setName(jsonObject.getString("sourceUserName"));
                cus.setUrl(jsonObject.getString("sourceAvatarUrl"));
                String action = RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO2;
                Intent intent = new Intent(action);
                intent.putExtra("Customer", cus);
                intent.putExtra("callAction", RongCallAction.ACTION_INCOMING_CALL.getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage(getApplication().getPackageName());
                startActivity(intent);
            } else if (jsonObject.getInt("actionType") == 5) { //视频音
                Customer cus = new Customer();
                cus.setUserID(jsonObject.getString("sourceUserId"));
                cus.setName(jsonObject.getString("sourceUserName"));
                cus.setUrl(jsonObject.getString("sourceAvatarUrl"));
                String action = RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO2;
                Intent intent = new Intent(action);
                intent.putExtra("Customer", cus);
                intent.putExtra("callAction", RongCallAction.ACTION_INCOMING_CALL.getName());
                intent.putExtra("checkPermissions", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage(getApplication().getPackageName());
                startActivity(intent);
            } else if (jsonObject.getInt("actionType") == 6 && jsonObject.getInt("mediaType") == 1) {
                Intent intent = new Intent();
                intent.setAction(getString(R.string.Receive_Spectator));
                sendBroadcast(intent);
            } else if (jsonObject.getInt("actionType") == 6 && jsonObject.getInt("mediaType") == 2) { // 视频音

            } else if (jsonObject.getInt("actionType") == 7) {
                if (SingleCallActivity2.instance != null) {
                    SingleCallActivity2.instance.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleHeartMessage(String msg) {
        String action = Tools.getFromBase64(getRetCodeByReturnData2("data", msg));
        Log.e("SocketService", "heart beart response" + action);
//        if (WatchCourseActivity3.isMeetingStarted) {
//            //TV已经在会议里面
//            return;
//        }

        if (!TextUtils.isEmpty(action)) {
            try {
                String meetingId = null;
                int meetingType = 0;
                JSONObject actionJson = new JSONObject(action);

                if(actionJson.has("tvBindUserId")){
                    updateBindUser(actionJson.getInt("tvBindUserId")+"");
                }else {
                    updateBindUser("");
                }

                if (actionJson.has("hasOwner")) {
                    //绑定了某台设备，或者web
                    boolean hasOwner = actionJson.getBoolean("hasOwner");

                    if (hasOwner) {
                        if (actionJson.has("tvOwnerMeetingId")) {
                            meetingId = actionJson.getString("tvOwnerMeetingId");
                        }else {
                            meetingId = "0";
                        }
                        if (actionJson.has("tvOwnerMeetingType")) {
                            meetingType = actionJson.getInt("tvOwnerMeetingType");
                        }

                        if(actionJson.has("tvOwnerDeviceType")){
                            sendBindStatusMessage(actionJson.getInt("tvOwnerDeviceType"));
                        }

                        if (WatchCourseActivity3.watch3instance || WatchCourseActivity2.watch2instance || SyncRoomActivity.watchSyncroomInstance) {
                            //已经在Meeting或者Document,或者SyncRoom里面
                            Log.e("BeartHeart","inside,and heart beat meeting id:" + meetingId);
                            if(TextUtils.isEmpty(meetingId) || meetingId.equals("0")){
                                sendEndMeetingMessage();
                                return;
                            }
                        } else {
                            //不在，跳进去
                            if (!TextUtils.isEmpty(AppConfig.BINDUSERID)) {
                                followUser(meetingId,meetingType);
                                Log.e("BeartHeart","enter again");
                            }
                        }
                    }else {
                        sendBindStatusMessage(-1);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            sendBindStatusMessage(-1);
        }

    }

    private void handleDisableTvFollow(String msg){
        sendLeaveMeetingMessage();
    }

    private void handleEnableFollowOnDocMode(String msg){

    }

    private void sendLeaveMeetingMessage(){
        JSONObject leaveJson = new JSONObject();
        try {
            leaveJson.put("action","BIND_TV_LEAVE_MEETING");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setAction("com.cn.socket");
        intent.putExtra("message", leaveJson.toString());
        sendBroadcast(intent);
        Log.e("send bind stutus","-1,in send leave meeting message");
        sendBindStatusMessage(-1);
    }

    private void sendEndMeetingMessage(){
        Log.e("SocketService","sendEndMeetingMessage");
        Intent intent = new Intent();
        intent.setAction("com.cn.socket");
        intent.putExtra("message", "END_MEETING");
        sendBroadcast(intent);
    }


    private void sendBindStatusMessage(int deviceType){
        Intent intent = new Intent("com.kloudsync.techexcel2.BIND_STATUS");
        intent.putExtra("device_type",deviceType);
        sendBroadcast(intent);
    }

    private void sendSocketInfo(String info){
        Log.e("KloudWebClientManager","sendSocketInfo");
        Intent intent = new Intent("socket_info");
        intent.putExtra("socket_info",info);
       sendBroadcast(intent);
    }

    private void updateBindUser(String bindUser){
        if(sharedPreferences !=  null){
            sharedPreferences.edit().putString("tv_bind_user",bindUser).commit();
            Log.e("tv_bind_user",sharedPreferences.getString("tv_bind_user",""));
            AppConfig.BINDUSERID = bindUser;
        }
    }

    private void delayOption(Runnable option){
        new Handler(getMainLooper()).postDelayed(option,700);
    }


}
