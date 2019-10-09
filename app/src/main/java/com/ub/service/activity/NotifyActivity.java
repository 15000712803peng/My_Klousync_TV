package com.ub.service.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.bean.EventUserName;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.info.Customer;
import com.kloudsync.techexcel2.service.ConnectService;
import com.kloudsync.techexcel2.start.LoginActivity;
import com.kloudsync.techexcel2.start.LoginGet;
import com.kloudsync.techexcel2.start.QrCodeActivity;
import com.kloudsync.techexcel2.tool.SoftInputUtils;
import com.kloudsync.techexcel2.ui.MainActivity;
import com.ub.techexcel.adapter.NotifyRecyclerAdapter;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.bean.UpcomingLesson;
import com.ub.techexcel.tools.SpliteSocket;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by wang on 2017/11/30.
 */

public class NotifyActivity extends Activity implements View.OnClickListener {

    private LinearLayout backll;
    private LinearLayout lin_enter;
    private LinearLayout lin_logout;
    private FrameLayout fl_main;
    private TextView courseid;
    private ListView courseList;
    private RecyclerView recyclerView;
    private List<ServiceBean> mList = new ArrayList<>();
    private NotifyRecyclerAdapter notifyAdapter;
    private TextView joinroom;
    private TextView tv_logout;
    private TextView tv_name;
    private SimpleDraweeView img_head;
    private EditText roomet;
    private int lessionid;
    private LinearLayout rootlayout;
    private UpcomingLesson lesson = null;
    private boolean flag_back;
    private boolean flag_enter;

    private List<ImageView> imgs = new ArrayList<ImageView>();
    private List<LinearLayout> imgsLayout = new ArrayList<LinearLayout>();
    private int imgIDs[] = {R.id.img1, R.id.img2, R.id.img3};
    private int imgLayoutIDs[] = {R.id.layout_card1, R.id.layout_card1, R.id.layout_card1};

    public static NotifyActivity instance = null;

    private int flag_t = 0;
    private int flag_b = 0;
    private boolean flag_bo;

