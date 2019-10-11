package com.ub.service.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.app.App;
import com.kloudsync.techexcel2.config.AppConfig;

import com.kloudsync.techexcel2.help.ApiTask;
import com.kloudsync.techexcel2.meeting.model.MeetingFile;
import com.kloudsync.techexcel2.meeting.model.MeetingSocketMessage;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.Tools;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;


import java.util.ArrayList;
import java.util.List;

import io.agora.openlive.ui.BaseActivity;

public class WatchCourseActivity4 extends BaseActivity implements KloudWebClientManager.OnMessageArrivedListener {

    private App app;
    private MeetingData currentMeetingData;
    public static boolean watch3instance = false;
    // ----- views
    XWalkView webView;

    // -----

    private static final int TYPE_DOCUMENT = 2;
    private static final int TYPE_SYNCROOM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watchcourse4);
        initData();
        initView();
        sendJoinMeetingMessage();

    }

    private void initData() {

        app = (App) getApplication();
        EventBus.getDefault().register(this);
        KloudWebClientManager.getDefault().addOnMessageArrivedListener(this);
        @SuppressLint("WrongConstant") PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "TEST").acquire();
        //----- prepare meeting data;
        currentMeetingData = new MeetingData();
        currentMeetingData.meetingId = getIntent().getStringExtra("meetingId");
        currentMeetingData.meetingType = getIntent().getIntExtra("meeting_type", 2);
        if (currentMeetingData.meetingId.contains(",")) {
            currentMeetingData.lessionId = currentMeetingData.meetingId.substring(0, currentMeetingData.meetingId.lastIndexOf(","));
        }
        currentMeetingData.isStarted = getIntent().getBooleanExtra("is_meeting", false);

    }

    private void initView() {

        //----------
        webView = (XWalkView) findViewById(R.id.webview);
        webView.setZOrderOnTop(false);
        webView.addJavascriptInterface(WatchCourseActivity4.this, "AnalyticsWebInterface");
        webView.getSettings().setDomStorageEnabled(true);
        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        webView.load("file:///android_asset/index.html", null);

        //--------

    }


    @SuppressLint("WrongConstant")
    private void sendJoinMeetingMessage() {
        if (TextUtils.isEmpty(currentMeetingData.meetingId) || currentMeetingData.meetingId.equals("0")) {
            Toast.makeText(getApplicationContext(), "meeting is null ,so closed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            JSONObject message = new JSONObject();
            message.put("action", "JOIN_MEETING");
            message.put("sessionId", AppConfig.UserToken);
            message.put("meetingId", currentMeetingData.meetingId);
            message.put("meetingPassword", "");
            message.put("clientVersion", "v20140605.0");
            message.put("role", 2);
            message.put("type", currentMeetingData.meetingType);
            message.put("lessonId", currentMeetingData.lessionId);
            message.put("isInstantMeeting", 1);
            Log.e("sendJoinMeetingMessage", "message:" + message.toString());
            sendSocketMessage(message.toString());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "join meeting message data error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onMessage(String message) {
        handleSocketMessage(message);
    }

    private static class MeetingData {
        public String meetingId;
        public int meetingType;
        public boolean isStarted;
        public String lessionId;
        public String bindUserName;
        public int currentFileId;
        public String currentFilePageIndex;
        public MeetingFile currentMeetingFile;

        @Override
        public String toString() {
            return "MeetingData{" +
                    "meetingId='" + meetingId + '\'' +
                    ", meetingType=" + meetingType +
                    ", isStarted=" + isStarted +
                    ", lessionId='" + lessionId + '\'' +
                    ", bindUserName='" + bindUserName + '\'' +
                    ", currentFileId=" + currentFileId +
                    ", currentFilePageIndex='" + currentFilePageIndex + '\'' +
                    ", currentMeetingFile=" + currentMeetingFile +
                    '}';
        }
    }

    @Override
    protected void initUIandEvent() {

    }

    @Override
    protected void deInitUIandEvent() {
        release();
    }

    private void release() {
        if (webView != null) {
            webView.removeAllViews();
            webView.onDestroy();
            webView = null;
        }
        if(KloudWebClientManager.getDefault() != null){
            KloudWebClientManager.getDefault().removeMessageArrivedLinster(this);
        }
        EventBus.getDefault().unregister(this);
    }

    private void handleSocketMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        String _message = Tools.getFromBase64(message);
        Log.e("handleSocketMessage", "onMessage:" + _message);
        String action = Tools.getActionInMessage("action", _message);
        switch (action) {
            case "JOIN_MEETING":
                handleSocketMessage_JoinMeeting(_message);
                break;

            case "END_MEETING":
                handleSocketMessage_BindUserEndMeeting(_message);
                break;
        }

    }

    MeetingSocketMessage meetingSocketMessage;

    @SuppressLint("LongLogTag")
    private void handleSocketMessage_JoinMeeting(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            Gson gson = new Gson();
            meetingSocketMessage = gson.fromJson(jsonObject.getJSONObject("retData").toString(), MeetingSocketMessage.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("meetingSocketMessage","message:" + meetingSocketMessage);
        if (meetingSocketMessage != null) {
            currentMeetingData.lessionId = meetingSocketMessage.getLessonId();
            String currentDocumentPage = meetingSocketMessage.getCurrentDocumentPage();
            if(!TextUtils.isEmpty(currentDocumentPage)){
                String[] datas = currentDocumentPage.split("-");
                currentMeetingData.currentFileId = Integer.parseInt(datas[0]);
                currentMeetingData.currentFilePageIndex = datas[1];
            }

        }

        Log.e("currentMeetingData",currentMeetingData+"");

        if (TextUtils.isEmpty(currentMeetingData.lessionId) || currentMeetingData.lessionId.equals("0")) {
//            sendJoinMeetingMessage();
            return;
        }

        requestDocumentByLessionId();

    }

    private void handleSocketMessage_BindUserEndMeeting(String message) {
        sendLeaveMeetingMessage();
        finish();
    }

    private void sendLeaveMeetingMessage() {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "LEAVE_MEETING");
            message.put("sessionId", AppConfig.UserToken);
            sendSocketMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // --------xwalk interface ---------

    @org.xwalk.core.JavascriptInterface
    public void reflect(String result) {
        Log.e("JavascriptInterface", "reflect-->result:" + result + ",thread:" + Thread.currentThread());

    }

    /**
     * pdf 加载完成
     */
    @org.xwalk.core.JavascriptInterface
    public void afterLoadFileFunction() {
        Log.e("JavascriptInterface", "afterLoadFileFunction-->" + ",thread:" + Thread.currentThread());

    }

    /**
     * 切换文档
     *
     * @param diff
     */
    @org.xwalk.core.JavascriptInterface
    public void autoChangeFileFunction(int diff) {
        Log.e("JavascriptInterface", "autoChangeFileFunction-->diff:" + diff + ",thread:" + Thread.currentThread());

    }

    // 播放视频
    @org.xwalk.core.JavascriptInterface
    public void videoPlayFunction(final int vid) {
        Log.e("JavascriptInterface", "videoPlayFunction-->vid:" + vid + ",thread:" + Thread.currentThread());
    }

    //打开
    @org.xwalk.core.JavascriptInterface
    public void videoSelectFunction(String s) {
        Log.e("JavascriptInterface", "videoSelectFunction-->s:" + s + ",thread:" + Thread.currentThread());

    }

    // 录制
    @org.xwalk.core.JavascriptInterface
    public void audioSyncFunction(final int id, final int isRecording) {
        Log.e("JavascriptInterface", "audioSyncFunction-->id:" + id + ",isRecording:" + isRecording + ",thread:" + Thread.currentThread());

    }

    /**
     * 加载PDF
     */
    @org.xwalk.core.JavascriptInterface
    public void afterLoadPageFunction() {
        Log.e("JavascriptInterface", "afterLoadPageFunction-->" + ",thread:" + Thread.currentThread());

    }

    @org.xwalk.core.JavascriptInterface
    public void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        Log.e("JavascriptInterface", "preLoadFileFunction-->url:" + url + ",currentpageNum:" + currentpageNum + ",showLoading:" + showLoading + ",thread:" + Thread.currentThread());

    }

    @org.xwalk.core.JavascriptInterface
    public void showErrorFunction(final String error) {
        Log.e("JavascriptInterface", "showErrorFunction-->error:" + error + ",thread:" + Thread.currentThread());

    }

    /**
     * @param pageid
     */
    @org.xwalk.core.JavascriptInterface
    public void addBlankPageFunction(final String pageid) {
        Log.e("JavascriptInterface", "addBlankPageFunction-->pageid:" + pageid + ",thread:" + Thread.currentThread());

    }

    /**
     * 每一页加载完后，金宝会调用这个方法
     */
    @org.xwalk.core.JavascriptInterface
    public void afterChangePageFunction(final String pageNum, int type) {
        // 1:play,2:showdocument,3:next,4:prev,5:topage,  0 :未知
        Log.e("JavascriptInterface", "afterChangePageFunction-->pageNum:" + pageNum + ",thread:" + Thread.currentThread());

    }


    private void requestDocumentByLessionId() {
        String url = "";
        if(currentMeetingData.meetingType == TYPE_DOCUMENT){
            url = AppConfig.URL_PUBLIC + "Lesson/Item?lessonID=" + currentMeetingData.lessionId;
        }else if(currentMeetingData.meetingType == TYPE_SYNCROOM){
             url = AppConfig.URL_PUBLIC + "TopicAttachment/List?topicID=" + currentMeetingData.lessionId;
        }

        asyncGetDocumentDetail(url);



    }

    List<MeetingFile> meetingFiles;

    private void asyncGetDocumentDetail(final String url){

        new ApiTask(new Runnable() {
            @Override
            public void run() {


                JSONObject response = ConnectService.getIncidentbyHttpGet(url);
                if(response == null){

                }else {
                    try {
                        JSONObject retData = response.getJSONObject("RetData");
                        Log.e("asyncGetDocumentDetail","ret data:" + retData);
                        if(retData != null){
                            JSONArray array = retData.getJSONArray("AttachmentList");
                            if(array != null){
                                Gson gson = new Gson();
                                Log.e("array","array:" + array);
                                meetingFiles = new ArrayList<>();
                                for(int i = 0 ; i < array.length();++i){
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    MeetingFile file = gson.fromJson(jsonObject.toString(),MeetingFile.class);
                                    for(int j = 0 ; j < file.getPageCount(); ++j){
                                        file.getAttachmentUrl();
                                    }
                                    meetingFiles.add(file);
                                }
                                EventBus.getDefault().post(meetingFiles);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start(ThreadManager.getManager());
    }

    @SuppressLint("WrongConstant")
    private void sendSocketMessage(String message){
        if (AppConfig.webSocketClient == null || AppConfig.webSocketClient.getReadyState() != WebSocket.READYSTATE.OPEN) {
            Toast.makeText(getApplicationContext(), "web socket status exception", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("sendSocketMessage","message:" + message);
        AppConfig.webSocketClient.send(message);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverFile(List<MeetingFile> files){
        Log.e("receiverFile","files:" + files);
        meetingFiles = files;
        if(meetingFiles != null && meetingFiles.size() > 0){
            int index = meetingFiles.indexOf(new MeetingFile(currentMeetingData.currentFileId));
            if(index < 0){
                index = 0;
            }
            currentMeetingData.currentMeetingFile = meetingFiles.get(index);
        }
    }






}

