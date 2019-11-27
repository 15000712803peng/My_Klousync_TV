package com.ub.service.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.widget.Toast;

import com.baidu.platform.comapi.map.A;
import com.google.gson.Gson;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.app.App;
import com.kloudsync.techexcel2.config.AppConfig;

import com.kloudsync.techexcel2.help.ApiTask;
import com.kloudsync.techexcel2.info.Uploadao;
import com.kloudsync.techexcel2.meeting.model.MeetingFile;
import com.kloudsync.techexcel2.meeting.model.MeetingPage;
import com.kloudsync.techexcel2.meeting.model.MeetingSocketMessage;
import com.kloudsync.techexcel2.tool.TvFileCache;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
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


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


import Decoder.BASE64Encoder;
import io.agora.openlive.ui.BaseActivity;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class WatchCourseActivity4 extends BaseActivity implements KloudWebClientManager.OnMessageArrivedListener {

    private App app;
    private MeetingData currentMeetingData;
    public static boolean watch3instance = false;
    TvFileCache cache;
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

    @SuppressLint("WrongConstant")
    private void initData() {
        app = (App) getApplication();
        cache = TvFileCache.getInstance(this);
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
        boolean createSuccess = FileUtils.createFileSaveDir(this);
        if (!createSuccess) {
            Toast.makeText(getApplicationContext(), "文件系统异常，打开失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
        public int currentFilePageIndex;
        public MeetingFile currentMeetingFile;
        public String notifyWebFileUrl;

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
        if (KloudWebClientManager.getDefault() != null) {
            KloudWebClientManager.getDefault().removeMessageArrivedLinster(this);
        }

        DownloadUtil.get().cancelAll();
        EventBus.getDefault().unregister(this);
    }


    private void handleSocketMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        String _message = Tools.getFromBase64(message);

        String action = Tools.getDataInMessage("action", _message);
        if (!action.equals("HELLO")) {
            Log.e("handleSocketMessage", "onMessage:" + _message + ",thread:" + Thread.currentThread());
        }
        switch (action) {
            case "JOIN_MEETING":
                handleSocketMessage_JoinMeeting(_message);
                break;
            case "END_MEETING":
                handleSocketMessage_BindUserEndMeeting(_message);
                break;
            case "BROADCAST_FRAME":
                String codeData = Tools.getDataInMessage("data", _message);
                if (!TextUtils.isEmpty(codeData)) {
                    String data = Tools.getFromBase64(codeData);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(data);
                            Log.e("BROADCAST_FRAME", "data:" + jsonObject);
                            if (jsonObject.has("type")) {
                                handleFrameMessage(jsonObject,jsonObject.getInt("type"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                break;
        }

    }

    private void handleFrameMessage(final JSONObject message ,int type) {
        Log.e("handleFrameMessage", "message:" + message);
        if(type == 2){
            // 翻页 ，检查file  是否下载成功
            try {
                int page = message.getInt("page");
                if(page < 1){
                    return;
                }
//
//                MeetingPage meetingPage =currentMeetingData.currentMeetingFile.getMeetingPages().get(page - 1);
//                Log.e("handleFrameMessage","meeting page:" + meetingPage);
//                if(cache.containFile(meetingPage.getPageUrl())){
//                    Log.e("handleFrameMessage","file path:"+ meetingPage.getSavedLocalPath());
//                    if(new File(meetingPage.getSavedLocalPath()).exists()){
//
//                    }
//
//                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("WebView_Load", "javascript:PlayActionByTxt('" + message + "','" + 1 + "')");
//                        webView.clearFormData();
//                        webView.setDrawingCacheEnabled(false);
//                        webView.clearCache(false);
                        webView.load("javascript:PlayActionByTxt('" + message + "','" + 1 + "')", null);

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("WebView_Load", "javascript:PlayActionByTxt('" + message + "','" + 1 + "')");
                    webView.load("javascript:PlayActionByTxt('" + message + "','" + 1 + "')", null);

                }
            });
        }



    }


    MeetingSocketMessage meetingSocketMessage;

    @SuppressLint({"LongLogTag", "WrongConstant"})
    private void handleSocketMessage_JoinMeeting(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            Gson gson = new Gson();
            meetingSocketMessage = gson.fromJson(jsonObject.getJSONObject("retData").toString(), MeetingSocketMessage.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("meetingSocketMessage", "message:" + meetingSocketMessage);
        if (meetingSocketMessage != null) {
            currentMeetingData.lessionId = meetingSocketMessage.getLessonId();
            String currentDocumentPage = meetingSocketMessage.getCurrentDocumentPage();
            if (!TextUtils.isEmpty(currentDocumentPage)) {
                String[] datas = currentDocumentPage.split("-");
                currentMeetingData.currentFileId = Integer.parseInt(datas[0]);
                currentMeetingData.currentFilePageIndex = (int) (Float.parseFloat(datas[1]));
            }
        }

        Log.e("currentMeetingData", currentMeetingData + "");

        if (TextUtils.isEmpty(currentMeetingData.lessionId) || currentMeetingData.lessionId.equals("0")) {
//            sendJoinMeetingMessage();
            Toast.makeText(this, "lession is:" + currentMeetingData.lessionId, Toast.LENGTH_SHORT).show();
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
    public void preLoadFileFunction(final String url, final int pageNum, final boolean showLoading) {
        Log.e("JavascriptInterface", "preLoadFileFunction-->url:" + url + ",pageNum:" + pageNum + ",showLoading:" + showLoading + ",thread:" + Thread.currentThread());
        ApiTask.start(ThreadManager.getManager(), new Runnable() {
            @Override
            public void run() {
                MeetingPage page = currentMeetingData.currentMeetingFile.getMeetingPages().get(pageNum - 1);
                safeDownloadFile(page,url,pageNum, true);
            }
        });
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
    public void afterChangePageFunction(final int pageNum, int type) {
        // 1:play,2:showdocument,3:next,4:prev,5:topage,  0 :未知
        Log.e("JavascriptInterface", "afterChangePageFunction-->pageNum:" + pageNum + ",type:" + type + ",thread:" + Thread.currentThread());
        currentMeetingData.currentFilePageIndex = pageNum;


    }


    private void requestDocumentByLessionId() {
        String url = "";
        if (currentMeetingData.meetingType == TYPE_DOCUMENT) {
            url = AppConfig.URL_PUBLIC + "Lesson/Item?lessonID=" + currentMeetingData.lessionId;
        } else if (currentMeetingData.meetingType == TYPE_SYNCROOM) {
            url = AppConfig.URL_PUBLIC + "TopicAttachment/List?topicID=" + currentMeetingData.lessionId;
        }
        asyncGetDocumentDetail(url);
    }

    List<MeetingFile> meetingFiles;

    private void asyncGetDocumentDetail(final String url) {

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject response = ConnectService.getIncidentbyHttpGet(url);
                if (response == null) {

                } else {
                    try {
                        JSONObject retData = response.getJSONObject("RetData");
                        Log.e("asyncGetDocumentDetail", "ret data:" + retData);
                        if (retData != null) {
                            JSONArray array = retData.getJSONArray("AttachmentList");
                            if (array != null) {
                                Gson gson = new Gson();
                                Log.e("array", "array:" + array);
                                meetingFiles = new ArrayList<>();
                                for (int i = 0; i < array.length(); ++i) {
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    MeetingFile file = gson.fromJson(jsonObject.toString(), MeetingFile.class);
                                    String attachmentUrl = file.getAttachmentUrl();
                                    String preUrl = "";
                                    String endUrl = "";
                                    if (!TextUtils.isEmpty(attachmentUrl)) {
                                        int index = attachmentUrl.lastIndexOf("<");
                                        int index2 = attachmentUrl.lastIndexOf(">");
                                        if (index > 0) {
                                            preUrl = attachmentUrl.substring(0, index);
                                        }
                                        if (index2 > 0) {
                                            endUrl = attachmentUrl.substring(index2 + 1, attachmentUrl.length());
                                        }
                                    }

                                    List<MeetingPage> meetingPages = new ArrayList<>();
                                    for (int j = 0; j < file.getPageCount(); ++j) {
                                        String pageUrl = "";
                                        MeetingPage meetingPage = new MeetingPage();
                                        if (TextUtils.isEmpty(preUrl)) {
                                            meetingPage.setPageUrl(pageUrl);
                                        } else {
                                            meetingPage.setPageUrl(preUrl + (j + 1) + endUrl);
                                        }
                                        meetingPages.add(meetingPage);
                                    }
                                    file.setMeetingPages(meetingPages);
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
    private void sendSocketMessage(String message) {
        if (AppConfig.webSocketClient == null || AppConfig.webSocketClient.getReadyState() != WebSocket.READYSTATE.OPEN) {
            Toast.makeText(getApplicationContext(), "web socket status exception", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("sendSocketMessage", "message:" + message);
        AppConfig.webSocketClient.send(message);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveFiles(List<MeetingFile> files) {
        Log.e("receiverFile", "files:" + files);
        meetingFiles = files;
        if (meetingFiles != null && meetingFiles.size() > 0) {
            int index = meetingFiles.indexOf(new MeetingFile(currentMeetingData.currentFileId));
            if (index < 0) {
                index = 0;
            }
            currentMeetingData.currentMeetingFile = meetingFiles.get(index);
            downLoadAndShowCurrentPage();
//            downLoadPages(currentMeetingData.currentMeetingFile.getMeetingPages());
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showPdf(MeetingPage page) {
        Log.e("showFilePage", "page:" + page);

        if (page != null && !TextUtils.isEmpty(page.getShowingPath())) {
            currentMeetingData.notifyWebFileUrl = page.getShowingPath();
            if (webView != null) {
                Log.e("WebView_Load", "javascript:ShowPDF('" + page.getShowingPath() + "',"
                        + currentMeetingData.currentFilePageIndex + ",0,'" + currentMeetingData.currentMeetingFile.getAttachmentID() + "')");
                webView.load("javascript:ShowPDF('" + page.getShowingPath() + "'," + currentMeetingData.currentFilePageIndex + ",0," + currentMeetingData.currentMeetingFile.getAttachmentID() + ")", null);
                webView.load("javascript:Record()", null);
                downLoadPages(currentMeetingData.currentMeetingFile.getMeetingPages());
            }

        }
    }


    private void downLoadPages(final List<MeetingPage> pages) {

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                for(int i = 0 ; i < pages.size(); ++i){
                    safeDownloadFile(pages.get(i),currentMeetingData.notifyWebFileUrl,i + 1,true);
                }
            }
        }).start(ThreadManager.getManager());
    }


    private void downLoadAndShowCurrentPage() {
        Observable.just(currentMeetingData.currentMeetingFile).observeOn(Schedulers.io()).map(new Function<MeetingFile, Object>() {
            @Override
            public Object apply(MeetingFile meetingFile) throws Exception {
                int pageIndex = 1;
                if (currentMeetingData.currentFilePageIndex == 0) {
                    pageIndex = 1;
                } else if (currentMeetingData.currentFilePageIndex > 0) {
                    pageIndex = currentMeetingData.currentFilePageIndex;
                }
                String pageUrl = meetingFile.getMeetingPages().get(pageIndex - 1).getPageUrl();
                queryAndDownLoadCurrentPageToShow(pageUrl, true);
                return pageUrl;
            }
        }).subscribe();
    }



    public String encoderByMd5(String str) {
        try {
            //确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            //加密后的字符串
            String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
            return newstr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Uploadao parseResponse(final String jsonstring) {
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject data = returnjson.getJSONObject("Data");

                JSONObject bucket = data.getJSONObject("Bucket");
                Uploadao uploadao = new Uploadao();
                uploadao.setServiceProviderId(bucket.getInt("ServiceProviderId"));
                uploadao.setRegionName(bucket.getString("RegionName"));
                uploadao.setBucketName(bucket.getString("BucketName"));
                return uploadao;
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }


    @SuppressLint({"WrongConstant", "LongLogTag"})
    private void queryAndDownLoadCurrentPageToShow(final String pageUrl, final boolean needRedownload) {
        boolean createSuccess = FileUtils.createFileSaveDir(this);
        if (!createSuccess) {
            Toast.makeText(getApplicationContext(), "文件系统异常，打开失败", Toast.LENGTH_SHORT).show();
            sendLeaveMeetingMessage();
            finish();
            return;
        }

        MeetingPage page = cache.getPageCache(pageUrl);
        Log.e("queryAndDownLoadCurrentPageToShow", "get cach page:" + page + "--> url:" + pageUrl);
        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
                && !TextUtils.isEmpty(page.getSavedLocalPath()) && !TextUtils.isEmpty(page.getShowingPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                EventBus.getDefault().post(page);
                return;
            } else {
                cache.removeFile(pageUrl);

            }

        }

        JSONObject queryDocumentResult = ServiceInterfaceTools.getinstance().syncQueryDocument(AppConfig.URL_LIVEDOC + "queryDocument",
                currentMeetingData.currentMeetingFile.getNewPath());
        if (queryDocumentResult != null) {
            Uploadao uploadao = parseResponse(queryDocumentResult.toString());
            String fileName = pageUrl.substring(pageUrl.lastIndexOf("/") + 1);
            String part = "";
            if (1 == uploadao.getServiceProviderId()) {
                part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + currentMeetingData.currentMeetingFile.getNewPath()
                        + "/" + fileName;
            } else if (2 == uploadao.getServiceProviderId()) {
                part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + currentMeetingData.currentMeetingFile.getNewPath() + "/" + fileName;
            }

            String pathLocalPath = FileUtils.getBaseDir() +
                    currentMeetingData.meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_" + currentMeetingData.currentFilePageIndex +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            final String showUrl = FileUtils.getBaseDir() +
                    currentMeetingData.meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_<" + currentMeetingData.currentMeetingFile.getPageCount() + ">" +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            int pageIndex = 1;
            if (currentMeetingData.currentFilePageIndex == 0) {
                pageIndex = 1;
            } else if (currentMeetingData.currentFilePageIndex > 0) {
                pageIndex = currentMeetingData.currentFilePageIndex;
            }

            Log.e("queryAndDownLoadCurrentPageToShow", "showUrl:" + showUrl);

            final MeetingPage meetingPage = currentMeetingData.currentMeetingFile.getMeetingPages().
                    get(pageIndex - 1);
            meetingPage.setSavedLocalPath(pathLocalPath);

            Log.e("queryAndDownLoadCurrentPageToShow", "meeting data:" + currentMeetingData);

            //保存在本地的地址

            DownloadUtil.get().download(pageUrl, pathLocalPath, new DownloadUtil.OnDownloadListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDownloadSuccess(int arg0) {
                    meetingPage.setShowingPath(showUrl);
                    Log.e("queryAndDownLoadCurrentPageToShow", "onDownloadSuccess:" + meetingPage);
                    cache.cacheFile(meetingPage);
                    EventBus.getDefault().post(meetingPage);
                }

                @Override
                public void onDownloading(final int progress) {

                }

                @Override
                public void onDownloadFailed() {

                    Log.e("queryAndDownLoadCurrentPageToShow", "onDownloadFailed:" + meetingPage);
                    if (needRedownload) {
                        queryAndDownLoadCurrentPageToShow(pageUrl, false);
                    }
                }
            });
        }

    }

    private void notifyWebFilePrepared(final String url, final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("WebView_Load", "javascript:AfterDownloadFile('" + url + "', " + index + ")");
                if(webView == null){
                    return;
                }
                webView.load("javascript:AfterDownloadFile('" + url + "', " + index + ")", null);

            }
        });
    }



    private void safeDownloadFile(final MeetingPage page, final String notifyUrl, final int index, final boolean needRedownload){

        Log.e("safeDownloadFile","start down load:" + page);

        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
                && !TextUtils.isEmpty(page.getSavedLocalPath()) && !TextUtils.isEmpty(page.getShowingPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                Log.e("safeDownloadFile","have down load and saved:" + page);
                notifyWebFilePrepared(notifyUrl,index);
                return;
            } else {
                //清楚缓存
                cache.removeFile(page.getPageUrl());
            }

        }

        String pathLocalPath = notifyUrl.substring(0,notifyUrl.lastIndexOf("<")) +
                index + notifyUrl.substring(notifyUrl.lastIndexOf("."));
        page.setSavedLocalPath(pathLocalPath);

        final ThreadLocal<MeetingPage> meetingPageLocal = new ThreadLocal<>();
        meetingPageLocal.set(page);
//        DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(meetingPageLocal.get(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {
                meetingPageLocal.get().setShowingPath(notifyUrl);
                Log.e("safeDownloadFile", "onDownloadSuccess:" + meetingPageLocal.get());
                cache.cacheFile(meetingPageLocal.get());
                notifyWebFilePrepared(notifyUrl, index);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + meetingPageLocal.get());
                if(needRedownload){
                    safeDownloadFile(page, notifyUrl, index, false);
                }
            }
        });
    }


}

