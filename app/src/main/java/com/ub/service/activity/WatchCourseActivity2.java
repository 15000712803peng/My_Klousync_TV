package com.ub.service.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.app.App;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.dialog.AddFileFromFavoriteDialog;
import com.kloudsync.techexcel2.help.PopAlbums;
import com.kloudsync.techexcel2.help.Popupdate;
import com.kloudsync.techexcel2.help.Popupdate2;
import com.kloudsync.techexcel2.httpgetimage.ImageLoader;
import com.kloudsync.techexcel2.info.Customer;
import com.kloudsync.techexcel2.info.Favorite;
import com.kloudsync.techexcel2.info.Uploadao;
import com.kloudsync.techexcel2.service.ConnectService;
import com.kloudsync.techexcel2.start.LoginGet;
import com.kloudsync.techexcel2.tool.Md5Tool;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.PreferencesCookieStore;
import com.ub.teacher.gesture.BrightnessHelper;
import com.ub.teacher.gesture.ShowChangeLayout;
import com.ub.teacher.gesture.VideoGestureRelativeLayout;
import com.ub.techexcel.adapter.BigAgoraAdapter;
import com.ub.techexcel.adapter.ChatAdapter;
import com.ub.techexcel.adapter.DefaultAgoraAdapter;
import com.ub.techexcel.adapter.MyRecyclerAdapter;
import com.ub.techexcel.adapter.MyRecyclerAdapter2;
import com.ub.techexcel.adapter.RightAgoraAdapter;
import com.ub.techexcel.adapter.SocketLogAdapter;
import com.ub.techexcel.adapter.TeacherRecyclerAdapter;
import com.ub.techexcel.bean.AgoraBean;
import com.ub.techexcel.bean.AudioActionBean;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.NotifyBean;
import com.ub.techexcel.bean.PageActionBean;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.DetchedPopup;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.FavoritePopup;
import com.ub.techexcel.tools.FavoriteVideoPopup;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.MoreactionPopup;
import com.ub.techexcel.tools.NotificationPopup;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.SpliteSocket;
import com.ub.techexcel.tools.Tools;
import com.ub.techexcel.tools.VideoPopup;
import com.ub.techexcel.tools.WebCamPopup;
import com.ub.techexcel.tools.YinxiangCreatePopup;
import com.ub.techexcel.tools.YinxiangEditPopup;
import com.ub.techexcel.tools.YinxiangPopup;
import com.ub.techexcel.view.CustomVideoView;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import Decoder.BASE64Encoder;
import io.agora.openlive.model.AGEventHandler;
import io.agora.openlive.ui.BaseActivity;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;


/**
 * Created by wang on 2017/6/16.
 */
public class WatchCourseActivity2 extends BaseActivity implements View.OnClickListener, AGEventHandler, VideoGestureRelativeLayout.VideoGestureListener,AddFileFromFavoriteDialog.OnFavoriteDocSelectedListener {

    private String targetUrl;
    private String newPath;
    public PopupWindow mPopupWindow1;
    public PopupWindow documentPopupWindow;
    public PopupWindow chatPopupWindow;

    public PopupWindow mPopupWindow2;
    public PopupWindow mPopupWindow4;
    private XWalkView wv_show;
    //视频手势
    private CustomVideoView videoView;
    private VideoGestureRelativeLayout videoGestureRelativeLayout;
    private ShowChangeLayout showChangeLayout;
    private AudioManager mAudioManager;
    private int maxVolume = 0;
    private int oldVolume = 0;
    private int newProgress = 0, oldProgress = 0;
    private BrightnessHelper mBrightnessHelper;
    private float brightness = 1;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;

    private ImageView closeVideo;
    private ProgressBar mProgressBar;
    private String studentid;
    private String teacherid;
    private String meetingId;
    private int isInstantMeeting; // 1 新的课程  0 旧的课程
    private boolean isStartCourse = false;
    public static WatchCourseActivity2 instance = null;

    private int identity = 0;
    /**
     * 学生或老师
     */
    private WebSocketClient mWebSocketClient; // 连接客户端
    public ImageLoader imageLoader;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private Customer teacherCustomer = new Customer();
    /**
     * 当前为presenter的student
     */
    private Customer studentCustomer = new Customer();
    /**
     * isPresenter 为true  表示此时老师为presenter   不可点击老师  只能点击学生
     * isPresenter 为false  表示此时学生为presenter  不可点击学生 只能点击老师
     */
    private String currentPresenterId = "";
    private RecyclerView auditorrecycleview; //旁听者的集合
    private RecyclerView teacherrecycler; //老师学生的集合
    private MyRecyclerAdapter myRecyclerAdapter;//旁听者adapter
    private TeacherRecyclerAdapter teacherRecyclerAdapter;
    /**
     * meeting中旁听者的信息
     */
    private List<Customer> auditorList = new ArrayList<>();
    private List<Customer> teacorstudentList = new ArrayList<>(); //老师学生的集合

    private List<String> invateList = new ArrayList<>(); // 其余在meeting中的获得新加入invatelist的旁听者
    private List<String> invitedUserIds = new ArrayList<>();// join meeting  返回的未加入meeting的invate

    private String leaveUserid;
    private RelativeLayout leavell;
    private RelativeLayout endll;
    private RelativeLayout refresh_notify_2;
    private RelativeLayout startll;
    private RelativeLayout bindidll;
    private RelativeLayout joinvideo;
    private RelativeLayout callMeLater;
    private RelativeLayout scanll;
    private RelativeLayout testdebug;

    private RecyclerView documentrecycleview;
    private MyRecyclerAdapter2 myRecyclerAdapter2;
    private LineItem lineItem;
    private List<LineItem> documentList = new ArrayList<>();

    private String currentAttachmentId;
    private String currentItemId;
    private String currentAttachmentPage;
    private String currentBlankPageNumber;

    private File cache;
    private List<Favorite> myFavoriteList = new ArrayList<>();
    private List<Favorite> myFavoriteVideoList = new ArrayList<>();

    private ImageView menu;
    private ImageView command_active;
    private LinearLayout activte_linearlayout;
    private LinearLayout menu_linearlayout;
    private RelativeLayout displayAudience, displayFile, displaychat, displaywebcam, displayVideo, setting, setting2, displayrecord;
    public static boolean watch2instance = false;
    private TextView endtextview;
    private TextView prompt;
    private String currentMode = "0";
    private int videoStatus = 0;
    private String videoFileId = "0";
    private String currentMaxVideoUserId = "";
    private String changeNumber = "0";
    private MyHandler handler;
    private int screenWidth;
    private RelativeLayout selectwebcam, selectconnection, select240, select360, select480, select720, peertimebase, kloudcall, external;
    private TextView right3bnt;
    private LinearLayout right1;
    private LinearLayout settingllback, settingllback2;
    private LinearLayout right2;
    private LinearLayout right3;
    private LinearLayout leftview, leftview2;

    private RelativeLayout bigRecyler, middleRecyler, smallRecyler;
    private ImageView bigimage, middleimage, smallimage;
    private LinearLayout llpre;
    private TextView leavepre;

    private final int LINE_PEERTIME = 0;
    private final int LINE_KLOUDPHONE = 2;
    private final int LINE_EXTERNOAUDIO = 4;
    private int currentLine = LINE_PEERTIME;
    private String zoomInfo;
    private List<LineItem> videoList = new ArrayList<>();
    private boolean isLoadPdfAgain = true;
    private RelativeLayout filedownprogress;
    private ProgressBar fileprogress;
    private TextView progressbartv;

    @Override
    public void onFavoriteDocSelected(String docId) {

    }

    private static class MyHandler extends Handler {
        private WeakReference<WatchCourseActivity2> activity2WeakReference;

        public MyHandler(WatchCourseActivity2 activity2) {
            activity2WeakReference = new WeakReference<>(activity2);
        }

