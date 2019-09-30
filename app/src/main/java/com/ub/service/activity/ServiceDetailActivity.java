package com.ub.service.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.dialog.message.CustomizeMessage;
import com.kloudsync.techexcel2.info.Customer;
import com.kloudsync.techexcel2.start.LoginGet;
import com.kloudsync.techexcel2.start.LoginGet.DetailGetListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class ServiceDetailActivity extends Activity implements OnClickListener {
    private static final int LOAD_FINISH1 = 0x1101;
    private TextView back;
    private TextView nameTV;
    private TextView telTV;
    private TextView addressTV;
    private TextView desTV;
    private TextView beizhuTV;
    private TextView soucontent;
    private int itemId;
    private ServiceBean bean = new ServiceBean();
    public static ServiceDetailActivity serviceDetailActivityInstabce;
    private LinearLayout servicedetail;
    private int conversationtype;
    private String mTargetId;
    private ImageView mImageView;

    private LinearLayout botton;
    private LinearLayout opencourse, startcourse, endcourse;
    private TextView startvalue;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:

                    if (bean.getStatusID() == 322) {
                        botton.setVisibility(View.VISIBLE);
                    } else if (bean.getStatusID() == 1) {
                        botton.setVisibility(View.INVISIBLE);
                    }

                    if(bean.getRoleinlesson()==2){ //teacher
                        startvalue.setText(getResources().getString(R.string.start));
                    }else {
                        startvalue.setText(getResources().getString(R.string.join));
                    }

                    LoginGet get = new LoginGet();
                    get.setDetailGetListener(new DetailGetListener() {

                        @Override
                        public void getUser(Customer user) {
                            // TODO Auto-generated method stub
                            bean.getCustomer().setUBAOUserID(user.getUBAOUserID());
                            bean.setCustomerRongCloudId(user.getUBAOUserID() + "");
                        }

                        @Override
                        public void getMember(Customer member) {
                            // TODO Auto-generated method stub
                        }
                    });
                    get.CustomerDetailRequest(ServiceDetailActivity.this, bean
                            .getCustomer().getUserID());

                    servicedetail.setVisibility(View.VISIBLE);
                    nameTV.setText(bean.getCustomer().getName());
                    telTV.setText(bean.getCustomer().getPhone());
                    addressTV.setText(bean.getCustomer().getCurrentPosition());
                    desTV.setText(bean.getDescription());
                    beizhuTV.setText(bean.getComment());
                    soucontent.setText(bean.getLinkedSolutionName());
                    // 初始化完成后可点击
                    initPopuptWindow();
                    mImageView.setEnabled(true);
                    break;
                case LOAD_FINISH1:
                    soucontent.setText(bean.getLinkedSolutionName());
                    break;
                case AppConfig.CONFIRM_SERVICE:
                    Intent ii = new Intent();
                    ii.setAction("com.ubao.techexcel.frgment");
                    sendBroadcast(ii);
                    finish();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicedetail);
        Log.e("haha", "onCreate");
        serviceDetailActivityInstabce = this;
        itemId = getIntent().getIntExtra("id", 0);
        conversationtype = getIntent().getIntExtra("conversationtype", 1);
        mTargetId = getIntent().getStringExtra("mTargetId");
        initView();
        getServiceDetail();
        if (getIntent().getBooleanExtra("ismodifyservice", false)) {
            Dialog(ServiceDetailActivity.this);
        }
    }

    private AlertDialog dialog2;

    public void Dialog(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View windov = inflater.inflate(R.layout.servicrmodify_dialog, null);
        windov.findViewById(R.id.smsinvate).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        dialog2.dismiss();
                    }
                });
        dialog2 = new AlertDialog.Builder(context).show();
        Window dialogWindow = dialog2.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        // p.height = (int) (d.getHeight() * 0.5);
        dialogWindow.setAttributes(p);
        dialog2.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        dialog2.setContentView(windov);

    }

    private void getServiceDetail() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                JSONObject returnjson = ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "Service/Item?ServiceID=" + itemId);
                formatServiceData(returnjson);
            }
        }).start();
    }

    public void onResume() {
        super.onResume();
        Log.e("haha", "onResume");
        MobclickAgent.onPageStart("ServiceDetailActivity");
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        Log.e("haha", "onPause");
        MobclickAgent.onPageEnd("ServiceDetailActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("haha", "onDestroy");
    }

    private void formatServiceData(JSONObject returnJson) {
        Log.e("returnJson", returnJson.toString());
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONObject service = returnJson.getJSONObject("RetData");
                    bean = new ServiceBean();
                    String name = service.getString("Name");
                    bean.setName((name == null || name.equals("null")) ? "" : name);
                    bean.setConcernID(service.getInt("ConcernID"));
                    String des = service.getString("Description");
                    bean.setDescription((des == null || des.equals("null")) ? ""
                            : des);
                    try {
                        JSONObject jsonObject = service.getJSONObject("User");
                        if (jsonObject != null) {
                            Customer customer = new Customer();
                            customer.setName(jsonObject.getString("Name"));
                            customer.setPhone(jsonObject.getString("Mobile"));
                            customer.setCurrentPosition(jsonObject
                                    .getString("Address"));
                            customer.setUserID(jsonObject.getString("ID"));
                            bean.setCustomer(customer);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int statusID = service.getInt("StatusID");
                    bean.setStatusID(statusID);
                    bean.setId(service.getInt("ID"));
                    bean.setTeacherId(service.getString("TeacherID"));
                    bean.setRoleinlesson(service.getInt("RoleInLesson"));

                    JSONObject linkedSolution = service
                            .getJSONObject("LinkedSolution");
                    bean.setLinkedSolutionID(linkedSolution.getInt("ID"));
                    bean.setLinkedSolutionName(linkedSolution.getString("Name")); //
                    JSONArray lineitems = service.getJSONArray("LineItems");
                    List<LineItem> items = new ArrayList<LineItem>();
                    for (int j = 0; j < lineitems.length(); j++) {
                        JSONObject lineitem = lineitems.getJSONObject(j);
                        LineItem item = new LineItem();
                        item.setEventID(lineitem.getInt("EventID"));
                        String linename = lineitem.getString("EventName");
                        item.setEventName((linename == null || linename
                                .equals("null")) ? "" : linename);
                        item.setUrl(lineitem.getString("AttachmentUrl"));
                        item.setAttachmentID(lineitem.getString("AttachmentID"));
                        items.add(item);
                    }
                    bean.setLineItems(items);
                    handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void initView() {
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        nameTV = (TextView) findViewById(R.id.username);
        telTV = (TextView) findViewById(R.id.tel);
        addressTV = (TextView) findViewById(R.id.address);
        desTV = (TextView) findViewById(R.id.description);
        beizhuTV = (TextView) findViewById(R.id.beizhu);
        soucontent = (TextView) findViewById(R.id.soucontent);
        servicedetail = (LinearLayout) findViewById(R.id.servicedetail);
        servicedetail.setVisibility(View.GONE);

        mImageView = (ImageView) findViewById(R.id.popimageview);
        mImageView.setOnClickListener(this);
        mImageView.setEnabled(false);

        botton = (LinearLayout) findViewById(R.id.botton);
        opencourse = (LinearLayout) findViewById(R.id.opencourse);
        opencourse.setOnClickListener(this);
        startcourse = (LinearLayout) findViewById(R.id.startcourse);
        endcourse = (LinearLayout) findViewById(R.id.endcourse);
        startcourse.setOnClickListener(this);
        endcourse.setOnClickListener(this);
        startvalue=(TextView)findViewById(R.id.startvalue);

    }

    public PopupWindow mPopupWindow;

    private void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater
                .from(ServiceDetailActivity.this);
        View popupWindow = layoutInflater.inflate(
                R.layout.servicedetail_dialog, null);

        LinearLayout isShowll = (LinearLayout) popupWindow
                .findViewById(R.id.isshow);
        LinearLayout lin_todialog = (LinearLayout) popupWindow
                .findViewById(R.id.slin_todialog);
        LinearLayout lin_groupchat = (LinearLayout) popupWindow
                .findViewById(R.id.slin_groupchat);
        LinearLayout lin_todialogs = (LinearLayout) popupWindow
                .findViewById(R.id.slin_todialogs);
        LinearLayout lin_addfriend = (LinearLayout) popupWindow
                .findViewById(R.id.slin_addfriend);

        lin_todialog.setOnClickListener(this);
        lin_groupchat.setOnClickListener(this);
        lin_todialogs.setOnClickListener(this);
        lin_addfriend.setOnClickListener(this);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        if (bean.getStatusID() == 1) { //该服务已结束
            isShowll.setVisibility(View.GONE);
            height = height / 2;
        } else if (bean.getStatusID() == 332) {  //进行中
            isShowll.setVisibility(View.VISIBLE);
        }
        mPopupWindow = new PopupWindow(popupWindow, width * 2 / 5, height / 3,
                false);
        mPopupWindow.getWidth();
        mPopupWindow.getHeight();
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.setAnimationStyle(R.style.anination);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    Intent intent;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                if (AppConfig.ISMODIFY_SERVICE) {
                    AppConfig.ISMODIFY_SERVICE = false;
                    finish();
                    Intent ii = new Intent();
                    ii.setAction("com.ubao.techexcel.frgment");
                    sendBroadcast(ii);
                } else {
                    finish();
                    if (AppConfig.isNewService) {
                        AppConfig.isNewService = false;
                    }
                }
                break;

            case R.id.popimageview:
//                mPopupWindow.showAsDropDown(v); //弹出popupwindow
                break;
            case R.id.slin_todialog: //发送消息
                mPopupWindow.dismiss();
                final Customer customer = bean.getCustomer();
                AppConfig.Name = customer.getName();
                RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                    @Override
                    public UserInfo getUserInfo(String s) {
                        return new UserInfo(
                                customer.getUBAOUserID(),
                                customer.getName(),
                                null);
                    }
                }, true);
                RongIM.getInstance().startPrivateChat(this,
                        customer.getUBAOUserID(), customer.getName());
                break;
            case R.id.slin_groupchat: // 修改服务
                mPopupWindow.dismiss();
                intent = new Intent(ServiceDetailActivity.this,
                        SelectCourseActivity.class);
                intent.putExtra("service", bean);
                intent.putExtra("conversationtype", conversationtype);// 会话类型
                intent.putExtra("mTargetId", mTargetId);// 群聊
                AppConfig.ISMODIFY_SERVICE = true;
                startActivity(intent);
                break;
            case R.id.slin_todialogs: // 确认结束
                mPopupWindow.dismiss();
                ConfirmFinish(bean);
                break;
            case R.id.slin_addfriend: // 发送服务
                mPopupWindow.dismiss();
                sendServiceToUser();
                break;
            case R.id.opencourse:
                intent = new Intent(ServiceDetailActivity.this, WatchCourseActivity2.class);
//                intent.putExtra("isHtml", bean.getLineItems().get(0).isHtml5());
//                intent.putExtra("url", bean.getLineItems().get(0).getUrl());
//                intent.putExtra("attachmentid", bean.getLineItems().get(0).getAttachmentID());
                intent.putExtra("userid", bean.getCustomer().getUserID());
                intent.putExtra("meetingId", bean.getId() + "");
                intent.putExtra("teacherid", bean.getTeacherId());
                intent.putExtra("identity", bean.getRoleinlesson());
                intent.putExtra("isStartCourse", false);
                intent.putExtra("isInstantMeeting", 0);
                startActivity(intent);
                break;
            case R.id.startcourse: // 开始上课
                intent = new Intent(ServiceDetailActivity.this, WatchCourseActivity2.class);
//                intent.putExtra("isHtml", bean.getLineItems().get(0).isHtml5());
//                intent.putExtra("url", bean.getLineItems().get(0).getUrl());
//                intent.putExtra("attachmentid", bean.getLineItems().get(0).getAttachmentID());
                intent.putExtra("userid", bean.getCustomer().getUserID());
                intent.putExtra("meetingId", bean.getId() + "");
                intent.putExtra("teacherid", bean.getTeacherId());
                intent.putExtra("identity", bean.getRoleinlesson());
                intent.putExtra("isStartCourse", true);
                intent.putExtra("isInstantMeeting", 0);
                startActivity(intent);
                break;
            case R.id.endcourse: // 结束课程
                ConfirmFinish(bean);
                break;
            default:
                break;
        }

    }

    /**
     * 确认结束服务
     *
     * @param serviceBean
     */
    private AlertDialog dialog;

    private void ConfirmFinish(final ServiceBean serviceBean) {
        final LayoutInflater inflater = LayoutInflater
                .from(ServiceDetailActivity.this);
        View windov = inflater.inflate(R.layout.confirmservice, null);
        windov.findViewById(R.id.no).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        windov.findViewById(R.id.yes).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            JSONObject submitjson = new JSONObject();
                            submitjson.put("ID", serviceBean.getId());
                            submitjson.put("StatusID", 1);
                            JSONObject jsonObject = ConnectService
                                    .submitDataByJson(AppConfig.URL_PUBLIC
                                            + "/Service/Forward", submitjson);

                            Log.e("结束课程", submitjson.toString() + "  " + jsonObject.toString());

                            if (jsonObject.getInt("RetCode") == 0) {
                                handler.obtainMessage(AppConfig.CONFIRM_SERVICE)
                                        .sendToTarget();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(this).show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(p);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(windov);

    }

    /**
     * 給用戶发送服务单
     */
    private void sendServiceToUser() {
        // TODO Auto-generated method stub
        if (conversationtype == 1) {
            ServiceBean sb = bean;
            CustomizeMessage msg = new CustomizeMessage(sb.getId() + "",
                    sb.getName(), sb.getConcernName(), "800");
            RongIM.getInstance()
                    .getRongIMClient()
                    .sendMessage(Conversation.ConversationType.PRIVATE,
                            sb.getCustomer().getUBAOUserID(), msg, "", "",
                            new RongIMClient.SendMessageCallback() {
                                @Override
                                public void onError(Integer messageId,
                                                    RongIMClient.ErrorCode e) {
                                    Toast.makeText(ServiceDetailActivity.this,
                                            "发送服务单失败", Toast.LENGTH_LONG)
                                            .show();
                                }

                                @Override
                                public void onSuccess(Integer integer) {
                                    Toast.makeText(ServiceDetailActivity.this,
                                            "发送服务单成功", Toast.LENGTH_LONG)
                                            .show();

                                }
                            });
        } else if (conversationtype == 2) {
            ServiceBean sb = bean;
            CustomizeMessage msg = new CustomizeMessage(sb.getId() + "",
                    sb.getName(), sb.getConcernName(), "800");
            RongIM.getInstance()
                    .getRongIMClient()
                    .sendMessage(Conversation.ConversationType.GROUP,
                            mTargetId, msg, "", "",
                            new RongIMClient.SendMessageCallback() {
                                @Override
                                public void onError(Integer messageId,
                                                    RongIMClient.ErrorCode e) {
                                    Toast.makeText(ServiceDetailActivity.this,
                                            "发送服务单失败", Toast.LENGTH_LONG)
                                            .show();
                                }

                                @Override
                                public void onSuccess(Integer integer) {
                                    Toast.makeText(ServiceDetailActivity.this,
                                            "发送服务单成功", Toast.LENGTH_LONG)
                                            .show();

                                }
                            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            if (AppConfig.ISMODIFY_SERVICE) {
                AppConfig.ISMODIFY_SERVICE = false;
                finish();
                AppConfig.ISONRESUME = true;
            } else {
                finish();
                if (AppConfig.isNewService) {
                    AppConfig.isNewService = false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