    private LinearLayout editLayout;
    private TextView deviceTypeText;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1003:  // CheckClassRoomExist
                    int retdata2 = (int) msg.obj;
                    if (retdata2 == 0) { //不存在
                        Toast.makeText(NotifyActivity.this, getResources().getString(R.string.existclass), Toast.LENGTH_LONG).show();
                    } else if (retdata2 == 1) { // 存在
                        getClassRoomLessonID(roomid);
                    }
                    break;
                case 0x1001:   // getClassRoomLessonID
                    lessionid = (int) msg.obj;
                    getClassRoomTeacherID(roomid);
                    break;
                case 0x1004:  // getClassRoomTeacherID
                    teacherid = (int) msg.obj;
                    if (lessionid == -1) {     //看看老师是否正在上课
                        getUpcomingLessonList(teacherid + "");
                    } else {
                        Intent ii = new Intent(NotifyActivity.this, WatchCourseActivity3.class);
                        ii.putExtra("meetingId", roomid + "");
                        ii.putExtra("identity", 1);  // 学生
                        ii.putExtra("lessionId", lessionid + "");
                        ii.putExtra("isInstantMeeting", 1);
                        ii.putExtra("teacherid", teacherid + "");
                        ii.putExtra("meeting_type",0);
                        ii.putExtra("is_meeting",true);
                        if (!WatchCourseActivity3.watch3instance) {
                            startActivity(ii);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                fl_main.setBackgroundColor(getApplication().getResources().getColor(R.color.darkblack2));
//                                    fl_main.setVisibility(View.GONE);
                                }
                            }, 2000);
                        }
                    }
                    break;
                case 0x1305:
                    for (int i = 0; i < upcomingLessonList.size(); i++) {
                        if (upcomingLessonList.get(i).getIsOnGoing() == 1) {
                            lesson = upcomingLessonList.get(i);
                        }
                    }
                    if (null == lesson) {
                        Toast.makeText(NotifyActivity.this, "No this lesson", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent ii;
                    Log.e("notifity", roomid + "    " + lessionid + "   " + teacherid + "   ");
                    if (lesson.getIsInClassroom() == 1) {
                        ii = new Intent(NotifyActivity.this, WatchCourseActivity3.class);
                        ii.putExtra("meetingId", roomid + "");
                        ii.putExtra("identity", 1);  // 学生
                        ii.putExtra("lessionId", lessionid + "");
                        ii.putExtra("isInstantMeeting", 1);
                        ii.putExtra("teacherid", teacherid + "");

                    } else {
                        ii = new Intent(NotifyActivity.this, WatchCourseActivity2.class);
                        ii.putExtra("meetingId", lesson.getLessonID() + "");
                        ii.putExtra("identity", 1);  // 学生
                        ii.putExtra("lessionId", lessionid + "");
                        ii.putExtra("isInstantMeeting", 0);
                        ii.putExtra("teacherid", teacherid + "");

                    }
                    if (!WatchCourseActivity3.watch3instance || !WatchCourseActivity2.watch2instance) {
                        startActivity(ii);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                fl_main.setBackgroundColor(getApplication().getResources().getColor(R.color.darkblack2));
//                                fl_main.setVisibility(View.GONE);
                            }
                        }, 2000);
                    }

                    break;
                case 0x1306:
                    Log.e("notifity", roomid + "   " + (String) msg.obj + "   ");
                    Intent intent = new Intent(NotifyActivity.this, WatchCourseActivity2.class);
                    intent.putExtra("userid", AppConfig.UserID);
                    intent.putExtra("meetingId", roomid + "");
                    intent.putExtra("identity", 1);  // 学生
                    intent.putExtra("isInstantMeeting", 0);
                    intent.putExtra("teacherid", (String) msg.obj);
                    intent.putExtra("isStartCourse", true);
                    if (!WatchCourseActivity2.watch2instance) {
                        startActivity(intent);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                fl_main.setBackgroundColor(getApplication().getResources().getColor(R.color.darkblack2));
//                                fl_main.setVisibility(View.GONE);
                            }
                        }, 2000);
                    }
                    break;

            }
        }
    };


    private int teacherid;


    ImageView openDocumentCard;
    ImageView startMeetingCard;
    ImageView openSyncroomCard;
    LinearLayout openDucumentLayout;
    LinearLayout startMeetingLayout;
    LinearLayout openSyncroomLayout;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        Log.e("NotifyActivity","on create");
        instance = this;
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        initView();
        registerHeartBeatMessage();
    }




    Intent service;

    BroadcastReceiver heartBeatMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if(!TextUtils.isEmpty(message)){
                handleHeartMessage(message);
            }

        }
    };

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


    private void sendEndMeetingMessage(){
        Log.e("SocketService","sendEndMeetingMessage");
        Intent intent = new Intent();
        intent.setAction("com.cn.socket");
        intent.putExtra("message", "END_MEETING");
        sendBroadcast(intent);
    }

    private void updateBindUser(String bindUser){
        if(sharedPreferences !=  null){
            sharedPreferences.edit().putString("tv_bind_user",bindUser).commit();
            Log.e("tv_bind_user",sharedPreferences.getString("tv_bind_user",""));
            AppConfig.BINDUSERID = bindUser;
            getBindUserInfo();
        }
    }

    private void sendBindStatusMessage(int deviceType){
        Intent intent = new Intent("com.kloudsync.techexcel2.BIND_STATUS");
        intent.putExtra("device_type",deviceType);
        sendBroadcast(intent);
    }

    private void handleHeartMessage(String msg) {
        String data = Tools.getFromBase64(getRetCodeByReturnData2("data", msg));
        Log.e("NotifyActivity", "heart beart response：" + data);
//        if (WatchCourseActivity3.isMeetingStarted) {
//            //TV已经在会议里面
//            return;
//        }
        if(TextUtils.isEmpty(data)){
            goToQrcodeActivity();
            return;
        }


        try {
                String meetingId = null;
                int meetingType = 0;
                JSONObject messageJson = new JSONObject(data);

                if(messageJson.has("tvBindUserId")){
                    updateBindUser(messageJson.getInt("tvBindUserId")+"");
                }else {
                    goToQrcodeActivity();
                    return;
                }

                if (messageJson.has("hasOwner")) {
                    //绑定了某台设备，或者web
                    boolean hasOwner = messageJson.getBoolean("hasOwner");

                    if (hasOwner) {

                        if(messageJson.has("tvOwnerDeviceType")){
                            setDeviceType(messageJson.getInt("tvOwnerDeviceType"));
                        }

                        if (messageJson.has("tvOwnerMeetingId")) {
                            meetingId = messageJson.getString("tvOwnerMeetingId");
                        }else {
                            meetingId = "0";
                        }

                        roomid = meetingId;
                        if(TextUtils.isEmpty(roomid) || roomid.equals("0")){
                            // 心跳里面没有meeting的信息
                            return;
                        }
                        if (messageJson.has("tvOwnerMeetingType")) {
                            meetingType = messageJson.getInt("tvOwnerMeetingType");
                        }

                        type = meetingType;



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
                                followUser();
                                Log.e("BeartHeart","enter again");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }



    private void registerHeartBeatMessage(){
        IntentFilter filter = new IntentFilter("com.kloudsync.techexcel2.HeartBeatMessage");
        registerReceiver(heartBeatMsgReceiver,filter);
    }



    private void initView() {
        rootlayout = (LinearLayout) findViewById(R.id.rootlayout);
        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        backll = (LinearLayout) findViewById(R.id.backll);
        lin_enter = (LinearLayout) findViewById(R.id.lin_enter);
        lin_logout = (LinearLayout) findViewById(R.id.lin_logout);
        courseList = (ListView) findViewById(R.id.courseList);
        backll.setOnClickListener(this);
        joinroom = (TextView) findViewById(R.id.joinroom2);
        tv_logout = (TextView) findViewById(R.id.tv_logout);
        tv_name = (TextView) findViewById(R.id.tv_name);
        roomet = (EditText) findViewById(R.id.roomet);
        editLayout = (LinearLayout) findViewById(R.id.layout_edit);
        joinroom.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        courseid = (TextView) findViewById(R.id.courseid);
        img_head = (SimpleDraweeView) findViewById(R.id.img_head);

        courseid.setText(AppConfig.ClassRoomID);
//        tv_name.setText(AppConfig.UserName);

        deviceTypeText = (TextView) findViewById(R.id.txt_device_type);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        openDocumentCard = (ImageView) findViewById(R.id.img1);
        openDucumentLayout = (LinearLayout) findViewById(R.id.layout_card1);
        startMeetingCard = (ImageView)findViewById(R.id.img2);
        startMeetingLayout = (LinearLayout)findViewById(R.id.layout_card2);
        openSyncroomCard = (ImageView)findViewById(R.id.img3);
        openSyncroomLayout = (LinearLayout)findViewById(R.id.layout_card3);
        openSyncroomCard.setOnClickListener(this);
        openDocumentCard.setOnClickListener(this);
        startMeetingCard.setOnClickListener(this);
        GetBottom();
        editListener();


//        EditHelp.hideSoftInputMethod(roomet, NotifyActivity.this);
//        GetPhoneInfo();
        getBindUserInfo();
        SoftInputUtils.hideSoftInput(NotifyActivity.this);
        flag_enter = getIntent().getBooleanExtra("enter", false);
        roomid = getIntent().getStringExtra("meetingId");
        if (flag_enter && !TextUtils.isEmpty(roomid)) {

            attachmentId = getIntent().getStringExtra("attachmentId");
            type = getIntent().getIntExtra("type", 0);
            Log.e("NotifyActivity","on create follow user");
            followUser();
        } else {
//            roomet.setFocusable(true);
//            roomet.setSelection(0);
            fl_main.setVisibility(View.GONE);
        }
        HoverHaha();
    }

    public void ShowBlack() {
        fl_main.setVisibility(View.GONE);
    }

    public void ShowWhite() {
        fl_main.setVisibility(View.VISIBLE);
    }

    private InputMethodManager inputManager;

    /**
     * 鼠标滑动监听
     */
    private void HoverHaha() {
        /*inputManager = (InputMethodManager) roomet
                .getContext().getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);*/
        roomet.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        roomet.setFocusable(true);
                        if (flag_bo || flag_t != 0) {
//                            SoftInputUtils.hideSoftInput(NotifyActivity.this);
//                            inputManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                        }
                        Log.e("haha", "ACTION_HOVER_ENTER");
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        Log.e("haha", "ACTION_HOVER_MOVE");
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        if (flag_bo || flag_t != 0) {
                            roomet.setFocusable(false);
                            SoftInputUtils.hideSoftInput(NotifyActivity.this);
                        }
                        Log.e("haha", "ACTION_HOVER_EXIT");
                        break;
                }

                return false;
            }
        });
//        roomet.setOnClickListener(this);
    }

    private void GetBottom() {
        for (int i = 0; i < imgIDs.length; i++) {
            ImageView img = (ImageView) findViewById(imgIDs[i]);
            imgs.add(img);
        }
        imgsLayout.add(openDucumentLayout);
        imgsLayout.add(startMeetingLayout);
        imgsLayout.add(openSyncroomLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String user = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE).getString("tv_bind_user","");
        Log.e("NotifyActivity","bind user id:" + user);
        if(TextUtils.isEmpty(user)|| user.equals("0")){
            goToQrcodeActivity();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        flag_enter = intent.getBooleanExtra("enter", false);
        roomid = intent.getStringExtra("meetingId");
        if (flag_enter && !TextUtils.isEmpty(roomid)) {
            Log.e("Test_Meeting"," onNewIntent meeting id:" + roomid);
            attachmentId = intent.getStringExtra("attachmentId");
            type = intent.getIntExtra("type", -1);
            Log.e("NotifyActivity","on create follow user");
            followUser();
        } else {
//            roomet.setFocusable(true);
//            roomet.setSelection(0);
            fl_main.setVisibility(View.GONE);
        }
        setIntent(intent);
    }

    private void followUser() {
        if (!TextUtils.isEmpty(roomid) && !TextUtils.isEmpty(AppConfig.BINDUSERID)) {
            Log.e("NotifyActivity","follow user,meeting type:" + type + ",meeting id" + roomid);
            if(type == 1 || type == 2){

                Intent intent = (type == 2) ? new Intent(NotifyActivity.this, WatchCourseActivity3.class) :
                        new Intent(NotifyActivity.this, SyncRoomActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userid", AppConfig.BINDUSERID);
                intent.putExtra("meetingId", roomid);
                intent.putExtra("isTeamspace", true);
                intent.putExtra("identity", 1);
                intent.putExtra("is_meeting", false);
                intent.putExtra("lessionId", "");
                intent.putExtra("isInstantMeeting", 1);
                intent.putExtra("meeting_type", type);
                intent.putExtra("teacherid", AppConfig.BINDUSERID.replace("-", ""));
                intent.putExtra("isStartCourse", false);
                startActivity(intent);
            }else if(type == 0){
//                checkClassRoomExist(roomid);
                Log.e("NotifyActivity","start meeting");
                Intent intent =  new Intent(NotifyActivity.this, WatchCourseActivity3.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userid", AppConfig.BINDUSERID);
                intent.putExtra("meetingId", roomid);
                intent.putExtra("isTeamspace", false);
                intent.putExtra("identity", 1);
                intent.putExtra("lessionId", "");
                intent.putExtra("isInstantMeeting", 1);
                intent.putExtra("meeting_type", type);
                intent.putExtra("teacherid", AppConfig.BINDUSERID.replace("-", ""));
                intent.putExtra("isStartCourse", false);
                intent.putExtra("is_meeting", true);
                startActivity(intent);
            }

            //  getServiceDetail();
        } else {
            Toast.makeText(NotifyActivity.this, getString(R.string.joinroom), Toast.LENGTH_LONG).show();
        }
    }

    private void getServiceDetail() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                JSONObject returnjson = com.ub.techexcel.service.ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "Lesson/Item?lessonID=" + roomid);
                formatServiceData(returnjson);
            }
        }).start();
    }

    private void formatServiceData(JSONObject returnJson) {
        Log.e("returnJson", returnJson.toString());
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONObject service = returnJson.getJSONObject("RetData");
                    String tid = null;
                    JSONArray memberlist = service.getJSONArray("MemberInfoList");
                    for (int i = 0; i < memberlist.length(); i++) {
                        JSONObject jsonObject = memberlist.getJSONObject(i);
                        int role = jsonObject.getInt("Role");
                        if (role == 2) { //teacher
                            tid = jsonObject.getString("MemberID");
                            break;
                        }
                    }
                    Message msg = new Message();
                    msg.obj = tid;
                    msg.what = 0x1306;
                    handler.sendMessage(msg);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getBindUserInfo() {
        LoginGet lg = new LoginGet();
        lg.setDetailGetListener(new LoginGet.DetailGetListener() {
            @Override
            public void getUser(Customer user) {
                tv_name.setText(user.getName());
                Intent data = new Intent("com.kloudsync.techexcel2.username_receive");
                data.putExtra("user_name",user.getName());
                Log.e("user_name_test","send user name:" + user.getName());
                sendBroadcast(data);
                String url = user.getUrl();
                if (!TextUtils.isEmpty(url)) {
                    Uri imageUri = Uri.parse(url);
                    img_head.setImageURI(imageUri);
                }
            }

            @Override
            public void getMember(Customer member) {
                // TODO Auto-generated method stub

            }
        });
        lg.CustomerDetailRequest(getApplicationContext(), AppConfig.BINDUSERID);

    }

    private void editListener() {
        roomet.setOnKeyListener(Key_listener());
    }

    private boolean flag;

    @NonNull
    private View.OnKeyListener Key_listener() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e("haha", (keyCode == KeyEvent.KEYCODE_ENTER) + ":" + (keyCode == KeyEvent.KEYCODE_BACK) + ":" + keyCode);
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String t1 = roomet.getText().toString();
                    if (!TextUtils.isEmpty(t1)) {
                        if (flag) {
                            JoinRoom2();
                        }
                        flag = !flag;
                    }
                    Log.e("haha", "KEYCODE_ENTER");
                }
                return false;
            }
        };
    }

    private void InputConfirmation() {
        roomid = roomet.getText().toString();
        if (!TextUtils.isEmpty(roomid)) {
//            roomid = "-" + roomid;
            checkClassRoomExist(roomid);
        } else {
            Toast.makeText(NotifyActivity.this, getString(R.string.joinroom), Toast.LENGTH_LONG).show();
        }
    }


    private void getClassRoomLessonID(final String classRoomId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomLessonID?classRoomID=" + classRoomId);
                    Log.e("getClassRoomLessonID", jsonObject.toString()); // {"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":-1}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            int retdate = jsonObject.getInt("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1001;
                            msg.obj = retdate;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private void checkClassRoomExist(final String classRoomId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/CheckClassRoomExist?classroomID=" + classRoomId);
                    Log.e("getClassRoomLessonID3", jsonObject.toString()); // {"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":-1}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            int retdate = jsonObject.getInt("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1003;
                            msg.obj = retdate;
                            handler.sendMessage(msg);
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void getClassRoomTeacherID(final String classRoomId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomTeacherID?classroomID=" + classRoomId);
                    Log.e("GetClassRoomTeacherID", jsonObject.toString()); // {"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":-1}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            int retdate = jsonObject.getInt("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1004;
                            msg.obj = retdate;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private String roomid;
    private String attachmentId;
    private int type;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.backll:
//                finish();

                goToQrcodeActivity();
                break;
            case R.id.roomet:
                ShowInput();
                break;
            case R.id.tv_logout:
                LogoutRoom();
                break;
            case R.id.joinroom2:
                JoinRoom2();
                break;
            case R.id.img1:
                Intent selectDocumentIntent = new Intent(this,SelectDocumentActivity.class);
                startActivity(selectDocumentIntent);
                break;
            default:
                break;
        }
    }

    private void LogoutRoom() {
        flag_back = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                        AppConfig.URL_WSS_SERVER + "/MeetingServer/tv/logout",
                        new JSONObject());
//                Log.e("logout_respose", responsedata.toString() + "");
                goToQrcodeActivity();
            }
        }).start();

    }

    private void ShowInput() {
        flag_t = 0;
        Select_etinput();
//        inputManager.showSoftInput(roomet, 0);
    }

    private void JoinRoom2() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(roomet.getWindowToken(), 0);

        InputConfirmation();
    }


    private List<UpcomingLesson> upcomingLessonList = new ArrayList<>();

    private void getUpcomingLessonList(final String teacherid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/UpcomingLessonList?teacherID=" + teacherid);
                    Log.e("upcoming", teacherid + "   " + jsonObject.toString());
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            JSONArray jsonArray = jsonObject.getJSONArray("RetData");
                            upcomingLessonList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject js = jsonArray.getJSONObject(i);
                                UpcomingLesson upcomingLesson = new UpcomingLesson();
                                upcomingLesson.setLessonID(js.getString("LessonID"));
                                upcomingLesson.setTitle(js.getString("Title"));
                                upcomingLesson.setStartDate(js.getString("StartDate"));
                                upcomingLesson.setTeacherID(js.getString("TeacherID"));
                                upcomingLesson.setStudentID(js.getString("StudentID"));
                                upcomingLesson.setCourseID(js.getString("CourseID"));
                                upcomingLesson.setCourseName(js.getString("CourseName"));
                                upcomingLesson.setLectureIDs(js.getString("LectureIDs"));
                                upcomingLesson.setIsInClassroom(js.getInt("IsInClassroom"));
                                upcomingLesson.setIsOnGoing(js.getInt("IsOnGoing"));
                                upcomingLessonList.add(upcomingLesson);
                            }
                            Message message = Message.obtain();
                            message.what = 0x1305;
                            handler.sendMessage(message);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //右键点击判断
    private boolean flag_right;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Fmhaha();