        @Override
        public void handleMessage(Message msg) {
            final WatchCourseActivity2 activity = activity2WeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0x1109:
                        if (activity.isWebViewLoadFinish) {
                            final String m = (String) msg.obj;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject(m);
                                        String actionType;
                                        try {
                                            Log.e("PlayActionByTxt---", jsonObject.toString());
                                            actionType = jsonObject.getString("actionType");
                                            Log.e("PlayActionByTxt---", actionType + "");
                                        } catch (JSONException e) {
                                            actionType = "";
                                            e.printStackTrace();
                                        }
                                        if (actionType.equals("8")) {
                                            String attachmentid = jsonObject.getString("itemId");
                                            String pagenumber = jsonObject.getString("pageNumber");
                                            Log.e("PlayActionByTxt---", activity.currentAttachmentId + "  " + attachmentid);
                                            if (activity.currentAttachmentId.equals(attachmentid)) {  //同一文档
                                                if (pagenumber.equals(activity.currentAttachmentPage)) {
                                                } else {
                                                    Log.e("PlayActionByTxt", "不同页");
                                                    activity.currentAttachmentPage = pagenumber;
                                                    String changpage = "{\"type\":2,\"page\":" + activity.currentAttachmentPage + "}";
                                                    activity.wv_show.load("javascript:PlayActionByTxt('" + changpage + "','" + 1 + "')", null);
                                                }
                                            } else {
                                                for (int i = 0; i < activity.documentList.size(); i++) {
                                                    LineItem lineItem2 = activity.documentList.get(i);
                                                    if ((attachmentid).equals(lineItem2.getAttachmentID())) {
                                                        activity.lineItem = lineItem2;
                                                        activity.currentAttachmentPage = pagenumber;
                                                        break;
                                                    }
                                                }
                                                Log.e("PlayActionByTxt", "不同文档");
                                                activity.changedocumentlabel(activity.lineItem);
                                            }
                                        } else {
                                            Log.e("PlayActionByTxt", "正常");
                                            int type = 34;  //鼠标移动接受的消息
                                            try {
                                                type = jsonObject.getInt("type");
                                            } catch (JSONException e) {
                                                type = 34;
                                                e.printStackTrace();
                                            }
                                            if (type != 34) {
                                                activity.wv_show.load("javascript:PlayActionByTxt('" + m + "','" + 1 + "')", null);
                                                activity.zoomInfo = null;
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        break;
                    case 0x1120:
                        final String m = (String) msg.obj;
                        Log.e("--------9", activity.currentAttachmentId + "   " + activity.currentAttachmentPage + "   " + activity.currentMode + "  " + m);
                        if (!m.equals(activity.currentMode)) {
                            activity.currentMode = m;
                            activity.switchMode();
                        }
                        break;
                    case AppConfig.SUCCESS: // join meeting  返回
                        activity.mProgressBar.setVisibility(View.GONE);
                        activity.getAllData((List<Customer>) msg.obj);
                        for (int i = 0; i < activity.documentList.size(); i++) {
                            LineItem lineItem2 = activity.documentList.get(i);
                            if ((activity.currentAttachmentId).equals(lineItem2.getAttachmentID())) {
                                String url = lineItem2.getUrl();
                                if ((url.equals(activity.targetUrl))) {
                                    return;
                                }
                            }
                        }
                        activity.getServiceDetail();
                        activity.getVideoList();
                        break;
                    case 0x3102:
                        activity.mProgressBar.setVisibility(View.GONE);
                        activity.getAllData((List<Customer>) msg.obj);
                        break;
                    case AppConfig.SUCCESS2:
                        activity.mProgressBar.setVisibility(View.GONE);
                        activity.getAllData((List<Customer>) msg.obj);
                        break;
                    case 0x1110:  // 收到改变presenter的socket
                        for (int i = 0; i < activity.teacorstudentList.size(); i++) {
                            Customer customer = activity.teacorstudentList.get(i);
                            if (customer.getUserID().equals(activity.currentPresenterId)) {
                                customer.setPresenter(true);
                                activity.studentCustomer = customer;
                            } else {
                                customer.setPresenter(false);
                            }
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (activity.currentPresenterId.equals(AppConfig.UserID)) {
                                    Log.e("---------", activity.currentPresenterId.equals(AppConfig.UserID) + "  dd  " + (activity.wv_show == null));
                                    activity.wv_show.load("javascript:ShowToolbar(" + true + ")", null);
                                    activity.wv_show.load("javascript:Record()", null);
                                } else {
                                    Log.e("---------", activity.currentPresenterId.equals(AppConfig.UserID) + "  dd  " + (activity.wv_show == null));
                                    activity.wv_show.load("javascript:ShowToolbar(" + true + ")", null);
                                    activity.wv_show.load("javascript:StopRecord()", null);
                                }
                            }
                        });
                        activity.teacherRecyclerAdapter.Update(activity.teacorstudentList);
                        activity.videoPopuP.setPresenter(activity.identity,
                                activity.currentPresenterId,
                                activity.studentCustomer, activity.teacherCustomer);
                        if (activity.isHavePresenter()) {
                            activity.setting.setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.videoline).setVisibility(View.VISIBLE);
                        } else {
                            activity.setting.setVisibility(View.GONE);
                            activity.findViewById(R.id.videoline).setVisibility(View.GONE);
                        }
                        break;
                    case 0x1111: //离开
                        activity.changeAllVisible(activity.leaveUserid);
                        break;
                    case 0x1121:  //invate  to  meeting
                        List<String> ll = (List<String>) msg.obj;
                        activity.getDetailInfo(ll, 1);
                        break;
                    case 0x1203: //学生或其他旁听者  收到的  旁听者信息 invate  to  meeting
                        activity.setDefaultAuditor((List<Customer>) msg.obj);
                        break;
                    case 0x1204: //join  meeting  返回的未进入meeting的人
                        activity.setDefaultAuditor2((List<Customer>) msg.obj);
                        break;
                    case 0x1301: // 提升旁听者为学生
//                        activity.promoteAuditor((String) (msg.obj));
                        break;
                    case 0x1205:
                        activity.lineItem = (LineItem) msg.obj;
                        activity.changedocumentlabel(activity.lineItem);
                        break;
                    case 0x4010:
                        final String ddd = (String) msg.obj;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (activity.wv_show != null) {
                                    if (!TextUtil.isEmpty(ddd)) {
                                        activity.wv_show.load("javascript:PlayActionByArray(" + ddd + "," + 0 + ")", null);
                                    }
                                }
                            }
                        });
                        break;
                    case 0x2101:   // addBlankPageFunction
                        String pageid = (String) msg.obj;
                        for (int i = 0; i < activity.documentList.size(); i++) {
                            LineItem lineItem = activity.documentList.get(i);
                            if ((lineItem.getAttachmentID()).equals(activity.currentAttachmentId)) {
                                String blankPageNumber = lineItem.getBlankPageNumber();
                                if (TextUtils.isEmpty(blankPageNumber)) {
                                    activity.currentBlankPageNumber = pageid;
                                } else {
                                    activity.currentBlankPageNumber = blankPageNumber + "," + pageid;
                                }
                                lineItem.setBlankPageNumber(activity.currentBlankPageNumber);
                                break;
                            }
                        }
                        break;
                    case 0x3121:
                        int lineId = (int) msg.obj;
                        switch (lineId) {
                            case 0:    //打开声网
                                activity.showAgora();
                                break;
                            case 2:    //打开kc
                                activity.showKloudCall();
                                break;
                            case 4:
                                activity.showExterNoAudio();
                                break;
                        }
                        break;
                    case 0x4001:
                        String prepareMode = (String) msg.obj;
                        int pre = Integer.parseInt(prepareMode);
                        if (pre == 1) {
                            activity.llpre.setVisibility(View.VISIBLE);
                        } else if (pre == 0) {
                            activity.llpre.setVisibility(View.GONE);
                        }
                        break;
                    case 0x4113:
                        String retcode = (String) msg.obj;
                        if (retcode.equals("-2002")) {
                            Toast.makeText(activity, "用户没有kloud call账号", Toast.LENGTH_LONG).show();
                        } else if (retcode.equals("-2301")) {
                            Toast.makeText(activity, "用户kloud call账号余额不足", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 0x1190:
                        activity.detectPopwindow((int) msg.obj);
                        break;
                    case 0x2105:  //join meeting 走进来的
                        activity.videoPopuP.setVideoList(activity.videoList);
                        if (activity.currentMode.equals(4 + "")) {
                            activity.startOrPauseVideo(activity.videoStatus, 0.0f, activity.videoFileId, "", 0);
                        }
                        break;
                    case 0x6002:
//                        int type = (int) msg.obj;
//                        if (type == 1) {  //pdf
//                            activity.getServiceDetail();
//                        } else if (type == 2) {  // video
//                            activity.getVideoList();
//                        }
                        break;
                    case 0x6115: //copy file finish
                        String s = (String) msg.obj;
                        activity.wv_show.load(s, null);
                        activity.wv_show.load("javascript:Record()", null);
                        break;
                    case AppConfig.FAILED:
                        String result = (String) msg.obj;
                        break;
                    case 0x7001:
                        List<AudioActionBean> audioActionBeanList = (List<AudioActionBean>) msg.obj;
                        if (audioActionBeanList.size() > 0) {
                            activity.newAudioActionTime(audioActionBeanList);
                        }
                        break;

                }
                super.handleMessage(msg);
            }
        }
    }

    private Timer actionTimer;

    private void newAudioActionTime(final List<AudioActionBean> audioActionBeanList) {
        Log.e("newAudioActionTime", audioActionBeanList.size() + "");
        if (actionTimer != null) {
            actionTimer.cancel();
            actionTimer = null;
        }
        actionTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // 1   拿当前音乐播放的进度
                if (audioActionBeanList.size() == 0) {
                    actionTimer.cancel();
                }
                for (int i1 = 0; i1 < audioActionBeanList.size(); i1++) {
                    AudioActionBean audioActionBean = audioActionBeanList.get(i1);
                    //  2 当前音乐播放的进度> action 时间
                    Log.e("newAudioActionTime", tttime + "   " + audioActionBean.getTime());
                    if (tttime >= audioActionBean.getTime()) {
                        // 3 划线
                        String data = audioActionBean.getData();
                        Message msg3 = Message.obtain();
                        msg3.obj = data;
                        msg3.what = 0x1109;
                        handler.sendMessage(msg3);
                        audioActionBeanList.remove(i1);
                        i1--;
                    } else {
                        break;
                    }
                }
            }
        };
        actionTimer.schedule(timerTask, 0, 500);
    }

    /**
     * 打开声网
     */
    private void showAgora() {
        if (startLessonPopup != null) {  // kcloud call  选择弹框
            startLessonPopup.dismiss();
        }
        currentLine = LINE_PEERTIME;
        callMeLater.setVisibility(View.GONE);
        initListen(true);
        icon_command_mic_enabel.setEnabled(true);
        openshengwang(2);
    }

    /**
     * 打开kloudcall
     */
    private void showKloudCall() {
        if (webCamPopuP != null) {
            webCamPopuP.dismiss();
        }
        callMeOrLater(identity, AppConfig.Mobile);
        callMeLater.setVisibility(View.VISIBLE);
        currentLine = LINE_KLOUDPHONE;
        initListen(false);
        icon_command_mic_enabel.setEnabled(false);
    }

    /**
     * 打开kloudcall
     */
    private void showKloudCall2() {
        callMeLater.setVisibility(View.VISIBLE);
        currentLine = LINE_KLOUDPHONE;
        initListen(false);
        icon_command_mic_enabel.setEnabled(false);
    }

    /**
     * 打开no audio 模式
     */
    private void showExterNoAudio() {
        currentLine = LINE_EXTERNOAUDIO;
        callMeLater.setVisibility(View.GONE);
        initListen(false);
        icon_command_mic_enabel.setEnabled(false);
    }


    /**
     * 老师第一次join时，调一下这个方法，将当前文档ID发给鹏飞
     */
    private void updatecurrentdocumentid() {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", "UPDATE_CURRENT_DOCUMENT_ID");
            loginjson.put("sessionId", AppConfig.UserToken);
            loginjson.put("documentId", currentItemId);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    private void changedocumentlabel(LineItem lineItem) {
        Log.e("dddddd", documentList.size() + "");
        if (documentList.size() > 0) {
            if (lineItem == null) {
                lineItem = documentList.get(0);
                lineItem.setSelect(true);
            } else {
                for (int i = 0; i < documentList.size(); i++) {
                    LineItem lineItem1 = documentList.get(i);
                    if (lineItem.getItemId().equals(lineItem1.getItemId())) {
                        lineItem1.setSelect(true);
                        lineItem = lineItem1;
                    } else {
                        lineItem1.setSelect(false);
                    }
                }
            }
            if (myRecyclerAdapter2 != null) {
                myRecyclerAdapter2.notifyDataSetChanged();
            }
            currentAttachmentId = lineItem.getAttachmentID();
            Log.e("dddddd", currentAttachmentId + "");
            currentItemId = lineItem.getItemId();
            targetUrl = lineItem.getUrl();
            newPath = lineItem.getNewPath();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wv_show.load("file:///android_asset/index.html", null);
                }
            });
            if (isHavePresenter()) {
                notifySwitchDocumentSocket(lineItem, "1");
            }
        }

    }
    private void notifySwitchDocumentSocket(LineItem lineItem, String pagenumber) {
        JSONObject json = new JSONObject();
        try {
            json.put("actionType", 8);
            json.put("eventID", lineItem.getEventID());
            json.put("attachmentUrl", lineItem.getUrl());
            json.put("eventName", lineItem.getEventName());
            json.put("meetingID", meetingId);
            json.put("itemId", lineItem.getItemId());
            json.put("incidentID", meetingId);
            json.put("pageNumber", pagenumber);
            json.put("isH5", lineItem.isHtml5());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("dsasa 切换文档", " " + lineItem.getItemId() + json.toString());
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
        updatecurrentdocumentid(lineItem);
    }

    /**
     * 老师第一次join时，调一下这个方法，将当前文档ID发给鹏飞
     */
    private void updatecurrentdocumentid(LineItem lineItem) {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", "UPDATE_CURRENT_DOCUMENT_ID");
            loginjson.put("sessionId", AppConfig.UserToken);
            loginjson.put("documentId", lineItem.getItemId());
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * invate meeting  设置收到旁听者的信息
     *
     * @param list
     */
    private void setDefaultAuditor(List<Customer> list) {
        for (Customer c : list) {
            c.setEnterMeeting(false);
            c.setRole(1);
        }
        for (int i = 0; i < list.size(); i++) {
            Customer c1 = list.get(i);
            boolean isexist = false;
            for (int j = 0; j < teacorstudentList.size(); j++) {
                Customer c2 = teacorstudentList.get(j);
                if (c1.getUserID().equals(c2.getUserID())) {
                    c2.setEnterMeeting(false);
                    isexist = true;
                }
            }
            if (!isexist) {
                teacorstudentList.add(c1);
            }
        }
        teacherRecyclerAdapter.Update(teacorstudentList);
    }


    /**
     * join_meeting 设置为加入meeting 的旁听者
     *
     * @param list
     */
    private void setDefaultAuditor2(List<Customer> list) {
        for (Customer c : list) {
            c.setEnterMeeting(false);
            c.setRole(1);
            teacorstudentList.add(c);
        }
//        teacherRecyclerAdapter.notifyDataSetChanged();
        teacherRecyclerAdapter.Update(teacorstudentList);
    }

    /**
     * 改变
     *
     * @param
     */
    private void changeAllVisible(String leaveUserid) {
        //更新老师学生列表
        for (int j = 0; j < teacorstudentList.size(); j++) {
            Customer c = teacorstudentList.get(j);
            if (c.getUserID().equals(leaveUserid)) {
                teacorstudentList.remove(j);
                j--;
            }
        }
//        teacherRecyclerAdapter.notifyDataSetChanged();
        teacherRecyclerAdapter.Update(teacorstudentList);
        //更新auditorList
        for (int j = 0; j < auditorList.size(); j++) {
            Customer c = auditorList.get(j);
            if (c.getUserID().equals(leaveUserid)) {
                auditorList.remove(j);
                j--;
            }
        }
        myRecyclerAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watchcourse3);
        instance = this;
        Log.e("WatchCourseActivity2", "onCreate");
        WindowManager wm = (WindowManager)
                getSystemService(WINDOW_SERVICE);
        NotifyActivity.instance.ShowWhite();
        EventBus.getDefault().register(this);
        screenWidth = wm.getDefaultDisplay().getWidth();
        handler = new MyHandler(this);
        watch2instance = true;
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "TEST");
        wl.acquire();
        teacherid = getIntent().getStringExtra("teacherid");
        studentid = getIntent().getStringExtra("userid");
        identity = getIntent().getIntExtra("identity", 0);
        meetingId = getIntent().getStringExtra("meetingId");
        isStartCourse = getIntent().getBooleanExtra("isStartCourse", false);
        isInstantMeeting = getIntent().getIntExtra("isInstantMeeting", 0);
        Log.e("-------", targetUrl + "  " + "" + "  meetingId: " + meetingId + "  identity : " + identity + "    isInstantMeeting :" + isInstantMeeting);
        imageLoader = new ImageLoader(this);
        cache = new File(Environment.getExternalStorageDirectory(), "Image");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        SpliteSocket.init(getApplicationContext());
        initView();
        //初始化老师学生列表
        initDisplayAudienceList();
        //初始化文档列表
        initDocumentListAudienceList();
        //初始化会话弹窗
        initChatAudienceList();
        initScreenLock();
        //获得我的收藏列表
        initVideoPopup();
        openSaveVideoPopup();
        ((App) getApplication()).initWorkerThread();
        initUIandEvent();
        icon_back.setTag(false);

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.cn.getGroupbroadcastReceiver");
        registerReceiver(getGroupbroadcastReceiver, intentFilter2);

        //注册广播 接受socket信息
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn.socket");
        registerReceiver(broadcastReceiver, intentFilter);

        initdefault();

    }

    private void initScreenLock() {
        timer.schedule(task, 10000, 10000);
    }

    /**
     * 不同身份进入初始化操作
     */
    private void initdefault() {
        mWebSocketClient = AppConfig.webSocketClient;
        if (mWebSocketClient == null) {
            return;
        }
        if (mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN) {
            sendStringBySocket2("JOIN_MEETING", AppConfig.UserToken, "", meetingId, "", true, "v20140605.0", false, identity, isInstantMeeting);
        }
    }

    /**
     * 获取选中的旁听者
     */
    @Override
    protected void onResume() {
        if (AppConfig.isUpdateAuditor) {
            for (Customer c : AppConfig.auditorList) {
                c.setEnterMeeting(false);
            }
            for (Customer d : AppConfig.auditorList) {
                boolean isex = false;
                for (Customer c : teacorstudentList) {
                    if (d.getUserID().equals(c.getUserID())) {
                        isex = true;
                    }
                }
                if (!isex) {
                    teacorstudentList.add(d);
                }
            }
            teacherRecyclerAdapter.Update(teacorstudentList);
            AppConfig.isUpdateAuditor = false;
            invite_meeting(AppConfig.auditorList);
            AppConfig.auditorList.clear();
        }
        if (wv_show != null) {
            wv_show.resumeTimers();
            wv_show.onShow();
        }
        super.onResume();
    }


    /**
     * 邀请旁听者
     *
     * @param list
     */
    private void invite_meeting(List<Customer> list) {
        String meetings = "";
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                meetings += list.get(i).getUserID();
            } else {
                meetings += "," + list.get(i).getUserID();
            }
        }
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", "INVITE_TO_MEETING");
            loginjson.put("sessionId", AppConfig.UserToken);
            loginjson.put("meetingId", meetingId);
            loginjson.put("userIds", meetings);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        try {
            json.put("meetingID", meetingId);
            json.put("sourceID", teacherid);
            json.put("targetID", studentid);
            json.put("incidentID", "");
            json.put("roleType", "1");
            json.put("attachmentUrl", targetUrl);
            json.put("actionType", 3);
            json.put("isH5", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("send_mesage", meetings + "   " + json.toString());
        //邀请旁听者上课
        send_message("SEND_MESSAGE", AppConfig.UserToken, 1, meetings, Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }


    /**
     * 通知 放大缩小视频
     */
    private void changevideo(int i, String currentSessionID) {
        if (isHavePresenter()) {
            changeVideo1(i, currentSessionID);
        }
    }

    private void changeVideo1(int i, String currentSessionID) {
        JSONObject json = new JSONObject();
        try {
            json.put("videoMode", i);
            json.put("actionType", 9);
            if (i == 2) {
                json.put("currentSessionID", currentSessionID);
            } else {
                json.put("currentSessionID", "null");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
        send_message("SEND_MESSAGE", AppConfig.UserToken, 1, AppConfig.UserID, Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }


    /**
     * join meeting 判断是否直播模式
     *
     * @param msg
     */
    private void isopenvideo(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            String status = getRetCodeByReturnData2("status", msg);
            Log.e("isopenvideo", status + "    " + currentLine + "          " + icon_command_webcam_enable.getTag());
            if (status.equals("1")) {  //  已经加入课程
                if (currentLine == LINE_PEERTIME) {  // 刚进来是  声网  模式
                    if (!(Boolean) icon_back.getTag()) {
                        openshengwang(0);  // 打开声网
                        icon_back.setTag(true);
                    }
                } else if (currentLine == LINE_KLOUDPHONE) {   // 刚进来是  kloudcall  模式
                    showKloudCall2();
                    if (!(Boolean) icon_back.getTag()) {
                        openshengwang(1);  // 打开声网
                        icon_back.setTag(true);
                    }
                } else if (currentLine == LINE_EXTERNOAUDIO) {  // 刚进来是  no audio  模式
                    showExterNoAudio();
                    if (!(Boolean) icon_back.getTag()) {
                        openshengwang(1);  // 打开声网
                        icon_back.setTag(true);
                    }
                }
                startll.setVisibility(View.GONE);
                leavell.setVisibility(View.VISIBLE);
                prompt.setVisibility(View.GONE);
            } else if (status.equals("0")) { // 老师还没开课
                endtextview.setText("Close");
                if (identity == 2) { //老师
                    if (isStartCourse) {
                        startCourse();
                    } else {
                        startll.setVisibility(View.VISIBLE);
                        leavell.setVisibility(View.GONE);
                    }
                } else if (identity == 1) {
                    //学生 自己加入
                    joinvideo.setVisibility(View.VISIBLE);
                    startll.setVisibility(View.GONE);
                    leavell.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    String recordingId;

    private void doJOIN_MEETING(String msg) {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            changeNumber = jsonObject.getString("changeNumber");
            JSONObject retdata = jsonObject.getJSONObject("retData");

            JSONArray jsonArray = retdata.getJSONArray("usersList");
            List<Customer> joinlist = Tools.getUserListByJoinMeeting(jsonArray);

            msg = retdata.toString();

            String currentLine2 = getRetCodeByReturnData2("currentLine", msg);
            currentLine = Integer.parseInt(currentLine2);
            currentMode = getRetCodeByReturnData2("currentMode", msg);
            currentMaxVideoUserId = getRetCodeByReturnData2("currentMaxVideoUserId", msg);
            isopenvideo(msg);

            invitedUserIds.clear();
            String ids = getRetCodeByReturnData2("invitedUserIds", msg);
            if (!TextUtils.isEmpty(ids)) {
                String idss[] = ids.split(",");
                for (String s : idss) {
                    invitedUserIds.add(s);
                }
            }
            currentMode = getRetCodeByReturnData2("currentMode", msg);
            try {
                String videoStatus2 = getRetCodeByReturnData2("videoStatus", msg);
                videoStatus = TextUtils.isEmpty(videoStatus2) ? 0 : Integer.parseInt(videoStatus2);
                videoFileId = getRetCodeByReturnData2("videoFileId", msg);
                currentMaxVideoUserId = getRetCodeByReturnData2("currentMaxVideoUserId", msg);
                zoomInfo = getRetCodeByReturnData2("zoomInfo", msg);
                zoomInfo = zoomInfo.replaceAll("'", "\"");
                Log.e("miao1", zoomInfo);
                zoomInfo = zoomInfo.replaceAll("'", "\"");
                Log.e("miao2", zoomInfo);
                changeNumber2();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            String currentpage = getRetCodeByReturnData2("CurrentDocumentPage", msg);
            String[] page = currentpage.split("-");
            currentItemId = page[0];
            currentAttachmentPage = page[1];

            Message message1 = Message.obtain();
            message1.obj = joinlist;
            message1.what = AppConfig.SUCCESS;
            handler.sendMessage(message1);

            String prepareMode = getRetCodeByReturnData2("prepareMode", msg);
            Message msg2 = Message.obtain();
            msg2.obj = prepareMode;
            msg2.what = 0x4001;
            handler.sendMessage(msg2);
            //播放音频
            String playAudioData = retdata.getString("playAudioData");
            String msg3 = Tools.getFromBase64(playAudioData);
            if (!TextUtils.isEmpty(msg3)) {
                JSONObject ms3 = new JSONObject(msg3);
                int actionType = ms3.getInt("actionType");
                if (actionType == 23) {
                    int soundtrackId = ms3.getInt("soundtrackId");
                    int stat = ms3.getInt("stat");
                    if (stat == 1 || stat == 4) {
                        getAudioAction(soundtrackId, 0);
                        getyinxiangdetail(soundtrackId);
                    }
                }
            }
            int hideCamera = retdata.getInt("hideCamera");
            if (hideCamera == 0) {
                currentisopen2 = 1;
            } else {
                currentisopen2 = 0;
            }
            recordingId = retdata.getString("recordingId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doLEAVE_MEETING(String msg) {
        notifyleave();
        String retdate = getRetCodeByReturnData2("retData", msg);
        try {
            leaveUserid = new JSONObject(retdate).getString("userId");
            Message msg2 = Message.obtain();
            msg2.what = 0x1111;
            handler.sendMessage(msg2);

            if (identity == 2) {
                if (!leaveUserid.equals(teacherCustomer.getUserID())) {
                    if (currentPresenterId.equals(leaveUserid)) {  // 离开的学生是presenter
                        //设置老师为presenter
                        sendStringBySocket3("MAKE_PRESENTER", teacherCustomer.getUsertoken(), meetingId, teacherCustomer.getUsertoken());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void  gotoMeeting(){
        command_active.setVisibility(View.VISIBLE);
        menu_linearlayout.setVisibility(View.GONE);
        displayFile.setVisibility(View.VISIBLE);
        initdefault();
        worker().joinChannel(meetingId.toUpperCase(), config().mUid);
    }

    private int audioTime = 0;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equals("START_JOIN_MEETING")) {
                gotoMeeting();
            }

            String msg = Tools.getFromBase64(message);
            String msg_action = getRetCodeByReturnData2("action", msg);
            if (msg_action.equals("JOIN_MEETING")) {
                if (getRetCodeByReturnData2("retCode", msg).equals("0")) {
                    doJOIN_MEETING(msg);
                } else if (getRetCodeByReturnData2("retCode", msg).equals("-1")) {
                    initdefault();  //重新 JOIN_MEETING
                }
            }
            if (msg_action.equals("OFFLINE_MODE") || msg_action.equals("ONLINE_MODE")) {
                try {
                    List<Customer> joinlist = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(msg);
                    JSONObject retdata = jsonObject.getJSONObject("retData");
                    JSONArray jsonArray = retdata.getJSONArray("usersList");
                    joinlist = Tools.getUserListByJoinMeeting(jsonArray);
                    Message message1 = Message.obtain();
                    message1.obj = joinlist;
                    message1.what = 0x3102;
                    handler.sendMessage(message1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg_action.equals("BIND_TV_LEAVE_MEETING")) {
                finish();
            }
            /**
             * 邀请学生加入声网 成功回调
             */
            if (msg_action.equals("MEETING_STATUS")) {
                if (getRetCodeByReturnData2("status", msg).equals("1")) { // 打开直播video
                    prompt.setVisibility(View.GONE);
                    if (currentLine == LINE_PEERTIME) {  //刚进来是  声网  模式
                        openshengwang(0);  // 打开声网
                    } else if (currentLine == LINE_KLOUDPHONE) {   // 刚进来是 kloudcall  模式
                        showKloudCall2();
                        openshengwang(1);  // 打开声网
                    } else if (currentLine == LINE_EXTERNOAUDIO) {  // 刚进来是 no audio  模式
                        showExterNoAudio();
                        openshengwang(1);  // 打开声网
                    }
                    icon_back.setTag(true);
                    startll.setVisibility(View.GONE);
                    leavell.setVisibility(View.VISIBLE);
                    prompt.setVisibility(View.GONE);
                }
            }
            /**
             * 收到升级旁听者的通知
             */
            if (msg_action.equals("PROMOTE_TO_STUDENT")) {
                String userid = getRetCodeByReturnData2("userId", msg);
                Message ms = Message.obtain();
                ms.obj = userid;
                ms.what = 0x1301;
                handler.sendMessage(ms);
            }
            /**
             * 邀请旁听者 其余在线收到的信息
             */
            if (msg_action.equals("INVITE_TO_MEETING")) {
                invateList.clear();
                String userIDS = getRetCodeByReturnData2("userIds", msg);
                if (!TextUtils.isEmpty(userIDS)) {
                    String userid[] = userIDS.split(",");
                    for (int i = 0; i < userid.length; i++) {
                        invateList.add(userid[i]);
                    }
                    Message invatemsg = Message.obtain();
                    invatemsg.obj = invateList;
                    invatemsg.what = 0x1121;
                    handler.sendMessage(invatemsg);
                }
            }
            /**
             * 切换文档  直播视频的切换
             */
            if (msg_action.equals("SEND_MESSAGE")) {
                String d = getRetCodeByReturnData2("data", msg);
                try {
                    final JSONObject jsonObject = new JSONObject(Tools.getFromBase64(d));
                    if (jsonObject.getInt("actionType") == 2) {  //横竖屏切换

                    } else if (jsonObject.getInt("actionType") == 8) { //切换文档
                        LineItem lineitem = new LineItem();
                        lineitem.setUrl(jsonObject.getString("attachmentUrl"));
                        lineitem.setHtml5(jsonObject.getBoolean("isH5"));
                        lineitem.setItemId(jsonObject.getString("itemId"));
                        currentAttachmentPage = jsonObject.getString("pageNumber");
                        AppConfig.currentPageNumber = jsonObject.getString("pageNumber");
                        Message documentMsg = Message.obtain();
                        documentMsg.obj = lineitem;
                        documentMsg.what = 0x1205;
                        handler.sendMessage(documentMsg);
                    } else if (jsonObject.getInt("actionType") == 9) { // 直播视频大小切换
                        Log.e("dddddddddddd", currentMode);
                        if (currentMode.equals("4")) {
                            currentMode = jsonObject.getInt("videoMode") + "";
                            if (currentMode.equals(0 + "")) {  // 关闭video
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startOrPauseVideo(2, 0, "", "", 0);
                                    }
                                });
                            }
                        } else {
                            currentMode = jsonObject.getInt("videoMode") + "";
                            if (!currentMode.equals(4 + "")) {  // 4 为播放视频   0 1 2 3
                                try {
                                    currentMaxVideoUserId = jsonObject.getString("currentSessionID");
                                    Log.e("dddddddddddd", currentMode + "   " + currentMaxVideoUserId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                switchMode();
                            }
                        }
                    } else if (jsonObject.getInt("actionType") == 16) {  //kloudcall or peertime
                        int lineId = jsonObject.getInt("lineId");       // 0  peertime
                        Message invatemsg = Message.obtain();
                        invatemsg.obj = lineId;
                        invatemsg.what = 0x3121;
                        handler.sendMessage(invatemsg);
                    } else if (jsonObject.getInt("actionType") == 19) {  //播放视频接受的信息
                        final int stat = jsonObject.getInt("stat");
                        final float time = jsonObject.getInt("time");
                        final String attachmentId = jsonObject.getString("vid");
                        final String url = jsonObject.getString("url");
                        final int videotype = jsonObject.getInt("type");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startOrPauseVideo(stat, time, attachmentId, url, videotype);
                            }
                        });
                    } else if (jsonObject.getInt("actionType") == 23) {
                        Log.e("mediaplayer-----", jsonObject.toString());
                        final int stat = jsonObject.getInt("stat");
                        final String soundtrackID = jsonObject.getString("soundtrackId");
                        if (stat == 4) {
                            audioTime = jsonObject.getInt("time"); //老师的时间
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("mediaplayer-----", stat + ":   " + (mediaPlayer == null));
                                if (stat == 1) {
                                    int vid2 = 0;
                                    if (!TextUtils.isEmpty(soundtrackID)) {
                                        vid2 = Integer.parseInt(soundtrackID);
                                    }
                                    getAudioAction(vid2, 0);
                                    getyinxiangdetail(vid2);
                                } else if (stat == 0) {
                                    StopMedia();
                                    StopMedia2();
                                    closeAudioSync();
                                } else if (stat == 2) {
                                    pauseMedia();
                                    pauseMedia2();
                                } else if (stat == 3) {
                                    resumeMedia();
                                    resumeMedia2();
                                } else if (stat == 4) {
                                    Log.e("mediaplayer-----", audioTime + "  " + tttime);
                                    if (audioTime - tttime > 3000) {
                                        tttime = audioTime;
                                        if (mediaPlayer != null) {
                                            mediaPlayer.seekTo(tttime);
                                        }
                                        if (mediaPlayer2 != null) {
                                            mediaPlayer2.seekTo(tttime);
                                        }
                                    }
                                }
                            }
                        });
                    } else if (jsonObject.getInt("actionType") == 14) { // 收到关闭自己的Audio
                        if (jsonObject.getInt("stat") == 0) {
                            initListen(false);
                            if (mViewType == VIEW_TYPE_NORMAL || mViewType == VIEW_TYPE_SING_NORMAL) {
                                openVideoByViewType();
                            }
                        } else if (jsonObject.getInt("stat") == 1) {
                            initListen(true);
                            if (mViewType == VIEW_TYPE_NORMAL || mViewType == VIEW_TYPE_SING_NORMAL) {
                                openVideoByViewType();
                            }
                        }
                    } else if (jsonObject.getInt("actionType") == 15) { //收到关闭自己的Video
                        if (jsonObject.getInt("stat") == 0) {
                            initMute(false);
                            openVideoByViewType();
                        }
                    } else if (jsonObject.getInt("actionType") == 22) {
                        String userid = jsonObject.getString("sourceUserId");
                        if (jsonObject.getInt("stat") == 0) {//设置自己听不到XX的声音
                            worker().getRtcEngine().muteRemoteAudioStream(Integer.parseInt(userid), true);
                        } else if (jsonObject.getInt("stat") == 1) {   //设置自己听到XX的声音
                            worker().getRtcEngine().muteRemoteAudioStream(Integer.parseInt(userid), false);
                        }
                    } else if (jsonObject.getInt("actionType") == 21) {
                        if (currentisopen == 0) {
                            if (jsonObject.getInt("isHide") == 0) {
//                                initMute(true);  // SHOW
                                if (currentisopen2 == 0) {
                                    currentisopen2 = 1;
                                    smalltoggleicon.setImageResource(R.drawable.eyeopen);
                                    mSmallLeftAgoraAdapter.setData(mUidsList, teacherid);
                                    mSmallLeftRecycler.setVisibility(View.VISIBLE);
                                } else {
                                    openVideoByViewType();
                                }
                            } else if (jsonObject.getInt("isHide") == 1) {
//                                initMute(false);  // HIDDEN
                                if (currentisopen2 == 1) {
                                    currentisopen2 = 0;
                                    smalltoggleicon.setImageResource(R.drawable.eyeclose);
                                    mSmallLeftAgoraAdapter.setData(null, teacherid);
                                    mSmallLeftRecycler.setVisibility(View.GONE);
                                } else {
//                                openVideoByViewType();
                                }
                            }
                        }
                    } else if (jsonObject.getInt("actionType") == 26) {
                        int deleteAttachmentId = jsonObject.getInt("attachmentId");
                        for (int i1 = 0; i1 < documentList.size(); i1++) {
                            LineItem lineItem = documentList.get(i1);
                            if (lineItem.getAttachmentID().equals(deleteAttachmentId + "")) {
                                documentList.remove(i1);
                                break;
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (myRecyclerAdapter2 != null) {
                                    myRecyclerAdapter2.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (msg_action.equals("KLOUD_CALL_STARTED")) {   //
                if (getRetCodeByReturnData2("retCode", msg).equals("1")) {
                    Message invatemsg = Message.obtain();
                    invatemsg.obj = 2; //kloudcall
                    invatemsg.what = 0x3121;
                    handler.sendMessage(invatemsg);
                }
            }
            if (msg_action.equals("CREATE_KLOUD_CALL_CONFERENCE")) {
                Message invatemsg = Message.obtain();
                invatemsg.obj = getRetCodeByReturnData2("retCode", msg);
                invatemsg.what = 0x4113;
                handler.sendMessage(invatemsg);
            }
            if (msg_action.equals("END_KLOUD_CALL_CONFERENCE")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callMeLater.setVisibility(View.GONE);
                    }
                });
            }
            if (msg_action.equals("CALL_ME")) {
                if (getRetCodeByReturnData2("retCode", msg).equals("-2403")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WatchCourseActivity2.this, "user exist in conference", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            //通知上传文档了
            if (msg_action.equals("ATTACHMENT_UPLOADED")) {
                getServiceDetail();
                getVideoList();
            }
            if (msg_action.equals("UPDATE_CURRENT_DOCUMENT_ID")) {
//                currentAttachmentId=getRetCodeByReturnData2("documentId", msg);
            }
            //获得课程Action
            if (msg_action.equals("BROADCAST_FRAME")) {
                String msg2 = Tools.getFromBase64(getRetCodeByReturnData2("data", msg));
                Message msg3 = Message.obtain();
                Log.e("duang", msg2 + "");
                msg3.obj = msg2;
                msg3.what = 0x1109;
                handler.sendMessage(msg3);
            }
            if (msg_action.equals("MAKE_PRESENTER")) {
                if (getRetCodeByReturnData2("retCode", msg).equals("1")) {  //此时设置student or teacher presenter success
                    currentPresenterId = getRetCodeByReturnData2("presenterId", msg);
                    Message msg4 = Message.obtain();
                    msg4.what = 0x1110;
                    handler.sendMessage(msg4);
                    if (currentPresenterId.equals(AppConfig.UserID)) {
                        changeVideo1(0, "");
                    }

                }
            }
            //离开课程  返回的socket
            if (msg_action.equals("LEAVE_MEETING")) {
                doLEAVE_MEETING(msg);
            }
            //老师 结束课程  所有人离开
            if (msg_action.equals("END_MEETING")) {
                if (getRetCodeByReturnData2("retCode", msg).equals("1")) {
                    if (isHavePresenter()) {

                    } else {
                    finish();
                }
            }
            }
            if (msg_action.equals("HELLO")) {
                AppConfig.isPresenter = isHavePresenter();
                AppConfig.status = 1 + "";
                AppConfig.currentLine = currentLine;
                AppConfig.currentMode = currentMode;
                AppConfig.currentPageNumber = currentAttachmentPage;
                AppConfig.currentDocId = currentItemId;
                try {
                    String data = getRetCodeByReturnData2("data", msg);
                    if (!TextUtils.isEmpty(data)) {
                        final JSONObject jsonObject = new JSONObject(Tools.getFromBase64(data));
                        String currentItemId2 = jsonObject.getString("currentItemId");
                        currentPresenterId = jsonObject.getString("currentPresenter");
                        String currentMode2 = jsonObject.getString("currentMode");
                        if (!currentPresenterId.equals(AppConfig.UserID)) {  //不是presenter
                            String currentAttachmentPage2 = jsonObject.getString("currentPageNumber");
                            if (currentItemId2.equals(currentItemId)) {  //当前是同一个文档
                                String changpage = "{\"type\":2,\"page\":" + currentAttachmentPage2 + "}";
                                Message msg3 = Message.obtain();
                                msg3.obj = changpage;
                                msg3.what = 0x1109;
                                if (currentAttachmentPage2.equals(currentAttachmentPage)) {
                                } else {
                                    handler.sendMessage(msg3);
                                }
                            } else {
                                currentItemId = currentItemId2;
                                for (int i = 0; i < documentList.size(); i++) {
                                    if (documentList.get(i).getItemId().equals(currentItemId)) {
                                        Message documentMsg = Message.obtain();
                                        documentMsg.obj = documentList.get(i);
                                        documentMsg.what = 0x1205;
                                        handler.sendMessage(documentMsg);
                                    }
                                }
                            }
                            Message msg4 = Message.obtain();
                            msg4.obj = currentMode2;
                            msg4.what = 0x1120;
//                            handler.sendMessage(msg4);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg_action.equals("NO_ACTIVITY_DETECTED")) {
                if (teacherCustomer.getUserID().equals(AppConfig.UserID)) {
                    Message msg3 = Message.obtain();
                    String countDown = getRetCodeByReturnData2("countDown", msg);
                    int countDown2 = TextUtils.isEmpty(countDown) ? 0 : Integer.parseInt(countDown);
                    msg3.obj = countDown2;
                    msg3.what = 0x1190;
                    handler.sendMessage(msg3);
                }
            }
        }
    };


    /**
     * 改变HELLO changenumber的值
     */
    private void changeNumber2() {
        Intent intent1 = new Intent();
        intent1.setAction("com.cn.changenumber");
        intent1.putExtra("changeNumber", changeNumber);
        sendBroadcast(intent1);
    }

    private void sendvoicenet(String action, String sessionId, String meetingId, int status, String lessionid) {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", action);
            loginjson.put("sessionId", sessionId);
            loginjson.put("meetingId", meetingId);
            loginjson.put("status", status);
            loginjson.put("lessonid", lessionid);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 老师给学生发送上课消息
     */
    private void send_message(String action, String sessionId, int type, String userlist, String data) {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", action);
            loginjson.put("sessionId", sessionId);
            loginjson.put("type", type);
            loginjson.put("userList", userlist);
            loginjson.put("data", data);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void sendStringBySocket2(String action, String sessionId, String username, String meetingId2,
                                     String itemId, boolean isPresenter, String clientVersion, boolean isLogin, int role, int isInstantMeeting) {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", action);
            loginjson.put("sessionId", sessionId);
            loginjson.put("meetingId", meetingId2);
            loginjson.put("meetingPassword", "");
            loginjson.put("clientVersion", clientVersion);
            loginjson.put("role", role);
            loginjson.put("isInstantMeeting", isInstantMeeting);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * MAKE_PRESENTER
     */
    private void sendStringBySocket3(String action, String sessionId, String meetingId, String newPresenterSessionId) {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", action);
            loginjson.put("sessionId", sessionId);
            loginjson.put("meetingId", meetingId);
            loginjson.put("newPresenterSessionId", newPresenterSessionId);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    private void initView() {

        puo2 = new Popupdate2();
        puo2.getPopwindow(WatchCourseActivity2.this);
        prompt = (TextView) findViewById(R.id.prompt);
        endtextview = (TextView) findViewById(R.id.endtextview);
        llpre = (LinearLayout) findViewById(R.id.llpre);
        leavepre = (TextView) findViewById(R.id.leavepre);
        leavepre.setOnClickListener(this);
        llpre.setOnClickListener(this);

        filedownprogress = (RelativeLayout) findViewById(R.id.filedownprogress);
        fileprogress = (ProgressBar) findViewById(R.id.fileprogress);
        progressbartv = (TextView) findViewById(R.id.progressbartv);

        wv_show = (XWalkView) findViewById(R.id.wv_show);
        wv_show.setZOrderOnTop(false);
        wv_show.getSettings().setDomStorageEnabled(true);
//        wv_show.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        wv_show.addJavascriptInterface(WatchCourseActivity2.this, "AnalyticsWebInterface");
        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);

        findViewById(R.id.hiddenwalkview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("haha", "onclick");
                activte_linearlayout.setVisibility(View.GONE);
                menu_linearlayout.setVisibility(View.GONE);
                findViewById(R.id.hiddenwalkview).setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                command_active.setImageResource(R.drawable.icon_command);

            }
        });
        findViewById(R.id.mainmain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("haha", "onclick");
                activte_linearlayout.setVisibility(View.GONE);
                menu_linearlayout.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                command_active.setImageResource(R.drawable.icon_command);
                findViewById(R.id.hiddenwalkview).setVisibility(View.GONE);

            }
        });
        preparedownprogress = (RelativeLayout) findViewById(R.id.preparedownprogress);
        //设置视频控制器
        videoView = (CustomVideoView) findViewById(R.id.videoView);
        videoView.setMediaController(new MediaController(this));
        videoView.setZOrderOnTop(true);
        videoView.setZOrderMediaOverlay(true);
        closeVideo = (ImageView) findViewById(R.id.closeVideo);
        closeVideo.setOnClickListener(this);
        videoGestureRelativeLayout = (VideoGestureRelativeLayout) findViewById(R.id.videoll);
        videoGestureRelativeLayout.setVideoGestureListener(this);
        showChangeLayout = (ShowChangeLayout) findViewById(R.id.scl);
        //初始化获取音量属性
        mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //初始化亮度调节
        mBrightnessHelper = new BrightnessHelper(this);
        //下面这是设置当前APP亮度的方法配置
        mWindow = getWindow();
        mLayoutParams = mWindow.getAttributes();
        brightness = mLayoutParams.screenBrightness;

        menu = (ImageView) findViewById(R.id.menu);
        menu.setOnClickListener(this);
        command_active = (ImageView) findViewById(R.id.command_active);
        command_active.setOnClickListener(this);

        activte_linearlayout = (LinearLayout) findViewById(R.id.activte_linearlayout);
        menu_linearlayout = (LinearLayout) findViewById(R.id.menu_linearlayout);

        displayAudience = (RelativeLayout) findViewById(R.id.displayAudience);
        displayAudience.setOnClickListener(this);
        displayFile = (RelativeLayout) findViewById(R.id.displayFile);
        displayFile.setOnClickListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        leavell = (RelativeLayout) findViewById(R.id.leavell);
        endll = (RelativeLayout) findViewById(R.id.endll);
        startll = (RelativeLayout) findViewById(R.id.startll);
        bindidll = (RelativeLayout) findViewById(R.id.bindidll);
        testdebug = (RelativeLayout) findViewById(R.id.testdebug);
        joinvideo = (RelativeLayout) findViewById(R.id.joinvideo);
        callMeLater = (RelativeLayout) findViewById(R.id.callmelater);
        scanll = (RelativeLayout) findViewById(R.id.scanll);
        refresh_notify_2 = (RelativeLayout) findViewById(R.id.refresh_notify_2);
        refresh_notify_2.setOnClickListener(this);
        leavell.setOnClickListener(this);
        endll.setOnClickListener(this);

        startll.setOnClickListener(this);
        bindidll.setOnClickListener(this);
        joinvideo.setOnClickListener(this);
        callMeLater.setOnClickListener(this);
        scanll.setOnClickListener(this);
        testdebug.setOnClickListener(this);

        startll.setVisibility(View.GONE);
        joinvideo.setVisibility(View.GONE);
        leavell.setVisibility(View.VISIBLE);

        if (identity == 2) { //老师
            endll.setVisibility(View.VISIBLE);
        } else {
            endll.setVisibility(View.GONE);
        }

        startll.setVisibility(View.VISIBLE);
        leavell.setVisibility(View.GONE);
        endtextview.setText("Close");

        displaychat = (RelativeLayout) findViewById(R.id.displaychat);
        displaychat.setOnClickListener(this);

        displaywebcam = (RelativeLayout) findViewById(R.id.displaywebcam);
        displaywebcam.setOnClickListener(this);
        displayVideo = (RelativeLayout) findViewById(R.id.displayvideo);
        displayVideo.setOnClickListener(this);
        displayrecord = (RelativeLayout) findViewById(R.id.displayrecord);
        displayrecord.setOnClickListener(this);
        setting = (RelativeLayout) findViewById(R.id.setting);
        setting2 = (RelativeLayout) findViewById(R.id.setting2);
        if (identity != 2) {
            setting.setVisibility(View.GONE);
            findViewById(R.id.videoline).setVisibility(View.GONE);
        }
        setting.setOnClickListener(this);
        setting2.setOnClickListener(this);
        settingllback = (LinearLayout) findViewById(R.id.settingllback);
        settingllback.setOnClickListener(this);
        settingllback2 = (LinearLayout) findViewById(R.id.settingllback2);
        settingllback2.setOnClickListener(this);

        selectwebcam = (RelativeLayout) findViewById(R.id.selectwebcam);
        selectconnection = (RelativeLayout) findViewById(R.id.selectconnect);
        select240 = (RelativeLayout) findViewById(R.id.select240);
        select360 = (RelativeLayout) findViewById(R.id.select360);
        select480 = (RelativeLayout) findViewById(R.id.select480);
        select720 = (RelativeLayout) findViewById(R.id.select720);
        peertimebase = (RelativeLayout) findViewById(R.id.peertimebase);
        kloudcall = (RelativeLayout) findViewById(R.id.kloudcall);
        external = (RelativeLayout) findViewById(R.id.external);
        right3bnt = (TextView) findViewById(R.id.right3bnt);
        right3bnt.setOnClickListener(this);
        leftview = (LinearLayout) findViewById(R.id.leftview);
        leftview.setOnClickListener(this);
        leftview2 = (LinearLayout) findViewById(R.id.leftview2);
        leftview2.setOnClickListener(this);

        selectwebcam.setOnClickListener(this);
        selectconnection.setOnClickListener(this);
        select240.setOnClickListener(this);
        select360.setOnClickListener(this);
        select480.setOnClickListener(this);
        select720.setOnClickListener(this);
        peertimebase.setOnClickListener(this);
        kloudcall.setOnClickListener(this);
        external.setOnClickListener(this);

        right1 = (LinearLayout) findViewById(R.id.right1);
        right2 = (LinearLayout) findViewById(R.id.right2);
        right3 = (LinearLayout) findViewById(R.id.right3);


        bigRecyler = (RelativeLayout) findViewById(R.id.bigRecyler);
        middleRecyler = (RelativeLayout) findViewById(R.id.middleRecyler);
        smallRecyler = (RelativeLayout) findViewById(R.id.smallRecyler);
        bigRecyler.setOnClickListener(this);
        middleRecyler.setOnClickListener(this);
        smallRecyler.setOnClickListener(this);
        bigimage = (ImageView) findViewById(R.id.bigimage);
        middleimage = (ImageView) findViewById(R.id.middleimage);
        smallimage = (ImageView) findViewById(R.id.smallimage);
        smallimage.setVisibility(View.VISIBLE);
    }


    private Favorite favoriteAudio = new Favorite();
    private MediaPlayer mediaPlayer;

    /**
     * @param url
     * @param isrecording 播放音乐判断是否录音
     */

    private Timer audioplaytimer;

    private void GetMediaPlay(String url) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (TextUtils.isEmpty(url)) {
            doLeaveChannel();
            mUidsList.clear();
            mLeftAgoraAdapter.setData(null, "");
            togglelinearlayout.setVisibility(View.GONE);

        } else {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    doLeaveChannel();
                    mUidsList.clear();
                    mLeftAgoraAdapter.setData(null, "");
                    togglelinearlayout.setVisibility(View.GONE);
                }
            });
        }
    }


    private boolean issetting = false;

    private void StopMedia() {
//        getLineAction(currentAttachmentPage, false);
        if (togglelinearlayout.getVisibility() == View.GONE) {
            worker().joinChannel(meetingId.toUpperCase(), config().mUid);
            togglelinearlayout.setVisibility(View.VISIBLE);
            issetting = true;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
        }

    }

    private void pauseMedia() {
        isPause = true;
        playstop.setImageResource(R.drawable.video_play);
        if (togglelinearlayout.getVisibility() == View.GONE) {
            worker().joinChannel(meetingId.toUpperCase(), config().mUid);
            togglelinearlayout.setVisibility(View.VISIBLE);
            issetting = true;
        }
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    private void resumeMedia() {
        isPause = false;
        playstop.setImageResource(R.drawable.video_stop);
        if (togglelinearlayout.getVisibility() == View.VISIBLE) {
            doLeaveChannel();
            mUidsList.clear();
            mLeftAgoraAdapter.setData(null, "");
            togglelinearlayout.setVisibility(View.GONE);
        }
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }


    private RelativeLayout audiosyncll;
    private ImageView playstop;
    private ImageView close;
    private TextView isStatus;
    private TextView audiotime;
    private ImageView syncicon;

    /**
     * @param isrecording 是否录制视频
     */
    private boolean isPause = false;
    private SeekBar mSeekBar;

    private void openAudioSync(long duration) {

        isPause = false;
        audiosyncll = (RelativeLayout) findViewById(R.id.audiosyncll);
        playstop = (ImageView) findViewById(R.id.playstop);
        playstop.setImageResource(R.drawable.video_stop);
        syncicon = (ImageView) findViewById(R.id.syncicon);
        close = (ImageView) findViewById(R.id.close);
        isStatus = (TextView) findViewById(R.id.isStatus);
        audiotime = (TextView) findViewById(R.id.audiotime);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        isStatus.setText("Playing");
        syncicon.setVisibility(View.GONE);
        mSeekBar.setVisibility(View.VISIBLE);
        int ss = Integer.parseInt(duration + "");
        mSeekBar.setMax(ss);
//        getLineAction(currentAttachmentPage, true);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tttime = progress;
                String time = new SimpleDateFormat("mm:ss").format(tttime);
                audiotime.setText(time);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekToTime(tttime);
            }
        });

        mSeekBar.setEnabled(false);
        playstop.setEnabled(false);
        close.setEnabled(false);

        playstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPause) {
                    resumeMedia();
                    resumeMedia2();
                } else {
                    pauseMedia();
                    pauseMedia2();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAudioSync();
                StopMedia();
                StopMedia2();
            }
        });
        refreshPlayTime();
        audiosyncll.setVisibility(View.VISIBLE);

    }

    private String actions = "";
    private boolean isChangePageNumber = false;

    private void seekToTime(int time) {
        Log.e("hhhhhhhhhhhu", time + "");
        tttime = time;
        getAudioAction(soundtrackID, time);
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(time);
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2.seekTo(time);
        }
        if (mSeekBar != null) {
            mSeekBar.setProgress(time);
        }
        String url = AppConfig.URL_PUBLIC + "Soundtrack/PageActions?soundtrackID=" + soundtrackID + "&time=" + time;
        ServiceInterfaceTools.getinstance().getPageActions(url, ServiceInterfaceTools.GETPAGEACTIONS, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                PageActionBean pageActionBean = (PageActionBean) object;
                final String pagenumber = pageActionBean.getPageNumber();
                actions = pageActionBean.getActions();
                Log.e("pagenumber", currentAttachmentPage + "  " + pagenumber);
                if (currentAttachmentPage.equals(pagenumber)) {  //后退到当前页
                    // 3 displayDrawingLine =0 展示所有的线
//                    getLineAction(currentAttachmentPage, !isPause);
                    Message msg = Message.obtain();
                    msg.what = 0x4010;
                    msg.obj = actions;
                    handler.sendMessage(msg);
                } else {  // 不同页 先进行翻頁
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isChangePageNumber = true;
                            currentAttachmentPage = pagenumber;
                            String changpage = "{\"type\":2,\"page\":" + currentAttachmentPage + "}";
                            wv_show.load("javascript:PlayActionByTxt('" + changpage + "','" + 0 + "')", null);
                        }
                    });
                }
            }
        });
    }


    int tttime = 0;

    private void refreshPlayTime() {
        tttime = 0;
        mSeekBar.setProgress(tttime);
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
        }
        audioplaytimer = new Timer();
        audioplaytimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isPause) {
                            if (mediaPlayer2 != null) {
                                if ((mediaPlayer2.getCurrentPosition() - tttime) >= 0) {
                                    tttime = mediaPlayer2.getCurrentPosition();
                                    mSeekBar.setProgress(tttime);
                                }
                                //处理action的展示
                                newAudioActionTime(tttime);

                            }
                        }
                    }
                });
            }
        }, 0, 100);
    }


    private void closeAudioSync() {
        if (audiosyncll != null) {
            audiosyncll.setVisibility(View.GONE);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wv_show.load("javascript:ClearPageAndAction()", null);
                }
            });
        }
    }

    /**
     * 获得所有用户信息 在meeting中的人
     */
    private void getAllData(List<Customer> mAttendesList) {
        auditorList.clear();
        teacorstudentList.clear();
        for (int i = 0; i < mAttendesList.size(); i++) {  // 都在线
            Customer a = mAttendesList.get(i);
            a.setEnterMeeting(true);
            Log.e("在meeting中的人", a.getRole() + "  " + a.getName());
            if (a.getRole() == 2) {   // 设置老师
                teacherCustomer = a;
                teacherid = teacherCustomer.getUserID();
                if (a.isPresenter()) {
                    currentPresenterId = a.getUserID();
                }
                teacorstudentList.add(a);
            } else if (a.getRole() == 1) { //设置学生信息
                if (a.isPresenter()) {
                    studentCustomer = a;
                    currentPresenterId = a.getUserID();
                }
                teacorstudentList.add(a);
            } else if (a.getRole() == 3) { //设置旁听者信息
                a.setEnterMeeting(true);
                auditorList.add(a);
            }
        }
        //刷新旁听者列表
        if (auditorList.size() > 0) {
            myRecyclerAdapter.notifyDataSetChanged();
        }
        //刷新老师与学生列表
        if (teacorstudentList.size() > 0) {
            teacherRecyclerAdapter.Update(teacorstudentList);
        }
        // 获取不在meeting中的人的信息
        if (invitedUserIds.size() > 0) {
//            getDetailInfo(invitedUserIds, 2);
        }
    }


    /**
     * 老师端与学生端
     * 得到学生与老师的详细信息
     * isinvite
     * 0  解析老师  学生  信息
     * 1  invate to  meeting  返回的旁听者信息
     * 2  invitedUserIds|  invitedUserIds的信息
     */
    private String userid2 = "";

    private void getDetailInfo(List<String> useridList, final int isinvite) {
        userid2 = "";
        for (int i = 0; i < useridList.size(); i++) {
            if (i == 0) {
                userid2 += useridList.get(i);
            } else {
                userid2 += "," + useridList.get(i);
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "User/UserListBasicInfo?userIds=" + userid2);
                formatjson(jsonObject, isinvite);
            }
        }).start();
    }


    private void formatjson(JSONObject jsonObject, int isinvate) {
        try {
            int retCode = jsonObject.getInt("RetCode");
            switch (retCode) {
                case 0:
                    JSONArray jsarray = jsonObject.getJSONArray("RetData");
                    List<Customer> mylist = new ArrayList<>();
                    for (int j = 0; j < jsarray.length(); j++) {
                        JSONObject js = jsarray.getJSONObject(j);
                        Customer s = new Customer();
                        s.setUserID(js.getString("UserID"));
                        s.setName(js.getString("UserName"));
                        s.setUBAOUserID(js.getString("RongCloudID"));
                        s.setUrl(js.getString("AvatarUrl"));
                        mylist.add(s);
                    }
                    Message msg = Message.obtain();
                    msg.obj = mylist;
                    if (isinvate == 1) { // invate-meeting 返回的
                        msg.what = 0x1203;
                    } else if (isinvate == 2) { // join  meeting  invate
                        msg.what = 0x1204;
                    }
                    handler.sendMessage(msg);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Popupdate2 puo2;

    Uploadao uploadao = new Uploadao();

    /**
     * 加载PDF
     */
    @org.xwalk.core.JavascriptInterface
    public void afterLoadPageFunction() {
        crpage = (int) Float.parseFloat(currentAttachmentPage);
        Log.e("当前文档信息", "url  " + targetUrl + "        " + crpage + "      newpath  " + newPath);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JsonDown();
            }
        });
    }


    private void JsonDown() {
        if(TextUtils.isEmpty(newPath)){
            newPath = targetUrl.substring(targetUrl.indexOf(".com") + 5, targetUrl.lastIndexOf("/"));
        }

        if(TextUtils.isEmpty(targetUrl)){
            ServiceInterfaceTools.getinstance().queryDocument(AppConfig.URL_LIVEDOC + "queryDocument", ServiceInterfaceTools.QUERYDOCUMENT,
                    newPath, new ServiceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {
                            String jsonstring = (String) object;
                            Log.e("当前文档信息", jsonstring);
                            uploadao = transfering2(jsonstring);
                            if (uploadao == null) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        JsonDown();
                                    }
                                }, 1000);
                            } else {
                                String filename = targetUrl.substring(targetUrl.lastIndexOf("/") + 1);
                                if (1 == uploadao.getServiceProviderId()) {
                                    targetUrl = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + newPath + "/" + filename;
                                } else if (2 == uploadao.getServiceProviderId()) {
                                    targetUrl = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + newPath + "/" + filename;
                                }
                                //https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D24893/3fffe932-5e52-4dbb-8376-9436a2de4dbe_1_2K.jpg
                                Log.e("当前文档信息", "url  " + targetUrl);
                                if (crpage == 0) {
                                    downloadPdf(targetUrl, 1);
                                } else {
                                    downloadPdf(targetUrl, crpage);
                                    crpage = 0;
                                }
                            }
                        }
                    });
        }else {
            if (crpage == 0) {
                downloadPdf(targetUrl, 1);
            } else {
                downloadPdf(targetUrl, crpage);
                crpage = 0;
            }
        }


    }


    private boolean isTemporary = false;

    private Uploadao transfering2(final String jsonstring) {
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject data = returnjson.getJSONObject("Data");

                JSONObject bucket = data.getJSONObject("Bucket");
                Uploadao uploadao = new Uploadao();
                uploadao.setServiceProviderId(bucket.getInt("ServiceProviderId"));
                uploadao.setRegionName(bucket.getString("RegionName"));
                uploadao.setBucketName(bucket.getString("BucketName"));

                isTemporary = data.getBoolean("IsTemporary");

                return uploadao;
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }


    private int transfering(final String jsonstring) {
        filedownprogress.setVisibility(View.VISIBLE);
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject dataJsonArray = returnjson.getJSONObject("Data");
                JSONObject js = dataJsonArray.getJSONObject(newPath);
                int Status = js.getInt("Status");
                if (Status == 2) {  //正在转换
                    JSONObject syncDetail = js.getJSONObject("SyncDetail");
                    final int finishPercent = syncDetail.getInt("FinishPercent");
                    fileprogress.setProgress(finishPercent);
                    progressbartv.setText(finishPercent + "%   File Converting,pls wait");
                    return 2;
                } else if (Status == 1) {  //下载
                    filedownprogress.setVisibility(View.GONE);
                    return 1;
                } else {  //文件不存在
                    filedownprogress.setVisibility(View.GONE);
                    return 3;
                }
            }
        } catch (JSONException e) {
            filedownprogress.setVisibility(View.GONE);
            return 1;
        }
        return 1;
    }


    int crpage = 0;
    private String prefixPdf;
    private int pageCount = 0; //每个PDF的总页数
    private String showpdfurl;

    private int getPdfCount(String pdfurl) {
        int first = pdfurl.lastIndexOf("<");
        int last = pdfurl.lastIndexOf(">");
        Log.e("hahh------------", first + "   " + last);
        if (first == 0 || last == 0) {  // 格式错误
            return 0;
        }
        if (last > first) {
            return Integer.parseInt(pdfurl.substring(first + 1, last));
        }
        return 0;
    }


    private void downloadPdf(final String pdfurl, final int page) {
        // https://peertime.cn/CWDocs/P49/Attachment/D3191/test_<10>.pdf
        if (TextUtils.isEmpty(pdfurl)) {
            return;
        }
        FileUtils fileUtils = new FileUtils(WatchCourseActivity2.this);
        File fileUtils1 = new File(fileUtils.getStorageDirectory());
        if (!fileUtils1.exists()) {
            fileUtils1.mkdir();
        }
        File fileUtils2 = new File(fileUtils.getStorageDirectory2());
        if (!fileUtils2.exists()) {
            fileUtils2.mkdir();
        }
        pageCount = getPdfCount(pdfurl);
        if (pageCount == 0) {
            return;
        }
        String sourceName = pdfurl.substring(pdfurl.lastIndexOf("/") + 1); // 得到test_<10>.pdf
        String houzui = sourceName.substring(sourceName.lastIndexOf("."));

        // 得到 https://peertime.cn/CWDocs/P49/Attachment/D3191/test_
        prefixPdf = pdfurl.substring(0, pdfurl.lastIndexOf("<"));

        // 得到下载地址  https:peertime.cn/CWDocs/P49/Attachment/D3191/test_1.pdf
        String downloadurl;
        Log.e("DDDDD", screenWidth + "  ");
//        if (screenWidth > 1920) {
//            downloadurl = prefixPdf + page + "_2K" + houzui;
//        } else {
//            downloadurl = prefixPdf + page + houzui;
//        }

        downloadurl = prefixPdf + page + houzui;
        String xxx = meetingId + "_" + EncoderByMd5(targetUrl).replaceAll("/", "_") + "_";

        //保存在本地的地址
        final String fileLocalUrl = fileUtils.getStorageDirectory() + File.separator
                + xxx + page + houzui;   // --->  /ubao/19999_xxxxx_1.pdf
        final String fileLocalUrl2 = fileUtils.getStorageDirectory2() + File.separator
                + xxx + page + houzui;   // --->  /ubao2/19999_xxxxx_1.pdf

        // show pdf 地址
        showpdfurl = fileUtils.getStorageDirectory2() + File.separator + xxx + sourceName.substring(sourceName.lastIndexOf("<"));  //--->  /ubao/xxxx_<10>.pdf
        Log.e("webview-downloadPdf", "保存在本地的地址  " + fileLocalUrl2 + "   show pdf 地址  " + showpdfurl);
        filedownprogress.setVisibility(View.GONE);
        if (fileUtils.isFileExists(fileLocalUrl2)) {
            Log.e("webview-downloadPdf", " 本地文件存在  show pdf 地址  " + showpdfurl + "  " + page + "  " + currentBlankPageNumber);
            currentAttachmentPage = page + "";
            AppConfig.currentPageNumber = currentAttachmentPage;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wv_show.load("javascript:ShowPDF('" + showpdfurl + "', " + page + ",0,'" + currentAttachmentId + "' )", null);
                    wv_show.load("javascript:Record()", null);
                }
            });
        } else {
            if (fileUtils.isFileExists(fileLocalUrl)) {
                new File(fileLocalUrl).delete();
            }
            filedownprogress.setVisibility(View.VISIBLE);
            DownloadUtil.get().download(downloadurl, fileLocalUrl, new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(int arg0) {
                    if (arg0 == 200) {
                        currentAttachmentPage = page + "";
                        AppConfig.currentPageNumber = currentAttachmentPage;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                filedownprogress.setVisibility(View.GONE);
                            }
                        });
                        Log.e("webview-downloadPdf", " 下载成功  show pdf 地址  " + fileLocalUrl);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int retcode = Tools.copyFileSdCard(fileLocalUrl, fileLocalUrl2);
                                if (retcode == 0) {
                                    Message message = Message.obtain();
                                    message.obj = "javascript:ShowPDF('" + showpdfurl + "', " + page + ",0,'" + currentAttachmentId + "' )";
                                    message.what = 0x6115;
                                    handler.sendMessage(message);
                                }
                            }
                        }).start();
                    } else if (arg0 == 404) {
                        downloadPdf(pdfurl, page);
                    }
                }

                @Override
                public void onDownloading(final int progress) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fileprogress.setProgress(progress);
                            progressbartv.setText(progress + "%   File downloading,pls wait");
                        }
                    });
                }

                @Override
                public void onDownloadFailed() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            filedownprogress.setVisibility(View.GONE);
                            Toast.makeText(WatchCourseActivity2.this, " download failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

        }
    }

    private RelativeLayout preparedownprogress;

    @org.xwalk.core.JavascriptInterface
    public void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        Log.e("webview-preLoadFile", url + "     currentpageNum   " + currentpageNum + "   showLoading    " + showLoading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showLoading) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            preparedownprogress.setVisibility(View.VISIBLE);
                        }
                    }, 500);
                    if (isTemporary) {  //  isTemporary=true
                        ServiceInterfaceTools.getinstance().queryDocument(AppConfig.URL_LIVEDOC + "queryDocument", ServiceInterfaceTools.QUERYDOCUMENT,
                                newPath, new ServiceInterfaceListener() {
                                    @Override
                                    public void getServiceReturnData(Object object) {
                                        String jsonstring = (String) object;
                                        Uploadao ud = transfering2(jsonstring);
                                        if (ud != null) {
                                            uploadao = ud;
                                            String filename = targetUrl.substring(targetUrl.lastIndexOf("/") + 1);
                                            if (1 == uploadao.getServiceProviderId()) {
                                                targetUrl = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + newPath + "/" + filename;
                                            } else if (2 == uploadao.getServiceProviderId()) {
                                                targetUrl = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + newPath + "/" + filename;
                                            }
                                            prefixPdf = targetUrl.substring(0, targetUrl.lastIndexOf("<"));
                                        }
                                    }
                                });
                    }
                    DownloadUtil.get().cancelAll();
                    downEveryOnePdf(url, currentpageNum);
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            preparedownprogress.setVisibility(View.GONE);
                        }
                    }, 500);
                    downEveryOnePdf(url, currentpageNum);
                }
            }
        });
    }


    private void downEveryOnePdf(final String url, final int currentpageNum) {
        Log.e("downEveryOnePdf1", url);
        if (currentpageNum <= pageCount && currentpageNum >= 0) {
            final String fileurl2 = url.substring(0, url.lastIndexOf("<")) + currentpageNum + url.substring(url.lastIndexOf("."));
            String fileurl = url.substring(0, url.lastIndexOf("<")) + currentpageNum + url.substring(url.lastIndexOf("."));
            fileurl = fileurl.replace("ubao2", "ubao");
            final FileUtils fileUtils = new FileUtils(WatchCourseActivity2.this);
            Log.e("downEveryOnePdf2", fileurl2 + "  " + fileurl);
            if (fileUtils.isFileExists(fileurl2)) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        preparedownprogress.setVisibility(View.GONE);
                    }
                }, 500);
                afterDownloadFile(url, currentpageNum);
            } else {
                if (fileUtils.isFileExists(fileurl)) {
                    new File(fileurl).delete();
                }
//                String downloadurl = prefixPdf + currentpageNum + "_2K" + url.substring(url.lastIndexOf("."));
                String downloadurl = prefixPdf + currentpageNum + url.substring(url.lastIndexOf("."));
                final String finalFileurl = fileurl;
                Log.e("downEveryOnePdf3", downloadurl);
                //开始新的下载
                DownloadUtil.get().download(downloadurl, fileurl, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(int arg0) {
                        final String fileurl2 = finalFileurl.replace("ubao", "ubao2");
                        Log.e("downEveryOnePdf4", "success   " + fileurl2);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int retcode = Tools.copyFileSdCard(finalFileurl, fileurl2);
                                if (retcode == 0) {
                                    File ff = new File(fileurl2);
                                    Log.e("downEveryOnePdf5", "success   " + ff.getPath());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            preparedownprogress.setVisibility(View.GONE);
                                        }
                                    }, 500);
                                    Log.e("downEveryOnePdf4", "copy  success  ");
                                    afterDownloadFile(url, currentpageNum);
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onDownloading(final int progress) {

                    }

                    @Override
                    public void onDownloadFailed() {
                        Log.e("downEveryOnePdf4", "failure   " + fileurl2);
//                        downEveryOnePdf(url, currentpageNum);
                    }
                });

            }
        }
    }
    @org.xwalk.core.JavascriptInterface
    public void showErrorFunction(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(error);
                    int code = jsonObject.getInt("Code");
                    if (code == 1) {
                        Toast.makeText(WatchCourseActivity2.this, "Not Current Page", Toast.LENGTH_LONG).show();
                    } else if (code == 2) {
                        Toast.makeText(WatchCourseActivity2.this, "Not Support", Toast.LENGTH_LONG).show();
                    } else if (code == 3) {
                        Toast.makeText(WatchCourseActivity2.this, "Not Current Document ", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void afterDownloadFile(final String url, final int currentpageNum) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(url) && wv_show != null) {
                    wv_show.load("javascript:AfterDownloadFile('" + url + "', " + currentpageNum + ")", null);
                }
            }
        });
    }


    /**
     * 获取每一页上的 Action
     *
     * @param pageNum
     */
    @JavascriptInterface
    public void afterChangePageFunction(final String pageNum, int type) {
        //1:play,2:showdocument,3:next,4:prev,5:topage,  0 :未知
        currentAttachmentPage = pageNum + "";
        AppConfig.currentPageNumber = currentAttachmentPage;
        String url;
        url = AppConfig.URL_PUBLIC + "PageObject/GetPageObjects?lessonID=" + meetingId + "&itemID=" + currentItemId +
                "&pageNumber=" + pageNum;
        Log.e("meetingservicrtools", url);
        MeetingServiceTools.getInstance().getGetPageObjects(url, MeetingServiceTools.GETGETPAGEOBJECTS, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                final String ddd = (String) object;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (wv_show != null) {
                            if (!TextUtil.isEmpty(ddd)) {
                                wv_show.load("javascript:PlayActionByArray(" + ddd + "," + 0 + ")", null);
                            }
                            if(audiosyncll!=null){
                                if (audiosyncll.getVisibility() == (View.VISIBLE)) {
                                    wv_show.load("javascript:ClearPageAndAction()", null);
                                }
                            }
                        }
                    }
                });
            }
        });

        if (isChangePageNumber) {
            isChangePageNumber = false;
//              getLineAction(currentAttachmentPage, !isPause);
            Message msg = Message.obtain();
            msg.what = 0x4010;
            msg.obj = actions;
            handler.sendMessage(msg);
        }


    }


    /**
     * @param pageid
     */
    @JavascriptInterface
    public void addBlankPageFunction(final String pageid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject js = new JSONObject();
                try {
                    js.put("attachmentID", currentAttachmentId);
                    js.put("pageNumber", pageid);
                    JSONObject json = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "EventAttachment/AddBlankPage?attachmentID=" + currentAttachmentId + "&pageNumber=" + pageid, js);
                    Log.e("webview-addBlankPage", AppConfig.URL_PUBLIC + "EventAttachment/AddBlankPage?attachmentID=" + currentAttachmentId + "&pageNumber=" + pageid + "   ");
                    if (json.getInt("RetCode") == 0) {
                        Message msg = Message.obtain();
                        msg.obj = pageid;
                        msg.what = 0x2101;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String EncoderByMd5(String str) {
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

    private static HttpUtils client;

    /**
     * 单例模式获得HttpUtils对象
     *
     * @param context
     * @return
     */
    public synchronized static HttpUtils getInstance(Context context) {
        if (client == null) {
            // 设置请求超时时间为10秒
            client = new HttpUtils(1000 * 10);
            client.configSoTimeout(1000 * 10);
            client.configResponseTextCharset("UTF-8");
            // 保存服务器端(Session)的Cookie
            PreferencesCookieStore cookieStore = new PreferencesCookieStore(
                    context);
            cookieStore.clear(); // 清除原来的cookie
            client.configCookieStore(cookieStore);
        }
        return client;
    }

    @JavascriptInterface
    public void reflect(String result) {
        Log.e("webview-reflect", result);
        if (identity == 1) { // 学生
            String f = AppConfig.UserID.replaceAll("-", "").toString();
            if (TextUtils.isEmpty(studentCustomer.getUserID())) {
                return;
            } else {
                if (studentCustomer.getUserID().equals(f)) {
                    Log.e("webview-reflect", "学生");
                    String newresult = Tools.getBase64(result).replaceAll("[\\s*\t\n\r]", "");
                    try {
                        JSONObject loginjson = new JSONObject();
                        loginjson.put("action", "ACT_FRAME");
                        loginjson.put("sessionId", studentCustomer.getUsertoken());
                        loginjson.put("retCode", 1);
                        loginjson.put("data", newresult);
                        loginjson.put("itemId", currentItemId);
                        loginjson.put("sequenceNumber", "3837");
                        loginjson.put("ideaType", "document");
                        String ss = loginjson.toString();
                        SpliteSocket.sendMesageBySocket(ss);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (identity == 2) { //老师端 绘制
            if (currentPresenterId.equals(teacherCustomer.getUserID())) {
                Log.e("webview-reflect", "老师");
                String newresult = Tools.getBase64(result).replaceAll("[\\s*\t\n\r]", "");
                try {
                    JSONObject loginjson = new JSONObject();
                    loginjson.put("action", "ACT_FRAME");
                    loginjson.put("sessionId", teacherCustomer.getUsertoken());
                    loginjson.put("retCode", 1);
                    loginjson.put("data", newresult);
                    loginjson.put("itemId", currentItemId);
                    loginjson.put("sequenceNumber", "3837");
                    loginjson.put("ideaType", "document");
                    String ss = loginjson.toString();
                    SpliteSocket.sendMesageBySocket(ss);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isWebViewLoadFinish = true;

    /**
     * pdf 加载完成
     */
    @JavascriptInterface
    public void afterLoadFileFunction() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("webview-afterLoadFile", "afterLoadFileFunction");
                wv_show.load("javascript:CheckZoom()", null);
                if (!TextUtils.isEmpty(zoomInfo)) {
                    Message msg3 = Message.obtain();
                    msg3.obj = zoomInfo;
                    Log.e("webview-afterLoadFile", "afterLoadFileFunction    " + zoomInfo);
                    msg3.what = 0x1109;
                    handler.sendMessage(msg3);
                }
                if (currentPresenterId.equals(AppConfig.UserID)) {
                    wv_show.load("javascript:ShowToolbar(" + true + ")", null);
                    wv_show.load("javascript:Record()", null);
                } else {
                    wv_show.load("javascript:ShowToolbar(" + true + ")", null);
                    wv_show.load("javascript:StopRecord()", null);
                }
                isWebViewLoadFinish = true;
            }
        });
    }

    /**
     * 切换文档
     *
     * @param diff
     */
    @JavascriptInterface
    public void autoChangeFileFunction(int diff) {
        Log.e("webview-autoChangeFile", diff + "");
        if (documentList.size() == 0) {
            return;
        }
        if (isHavePresenter()) {
            for (int i = 0; i < documentList.size(); i++) {
                LineItem line = documentList.get(i);
                if (line.getAttachmentID().equals(lineItem.getAttachmentID())) { //当前文档
                    if (diff == 1) {  // 往后一页
                        if (i == documentList.size() - 1) {  // 切换到首页
                            line.setSelect(false);
                            lineItem = documentList.get(0);
                        } else {
                            line.setSelect(false);
                            lineItem = documentList.get(i + 1);
                        }

                    } else if (diff == -1) {  //往前一页
                        if (i == 0) {
                            line.setSelect(false);
                            lineItem = documentList.get(documentList.size() - 1);  //切换到最后一页
                        } else {
                            line.setSelect(false);
                            lineItem = documentList.get(i - 1);
                        }
                    }
                    break;
                }
            }
            lineItem.setSelect(true);
            myRecyclerAdapter2.notifyDataSetChanged();
            currentAttachmentId = lineItem.getAttachmentID();
            currentItemId = lineItem.getItemId();
            targetUrl = lineItem.getUrl();
            newPath = lineItem.getNewPath();
            currentBlankPageNumber = lineItem.getBlankPageNumber();

            if (diff == 1) {
                currentAttachmentPage = "1";
                notifySwitchDocumentSocket(lineItem, "0");
            } else if (diff == -1) {
                if (!TextUtils.isEmpty(lineItem.getUrl())) {
                    currentAttachmentPage = getPdfCount(lineItem.getUrl()) + "";
                    notifySwitchDocumentSocket(lineItem, currentAttachmentPage);
                }
            }
            AppConfig.currentPageNumber = currentAttachmentPage;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wv_show.load("file:///android_asset/index.html", null);
                }
            });
        }
    }


    // 播放视频
    @JavascriptInterface
    public void videoPlayFunction(final int vid) {
        Log.e("videoPlayFunction", vid + " videoPlayFunction");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webVideoPlay(vid, false, 0);
            }
        });
    }

    //打开
    @JavascriptInterface
    public void videoSelectFunction(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (favoritePopup != null) {
                    favoritePopup.StartPop(findViewById(R.id.layout));
                    favoritePopup.setData(2, false);
                }
            }
        });
    }

    // 录制
    @JavascriptInterface
    public void audioSyncFunction(final int id, final int isRecording) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webVideoPlay(id, true, isRecording);  //录音和录action
            }
        });
    }

    /**
     * 显示老师学生  信息的列表
     *
     * @param
     */
    private void initDisplayAudienceList() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater
                .inflate(R.layout.teacherlist_pop, null);
        RelativeLayout teacherll = (RelativeLayout) view.findViewById(R.id.teacherll);
        teacherll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow1.dismiss();
            }
        });
        teacherrecycler = (RecyclerView) view.findViewById(R.id.teacherrecycleview);
        auditorrecycleview = (RecyclerView) view.findViewById(R.id.auditorrecycleview);
        ImageView addauditorimageview = (ImageView) view.findViewById(R.id.addauditorimageview);
        addauditorimageview.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        teacherrecycler.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        auditorrecycleview.setLayoutManager(linearLayoutManager2);

        //老师和学生适配器
        teacherRecyclerAdapter = new TeacherRecyclerAdapter(this, teacorstudentList);
        teacherRecyclerAdapter.setOnItemClickListener(new TeacherRecyclerAdapter.OnItemClickListener2() {
            @Override
            public void onClick(Customer position) {
//                if (identity == 2) {  //只有老师才能设置presenter
//
//                }
                initPopuptWindowPresenter(position);
            }
        });
        teacherrecycler.setAdapter(teacherRecyclerAdapter);

        //旁听者适配器
        myRecyclerAdapter = new MyRecyclerAdapter(this, auditorList);
        myRecyclerAdapter.setOnItemClickListener3(new MyRecyclerAdapter.OnItemClickListener3() {
            @Override
            public void onClick(int position) {
                if (identity == 2) {
                    if (auditorList.get(position).isEnterMeeting()) {
                        initPopuptWindowPromote(auditorList.get(position));
                    }
                }
            }
        });
        auditorrecycleview.setAdapter(myRecyclerAdapter);
        mPopupWindow1 = new PopupWindow(view, screenWidth,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                findViewById(R.id.bottomrl).setVisibility(View.VISIBLE);
            }
        });
        mPopupWindow1.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow1.setAnimationStyle(R.style.anination2);
        mPopupWindow1.setFocusable(true);
    }

    /**
     * 文档列表popupwindow
     *
     * @param
     */
    private AddFileFromFavoriteDialog addFileFromFavoriteDialog;
    private void initDocumentListAudienceList() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater
                .inflate(R.layout.auditor_pop, null);
        RelativeLayout auditor = (RelativeLayout) view.findViewById(R.id.auditor);
        final LinearLayout upload_linearlayout = (LinearLayout) view.findViewById(R.id.upload_linearlayout);
        auditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (upload_linearlayout.getVisibility() == View.VISIBLE) {
                    upload_linearlayout.setVisibility(View.GONE);
                } else {
                    documentPopupWindow.dismiss();
                }
            }
        });
        documentrecycleview = (RecyclerView) view.findViewById(R.id.recycleview2);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(this);
        linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        documentrecycleview.setLayoutManager(linearLayoutManager3);

        ImageView selectfile = (ImageView) view.findViewById(R.id.selectfile);
        selectfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (upload_linearlayout.getVisibility() == View.GONE) {
                    upload_linearlayout.setVisibility(View.VISIBLE);
                } else if (upload_linearlayout.getVisibility() == View.VISIBLE) {
                    upload_linearlayout.setVisibility(View.GONE);
                }
            }
        });
        RelativeLayout take_photo = (RelativeLayout) view.findViewById(R.id.take_photo);
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        RelativeLayout file_library = (RelativeLayout) view.findViewById(R.id.file_library);
        file_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        RelativeLayout save_file = (RelativeLayout) view.findViewById(R.id.save_file);
        save_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_linearlayout.setVisibility(View.GONE);
                addFileFromFavoriteDialog = new AddFileFromFavoriteDialog(WatchCourseActivity2.this);
                addFileFromFavoriteDialog.setOnFavoriteDocSelectedListener(WatchCourseActivity2.this);
                addFileFromFavoriteDialog.show();
            }
        });
        documentPopupWindow = new PopupWindow(view, screenWidth,
                ViewGroup.LayoutParams.MATCH_PARENT);
        documentPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                findViewById(R.id.bottomrl).setVisibility(View.VISIBLE);
            }
        });
        documentPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        documentPopupWindow.setAnimationStyle(R.style.anination2);
        documentPopupWindow.setFocusable(true);

    }


    private MoreactionPopup moreactionPopup;

    private void moreActionPopupWindow() {
        moreactionPopup = new MoreactionPopup();
        moreactionPopup.getPopwindow(WatchCourseActivity2.this);
        moreactionPopup.setInvitePopupListener(new MoreactionPopup.InvitePopupListener() {
            @Override
            public void mute() {
                initListen(false);
                sendIsHidden(0);
            }

            @Override
            public void unmute() {
                initListen(true);
                sendIsHidden(1);
            }

            @Override
            public void debug() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wv_show.load("javascript:ShowDebugInfo('" + true + "')", null);
                    }
                });
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
            }

            @Override
            public void docrecord() {

            }


        });
        moreactionPopup.StartPop(testdebug, isHavePresenter(), false);
    }


    private void sendIsHidden(int stat) {
        JSONObject json = new JSONObject();
        try {
            json.put("stat", stat);
            json.put("actionType", 14);
            json.put("userId", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }


    /**
     * 会话 popupwindow
     *
     * @param
     */
    private String mGroupId;
    private ListView chatListview;
    private ChatAdapter chatAdapter;
    private List<TextMessage> chatMessages = new ArrayList<>();

    private void initChatAudienceList() {
        mGroupId = getResources().getString(R.string.Classroom) + meetingId;
//        Toast.makeText(WatchCourseActivity2.this, mGroupId, Toast.LENGTH_LONG).show();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater
                .inflate(R.layout.chat_pop, null);
        RelativeLayout auditor = (RelativeLayout) view.findViewById(R.id.auditor);
        auditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatPopupWindow.dismiss();
            }
        });
        chatListview = (ListView) view.findViewById(R.id.listview);
        chatAdapter = new ChatAdapter(WatchCourseActivity2.this, chatMessages);
        chatListview.setAdapter(chatAdapter);

        final EditText editText = (EditText) view.findViewById(R.id.edit);
        ImageView send = (ImageView) view.findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String context = editText.getText().toString();
                if (!TextUtils.isEmpty(context)) {
                    TextMessage myTextMessage = TextMessage.obtain(context);
                    myTextMessage.setExtra(AppConfig.UserID);
                    //发送message
                    Tools.sendMessage(WatchCourseActivity2.this, myTextMessage, mGroupId, AppConfig.UserID);
                    editText.setText("");
                }
            }
        });
        ImageView icon_socket_chat = (ImageView) view.findViewById(R.id.icon_socket_chat);
        icon_socket_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 打开群聊页面
                Tools.openGroup(WatchCourseActivity2.this, mGroupId);
                findViewById(R.id.bottomrl).setVisibility(View.GONE);
            }
        });
        chatPopupWindow = new PopupWindow(view, screenWidth,
                ViewGroup.LayoutParams.MATCH_PARENT);
        chatPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                findViewById(R.id.bottomrl).setVisibility(View.VISIBLE);
            }
        });
        chatPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        chatPopupWindow.setAnimationStyle(R.style.anination2);
        chatPopupWindow.setFocusable(true);

    }

    /**
     * 发送消息成功回调
     *
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(io.rong.imlib.model.Message message) {
        getGroupinfo(1);
    }


    private BroadcastReceiver getGroupbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getGroupinfo(1);
        }
    };

    public synchronized void getGroupinfo(int messageNumber) {
        RongIM.getInstance().getLatestMessages(Conversation.ConversationType.GROUP, mGroupId, messageNumber, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
            @Override
            public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                for (io.rong.imlib.model.Message message : messages) {
                    MessageContent m = message.getContent();
                    if (m instanceof TextMessage) {
                        chatMessages.add((TextMessage) m);
                    }
                }
                for (TextMessage message : chatMessages) {
                    for (int i1 = 0; i1 < teacorstudentList.size(); i1++) {
                        Customer customer = teacorstudentList.get(i1);
                        if (message.getExtra().equals(customer.getUserID())) {
                            message.setExtra(customer.getName());
                            break;
                        }
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    //锁屏、唤醒相关
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UnlockScreen();

                }
            });
        }
    };

    private void UnlockScreen() {
        Toast toast = new Toast(getApplicationContext());
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_null, (ViewGroup) findViewById(R.id.lin_null));
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(v);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    /**
     * 设置 presenter
     *
     * @param
     */
    private void initPopuptWindowPresenter(final Customer customer) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupWindow = layoutInflater
                .inflate(R.layout.course_popup, null);
        TextView cancel = (TextView) popupWindow.findViewById(R.id.cancel);
        TextView title = (TextView) popupWindow.findViewById(R.id.title);
        title.setText("Set presenter");
        RelativeLayout relativeLayout = (RelativeLayout) popupWindow.findViewById(R.id.ssss);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow2.dismiss();
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customer.isPresenter()) {

                } else {
                    if (customer.getUserID().equals(teacherCustomer.getUserID())) { //点击了老师按钮
                        sendStringBySocket3("MAKE_PRESENTER", teacherCustomer.getUsertoken(), meetingId, teacherCustomer.getUsertoken());

                    } else { //点击了某一个学生按钮
                        if (!customer.getUserID().equals(studentCustomer.getUserID())) { //此学生不是presenter
                            sendStringBySocket3("MAKE_PRESENTER", teacherCustomer.getUsertoken(), meetingId, customer.getUsertoken());

                        }
                    }
                }
                mPopupWindow2.dismiss();
            }
        });
        mPopupWindow2 = new PopupWindow(popupWindow, screenWidth - 30,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getWindow().getDecorView().setAlpha(1f);
            }
        });
        mPopupWindow2.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow2.setAnimationStyle(R.style.anination2);
        mPopupWindow2.setFocusable(true);
        mPopupWindow2.showAtLocation(wv_show, Gravity.BOTTOM, 0, 20);
        getWindow().getDecorView().setAlpha(0.5f);
    }

    /**
     * 提升旁听者
     *
     * @param
     */
    private void initPopuptWindowPromote(final Customer customer) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupWindow = layoutInflater
                .inflate(R.layout.course_popup, null);
        TextView cancel = (TextView) popupWindow.findViewById(R.id.cancel);
        TextView title = (TextView) popupWindow.findViewById(R.id.title);
        title.setText("Promote to student");
        RelativeLayout relativeLayout = (RelativeLayout) popupWindow.findViewById(R.id.ssss);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow4.dismiss();
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //提升旁听者为学生
                try {
                    JSONObject loginjson = new JSONObject();
                    loginjson.put("action", "PROMOTE_TO_STUDENT");
                    loginjson.put("sessionId", AppConfig.UserToken);
                    loginjson.put("meetingId", meetingId);
                    loginjson.put("userId", customer.getUserID());
                    String ss = loginjson.toString();
                    SpliteSocket.sendMesageBySocket(ss);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mPopupWindow4.dismiss();
            }
        });
        mPopupWindow4 = new PopupWindow(popupWindow, screenWidth - 30,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow4.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getWindow().getDecorView().setAlpha(1f);
            }
        });
        mPopupWindow4.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow4.setAnimationStyle(R.style.anination2);
        mPopupWindow4.setFocusable(true);
        mPopupWindow4.showAtLocation(wv_show, Gravity.BOTTOM, 0, 20);
        getWindow().getDecorView().setAlpha(0.5f);

    }

    private int currentPosition = -1;
    private FavoriteVideoPopup favoritePopup;

    private void openSaveVideoPopup() {
        favoritePopup = new FavoriteVideoPopup();
        favoritePopup.getPopwindow(WatchCourseActivity2.this);
        favoritePopup.setFavoritePoPListener(new FavoriteVideoPopup.FavoriteVideoPoPListener() {
            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);
            }

            @Override
            public void selectFavorite(int position) {
                currentPosition = position;
            }

            @Override
            public void cancel() {
                wv_show.load("javascript:VideoTagAfterSelect(" + null + ")", null);
            }

            @Override
            public void save(int type, boolean isYinxiang) {
                if (type == 2) {  //video
                    if (currentPosition >= 0) {
                        JSONObject jsonObject = favoritePopup.getData().get(currentPosition).getJsonObject();
                        wv_show.load("javascript:VideoTagAfterSelect(" + jsonObject + ")", null);
                    }
                } else if (type == 3) {  // audio
                    if (currentPosition >= 0) {
                        JSONObject jsonObject = favoritePopup.getData().get(currentPosition).getJsonObject();
                        wv_show.load("javascript:VideoTagAfterSelect(" + jsonObject + ")", null);
                    }
                }


            }

            @Override
            public void uploadFile() {

            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.addauditorimageview: //添加旁听者
                if (identity == 1 || identity == 2) {
                    Intent startAuditor = new Intent(WatchCourseActivity2.this, AddAuditorActivity.class);
                    startAuditor.putExtra("mAttendesList", (Serializable) teacorstudentList);
                    startAuditor.putExtra("teacher", teacherCustomer);
                    startAuditor.putExtra("student", studentCustomer);
                    startActivity(startAuditor);
                }
                break;
            case R.id.menu: //弹出file audience chat列表
                if (menu_linearlayout.getVisibility() == View.VISIBLE) {
                    menu_linearlayout.setVisibility(View.GONE);
                    menu.setImageResource(R.drawable.icon_menu);
                } else if (menu_linearlayout.getVisibility() == View.GONE) {
                    if (isHavePresenter()) {
                        displayrecord.setVisibility(View.VISIBLE);
                        TextView startrecordcontent = (TextView) findViewById(R.id.startrecordcontent);
                        ImageView recordicon = (ImageView) findViewById(R.id.recordicon);

                    } else {
                        displayrecord.setVisibility(View.GONE);
                    }
                    menu_linearlayout.setVisibility(View.VISIBLE);
                    findViewById(R.id.hiddenwalkview).setVisibility(View.VISIBLE);
                    menu.setImageResource(R.drawable.icon_menu_active);
                    activte_linearlayout.setVisibility(View.GONE);
                    command_active.setImageResource(R.drawable.icon_command);
                }
                break;
            case R.id.command_active:  // 弹出语音
                if (activte_linearlayout.getVisibility() == View.VISIBLE) {
                    activte_linearlayout.setVisibility(View.GONE);
                    command_active.setImageResource(R.drawable.icon_command);
                } else if (activte_linearlayout.getVisibility() == View.GONE) {
                    activte_linearlayout.setVisibility(View.VISIBLE);
                    findViewById(R.id.hiddenwalkview).setVisibility(View.VISIBLE);
                    command_active.setImageResource(R.drawable.icon_command_active);
                    menu_linearlayout.setVisibility(View.GONE);
                    menu.setImageResource(R.drawable.icon_menu);
                }
                break;
            case R.id.refresh_notify_2:
                if (teacorstudentList.size() + auditorList.size() > 1) {
                    if (mWebSocketClient != null) {
                        try {
                            JSONObject loginjson = new JSONObject();
                            loginjson.put("action", "LEAVE_MEETING");
                            loginjson.put("sessionId", AppConfig.UserToken);
                            String ss = loginjson.toString();
                            SpliteSocket.sendMesageBySocket(ss);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mProgressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent();
                intent.setAction("com.cn.refreshsocket");
                sendBroadcast(intent);
                break;
            case R.id.displayAudience:
                mPopupWindow1.showAtLocation(wv_show, Gravity.BOTTOM, 0, 0);
                menu_linearlayout.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                findViewById(R.id.bottomrl).setVisibility(View.GONE);
                break;
            case R.id.displayFile:
                documentPopupWindow.showAtLocation(wv_show, Gravity.BOTTOM, 0, 0);
                menu_linearlayout.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                findViewById(R.id.bottomrl).setVisibility(View.GONE);
                break;
            case R.id.leavell:
                notifyleave();
                closeCourse(0);
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                finish();
                break;
            case R.id.endll:
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                endDialog();
                break;
            case R.id.startll:
                startCourse();
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                break;
            case R.id.bindidll:
                bindDevice();
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                break;
            case R.id.testdebug:
                moreActionPopupWindow();
                break;
            case R.id.joinvideo:
                openshengwang(0);
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                break;
            case R.id.callmelater:
                callMeOrLater(identity, callMeLaterPhone);
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                break;
            case R.id.displaychat:
                chatPopupWindow.showAtLocation(wv_show, Gravity.BOTTOM, 0, 0);
                menu_linearlayout.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                findViewById(R.id.bottomrl).setVisibility(View.GONE);
                break;
            case R.id.displaywebcam:
                if (mViewType == VIEW_TYPE_DEFAULT) {
                    switchToBigVideoView();
                    changevideo(1, "");
                } else if (mViewType == VIEW_TYPE_NORMAL || mViewType == VIEW_TYPE_SING_NORMAL) {
                    switchToDefaultVideoView();
                    changevideo(0, "");
                }
                menu_linearlayout.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                findViewById(R.id.bottomrl).setVisibility(View.VISIBLE);
                break;

            case R.id.displayvideo:
                videoPopuP.startVideoPop(wv_show, menu_linearlayout, menu);
                videoPopuP.setPresenter(identity,
                        currentPresenterId,
                        studentCustomer, teacherCustomer);
                break;
            case R.id.displayrecord:

                menu_linearlayout.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                findViewById(R.id.bottomrl).setVisibility(View.VISIBLE);

                break;
            case R.id.setting:
                menu_linearlayout.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                findViewById(R.id.settingll).setVisibility(View.VISIBLE);
                rightViewEnter();
                break;
            case R.id.settingllback:
                if (right2.getVisibility() == View.VISIBLE) {
                    right2Out("");
                } else if (right3.getVisibility() == View.VISIBLE) {
                    right3Out("");
                } else {
                    rightViewOut();
                }
                break;
            case R.id.setting2:
                menu_linearlayout.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                findViewById(R.id.settingll2).setVisibility(View.VISIBLE);
                break;
            case R.id.settingllback2:
                findViewById(R.id.settingll2).setVisibility(View.GONE);
                break;
            case R.id.selectwebcam:
                right1.setVisibility(View.INVISIBLE);
                right3.setVisibility(View.INVISIBLE);
                right2.setVisibility(View.VISIBLE);
                right2Enter();
                break;
            case R.id.selectconnect:
                right1.setVisibility(View.INVISIBLE);
                right2.setVisibility(View.INVISIBLE);
                right3.setVisibility(View.VISIBLE);
                right3Enter();
                break;
            case R.id.bigRecyler:
                bigimage.setVisibility(View.VISIBLE);
                middleimage.setVisibility(View.INVISIBLE);
                smallimage.setVisibility(View.INVISIBLE);
                findViewById(R.id.settingll2).setVisibility(View.GONE);
                LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mLeftRecycler.getLayoutParams();
                linearParams.width = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()));
                mLeftRecycler.setLayoutParams(linearParams);
                mLeftAgoraAdapter.setHeightByCount(3);
                break;
            case R.id.middleRecyler:
                bigimage.setVisibility(View.INVISIBLE);
                middleimage.setVisibility(View.VISIBLE);
                smallimage.setVisibility(View.INVISIBLE);
                findViewById(R.id.settingll2).setVisibility(View.GONE);
                LinearLayout.LayoutParams linearParams2 = (LinearLayout.LayoutParams) mLeftRecycler.getLayoutParams();
                linearParams2.width = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()));
                mLeftRecycler.setLayoutParams(linearParams2);
                mLeftAgoraAdapter.setHeightByCount(5);
                break;
            case R.id.smallRecyler:
                bigimage.setVisibility(View.INVISIBLE);
                middleimage.setVisibility(View.INVISIBLE);
                smallimage.setVisibility(View.VISIBLE);
                findViewById(R.id.settingll2).setVisibility(View.GONE);
                LinearLayout.LayoutParams linearParams3 = (LinearLayout.LayoutParams) mLeftRecycler.getLayoutParams();
                linearParams3.width = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
                mLeftRecycler.setLayoutParams(linearParams3);
                mLeftAgoraAdapter.setHeightByCount(7);
                break;
            case R.id.select240:
                right2Out("Webcam240p");
                setArrow(1);
                break;
            case R.id.select360:
                right2Out("Webcam360p");
                setArrow(2);
                break;
            case R.id.select480:
                right2Out("Webcam480p");
                setArrow(3);
                break;
            case R.id.select720:
                right2Out("Webcam720p");
                setArrow(4);
                break;
            case R.id.peertimebase:
                if (mode != 0) {
                    right3value = "PeerTime-Based";
                    setRight3Arrow(0);
                }
                break;
            case R.id.kloudcall:
                if (mode != 1) {
                    right3value = "KloudCall";
                    setRight3Arrow(1);
                }
                break;
            case R.id.external:
                if (mode != 2) {
                    right3value = "External Tools or No Audio";
                    setRight3Arrow(2);
                }
                break;
            case R.id.right3bnt:
                if (mode == runnmode) { //选择相同

                } else {
                    TextView tv = (TextView) findViewById(R.id.right2value);
                    tv.setText(right3value);
                    switchline(mode);
                }
                findViewById(R.id.settingll).setVisibility(View.GONE);
                break;
            case R.id.leftview:
                findViewById(R.id.settingll).setVisibility(View.GONE);
                break;
            case R.id.leftview2:
                findViewById(R.id.settingll2).setVisibility(View.GONE);
                break;
            case R.id.llpre:
                break;
            case R.id.leavepre:
                notifyleave();
                closeCourse(0);
                finish();
                break;
            case R.id.closeVideo:
                icon_command_mic_enabel.setEnabled(true);
                initListen(true);
                if (isFileVideo) {
                    sendVideoSocket(VIDEOSTATUSCLOSE, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0);  //  Close
                } else {
                    changevideo(0, "");
                }
                videoPopuP.notifyDataChange(-1);
                findViewById(R.id.videoll).setVisibility(View.GONE);
                videoView.suspend();
                videoView.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private YinxiangPopup yinxiangPopup;


    private void JsonDown2(final Uploadao ud, final SoundtrackBean soundtrackBean) {
        final String playurl = soundtrackBean.getNewAudioInfo().getFileDownloadURL();
        newPath = playurl.substring(playurl.indexOf(".com") + 5, playurl.lastIndexOf("/"));
        Log.e("当前录音信息", playurl + "       " + newPath);
        ServiceInterfaceTools.getinstance().queryDownloading(AppConfig.URL_LIVEDOC + "queryDownloading", ServiceInterfaceTools.QUERYDOWNLOADING, ud,
                newPath, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        // https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D44811/20190527025334.wav
                        //拼接  url
                        String jsonstring = (String) object;
                        Log.e("当前录音信息json", jsonstring);
                        int xx = transfering(jsonstring);
                        if (2 == xx) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    JsonDown2(ud, soundtrackBean);
                                }
                            }, 1000);
                        } else if (1 == xx) {
                            String filename = playurl.substring(playurl.lastIndexOf("/") + 1);
                            String newpath = "";
                            if (1 == ud.getServiceProviderId()) {
                                newpath = "https://s3." + ud.getRegionName() + ".amazonaws.com/" + ud.getBucketName() + "/" + newPath + "/" + filename;
                            } else if (2 == ud.getServiceProviderId()) {
                                newpath = "https://" + ud.getBucketName() + "." + ud.getRegionName() + "." + "aliyuncs.com" + "/" + newPath + "/" + filename;
                            }
                            //https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D24893/3fffe932-5e52-4dbb-8376-9436a2de4dbe_1_2K.jpg
                            Log.e("当前录音信息", "url  " + newpath);

                            GetMediaPlay2(newpath);

                        } else {
                            Toast.makeText(WatchCourseActivity2.this, "Not find the file.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private int soundtrackID = -1;


    private void getyinxiangdetail(final int soundtrackID2) {
        ServiceInterfaceTools.getinstance().getSoundItem(AppConfig.URL_PUBLIC + "Soundtrack/Item?soundtrackID=" + soundtrackID2,
                ServiceInterfaceTools.GETSOUNDITEM,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        final SoundtrackBean soundtrackBean = (SoundtrackBean) object;
                        String duration = soundtrackBean.getDuration();
                        soundtrackID = soundtrackBean.getSoundtrackID();
                        openAudioSync(TextUtils.isEmpty(duration) ? 0 : Long.parseLong(duration));
                        if (soundtrackBean.getBackgroudMusicAttachmentID() != 0) {
                            GetMediaPlay(soundtrackBean.getBackgroudMusicInfo().getFileDownloadURL());
                        } else {
                            GetMediaPlay("");
                        }
                        // 播放录音
                        if (soundtrackBean.getNewAudioAttachmentID() != 0) {
                            if (uploadao.getServiceProviderId() == 0) {
                                //1  拿 Bucket 信息
                                LoginGet lg = new LoginGet();
                                lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
                                    @Override
                                    public void getUD(final Uploadao ud) {
                                        uploadao = ud;
                                        // 2 调queryDownloading接口
                                        JsonDown2(ud, soundtrackBean);
                                    }
                                });
                                lg.GetprepareUploading(WatchCourseActivity2.this);
                            } else {
                                JsonDown2(uploadao, soundtrackBean);
                            }
                        } else if (soundtrackBean.getSelectedAudioAttachmentID() != 0) {
                            GetMediaPlay2(soundtrackBean.getSelectedAudioInfo().getFileDownloadURL());
                        }
                    }
                });
    }


    private int runnmode, mode = 0;
    private String right3value;

    private void switchline(int mode) {
        switch (mode) {
            case 0:  //切换peertime
                sendMessage(0);
                break;
            case 1: //kloudphone
                sendMessage(2);
                break;
            case 2:
                sendMessage(4);
                break;
            default:
                break;
        }
    }

    private void sendMessage(int value) {
        if (value == 0) {//切回peertime
            switcKcOrPeerTime(0);
            endConference2();
            runnmode = 0;
        } else if (value == 2) {  //kloudphone
            createConference(AppConfig.Mobile);
            runnmode = 1;
        } else if (value == 4) {  //External Tools or No Audio
            switcKcOrPeerTime(4);
            runnmode = 2;
        }
    }

    private void switcKcOrPeerTime(int mode) {
        JSONObject json = new JSONObject();
        try {
            json.put("actionType", 16);
            json.put("lineId", mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
        send_message("SEND_MESSAGE", AppConfig.UserToken, 1, teacherid, Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }

    private void createConference(String callPhone) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", "CREATE_KLOUD_CALL_CONFERENCE");
            json.put("sessionId", AppConfig.UserToken);
            json.put("phoneNumber", callPhone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String ss = json.toString();
        SpliteSocket.sendMesageBySocket(ss);
    }


    private NotificationPopup startLessonPopup;

    private void callMeOrLater(final int identity, String phoneNumber) {
        if (startLessonPopup != null && startLessonPopup.isShowing()) {
            return;
        }
        startLessonPopup = new NotificationPopup();
        startLessonPopup.getPopwindow(WatchCourseActivity2.this, identity, phoneNumber);
        startLessonPopup.setStartLessonPopupListener(new NotificationPopup.StartLessonPopupListener() {
            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);
            }

            @Override
            public void callMe(String callPhone) {
                scallMe(callPhone);
            }

            @Override
            public void callLater(String callPhone) {
                callMeLaterPhone = callPhone;
            }

            @Override
            public void endConference() {
                endConference2();
            }
        });
        startLessonPopup.StartPop(findViewById(R.id.mainmain));
    }

    private String callMeLaterPhone;

    private void scallMe(String callPhone) {
        JSONObject json = new JSONObject();
        try {
            json.put("action", "CALL_ME");
            json.put("sessionId", AppConfig.UserToken);
            json.put("phoneNumber", callPhone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String ss = json.toString();
        SpliteSocket.sendMesageBySocket(ss);
    }

    private void endConference2() {
        JSONObject json = new JSONObject();
        try {
            json.put("action", "END_KLOUD_CALL_CONFERENCE");
            json.put("sessionId", AppConfig.UserToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String ss = json.toString();
        SpliteSocket.sendMesageBySocket(ss);
    }

    private void rightViewEnter() {
        leftview.setVisibility(View.INVISIBLE);
        final LinearLayout rightview = (LinearLayout) findViewById(R.id.rightview);
        final float curTranslationX3 = rightview.getTranslationX();
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(rightview, "translationX", curTranslationX3 + screenWidth / 2, curTranslationX3).setDuration(300);
        animator3.setDuration(300);
        animator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                rightview.setTranslationX(curTranslationX3);
                leftview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator3.start();
    }


    private void rightViewOut() {
        final LinearLayout rightview = (LinearLayout) findViewById(R.id.rightview);
        final float curTranslationX3 = rightview.getTranslationX();
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(rightview, "translationX", curTranslationX3, curTranslationX3 + screenWidth / 2).setDuration(300);
        animator3.setDuration(300);
        animator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                rightview.setTranslationX(curTranslationX3);
                leftview.setVisibility(View.GONE);
                findViewById(R.id.settingll).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator3.start();
    }

    private void right2Enter() {
        final float curTranslationX = right2.getTranslationX();
        ObjectAnimator animator = ObjectAnimator.ofFloat(right2, "translationX", curTranslationX + screenWidth / 2, curTranslationX).setDuration(300);
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                right2.setTranslationX(curTranslationX);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private void right2Out(final String value) {
        final float curTranslationX = right2.getTranslationX();
        ObjectAnimator animator = ObjectAnimator.ofFloat(right2, "translationX", curTranslationX, curTranslationX + screenWidth / 2);
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                right1.setVisibility(View.VISIBLE);
                right2.setVisibility(View.INVISIBLE);
                right3.setVisibility(View.INVISIBLE);
                if (!TextUtils.isEmpty(value)) {
                    TextView tv = (TextView) findViewById(R.id.right1value);
                    tv.setText(value);
                }
                right2.setTranslationX(curTranslationX);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private void right3Enter() {
        final float curTranslationX2 = right3.getTranslationX();
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(right3, "translationX", curTranslationX2 + screenWidth / 2, curTranslationX2);
        animator2.setDuration(300);
        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                right3.setTranslationX(curTranslationX2);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator2.start();
    }

    private void right3Out(final String value) {
        final float curTranslationX = right3.getTranslationX();
        ObjectAnimator animator = ObjectAnimator.ofFloat(right3, "translationX", curTranslationX, curTranslationX + screenWidth / 2);
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                right1.setVisibility(View.VISIBLE);
                right2.setVisibility(View.INVISIBLE);
                right3.setVisibility(View.INVISIBLE);
                if (!TextUtils.isEmpty(value)) {
                    TextView tv = (TextView) findViewById(R.id.right2value);
                    tv.setText(value);
                }
                right3.setTranslationX(curTranslationX);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private void setArrow(int i) {
        ImageView right21 = (ImageView) findViewById(R.id.right21);
        ImageView right22 = (ImageView) findViewById(R.id.right22);
        ImageView right23 = (ImageView) findViewById(R.id.right23);
        ImageView right24 = (ImageView) findViewById(R.id.right24);
        right21.setVisibility(View.GONE);
        right22.setVisibility(View.GONE);
        right23.setVisibility(View.GONE);
        right24.setVisibility(View.GONE);
        switch (i) {
            case 1:
                right21.setVisibility(View.VISIBLE);
                break;
            case 2:
                right22.setVisibility(View.VISIBLE);
                break;
            case 3:
                right23.setVisibility(View.VISIBLE);
                break;
            case 4:
                right24.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void setRight3Arrow(int i) {
        mode = i;
        ImageView right31 = (ImageView) findViewById(R.id.right31);
        ImageView right32 = (ImageView) findViewById(R.id.right32);
        ImageView right33 = (ImageView) findViewById(R.id.right33);
        right31.setVisibility(View.GONE);
        right32.setVisibility(View.GONE);
        right33.setVisibility(View.GONE);
        switch (i) {
            case 0:
                right31.setVisibility(View.VISIBLE);
                break;
            case 1:
                right32.setVisibility(View.VISIBLE);
                break;
            case 2:
                right33.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 老师开始上课
     */
    private void startCourse() {
        JSONObject json = new JSONObject();
        try {
            json.put("meetingID", meetingId);
            json.put("sourceID", teacherid);
            json.put("targetID", studentid);
            json.put("incidentID", "");
            json.put("roleType", "1");
            json.put("attachmentUrl", targetUrl);
            json.put("actionType", 1);
            json.put("isH5", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 1, studentid, Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
        sendvoicenet("MEETING_STATUS", AppConfig.UserToken, meetingId, 1, meetingId);
    }

    private WebCamPopup webCamPopuP;
    private boolean isOpenShengwang = false;

    private void openshengwang(int i) {
        initListen(false);
        initMute(false);
        isOpenShengwang = true;
        isShowDefaultVideo = true;
        switchMode();
        toggle.setVisibility(View.VISIBLE);
        joinvideo.setVisibility(View.GONE);
        startll.setVisibility(View.GONE);
        leavell.setVisibility(View.VISIBLE);
        endtextview.setText("End");
        if (currentLine == LINE_PEERTIME) {  //  声网  模式

        } else if (currentLine == LINE_KLOUDPHONE) {   // kloudcall  模式
            createConference(AppConfig.Mobile);
        } else if (currentLine == LINE_EXTERNOAUDIO) {  //  no audio  模式
            switcKcOrPeerTime(4);
        }

    }

    private DetchedPopup detchedPopup;

    private void detectPopwindow(int countDown) {
        detchedPopup = new DetchedPopup();
        detchedPopup.getPopwindow(WatchCourseActivity2.this, (RelativeLayout) findViewById(R.id.layout), countDown);
        detchedPopup.setDetchedPopupListener(new DetchedPopup.DetchedPopupListener() {
            @Override
            public void closeNow() {
                detchedPopup.dismiss();
                endDialog();
            }
        });
        detchedPopup.StartPop(findViewById(R.id.mainmain));
    }


    private void endDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("")
                .setMessage("Do you want to save Instant Lesson?")
                .setPositiveButton("Save and Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject jsonObject = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/SaveInstantLesson?lessonID=" + meetingId, null);
                                Log.e("ennnnnnnnnnd", jsonObject.toString());
                                try {
                                    if (jsonObject.getInt("RetCode") == 0) {
                                        notifyend();
                                        closeCourse(1);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                })
                .setNegativeButton("Exit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.dismiss();
                                notifyend();
                                closeCourse(1);
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //-----------------------------------------------video-------------------
    private VideoPopup videoPopuP;
    private final int VIDEOSTATUSPLAY = 1;
    private final int VIDEOSTATUSPAUSE = 0;
    private final int VIDEOSTATUSCLOSE = 2;
    private String currentPlayVideoId;
    private int videoDuration; //视频总长度

    private void initVideoPopup() {
        videoPopuP = new VideoPopup();
        videoPopuP.getPopwindow(WatchCourseActivity2.this, screenWidth, (RelativeLayout) findViewById(R.id.bottomrl));
        // 两种情况
        videoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
            @Override
            public void onPlay() {
                if (identity == 1) { // 学生
                    if (TextUtils.isEmpty(studentCustomer.getUserID())) {
                        return;
                    }
                    if (studentCustomer.getUserID().equals(AppConfig.UserID.replace("-", ""))) {
                        initListen(false);
                        icon_command_mic_enabel.setEnabled(false);
                        sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0); //播放

                    }
                } else if (identity == 2) {
                    if (currentPresenterId.equals(teacherCustomer.getUserID())) {
                        initListen(false);
                        icon_command_mic_enabel.setEnabled(false);
                        sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0); //播放
                    }
                }
            }

            @Override
            public void onPause() {
                if (identity == 1) { // 学生
                    if (TextUtils.isEmpty(studentCustomer.getUserID())) {
                        return;
                    }
                    if (studentCustomer.getUserID().equals(AppConfig.UserID.replace("-", ""))) {
                        initListen(true);
                        sendVideoSocket(VIDEOSTATUSPAUSE, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0); //暂停
                    }
                } else if (identity == 2) {
                    if (currentPresenterId.equals(teacherCustomer.getUserID())) {
                        initListen(true);
                        sendVideoSocket(VIDEOSTATUSPAUSE, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0);
                    }
                }
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoDuration = videoView.getDuration();
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                        int currentPosition = videoView.getCurrentPosition();
                        Log.e("onBufferingUpdate", "当前播放时间    " + currentPosition + "");
                    }
                });
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (isHavePresenter()) {
                    initListen(true);
                    icon_command_mic_enabel.setEnabled(true);
                    sendVideoSocket(VIDEOSTATUSCLOSE, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0);  //  Close
                    videoPopuP.notifyDataChange(-1);
                    findViewById(R.id.videoll).setVisibility(View.GONE);
                    videoView.suspend();
                    videoView.setVisibility(View.GONE);
                    if (isFileVideo) {  // html
                        isFileVideo = false;
                    } else {
                        changevideo(0, "");
                    }
                }
                resumeYinxiang();
            }
        });


        videoPopuP.setOnVideoListener(new VideoPopup.VideoListener() {
            @Override
            public void selectVideo() {

            }

            @Override
            public void takePhoto() {

            }

            @Override
            public void selectPlayVideo(final int position) {  // 选择视频播放
                LineItem lineitem = videoList.get(position);
                if (!lineitem.isSelect()) {
                    videoPopuP.notifyDataChange(position);
                    videoGestureRelativeLayout.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    playVideo(lineitem.getUrl(), 0);
                    //设置当前 currentMode为4
                    changevideo(4, "");
                    //播放通知
                    currentPlayVideoId = videoList.get(position).getAttachmentID();
                    initListen(false);
                    icon_command_mic_enabel.setEnabled(false);
                    playUrl = lineitem.getUrl();
                    sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0);
                }
            }

            @Override
            public void openSaveVideo() { // 打开我的收藏 video


            }
        });
    }

    //-----------------------------------手势回调---------------------
    @Override
    public void onDown(MotionEvent e) {
        //每次按下的时候更新当前亮度和音量，还有进度
        oldProgress = newProgress;
        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        brightness = mLayoutParams.screenBrightness;
        if (brightness == -1) {
            //一开始是默认亮度的时候，获取系统亮度，计算比例值
            brightness = mBrightnessHelper.getBrightness() / 255f;
        }
    }

    //音量回调
    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int value = videoGestureRelativeLayout.getHeight() / maxVolume;  //最大音量
        int newVolume = (int) ((e1.getY() - e2.getY()) / value + oldVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND);
        //要强行转Float类型才能算出小数点，不然结果一直为0
        int volumeProgress = (int) (newVolume / Float.valueOf(maxVolume) * 100);
        if (volumeProgress >= 50) {
            showChangeLayout.setImageResource(R.drawable.volume_higher_w);
        } else if (volumeProgress > 0) {
            showChangeLayout.setImageResource(R.drawable.volume_lower_w);
        } else {
            showChangeLayout.setImageResource(R.drawable.volume_off_w);
        }
        showChangeLayout.setProgress(volumeProgress);  //设置进度条
        showChangeLayout.show();
    }

    /**
     * APP亮度的方法
     *
     * @param e1
     * @param e2
     * @param distanceX
     * @param distanceY
     */
    @Override
    public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float newBrightness = (e1.getY() - e2.getY()) / videoGestureRelativeLayout.getHeight();
        Log.e("ddddd", newBrightness + "ffff");
        newBrightness += brightness;
        if (newBrightness < 0) {
            newBrightness = 0;
        } else if (newBrightness > 1) {
            newBrightness = 1;
        }
        mLayoutParams.screenBrightness = newBrightness;
        mWindow.setAttributes(mLayoutParams);
        showChangeLayout.setProgress((int) (newBrightness * 100));
        showChangeLayout.setImageResource(R.drawable.brightness_w);
        showChangeLayout.show();
    }

    /**
     * 快进快退手势，手指在Layout左右滑动的时候调用
     */
    @Override
    public void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float offset = e2.getX() - e1.getX();
        //根据移动的正负决定快进还是快退
        if (offset > 0) {
            showChangeLayout.setImageResource(R.drawable.ff);
            newProgress = (int) (oldProgress + offset / videoGestureRelativeLayout.getWidth() * 100);
            if (newProgress > 100) {
                newProgress = 100;
            }
        } else {
            showChangeLayout.setImageResource(R.drawable.fr);
            newProgress = (int) (oldProgress + offset / videoGestureRelativeLayout.getWidth() * 100);
            if (newProgress < 0) {
                newProgress = 0;
            }
        }
        showChangeLayout.setProgress(newProgress);
        showChangeLayout.show();
    }

    @Override
    public void onEndFF_REW(MotionEvent e) {  // 0---100
        if (identity == 1) { // 学生
            if (TextUtils.isEmpty(studentCustomer.getUserID())) {
                return;
            }
            if (studentCustomer.getUserID().equals(AppConfig.UserID.replace("-", ""))) {
                videoView.seekTo(newProgress * videoDuration / 100);
                sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0); //播放
            }
        } else if (identity == 2) { //老师端 绘制
            if (currentPresenterId.equals(teacherCustomer.getUserID())) {
                videoView.seekTo(newProgress * videoDuration / 100);
                sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0); //播放
            }
        }
    }

    @Override
    public void onSingleTapGesture(MotionEvent e) {

    }

    @Override
    public void onDoubleTapGesture(MotionEvent e) {

    }

    //-----------------------------------手势回调---------------------
    private void openMySavePopup(final List<Favorite> favoriteList, final int type) {
        final FavoritePopup favoritePopup = new FavoritePopup();
        favoritePopup.getPopwindow(WatchCourseActivity2.this, favoriteList, type);
        favoritePopup.setFavoritePoPListener(new FavoritePopup.FavoritePoPListener() {
            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);
            }

            @Override
            public void selectFavorite(int position) {
                favoritePopup.dismiss();
                final Favorite favorite = favoriteList.get(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js = new JSONObject();
                            js.put("lessonID", meetingId);
                            js.put("itemIDs", favorite.getItemID());
                            JSONObject jsonObject = ConnectService.submitDataByJson
                                    (AppConfig.URL_PUBLIC + "EventAttachment/UploadFromFavorite?lessonID=" + meetingId + "&itemIDs=" + favorite.getItemID(), js);
                            Log.e("save_file", js.toString() + "   " + jsonObject.toString());
                            Message msg = Message.obtain();
                            msg.what = 0x6002;
                            msg.obj = type;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void uploadFile() {
                favoritePopup.dismiss();

            }
        });
        favoritePopup.StartPop(findViewById(R.id.mainmain));
    }


    /**
     * @param state        当前播放状态 status == 0,暂停，== 1 播放，== 2:Close
     * @param time         时长
     * @param attachmentID 视频vid
     * @param url          video url
     * @param videotype    video type 0: video in list 1: video in file
     */
    private void sendVideoSocket(int state, float time, String attachmentID, String url, int videotype) {
        JSONObject json = new JSONObject();
        try {
            json.put("stat", state);
            json.put("actionType", 19);
            json.put("time", time);
            json.put("vid", attachmentID);
            json.put("url", url);
            json.put("type", isFileVideo ? 1 : 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }

    /**
     * 播放音频socket
     *
     * @param state
     * @param
     */
    private void sendAudioSocket(int state, int soundtrackID) {
        JSONObject json = new JSONObject();
        try {
            json.put("actionType", 23);
            json.put("soundtrackId", soundtrackID);
            json.put("stat", state);
            if (isHavePresenter() && state == 4) {
                json.put("time", tttime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }


    private void getjspagenumber() {
        wv_show.evaluateJavascript("javascript:GetCurrentPageNumber()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.e("GetCurrentPageNumber", s);
                int id = 1;
                if (TextUtils.isEmpty(s)) {

                } else {
                    id = Integer.parseInt(s);
                }
                JSONObject js = new JSONObject();
                try {
                    js.put("type", 2);
                    js.put("page", id);
                    js.put("time", 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String newresult = Tools.getBase64(js.toString()).replaceAll("[\\s*\t\n\r]", "");
                try {
                    JSONObject loginjson = new JSONObject();
                    loginjson.put("action", "ACT_FRAME");
                    loginjson.put("sessionId", teacherCustomer.getUsertoken());
                    loginjson.put("retCode", 1);
                    loginjson.put("data", newresult);
                    loginjson.put("itemId", currentAttachmentId);
                    loginjson.put("sequenceNumber", "3837");
                    loginjson.put("ideaType", "document");
                    String ss = loginjson.toString();
                    SpliteSocket.sendMesageBySocket(ss);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 学生收到消息是否播放视频
     *
     * @param stat
     * @param time
     * @param attachmentId
     */
    private void startOrPauseVideo(int stat, float time, String attachmentId, String url, int videotype) {
        Log.e("学生收到消息是否播放视频", stat + "    " + time + "         " + attachmentId + "    " + url + "    " + videotype);
        //关闭video
        if (stat == VIDEOSTATUSPLAY) {  //play
            initListen(false);
            icon_command_mic_enabel.setEnabled(false);
            if (videoView.isPlaying()) {
                Log.e("学生收到消息是否播放视频", "1");
                videoView.seekTo((int) time * 1000);
            } else {
                // 找到播放地址---------------------------
                String playurl = "";
                if (videotype == 0) {
                    for (int i = 0; i < videoList.size(); i++) {
                        LineItem item = videoList.get(i);
                        if (item.getAttachmentID().equals(attachmentId)) {
                            videoPopuP.notifyDataChange(i);
                            playurl = item.getUrl();
                            break;
                        }
                    }
                } else if (videotype == 1) {
                    playurl = url;
                }
                Log.e("学生收到消息是否播放视频", stat + "    " + time + "         " + attachmentId + "    " + playurl + "    " + videotype);
                //找到播放地址---------------------------
                if (!TextUtils.isEmpty(playurl)) {
                    videoGestureRelativeLayout.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    playVideo(playurl, time);
                }
            }
        } else if (stat == VIDEOSTATUSPAUSE) { // pause
            initListen(true);
            if (videoView.isPlaying()) {
                videoView.pause();
            }
        } else if (stat == VIDEOSTATUSCLOSE) {  // close
            initListen(true);
            icon_command_mic_enabel.setEnabled(true);
            videoPopuP.notifyDataChange(-1);
            videoGestureRelativeLayout.setVisibility(View.GONE);
            videoView.suspend();
            videoView.setVisibility(View.GONE);
            resumeYinxiang();
        }
    }


    private boolean isFileVideo = false;
    /**
     * html 播放视频
     *
     * @param attachmentId
     */
    String playUrl;

    private void webVideoPlay(final int vid, final boolean playorrecord, final int isRecording) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC +
                            "FavoriteAttachment/GetFavoriteAttachmentByID?itemID=" + vid);
                    Log.e("MyFavoriteAttachments", returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        final JSONObject jsonObject = returnjson.getJSONObject("RetData");
                        final String url = jsonObject.getString("AttachmentUrl");
                        final int filetype = jsonObject.getInt("FileType");

                        favoriteAudio = new Favorite();
                        favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                        favoriteAudio.setItemID(jsonObject.getInt("ItemID"));
                        favoriteAudio.setTitle(jsonObject.getString("Title"));
                        favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (filetype == 4) {  //处理视频
                                    findViewById(R.id.videoll).setVisibility(View.VISIBLE);
                                    videoView.setVisibility(View.VISIBLE);
                                    playUrl = url;
                                    isFileVideo = true;
                                    playVideo(playUrl, 0.0f);
                                    // changevideo(4, "");  // 当前仍处于声网模式
                                    //播放通知
                                    currentPlayVideoId = vid + "";
                                    initListen(false); //
                                    icon_command_mic_enabel.setEnabled(false);
                                    sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 1); //1 html 播放
                                } else if (filetype == 5) {  // 处理音频

                                }
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        }).start();

    }


    /**
     * 拿随 某时刻后面 20秒  音向 的Actions
     *
     * @param
     */
    private List<AudioActionBean> audioActionBeanList = new ArrayList<>();
    private int startTimee;

    private void getAudioAction(final int soundtrackID, int startTime) {
        startTimee = startTime;
        String url = AppConfig.URL_PUBLIC + "Soundtrack/SoundtrackActions?soundtrackID=" + soundtrackID + "&startTime=" + startTime + "&endTime=" + (startTime + 20000);
        ServiceInterfaceTools.getinstance().getSoundtrackActions(url, ServiceInterfaceTools.GETSOUNDTRACKACTIONS, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<AudioActionBean> audioActionBeanList2 = (List<AudioActionBean>) object;
                audioActionBeanList.clear();
                if (audioActionBeanList2.size() > 0) {
                    audioActionBeanList.addAll(audioActionBeanList2);
                }
            }
        });
    }

    private void newAudioActionTime(int locateTime) {
        Log.e("newAudioActionTime", audioActionBeanList.size() + "    当前播放器的位置 " + locateTime);
        for (int i1 = 0; i1 < audioActionBeanList.size(); i1++) {
            AudioActionBean audioActionBean = audioActionBeanList.get(i1);
            Log.e("newAudioActionTime", locateTime + "   " + audioActionBean.getTime() + "      " + audioActionBean.getData());
            if (locateTime >= audioActionBean.getTime()) {
                String data = audioActionBean.getData();
                if (doVideoAction(data)) { //存在  视频文件播放
                    audioActionBeanList.remove(i1);
                    i1--;
                } else {  // 不存在 视频文件播放
                    Message msg3 = Message.obtain();
                    msg3.obj = data;
                    msg3.what = 0x1109;
                    handler.sendMessage(msg3);
                    audioActionBeanList.remove(i1);
                    i1--;
                }
            } else {
                break;
            }
        }
        if (locateTime > (startTimee + 10000)) {
            getAudioAction(soundtrackID, locateTime);
        }
    }

    private boolean doVideoAction(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int actionType = jsonObject.getInt("actionType");
            if (actionType == 19) {
                final int stat = jsonObject.getInt("stat");
                if (stat == 1) { // 播放视频  暂停音响的播放
                    pauseMedia();
                    pauseMedia2();
                } else if (stat == 2) {
                    resumeMedia();
                    resumeMedia2();
                }
                final float time = jsonObject.getInt("time");
                final String attachmentId = jsonObject.getString("vid");
                final String url = jsonObject.getString("url");
                final int videotype = jsonObject.getInt("type");
                startOrPauseVideo(stat, time, attachmentId, url, videotype);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void resumeYinxiang() {
        if (audiosyncll != null) {  //处于播音想的状态
            if (audiosyncll.getVisibility() == View.VISIBLE) {
                if (isPause) {
                    resumeMedia();
                    resumeMedia2();
                }
            }
        }
    }


    private MediaPlayer mediaPlayer2;

    private void GetMediaPlay2(String url) {
        if (mediaPlayer2 != null) {
            mediaPlayer2.stop();
            mediaPlayer2.reset();
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
        mediaPlayer2 = new MediaPlayer();
        try {
            mediaPlayer2.setDataSource(url);
            mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer2.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                closeAudioSync();
                StopMedia();
                StopMedia2();
            }
        });
    }

    private void StopMedia2() {
        if (mediaPlayer2 != null) {
            mediaPlayer2.stop();
            mediaPlayer2.reset();
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
    }

    private void pauseMedia2() {
        if (mediaPlayer2 != null) {
            mediaPlayer2.pause();
        }
    }

    private void resumeMedia2() {
        if (mediaPlayer2 != null) {
            mediaPlayer2.start();
        }
    }

    /**
     * start video
     *
     * @param path
     * @param time
     */
    private void playVideo(final String path, final float time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse(path);
                videoView.setVideoURI(uri);
                if (!videoView.isPlaying()) {
                    videoView.start();
                }
                videoView.seekTo((int) time * 1000);
                // 是否隐藏视频暂停按钮 （权限）
                if (isHavePresenter()) {
                    MediaController mc = new MediaController(WatchCourseActivity2.this);
                    mc.setVisibility(View.VISIBLE);
                    closeVideo.setVisibility(View.VISIBLE);
                    videoView.setMediaController(mc);
                } else {
                    MediaController mc = new MediaController(WatchCourseActivity2.this);
                    mc.setVisibility(View.INVISIBLE);
                    closeVideo.setVisibility(View.GONE);
                    videoView.setMediaController(mc);
                }
            }
        });
    }

    /**
     * 获得video列表
     */
    private void getVideoList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnjson = com.ub.techexcel.service.ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "Lesson/VideoList?LessonID=" + meetingId);
                Log.e("video文档列表", returnjson.toString());
                formatServiceData2(returnjson);
            }
        }).start();
    }

    private void formatServiceData2(JSONObject returnjson) {
        try {
            int retcode = returnjson.getInt("RetCode");
            if (retcode == 0) {
                videoList.clear();
                JSONArray lineitems = returnjson.getJSONArray("RetData");
                for (int i = 0; i < lineitems.length(); i++) {
                    JSONObject lineitem = lineitems.getJSONObject(i);
                    LineItem item = new LineItem();
                    item.setIncidentID(lineitem.getInt("IncidentID"));
                    item.setEventID(lineitem.getInt("EventID"));
                    String linename = lineitem.getString("EventName");
                    item.setEventName((linename == null || linename
                            .equals("null")) ? "" : linename);
                    item.setFileName(lineitem.getString("FileName"));
                    item.setUrl(lineitem.getString("AttachmentUrl"));
                    item.setHtml5(false);
                    item.setAttachmentID(lineitem.getString("AttachmentID"));
                    item.setBlankPageNumber(lineitem.getString("BlankPageNumber"));
                    videoList.add(item);
                }
                handler.sendEmptyMessage(0x2105);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void notifyleave() {
        boolean isExist = false;
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (AppConfig.progressCourse.get(i).getMeetingId().equals(meetingId)) {
                AppConfig.progressCourse.get(i).setStatus(true);
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            NotifyBean notifyBean = new NotifyBean();
            notifyBean.setMeetingId(meetingId);
            notifyBean.setStatus(true);
            AppConfig.progressCourse.add(notifyBean);
        }
        Intent intent = new Intent();
        intent.setAction(getResources().getString(R.string.Receive_Course));
        sendBroadcast(intent);
    }

    private void notifyend() {
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

    /**
     * 获得PDF文档
     */
    private void getServiceDetail() {
        if (meetingId.equals("-1")) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "Lesson/Item?lessonID=" + meetingId;
        MeetingServiceTools.getInstance().getPdfList(url, MeetingServiceTools.GETPDFLIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<LineItem> list = (List<LineItem>) object;
                documentList.clear();
                documentList.addAll(list);
                initpdfdata(documentList);
            }
        });
    }

    private void initpdfdata(final List<LineItem> documentList) {
        if (documentList.size() > 0) {
            for (int i = 0; i < documentList.size(); i++) {
                String id = documentList.get(i).getItemId();
                if (!TextUtils.isEmpty(currentItemId)) {
                    if (currentItemId.equals(id)) {
                        documentList.get(i).setSelect(true);
                    }
                }

            }
            myRecyclerAdapter2 = new MyRecyclerAdapter2(this, documentList);
            documentrecycleview.setAdapter(myRecyclerAdapter2);
            myRecyclerAdapter2.setMyItemClickListener(new MyRecyclerAdapter2.MyItemClickListener() {
                @SuppressLint("JavascriptInterface")
                @Override
                public void onItemClick(int position) {
                    currentAttachmentPage = "0";
                    AppConfig.currentPageNumber = "0";
                    for (int i = 0; i < documentList.size(); i++) {
                        documentList.get(i).setSelect(false);
                    }
                    lineItem = documentList.get(position);
                    lineItem.setSelect(true);
                    myRecyclerAdapter2.notifyDataSetChanged();
                    currentAttachmentId = lineItem.getAttachmentID();
                    currentItemId = lineItem.getItemId();
                    currentBlankPageNumber = lineItem.getBlankPageNumber();
                    targetUrl = lineItem.getUrl();
                    newPath = lineItem.getNewPath();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wv_show.load("file:///android_asset/index.html", null);
                        }
                    });
                    notifySwitchDocumentSocket(lineItem, "1");
                }
            });
        }
        for (int i = 0; i < documentList.size(); i++) {
            LineItem lineItem2 = documentList.get(i);
            if (currentItemId.equals("0")) {
            } else {
                if ((currentItemId).equals(lineItem2.getItemId())) {
                    lineItem = lineItem2;
                    break;
                }
            }
        }
        if (isLoadPdfAgain) {
            isLoadPdfAgain = false;
            changedocumentlabel(lineItem);
        }
        if (isHavePresenter()) {
            updatecurrentdocumentid();
        }
    }

    /**
     * 结束课程   关闭此meeting
     */
    private void closeCourse(int id) {
        if (mWebSocketClient != null) {
            try {
                JSONObject loginjson = new JSONObject();
                if (id == 1) {
                    loginjson.put("action", "END_MEETING");
                } else if (id == 0) {
                    loginjson.put("action", "LEAVE_MEETING");
                }
                loginjson.put("sessionId", AppConfig.UserToken);
                String ss = loginjson.toString();
                SpliteSocket.sendMesageBySocket(ss);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            closeCourse(0); // 离开课程
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    private Popupdate puo;


    private String fileNamebase;


    //  ----------------------------------------------------------  video  ----------------------------------------------------
    private RecyclerView mLeftRecycler;
    private DefaultAgoraAdapter mLeftAgoraAdapter;
    private RecyclerView mRightRecycler;
    private RightAgoraAdapter mRightAgoraAdapter;
    private RecyclerView mBigRecycler;
    private BigAgoraAdapter mBigAgoraAdapter;
    private RelativeLayout bigbg;


    private RecyclerView mSmallLeftRecycler;
    private RightAgoraAdapter mSmallLeftAgoraAdapter;


    private ImageView icon_command_mic_enabel;
    private ImageView icon_command_webcam_enable;
    private ImageView icon_command_switch;
    private ImageView icon_ear_active;

    private RelativeLayout toggle;

    private LinearLayout smalltoggle;
    private ImageView smalltoggleicon;
    private LinearLayout togglelinearlayout;

    //刚进入是否显示默认video
    private boolean isShowDefaultVideo = true;
    private ImageView toggleicon;
    private ImageView icon_back;
    private final List<AgoraBean> mUidsList = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private boolean isBroadcaster(int cRole) {
        return cRole == Constants.CLIENT_ROLE_BROADCASTER;
    }

    private boolean isBroadcaster() {
        return isBroadcaster(config().mClientRole);
    }


    boolean isRoute = true;
    int cRole = 0;
    float mPosX = 0, mCurPosX = 0;


  /*  @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPosX = event.getX();
                break;
        }
        return super.dispatchTouchEvent(event);
    }*/

    private int currentisopen = 0;  // 右边模式   0  左上角模式
    private int currentisopen2 = 1;  //展开模式
    int clickNumber = 0;

    @Override
    protected void initUIandEvent() {
        event().addEventHandler(this);
        cRole = Constants.CLIENT_ROLE_BROADCASTER;
        if (identity == 2 || identity == 1) { // 学生或老师
            cRole = Constants.CLIENT_ROLE_BROADCASTER;
        } else if (identity == 3) {   // 旁听者
            cRole = Constants.CLIENT_ROLE_AUDIENCE;
        }
        if (cRole == 0) {
            throw new RuntimeException("Should not reach here");
        }
        doConfigEngine(cRole);

        icon_command_mic_enabel = (ImageView) findViewById(R.id.icon_command_mic_enabel);
        icon_command_webcam_enable = (ImageView) findViewById(R.id.icon_command_webcam_enable);
        icon_ear_active = (ImageView) findViewById(R.id.icon_ear_active);
        icon_command_switch = (ImageView) findViewById(R.id.icon_command_switch);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changevideo(0, "");
                switchToDefaultVideoView();
            }
        });
        toggle = (RelativeLayout) findViewById(R.id.toggle);
        toggleicon = (ImageView) findViewById(R.id.toggleicon);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentisopen == 1) {
                    currentisopen = 0;
                    toggleicon.setImageResource(R.drawable.eyeclose);
                    mLeftAgoraAdapter.setData(null, teacherid);
                    mLeftRecycler.setVisibility(View.GONE);

                    mSmallLeftAgoraAdapter.setData(mUidsList, teacherid);
                    mSmallLeftRecycler.setVisibility(View.VISIBLE);
                    smalltoggle.setVisibility(View.VISIBLE);

                } else if (currentisopen == 0) {
                    currentisopen = 1;
                    mSmallLeftAgoraAdapter.setData(null, teacherid);
                    mSmallLeftRecycler.setVisibility(View.GONE);
                    smalltoggle.setVisibility(View.GONE);

                    toggleicon.setImageResource(R.drawable.eyeopen);
                    mLeftAgoraAdapter.setData(mUidsList, teacherid);
                    mLeftRecycler.setVisibility(View.VISIBLE);
                }
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("CheckZoomeckZoom", "CheckZoom");
                        wv_show.loadUrl("javascript:CheckZoom()", null);
                    }
                }, 500);
            }
        });

        smalltoggle = (LinearLayout) findViewById(R.id.smalltoggle);
        smalltoggle.setVisibility(View.GONE);
        smalltoggleicon = (ImageView) findViewById(R.id.smalltoggleicon);
        togglelinearlayout = (LinearLayout) findViewById(R.id.togglelinearlayout);
        smalltoggle.setOnTouchListener(new View.OnTouchListener() {
            private int startY;
            private int startX;
            private boolean isOnClick = true;
            private int endX, endY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isOnClick = true;
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        endX = startX;
                        endY = startY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        int move_bigX = moveX - startX;
                        int move_bigY = moveY - startY;
                        Log.e("手指移动距离的大小", move_bigX + "    " + move_bigY);
                        if (Math.abs(move_bigX) > 0 || Math.abs(move_bigY) > 0) {
                            isOnClick = false;
                            //拿到当前控件未移动的坐标
                            int left = togglelinearlayout.getLeft();
                            int top = togglelinearlayout.getTop();
                            left += move_bigX;
                            top += move_bigY;
                            int right = left + togglelinearlayout.getWidth();
                            int bottom = top + togglelinearlayout.getHeight();
                            togglelinearlayout.layout(left, top, right, bottom);
                        } else {
                            isOnClick = true;
                        }
                        startX = moveX;
                        startY = moveY;
                        break;
                    case MotionEvent.ACTION_UP:
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = togglelinearlayout.getLeft();
                        params.topMargin = togglelinearlayout.getTop();
                        params.setMargins(togglelinearlayout.getLeft(), togglelinearlayout.getTop(), 0, 0);
                        togglelinearlayout.setLayoutParams(params);
                        Log.e("手指移动距离的大小", isOnClick + "");

                        int moveX1 = (int) event.getRawX();
                        int moveY1 = (int) event.getRawY();
                        int move_bigX1 = moveX1 - endX;
                        int move_bigY1 = moveY1 - endY;

                        if (isOnClick && Math.abs(move_bigX1) < 5 && Math.abs(move_bigY1) < 5) {
                            if (currentisopen == 0) {
                                if (currentisopen2 == 1) {
                                    currentisopen2 = 0;
                                    initMute(false); //禁止推流
                                    smalltoggleicon.setImageResource(R.drawable.eyeclose);
                                    mSmallLeftAgoraAdapter.setData(null, teacherid);
                                    mSmallLeftRecycler.setVisibility(View.GONE);
                                    if (isHavePresenter()) {
                                        JSONObject json = new JSONObject();
                                        try {
                                            json.put("actionType", 21);
                                            json.put("isHide", 1);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
                                    }

                                } else {
                                    currentisopen2 = 1;
                                    initMute(true);
                                    smalltoggleicon.setImageResource(R.drawable.eyeopen);
                                    mSmallLeftAgoraAdapter.setData(mUidsList, teacherid);
                                    mSmallLeftRecycler.setVisibility(View.VISIBLE);
                                    if (isHavePresenter()) {
                                        JSONObject json = new JSONObject();
                                        try {
                                            json.put("actionType", 21);
                                            json.put("isHide", 0);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
                                    }
                                }
                            }
                            activte_linearlayout.setVisibility(View.GONE);
                            command_active.setImageResource(R.drawable.icon_command);
                        }
                        break;

                }
                return true;
            }
        });

        icon_command_mic_enabel.setTag(false);
        icon_command_mic_enabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Boolean) icon_command_mic_enabel.getTag()) {
                    initListen(false);
                } else {
                    initListen(true);
                }
            }
        });

        icon_ear_active.setOnClickListener(new View.OnClickListener() {  //耳机  语音路由
            @Override
            public void onClick(View view) {
                clickNumber++;
                Log.e("hahha", clickNumber + "");
                if (clickNumber == 3) {
                    clickNumber = 0;
                    worker().getRtcEngine().muteAllRemoteAudioStreams(true);  //我想静一静
                    icon_ear_active.setImageResource(R.drawable.voiceallclose);
                } else {
                    worker().getRtcEngine().muteAllRemoteAudioStreams(false);  //我不想静一静
                    if (isRoute) {
                        isRoute = false;
                        worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(false);
                        worker().getRtcEngine().setEnableSpeakerphone(false);
                        icon_ear_active.setImageResource(R.drawable.icon_ear_active);
                    } else {
                        isRoute = true;
                        worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
                        worker().getRtcEngine().setEnableSpeakerphone(true);
                        icon_ear_active.setImageResource(R.drawable.icon_voice_active_1);
                    }
                }
            }
        });

        icon_command_webcam_enable.setTag(false);
        icon_command_webcam_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Boolean) icon_command_webcam_enable.getTag()) {
                    initMute(false);
                } else {
                    initMute(true);
                }
                openVideoByViewType();
            }
        });

        icon_command_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                worker().getRtcEngine().switchCamera();
            }
        });

        mLeftRecycler = (RecyclerView) findViewById(R.id.grid_video_view_container2);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mLeftAgoraAdapter != null) {
                    int type = mLeftAgoraAdapter.getItemViewType(position);
                    if (type == 1) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
                return 1;

            }
        });
        mLeftRecycler.setLayoutManager(gridLayoutManager);


        mRightRecycler = (RecyclerView) findViewById(R.id.grid_video_view_container3);
        mRightRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mBigRecycler = (RecyclerView) findViewById(R.id.small_video_view_container);
        mBigRecycler.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        bigbg = (RelativeLayout) findViewById(R.id.bigbg);


        mSmallLeftRecycler = (RecyclerView) findViewById(R.id.small_grid_video_view_container2);
        mSmallLeftRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mSmallLeftAgoraAdapter = new RightAgoraAdapter(WatchCourseActivity2.this);
        mSmallLeftAgoraAdapter.setItemEventHandler(new VideoControlCallback() {
            @Override
            public void onItemDoubleClick(Object item) {
                changevideo(1, "");
                switchToBigVideoView();
            }
        });
        mSmallLeftRecycler.setAdapter(mSmallLeftAgoraAdapter);

        mLeftAgoraAdapter = new DefaultAgoraAdapter(WatchCourseActivity2.this);
        mLeftAgoraAdapter.setItemEventHandler(new VideoControlCallback() {
            @Override
            public void onItemDoubleClick(Object item) {
                changevideo(1, "");
                switchToBigVideoView();
            }
        });
        mLeftAgoraAdapter.setHasStableIds(true);
        mLeftRecycler.setAdapter(mLeftAgoraAdapter);
        mLeftRecycler.setDrawingCacheEnabled(true);
        mLeftRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        mRightAgoraAdapter = new RightAgoraAdapter(WatchCourseActivity2.this);
        mRightAgoraAdapter.setItemEventHandler(new VideoControlCallback() {
            @Override
            public void onSwitchVideo(AgoraBean item) {   // 切换大小屏
                switchVideo(item);
                changevideo(2, item.getuId() + "");
            }
        });
        mRightAgoraAdapter.setHasStableIds(true);
        mRightRecycler.setAdapter(mRightAgoraAdapter);
        mRightRecycler.setDrawingCacheEnabled(true);
        mRightRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        mBigAgoraAdapter = new BigAgoraAdapter(WatchCourseActivity2.this);
        mBigAgoraAdapter.setItemEventHandler(new VideoControlCallback() {
            @Override
            public void isEnlarge(AgoraBean user) {
                if (mViewType == VIEW_TYPE_NORMAL) {
                    if (mUidsList.size() > 1) {
                        switchVideo(user);
                        changevideo(2, user.getuId() + "");
                    }
                } else if (mViewType == VIEW_TYPE_SING_NORMAL) {
                    if (mUidsList.size() > 1) {
                        switchToBigVideoView();
                        changevideo(1, "");
                    }
                }
            }

            @Override
            public void closeOtherAudio(AgoraBean user) {
                if (isHavePresenter() || config().mUid == user.getuId()) {  //有权限
                    JSONObject json = new JSONObject();
                    try {
                        json.put("stat", 0);
                        json.put("actionType", 14);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    send_message("SEND_MESSAGE", AppConfig.UserToken, 1, user.getuId() + "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
                }
            }

            @Override
            public void closeOtherVideo(AgoraBean user) {
                if (isHavePresenter() || config().mUid == user.getuId()) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("stat", 0);
                        json.put("actionType", 15);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    send_message("SEND_MESSAGE", AppConfig.UserToken, 1, user.getuId() + "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));

                }
            }

            @Override
            public void openMyAudio(AgoraBean user) {
                if (config().mUid == user.getuId()) {
                    initListen(true);
                }
                super.openMyAudio(user);
            }

            @Override
            public void openMyVideo(AgoraBean user) {
                if (config().mUid == user.getuId()) {
                    initMute(true);
                    openVideoByViewType();
                }
                super.openMyVideo(user);
            }
        });
        mBigAgoraAdapter.setHasStableIds(true);
        mBigRecycler.setAdapter(mBigAgoraAdapter);
        mBigRecycler.setDrawingCacheEnabled(true);
        mBigRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        //加入频道
        worker().joinChannel(meetingId.toUpperCase(), config().mUid);
    }


    private boolean isHavePresenter() {
        if (identity == 1) { // 学生
            if (TextUtils.isEmpty(studentCustomer.getUserID())) {
                return false;
            }
            if (studentCustomer.getUserID().equals(AppConfig.UserID.replace("-", ""))) {
                return true;
            }
        } else if (identity == 2) {
            if (currentPresenterId.equals(teacherCustomer.getUserID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * isVoice  true
     * false 话筒禁止
     */
    private void initListen(boolean isVoice) {
        icon_command_mic_enabel.setTag(isVoice);
        if (isVoice == false) { // false
            worker().getRtcEngine().muteLocalAudioStream(true);
            icon_command_mic_enabel.setImageResource(R.drawable.icon_command_mic_disable);
        } else if (isVoice == true) {
            worker().getRtcEngine().muteLocalAudioStream(false);
            icon_command_mic_enabel.setImageResource(R.drawable.icon_command_mic_enabel);
        }
    }

    /**
     * isMute false  不让推流
     * true    正常
     */
    private void initMute(boolean isMute) {
        icon_command_webcam_enable.setTag(isMute);
        for (AgoraBean agoraBean : mUidsList) {
            if (agoraBean.getuId() == config().mUid) {
                agoraBean.setMuteVideo(!isMute);
            }
        }
        if (isMute == true) {  // true 可以推流
            worker().getRtcEngine().muteLocalVideoStream(false);
            icon_command_webcam_enable.setImageResource(R.drawable.icon_command_webcam_enable);
        } else if (isMute == false) {  // false 不让推流
            worker().getRtcEngine().muteLocalVideoStream(true);
            icon_command_webcam_enable.setImageResource(R.drawable.icon_command_webcam_disable);
        }
    }


    private void closeAlbum() {
        worker().getRtcEngine().disableAudio();
        worker().getRtcEngine().enableLocalVideo(false);
    }

    private void openAlbum() {
        worker().getRtcEngine().enableAudio();
        worker().getRtcEngine().enableVideo();
        worker().getRtcEngine().enableLocalVideo(true);
    }

    private long currentTime = 0;

    private void doConfigEngine(int cRole) {
        int vProfile = Constants.VIDEO_PROFILE_480P;
        worker().configEngine(cRole, vProfile);
        //启用说话者音量提示
//        worker().getRtcEngine().enableAudioVolumeIndication(200, 3);
        worker().getRtcEngine().enableWebSdkInteroperability(true);
        //记录当前时间
        currentTime = System.currentTimeMillis();
        Log.e("onAudioVolumeIndication", currentTime + ":");
    }


    /**
     * onDestory
     */
    @Override
    protected void deInitUIandEvent() {
        AppConfig.isPresenter = false;
        AppConfig.status = "0";
        AppConfig.currentLine = 0;
        AppConfig.currentMode = "0";
        AppConfig.currentPageNumber = "0";
        AppConfig.currentDocId = "0";
        if (NotifyActivity.instance != null && !NotifyActivity.instance.isFinishing()) {
            NotifyActivity.instance.finish();
        }
        watch2instance = false;
        wl.release();
        doLeaveChannel();
        event().removeEventHandler(this);
        mUidsList.clear();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }

        if (getGroupbroadcastReceiver != null) {
            unregisterReceiver(getGroupbroadcastReceiver);
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        sendAudioSocket(0, soundtrackID);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
        }
        if (actionTimer != null) {
            actionTimer.cancel();
            actionTimer = null;
        }

        StopMedia2();
        Tools.removeGroupMeaage(mGroupId);
        if (wv_show != null) {
            wv_show.removeAllViews();
            wv_show.onDestroy();
            wv_show = null;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        StopMedia2();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (wv_show != null) {
            wv_show.pauseTimers();
            wv_show.onHide();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (wv_show != null) {
            wv_show.onNewIntent(intent);
        }
    }

    private void doLeaveChannel() {
        worker().leaveChannel(config().mChannel);
        if (isBroadcaster()) {
            worker().preview(false, null, 0);
        }
    }

    private RelativeLayout remotevideoRelative;
    private FrameLayout remotevideoframe;

    /**
     * 远端视频接收解码回调
     *
     * @param uid
     * @param width
     * @param height
     * @param elapsed
     */
    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {

    }

    /**
     * @param uid
     * @return
     */
    private boolean isExistUid(int uid) {
        for (AgoraBean agoraBean : mUidsList) {
            if (agoraBean.getuId() == uid) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
        Log.e("RRRRRRRRRRRRRRRRRR", "onJoinChannelSuccess    " + uid + "  " + mUidsList.size());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                if (isExistUid(uid)) {
                    Log.e("RRRRRRRRRRRRRRRRRR", "已有    ");
                    return;
                }
                Log.e("RRRRRRRRRRRRRRRRRR", "没有    " + cRole);
                if (isBroadcaster(cRole)) {
                    Log.e("RRRRRRRRRRRRRRRRRR", "没有2    ");
                    AgoraBean agoraBean = new AgoraBean();
                    agoraBean.setuId(uid);
                    SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                    rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, 0));
                    surfaceV.setZOrderOnTop(true);
                    surfaceV.setZOrderMediaOverlay(true);
                    agoraBean.setSurfaceView(surfaceV);
                    for (int i1 = 0; i1 < teacorstudentList.size(); i1++) {
                        if (teacorstudentList.get(i1).getUserID().equals(uid + "")) {
                            agoraBean.setUserName(teacorstudentList.get(i1).getName());
                        }
                    }
                    mUidsList.add(agoraBean);
                }
                initMute(false);
                initListen(false);
                videoByUser();
            }
        });
    }


    /**
     * 视频列表按user排序
     */
    private void videoByUser() {
        for (int i = teacherRecyclerAdapter.getmDatas().size() - 1; i >= 0; i--) {
            Customer customer = teacherRecyclerAdapter.getmDatas().get(i);
            for (AgoraBean mData : mUidsList) {
                AgoraBean agoraBean = new AgoraBean();
                agoraBean.setuId(mData.getuId());
                agoraBean.setSurfaceView(mData.getSurfaceView());
                agoraBean.setMuteAudio(mData.isMuteAudio());
                agoraBean.setMuteVideo(mData.isMuteVideo());
                agoraBean.setUserName(mData.getUserName());
                if (customer.getUserID().equals(mData.getuId() + "")) {
                    mUidsList.add(0, agoraBean);
                    mUidsList.remove(mData);
                    break;
                }
            }
        }
    }

    private boolean isClose = true;

    private void bindDevice() {
        if (bindUid == 0) {
            return;
        }
        TextView tv = (TextView) findViewById(R.id.binddevice);
        if (isClose) {
            isClose = false;
            tv.setText("BindDevice(Open)");
            worker().getRtcEngine().muteRemoteAudioStream(bindUid, false);
        } else {
            isClose = true;
            tv.setText("BindDevice(Close)");
            worker().getRtcEngine().muteRemoteAudioStream(bindUid, true);
        }
    }

    private int bindUid = 0;  //需要保存的uid

    @Override
    public void onUserJoined(final int uid, int elapsed) {
        Log.e("RRRRRRRRRRRRRRRRRR", "onUserJoined    " + uid + "  " + mUidsList.size());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (uid < 1000000000 && uid > 0) {
                    if (isFinishing()) {
                        return;
                    }
                    if (isExistUid(uid)) {

                    } else {
                        if (AppConfig.BINDUSERID.equals(uid + "")) {
                            worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                            worker().getRtcEngine().muteRemoteAudioStream(uid, true);
                            bindUid = uid;
                        }
                        AgoraBean agoraBean = new AgoraBean();
                        agoraBean.setuId(uid);
                        SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                        surfaceV.setZOrderOnTop(true);
                        surfaceV.setZOrderMediaOverlay(true);
                        if (config().mUid == uid) {
                            rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                        } else {
                            rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));
                        }
                        agoraBean.setSurfaceView(surfaceV);
                        for (int i1 = 0; i1 < teacorstudentList.size(); i1++) {
                            if (teacorstudentList.get(i1).getUserID().equals(uid + "")) {
                                agoraBean.setUserName(teacorstudentList.get(i1).getName());
                            }
                        }
                        mUidsList.add(agoraBean);
                    }
                    videoByUser();
                    openVideoByViewType();
                } else {
                    if (uid > 1000000000 && uid < 1500000000) {
                        if (isFinishing()) {
                            return;
                        }
                        SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                        surfaceV.setZOrderOnTop(true);
                        surfaceV.setZOrderMediaOverlay(true);
                        rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                        remotevideoframe = (FrameLayout) findViewById(R.id.remotevideoframe);
                        remotevideoRelative = (RelativeLayout) findViewById(R.id.remotevideoRelative);
                        remotevideoRelative.setVisibility(View.VISIBLE);
                        if (remotevideoframe.getChildCount() == 0) {
                            ViewParent parent = surfaceV.getParent();
                            if (parent != null) {
                                ((FrameLayout) parent).removeView(surfaceV);
                            }
                        }
                        remotevideoframe.addView(surfaceV, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                }
            }
        });
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (uid > 1000000000 && uid < 1500000000) {
                    remotevideoframe = (FrameLayout) findViewById(R.id.remotevideoframe);
                    remotevideoRelative = (RelativeLayout) findViewById(R.id.remotevideoRelative);
                    SurfaceView surfaceView = (SurfaceView) remotevideoframe.getChildAt(0);
                    if (surfaceView != null) {
                        surfaceView.setVisibility(View.GONE);
                    }
                    remotevideoframe.removeAllViews();
                    remotevideoframe = null;
                    remotevideoRelative.setVisibility(View.GONE);
                } else if (uid < 1000000000) {
                    if (isFinishing()) {
                        return;
                    }
                    for (int i1 = 0; i1 < mUidsList.size(); i1++) {
                        AgoraBean agoraBean = mUidsList.get(i1);
                        if (agoraBean.getuId() == uid) {
                            mUidsList.remove(agoraBean);
                        }
                    }
                    int bigBgUid = -1;
                    if (mViewType == VIEW_TYPE_DEFAULT || uid == bigBgUid) {
                        switchToDefaultVideoView();
                        Log.e("wahaha", 1 + "");
                    } else {
                        if (mViewType == VIEW_TYPE_NORMAL) {
                            if (mUidsList.size() >= 1) {
                                switchToBigVideoView();
                            }
                        } else if (mViewType == VIEW_TYPE_SING_NORMAL) {
                            if (mUidsList.size() > 1) {
                                switchVideo(mUser);
                            }
                        }
                    }
                }
            }
        });
    }

    public static final int AUDIO_ROUTE_HEADSET = 0;
    public static final int AUDIO_ROUTE_EARPIECE = 1;
    public static final int AUDIO_ROUTE_SPEAKERPHONE = 3;
    public static final int AUDIO_ROUTE_HEADSETBLUETOOTH = 5;

    /**
     * 语音路由已变更回调
     *
     * @param routing
     */
    @Override
    public void onAudioRouteChanged(final int routing) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (routing) {
                    case AUDIO_ROUTE_HEADSET:
                        icon_ear_active.setEnabled(false);
                        isRoute = false;
                        icon_ear_active.setImageResource(R.drawable.icon_headphone_active);
                        break;
                    case AUDIO_ROUTE_EARPIECE:
                        icon_ear_active.setEnabled(true);
                        isRoute = false;
                        icon_ear_active.setImageResource(R.drawable.icon_ear_active);
                        break;
                    case AUDIO_ROUTE_SPEAKERPHONE:
                        icon_ear_active.setEnabled(true);
                        isRoute = true;
                        icon_ear_active.setImageResource(R.drawable.icon_voice_active_1);
                        break;
                    case AUDIO_ROUTE_HEADSETBLUETOOTH:
                        icon_ear_active.setEnabled(false);
                        isRoute = false;
                        icon_ear_active.setImageResource(R.drawable.icon_headphone_active);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 其他用户已停发/已重发视频流
     *
     * @param uid
     * @param muted
     */
    @Override
    public void onUserMuteVideo(final int uid, final boolean muted) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (AgoraBean agoraBean : mUidsList) {
                    if (agoraBean.getuId() == uid) {
                        agoraBean.setMuteVideo(muted);
                    }
                }
                openVideoByViewType();
            }
        });
    }

    /**
     * 其他用户已停发/已重发音频流
     *
     * @param uid
     * @param muted
     */
    @Override
    public void onUserMuteAudio(final int uid, final boolean muted) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (AgoraBean agoraBean : mUidsList) {
                    if (agoraBean.getuId() == uid) {
                        agoraBean.setMuteAudio(muted);
                    }
                }
                if (mViewType == VIEW_TYPE_SING_NORMAL || mViewType == VIEW_TYPE_SING_NORMAL) {
                    openVideoByViewType();
                }
            }
        });
    }

    /**
     * 远端视频统计回调
     *
     * @param stats
     */
    @Override
    public void onRemoteVideoStats(final IRtcEngineEventHandler.RemoteVideoStats stats) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (stats.uid > 1000000000) {
                    Log.e("-------doRenderRemoteUi", "onRemoteVideoStats");
                    int[] matrix = getMaxSizeSgareScreenWithWidth(stats.width, stats.height);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) remotevideoframe.getLayoutParams();
                    params.width = matrix[0];
                    params.height = matrix[1];
                    remotevideoframe.setLayoutParams(params);
                }
            }
        });
    }

    private int[] getMaxSizeSgareScreenWithWidth(int width, int height) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int screenwidth = mDisplayMetrics.widthPixels;
        int screenheight = mDisplayMetrics.heightPixels;
        float scale = (float) height / (float) width;
        int[] matrix = new int[2];
        if (scale > 0) {
            if (screenwidth * scale > screenheight) {
                matrix[0] = (int) (screenheight / scale);
                matrix[1] = screenheight;
            } else {
                matrix[0] = screenwidth;
                matrix[1] = (int) (screenwidth * scale);
            }
        }
        return matrix;
    }

    /**
     * 打开声网显示  本地切換
     */
    private void openVideoByViewType() {
        if (isOpenShengwang) {
            if (mViewType == VIEW_TYPE_DEFAULT) {
                switchToDefaultVideoView();
            } else if (mViewType == VIEW_TYPE_NORMAL) {
                if (mUidsList.size() >= 1) {
                    switchToBigVideoView();
                }
            } else if (mViewType == VIEW_TYPE_SING_NORMAL) {
                if (mUidsList.size() > 1) {
                    if (mUser == null) {
                        String currentSessionID = currentMaxVideoUserId;
                        if (currentMaxVideoUserId.equals("null")) {
                            return;
                        }
                        if (!TextUtils.isEmpty(currentSessionID)) {
                            int uid = Integer.parseInt(currentSessionID);
                            for (AgoraBean agoraBean : mUidsList) {
                                if (agoraBean.getuId() == uid) {
                                    switchVideo(agoraBean);
                                    break;
                                }
                            }
                        }
                    } else {
                        switchVideo(mUser);
                    }
                }
            }
        }
    }

    /**
     * socket 切換
     */
    private void switchMode() {
        if (currentMode.equals("0") || currentMode.equals("3")) {
            switchToDefaultVideoView();
            Log.e("wahaha", 4 + "");
        } else if (currentMode.equals("1")) {
            if (mUidsList.size() >= 1) {
                switchToBigVideoView();
            }
        } else if (currentMode.equals("2")) {
            String currentSessionID = currentMaxVideoUserId;
            if (currentMaxVideoUserId.equals("null")) {
                return;
            }
            if (!TextUtils.isEmpty(currentSessionID)) {
                int uid = Integer.parseInt(currentSessionID);
                if (mUidsList.size() > 1) {
                    for (AgoraBean agoraBean : mUidsList) {
                        if (agoraBean.getuId() == uid) {
                            switchVideo(agoraBean);
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean selfIsSpeaker = false;

    @Override
    public void onAudioVolumeIndication(final IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (IRtcEngineEventHandler.AudioVolumeInfo info : speakers) {
                    //0代表本地用户
                    if (info.uid == 0 && info.volume >= 100) { //自己是否说话了
                        Log.e("onAudioVolumeIndication", info.uid + "  " + info.volume);
                        selfIsSpeaker = true;
                        break;
                    }
                    long time1 = System.currentTimeMillis() - currentTime;
                    if (time1 > 30000 && selfIsSpeaker) {
                        selfIsSpeaker = false;
                        currentTime = System.currentTimeMillis();
                        // 监控说话的是否是自己，如果自己30s内说过话，就发这条消息MEMBER_SPEAKING给server
                        try {
                            JSONObject loginjson = new JSONObject();
                            loginjson.put("action", "MEMBER_SPEAKING");
                            loginjson.put("sessionId", AppConfig.UserToken);
                            String ss = loginjson.toString();
                            SpliteSocket.sendMesageBySocket(ss);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }


    private void switchToDefaultVideoView() {
        mViewType = VIEW_TYPE_DEFAULT;
        icon_back.setVisibility(View.GONE);
        toggle.setVisibility(View.VISIBLE);
        if (mRightAgoraAdapter != null) {
            mRightAgoraAdapter.setData(null, teacherid);
            mRightRecycler.setVisibility(View.GONE);
        }
        if (mBigAgoraAdapter != null) {
            final LinearLayout ll = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_black, null);
            bigbg.addView(ll, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mBigAgoraAdapter.setData(null, teacherid);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ll != null)
                        bigbg.removeView(ll);
                }
            }, 500);
            mBigRecycler.setVisibility(View.GONE);
            bigbg.setVisibility(View.GONE);
        }
        if (isShowDefaultVideo == true) {

            if (currentisopen == 1) {
                mSmallLeftAgoraAdapter.setData(null, teacherid);
                mSmallLeftRecycler.setVisibility(View.GONE);

                mLeftAgoraAdapter.setData(mUidsList, teacherid);
                mLeftRecycler.setVisibility(View.VISIBLE);
                toggleicon.setImageResource(R.drawable.eyeopen);

            } else if (currentisopen == 0) {

                if (currentisopen2 == 1) {
                    smalltoggle.setVisibility(View.VISIBLE);
                    smalltoggleicon.setImageResource(R.drawable.eyeopen);
                    mSmallLeftAgoraAdapter.setData(mUidsList, teacherid);
                    mSmallLeftRecycler.setVisibility(View.VISIBLE);
                } else {
                    smalltoggleicon.setImageResource(R.drawable.eyeclose);
                    mSmallLeftAgoraAdapter.setData(null, teacherid);
                    mSmallLeftRecycler.setVisibility(View.GONE);
                }
                toggleicon.setImageResource(R.drawable.eyeclose);
                mLeftAgoraAdapter.setData(null, teacherid);
                mLeftRecycler.setVisibility(View.GONE);
            }

        } else {
            mLeftAgoraAdapter.setData(null, teacherid);
            mLeftRecycler.setVisibility(View.GONE);
            toggleicon.setImageResource(R.drawable.eyeclose);
        }
    }


    //----------------------- big video ----------------------
    private void switchToBigVideoView() {
//        wv_show.setVisibility(View.GONE);
        icon_back.setVisibility(View.VISIBLE);
        toggle.setVisibility(View.GONE);
        if (mLeftAgoraAdapter != null) {
            mLeftAgoraAdapter.setData(null, teacherid);
            mLeftRecycler.setVisibility(View.GONE);
        }
        if (mRightAgoraAdapter != null) {
            mRightAgoraAdapter.setData(null, teacherid);
            mRightRecycler.setVisibility(View.GONE);
        }
        mViewType = VIEW_TYPE_NORMAL;
        bindToBigVideoView(mUidsList);
    }

    public static int mViewType = 0;
    public static final int VIEW_TYPE_DEFAULT = 0;
    public static final int VIEW_TYPE_NORMAL = 1;
    public static final int VIEW_TYPE_SING_NORMAL = 2;

    private void bindToBigVideoView(final List<AgoraBean> mUidsList) {
        final LinearLayout ll = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_black, null);
        bigbg.addView(ll, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        icon_back.setVisibility(View.VISIBLE);
        mBigRecycler.setVisibility(View.VISIBLE);
        bigbg.setVisibility(View.VISIBLE);
        GridLayoutManager s = (GridLayoutManager) mBigRecycler.getLayoutManager();
        final int currentSpanCount = s.getSpanCount();
        if (mUidsList.size() == 1) {
            if (currentSpanCount != 1) {
                mBigRecycler.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
            }

        } else if (mUidsList.size() > 1 && mUidsList.size() <= 4) {
            if (currentSpanCount != 2) {
                mBigRecycler.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
            }
        } else if (mUidsList.size() > 4 && mUidsList.size() <= 6) {
            if (currentSpanCount != 3) {
                mBigRecycler.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
            }
        } else if (mUidsList.size() > 6 && mUidsList.size() <= 8) {
            if (currentSpanCount != 4) {
                mBigRecycler.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
            }
        } else {
            if (currentSpanCount != 5) {
                mBigRecycler.setLayoutManager(new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false));
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ll != null)
                    bigbg.removeView(ll);
            }
        }, 500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBigAgoraAdapter.setData(mUidsList, teacherid);
            }
        }, 300);

    }

    private AgoraBean mUser;

    /**
     * 切换 全屏单个video
     *
     * @param user 被放大的user
     */
    private void switchVideo(AgoraBean user) {
        if (user == null) {
            return;
        }
        if (mLeftAgoraAdapter != null) {
            mLeftAgoraAdapter.setData(null, teacherid);
            mLeftRecycler.setVisibility(View.GONE);
        }
        mViewType = VIEW_TYPE_SING_NORMAL;
        mUser = user;
        if (mUidsList.size() > 1) {
            final List<AgoraBean> mUidsList2 = new ArrayList<>();
            List<AgoraBean> mUidsList3 = new ArrayList<>();
            for (AgoraBean agoraBean : mUidsList) {
                if (agoraBean.getuId() != user.getuId()) {
                    mUidsList2.add(agoraBean);
                } else {
                    mUidsList3.add(agoraBean);
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRightAgoraAdapter.hiddenText(true);
                    mRightAgoraAdapter.setData(mUidsList2, teacherid);
                    mRightRecycler.setVisibility(View.VISIBLE);
                }
            }, 500);
            bindToBigVideoView(mUidsList3);
        }
    }

}
