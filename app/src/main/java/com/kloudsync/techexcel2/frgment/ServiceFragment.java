package com.kloudsync.techexcel2.frgment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ub.service.activity.MyKlassroomActivity;
import com.ub.service.activity.NotifyActivity;
import com.ub.service.activity.SelectCourseActivity;
import com.ub.service.activity.ServiceDetailActivity;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.techexcel.adapter.ServiceAdapter2;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.ServiceTool;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.info.Customer;
import com.kloudsync.techexcel2.start.LoginGet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class ServiceFragment extends MyFragment implements View.OnClickListener,
        ViewPager.OnPageChangeListener {
    private boolean isLoadDataFinish = false, isPrepared = false;
    private ServiceAdapter2 serviceAdapter1;
    private PullToRefreshListView serviceListView1;
    private List<ServiceBean> mList1 = new ArrayList<>(),
            mList2 = new ArrayList<>(),
            myList = new ArrayList<>();
    private List<List<ServiceBean>> mlist = new ArrayList<>();  //所有课程的list集合
    private TextView addService;
    private TextView allServiceTv, serviceStatusTv, allTypeTv;
    private ImageView img_notice;
    private TextView underline;
    private TextView tv_ns;
    private BroadcastReceiver broadcastReceiver;

    private ArrayList<Customer> custometList = new ArrayList<Customer>();
    private BroadcastReceiver broadcastReceiver_finish;
    private LinearLayout defaultPage;
    private PopupWindow mPopupWindow;
    private LinearLayout lin_myroom, lin_join, lin_schedule;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    defaultPage.setVisibility(View.GONE);
                    serviceListView1.onRefreshComplete();
                    mList1 = sortBydata(mList1);
                    isLoadDataFinish = true;
                    serviceAdapter1 = new ServiceAdapter2(getActivity(), mList1, true, true);
                    serviceListView1.setAdapter(serviceAdapter1);
                    serviceAdapter1
                            .setOnModifyServiceListener(new ServiceAdapter2.OnModifyServiceListener() {
                                @Override
                                public void onBeginStudy(int position) { // 开始上课
                                    Intent intent = new Intent(getActivity(), WatchCourseActivity2.class);
                                    intent.putExtra("userid", mList1.get(position).getCustomer().getUserID());
                                    intent.putExtra("meetingId", mList1.get(position).getId() + "");
                                    intent.putExtra("teacherid", mList1.get(position).getTeacherId());
                                    intent.putExtra("identity", mList1.get(position).getRoleinlesson());
                                    intent.putExtra("isInstantMeeting", 0);
                                    intent.putExtra("isStartCourse", true);
                                    startActivity(intent);
                                }
                            });
                    break;

                case 0x1102:
                    Intent intent = new Intent(getActivity(), SelectCourseActivity.class);
                    intent.putExtra("service", (ServiceBean) msg.obj);
                    AppConfig.ISMODIFY_SERVICE = true;
                    startActivity(intent);
                    break;
//                case 0x1104:  //加入会议室
//                    ServiceBean serviceBean = (ServiceBean) msg.obj;
//                    Intent intent3 = new Intent(getActivity(), WatchCourseActivity2.class);
//                    intent3.putExtra("isHtml", serviceBean.getLineItems().get(0).isHtml5());
//                    intent3.putExtra("url", serviceBean.getLineItems().get(0).getUrl());
//                    intent3.putExtra("CustomerRongCloudID", serviceBean.getCustomerRongCloudId());
//                    intent3.putExtra("attachmentid", serviceBean.getLineItems().get(0).getAttachmentID());
//                    intent3.putExtra("userid", serviceBean.getUserId());
//                    intent3.putExtra("meetingId", serviceBean.getId() + "");
//                    intent3.putExtra("teacherid", serviceBean.getTeacherId());
//                    intent3.putExtra("isInstantMeeting", 0);
//                    if (serviceBean.getTeacherId().equals((AppConfig.UserID + "").replace("-", ""))) {
//                        intent3.putExtra("identity", 2);
//                    } else if (serviceBean.getUserId().equals((AppConfig.UserID + "").replace("-", ""))) {
//                        intent3.putExtra("identity", 1);
//                    } else {
//                        intent3.putExtra("identity", 3);
//                    }
//                    intent3.putExtra("lineItems", (Serializable) serviceBean.getLineItems());
//                    startActivity(intent3);
//                    break;
                default:
                    break;
            }
        }
    };



    private List<ServiceBean> sortBydata(List<ServiceBean> mList1) {

        Collections.sort(mList1, new Comparator<ServiceBean>() {
            @Override
            public int compare(ServiceBean s1, ServiceBean s2) {
                String x1 = s1.getPlanedStartDate();
                String x2 = s2.getPlanedStartDate();
                if (TextUtils.isEmpty(x1)) {
                    x1 = "0";
                }
                if (TextUtils.isEmpty(x2)) {
                    x2 = "0";
                }
                if (Long.parseLong(x1) > Long.parseLong(x2)) {
                    return 1;
                }
                if (Long.parseLong(x1) == Long.parseLong(x2)) {
                    return 0;
                }
                return -1;
            }
        });


        for (ServiceBean bean : mList1) {
            String planedsatrtdate = bean.getPlanedStartDate();
            if (TextUtils.isEmpty(planedsatrtdate)) {
                bean.setDateType(4);
            } else {
                long today = System.currentTimeMillis();
                long planed = Long.parseLong(planedsatrtdate);
                long diff = diffTime();

                long xx = planed - today;
                if (xx < 0) {
                    bean.setDateType(4);//今天之前的  已结束的
                } else if (xx >= 0 && xx < diff) {
                    bean.setDateType(1); //今天的
                    bean.setMins((int) (xx / 1000 / 60));
                } else if (xx >= diff && xx < 86400000 * 2) {
                    bean.setDateType(2); //明天的
                } else if (xx >= 86400000 * 2) {
                    bean.setDateType(3);//后天及以后
                }
            }
        }
        List<ServiceBean> list = new ArrayList<>();

        for (ServiceBean bean : mList1) {
            if (bean.getDateType() == 1) {
                list.add(bean);
            }
        }

        for (ServiceBean bean : mList1) {
            if (bean.getDateType() == 2) {
                list.add(bean);
            }
        }

        for (ServiceBean bean : mList1) {
            if (bean.getDateType() == 3) {
                list.add(bean);
            }
        }

        for (ServiceBean bean : mList1) {
            if (bean.getDateType() == 4) {
                list.add(bean);
            }
        }

        for (ServiceBean bean : list) {
            if (bean.getDateType() == 1) {
                bean.setShow(true);
                break;
            }
        }
        for (ServiceBean bean : list) {
            if (bean.getDateType() == 2) {
                bean.setShow(true);
                break;
            }
        }
        for (ServiceBean bean : list) {
            if (bean.getDateType() == 3) {
                bean.setShow(true);
                break;
            }
        }
        for (ServiceBean bean : list) {
            if (bean.getDateType() == 4) {
                bean.setShow(true);
                break;
            }
        }
        return list;
    }

    private long diffTime() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long diff = cal.getTimeInMillis() - System.currentTimeMillis();
        return diff;
    }


    /**
     * 按planstartdate排序
     */
    private void sortList(List<ServiceBean> list) {
        int i, j;
        int n = list.size();
        for (i = 0; i < n; i++) {
            for (j = 1; j < n - 1; j++) {

                String x1 = list.get(j - 1).getPlanedStartDate();
                String x2 = list.get(j).getPlanedStartDate();
                if (TextUtils.isEmpty(x1)) {
                    x1 = "0";
                }
                if (TextUtils.isEmpty(x2)) {
                    x2 = "0";
                }
                if (Long.parseLong(x1) > Long.parseLong(x2)) {
                    ServiceBean serviceBean = list.get(j - 1);
                    list.set(j - 1, list.get(j));
                    list.set(j, serviceBean);
                }
            }
        }
    }

    /**
     * @param customer
     */
    public void GoToDialog(final Customer customer, final String rongcloudid) {
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
        RongIM.getInstance().startPrivateChat(getActivity(),
                rongcloudid + "", customer.getName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service, container, false);
        initView(view);
        InitImageView();
        isPrepared = true;
        lazyLoad();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        broadcastReceiver_finish = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getAllServiceData(mlist);
            }
        };
        IntentFilter filter_finish = new IntentFilter();
        filter_finish.addAction("com.ubao.techexcel.frgment");
        getActivity().registerReceiver(broadcastReceiver_finish, filter_finish);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        getActivity().unregisterReceiver(broadcastReceiver_finish);
        broadcastReceiver_finish = null;
        if (broadcastReceiver != null && getActivity() != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
        super.onDestroy();
    }


    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible) {
            if (!isLoadDataFinish) {
                getCustomerList();
            }
        }
    }


    /**
     * 获得所有原始数据
     *
     * @param pjList
     */
    private void getAllServiceData(List<List<ServiceBean>> pjList) {
        List<ServiceBean> list0 = new ArrayList<>();
        List<ServiceBean> list1 = new ArrayList<>();
        List<ServiceBean> list2 = new ArrayList<>();
        List<ServiceBean> list3 = new ArrayList<>();
        List<ServiceBean> list4 = new ArrayList<>();
        mList1.clear();
        mList2.clear();
        myList.clear();

        pjList.clear();

        pjList.add(list0);
        pjList.add(list1);
        pjList.add(list2);
        pjList.add(list3);
        pjList.add(list4);

        ExecutorService executorService = Executors.newFixedThreadPool(pjList
                .size());
        for (int i = 0; i < pjList.size(); i++) {
            executorService.execute(new ServiceTool(i, pjList.get(i), custometList));
        }

//        List<Callable<String>> list=new ArrayList<>();
//        try {
//          List<Future<String>>  ll= executorService.invokeAll(list);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Future<String> f = executorService.submit(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                return null;
//            }
//        });

        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                Log.e("大小", list4.size() + "  ");
                for (int i = 0; i < list0.size(); i++) {
                    ServiceBean s = list0.get(i);
                    if (s.getStatusID() == 0) {
                        mList1.add(s);
                    }
                }
                for (int i = 0; i < list1.size(); i++) {
                    ServiceBean s = list1.get(i);
                    if(s.getStatusID() == 0) {
                        mList1.add(s);
                    }
                }
                for (int i = 0; i < list2.size(); i++) {
                    ServiceBean s = list2.get(i);
                    if (s.getStatusID() == 0) {
                        mList2.add(s);
                    }
                }
                for (int i = 0; i < list3.size(); i++) {
                    ServiceBean s = list3.get(i);
                    if (s.getStatusID() == 0) {
                        mList2.add(s);
                    }
                }
                for (int i = 0; i < list4.size(); i++) {
                    ServiceBean s = list4.get(i);
                    myList.add(s);
                }
                handler.sendEmptyMessage(AppConfig.LOAD_FINISH);
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppConfig.newlesson) {
            AppConfig.newlesson = false;
            getCustomerList();
        }
    }


    private void getCustomerList() {
        final LoginGet loginGet = new LoginGet();
        loginGet.setLoginGetListener(new LoginGet.LoginGetListener() {
            @Override
            public void getMember(ArrayList<Customer> list) {

            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                custometList = new ArrayList<Customer>();
                custometList = list;
                getLoginUser();
            }
        });
        loginGet.CustomerRequest(getActivity());
    }


    //登录账号信息
    private void getLoginUser() {
        final LoginGet loginget = new LoginGet();
        loginget.setDetailGetListener(new LoginGet.DetailGetListener() {

            @Override
            public void getUser(Customer CustomerDetailRequest) {
                custometList.add(CustomerDetailRequest);
                getAllServiceData(mlist);
            }

            @Override
            public void getMember(Customer member) {

            }
        });
        loginget.CustomerDetailRequest(getActivity(), AppConfig.UserID);
    }


    private void initView(View view) {
        Fresco.initialize(getActivity());
        lin_myroom = (LinearLayout) view.findViewById(R.id.lin_myroom);
        lin_join = (LinearLayout) view.findViewById(R.id.lin_join);
        lin_schedule = (LinearLayout) view.findViewById(R.id.lin_schedule);
        lin_myroom.setOnClickListener(this);
        lin_join.setOnClickListener(this);
        lin_schedule.setOnClickListener(this);

        allServiceTv = (TextView) view.findViewById(R.id.allService);
        serviceStatusTv = (TextView) view.findViewById(R.id.serviceStatus);
        allTypeTv = (TextView) view.findViewById(R.id.allType);
        addService = (TextView) view.findViewById(R.id.addService);
        underline = (TextView) view.findViewById(R.id.underline);

        allServiceTv.setTextColor(getResources().getColor(R.color.c1));
        allServiceTv.setOnClickListener(this);
        serviceStatusTv.setOnClickListener(this);
        allTypeTv.setOnClickListener(this);
        addService.setOnClickListener(this);

        tv_ns = (TextView) view.findViewById(R.id.tv_ns);
        img_notice = (ImageView) view.findViewById(R.id.img_notice);
        img_notice.setOnClickListener(this);

        defaultPage = (LinearLayout) view.findViewById(R.id.defaultpage);

        serviceListView1 = (PullToRefreshListView) view.findViewById(R.id.serviceList);
        serviceListView1.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        serviceListView1.getLoadingLayoutProxy().setPullLabel(
                getString(R.string.pullsliding));
        serviceListView1.getLoadingLayoutProxy().setReleaseLabel(
                getString(R.string.pullrefresh));
        serviceListView1.getLoadingLayoutProxy().setRefreshingLabel(
                getString(R.string.pull));
        serviceListView1.setOnRefreshListener(new PullRefreshListtener());

        serviceListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(getActivity(),
                        ServiceDetailActivity.class);
                intent.putExtra("id", mList1.get(arg2 - 1).getId());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
            }
        });
        GetCourseBroad();

    }


    class PullRefreshListtener implements PullToRefreshBase.OnRefreshListener {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            serviceListView1.getLoadingLayoutProxy().setReleaseLabel(
                    getString(R.string.pullrefresh));
            serviceListView1.getLoadingLayoutProxy().setRefreshingLabel(
                    getString(R.string.pull));
            if (refreshView.isShownFooter()) { // 下拉分页加载

            } else if (refreshView.isShownHeader()) { // 上拉刷新
                getCustomerList();
            }
        }
    }

    private void GetCourseBroad() {
        RefreshNotify();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RefreshNotify();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.Receive_Course));
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    private void RefreshNotify() {
        int sum = 0;
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (!AppConfig.progressCourse.get(i).isStatus()) {
                sum++;
            }
        }
        tv_ns.setText(sum + "");
        tv_ns.setVisibility(sum == 0 ? View.GONE : View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.allService:
                setHeadColor((TextView) view);
                break;
            case R.id.serviceStatus:
                setHeadColor((TextView) view);
                break;
            case R.id.allType:
                setHeadColor((TextView) view);
                break;
            case R.id.addService:
                Intent intent = new Intent(getActivity(), SelectCourseActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                break;
            case R.id.img_notice:
                startActivity(new Intent(getActivity(), NotifyActivity.class));
                break;
            case R.id.lin_myroom:
                startActivity(new Intent(getActivity(), MyKlassroomActivity.class));
                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                break;
            case R.id.lin_join:
                startActivity(new Intent(getActivity(), NotifyActivity.class));
                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                break;
            case R.id.lin_schedule:
                Intent schintent = new Intent(getActivity(), SelectCourseActivity.class);
                startActivity(schintent);
                getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
                break;
            default:
                break;
        }
    }


    /**
     * PagerAdapter 设置head颜色
     *
     * @param view
     */
    @SuppressLint("NewApi")
    private void setHeadColor(TextView view) {
        allServiceTv.setTextColor(getResources().getColor(R.color.c5));
        serviceStatusTv.setTextColor(getResources().getColor(R.color.c5));
        allTypeTv.setTextColor(getResources().getColor(R.color.c5));
        view.setTextColor(getResources().getColor(R.color.c1));

    }

    int one, two;
    private int offset = 0;  // 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号

    private void InitImageView() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        one = screenW / 3;
        two = one * 2;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        Animation animation = null;
        switch (arg0) {
            case 0:
                setHeadColor(allServiceTv);
                if (currIndex == 1) {
                    animation = new TranslateAnimation(one, 0, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, 0, 0, 0);
                }
                break;
            case 1:
                setHeadColor(serviceStatusTv);
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, one, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, one, 0, 0);
                }
                break;
            case 2:
                setHeadColor(allTypeTv);
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, two, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, two, 0, 0);
                }
                break;
            default:
                break;
        }
        currIndex = arg0;
        animation.setFillAfter(true);// True:图片停在动画结束位置
        animation.setDuration(200);
        underline.startAnimation(animation);
    }

}