//            finish();
//            overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            Select_etinput();
            Log.e("haha", "KEYCODE_DPAD_UP");
            return false;

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            flag_bo = true;
            flag_t = 0;//默认向上选中输入框
            select(flag_b);
            Log.e("haha", "KEYCODE_DPAD_DOWN");
            roomet.setFocusable(false);
            editLayout.setBackground(null);
            roomet.setBackgroundResource(R.drawable.bg_input_meeting);
            lin_enter.setBackground(null);
            lin_logout.setBackground(null);
            return false;

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (flag_bo) {
//                lin_enter.setBackground(null);
                flag_b--;
                select(flag_b);
            } else {
                if (flag_t > 0) {
                    flag_t--;
                }
                switch (flag_t) {
                    case 0:
                        roomet.setBackground(null);
                        editLayout.setBackgroundResource(R.drawable.bg_input);
                        lin_enter.setBackground(null);
                        break;
                    case 1:
                        lin_enter.setBackgroundResource(R.drawable.bg_input);
                        editLayout.setBackground(null);
                        roomet.setBackgroundResource(R.drawable.bg_input_meeting);
                        lin_logout.setBackground(null);
                        break;
                    default:
                        break;
                }
            }
            Log.e("haha", "KEYCODE_DPAD_LEFT");
            return false;

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (flag_bo) {
                flag_b++;
                select(flag_b);
            } else {
                roomet.setFocusable(false);
                if (flag_t < 2) {
                    flag_t++;
                }
                switch (flag_t) {
                    case 1:
                        lin_enter.setBackgroundResource(R.drawable.bg_input);
                        editLayout.setBackground(null);
                        roomet.setBackgroundResource(R.drawable.bg_input_meeting);
                        lin_logout.setBackground(null);
                        break;
                    case 2:
                        lin_enter.setBackground(null);
                        lin_logout.setBackgroundResource(R.drawable.bg_input);
                        editLayout.setBackground(null);
                        roomet.setBackgroundResource(R.drawable.bg_input_meeting);
                        break;
                    default:
                        break;
                }
            }
            Log.e("haha", "KEYCODE_DPAD_RIGHT");
            return false;

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            Log.e("haha", "KEYCODE_DPAD_CENTER:" + flag_b + ":" + flag_bo + ":" + roomet.isFocusable());
            if (!flag_bo && !roomet.isFocusable()) {
                if (1 == flag_t) {
                    InputConfirmation();
                } else if (2 == flag_t) {
                    LogoutRoom();
                }else if(0 == flag_t){
                    roomet.setFocusable(true);
                }
            }
            return false;

        } else if(keyCode == KeyEvent.KEYCODE_MENU){
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        /*if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
            Toast.makeText(NotifyActivity.this, "鼠标右键单击事件", Toast.LENGTH_SHORT).show();
            return false;
        } else if (event.getButtonState() == MotionEvent.BUTTON_BACK) {
            flag_right = true;
//            Toast.makeText(NotifyActivity.this, "返回单击事件" + event.getAction(), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
            if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                Toast.makeText(NotifyActivity.this, "鼠标左键单击事件", Toast.LENGTH_SHORT).show();
                return false;
            } else if (event.getButtonState() == MotionEvent.BUTTON_TERTIARY) {
                Toast.makeText(NotifyActivity.this, "鼠标中键单击事件", Toast.LENGTH_SHORT).show();
                return false;
            }
        }*/
        return super.onGenericMotionEvent(event);
    }


    private void Select_etinput() {
        if (flag_bo) {
//            roomet.setFocusable(true);
            flag_t = 0;
        }
        flag_bo = false;
        lin_logout.setBackground(null);
        lin_enter.setBackground(null);
        editLayout.setBackground(getResources().getDrawable(R.drawable.bg_input));
        roomet.setBackground(null);
        for (int i = 0; i < imgsLayout.size(); i++) {
            imgsLayout.get(i).setBackground(null);
        }
    }

    private void select(int x) {
        if (flag_bo) {
            if (x < 0) {
                flag_b = 0;
            } else if (x > imgsLayout.size() - 1) {
                flag_b = imgsLayout.size() - 1;
                return;
            }

            for (int i = 0; i < imgsLayout.size(); i++) {
                imgsLayout.get(i).setBackground(i == flag_b ? getResources().getDrawable(R.drawable.bg_input) : null);
            }
        }
    }

    private void Fmhaha() {
        if (!flag_back) {
            /*if (QrCodeActivity.instance != null && !QrCodeActivity.instance.isFinishing()) {
                QrCodeActivity.instance.finish();
            }*/
//            if (MainActivity.instance != null && !MainActivity.instance.isFinishing()) {
//                MainActivity.instance.finish();
//            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e("NotifyActivity","on Destroy");
        Fmhaha();
        if(heartBeatMsgReceiver != null){
            unregisterReceiver(heartBeatMsgReceiver);
        }

    }


    private void setDeviceType(int deviceType){
        if(deviceTypeText == null){
            return;
        }
        Log.e("NotifyActivity","setBindStatus:" + deviceType);
        if(deviceType >= 0){
            deviceTypeText.setVisibility(View.VISIBLE);
            if(deviceType == 0){
                deviceTypeText.setText(" AutoSync is on with web");
            }else if(deviceType == 1 || deviceType == 2){
                deviceTypeText.setText(" AutoSync is on with phone");
            }
        }else {
            deviceTypeText.setText(" AutoSync is off");
            deviceTypeText.setVisibility(View.INVISIBLE);
        }
    }

    private void goToQrcodeActivity(){
        getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE).edit().putString("tv_bind_user","").commit();
        AppConfig.BINDUSERID = "";
        Intent intent = new Intent(this,QrCodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
        startActivity(intent);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },500);
    }
}




