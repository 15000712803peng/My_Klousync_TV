package com.kloudsync.techexcel2.frgment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ub.friends.activity.AddFriendsActivity;
import com.ub.friends.activity.NewFriendsActivity;
import com.ub.service.activity.NotifyActivity;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.adapter.CustomerAdapter;
import com.kloudsync.techexcel2.adapter.HealthAdapter;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.contact.AddUser;
import com.kloudsync.techexcel2.contact.ContactMap;
import com.kloudsync.techexcel2.contact.MemberDetail;
import com.kloudsync.techexcel2.contact.UserDetail;
import com.kloudsync.techexcel2.help.ContactHelpInterface;
import com.kloudsync.techexcel2.help.SideBar;
import com.kloudsync.techexcel2.help.SideBar.OnTouchingLetterChangedListener;
import com.kloudsync.techexcel2.help.SideBarSortHelp;
import com.kloudsync.techexcel2.info.Customer;
import com.kloudsync.techexcel2.start.LoginGet;
import com.kloudsync.techexcel2.view.ClearEditText;

import java.util.ArrayList;

public class ContactFragment extends Fragment implements ContactHelpInterface {

    private View view;

    //    private TextView tv_customer, tv_healthcl, tv_map_mode;
    private TextView tv_cancel, tv_new;
    private TextView tv_ns;
    private TextView tv_red;
    private ImageView img_addCustomer;
    private ImageView img_add;
    private ImageView img_notice;
    private ListView lv_contact;
    private LinearLayout lin_search, lin_edit, lin_new;
    private LinearLayout lin_none;
    private ClearEditText et_search;
    private SideBar sidebar;

    private CustomerAdapter cAdapter;
    private HealthAdapter hAdapter;

    private ArrayList<Customer> cuslist = new ArrayList<Customer>();
    private ArrayList<Customer> healthlist = new ArrayList<Customer>();
    ArrayList<Customer> eList = new ArrayList<Customer>();
    ArrayList<Customer> mList = new ArrayList<Customer>();

    private boolean isFirst = true;
    private boolean isCustomer = true;
    private int mySelect = 0;

    BroadcastReceiver broadcastReceiver;

    private InputMethodManager inputManager;

    public PopupWindow mPopupWindow;

    private boolean isFragmentVisible = false;

    private double lats[] = {31.199105, 31.199344, 31.198661, 31.198437,
            31.199425, 31.198939, 31.199089, 31.198757};
    private double longs[] = {121.438247, 121.437556, 121.437556, 121.438378,
            121.438337, 121.43902, 121.438454, 121.438189};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.contact_fragment, container, false);
            initView();
        }

        return view;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    private void initView() {
        img_addCustomer = (ImageView) view.findViewById(R.id.img_addCustomer);
        img_add = (ImageView) view.findViewById(R.id.img_add);
        img_notice = (ImageView) view.findViewById(R.id.img_notice);
//        img_newuser = (ImageView) view.findViewById(R.id.img_newuser);
//        tv_map_mode = (TextView) view.findViewById(tv_map_mode);
//        tv_customer = (TextView) view.findViewById(tv_customer);
//        tv_healthcl = (TextView) view.findViewById(tv_healthcl);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_new = (TextView) view.findViewById(R.id.tv_new);
        tv_ns = (TextView) view.findViewById(R.id.tv_ns);
        tv_red = (TextView) view.findViewById(R.id.tv_red);
//        tv_add = (TextView) view.findViewById(tv_add);
        lin_search = (LinearLayout) view.findViewById(R.id.lin_search);
        lin_edit = (LinearLayout) view.findViewById(R.id.lin_edit);
        lin_new = (LinearLayout) view.findViewById(R.id.lin_new);
        lin_none = (LinearLayout) view.findViewById(R.id.lin_none);
        et_search = (ClearEditText) view.findViewById(R.id.et_search);
        lv_contact = (ListView) view.findViewById(R.id.lv_contact);
        sidebar = (SideBar) view.findViewById(R.id.sidebar);
        getPopupWindowInstance();
    }

    LocalBroadcastManager localBroadcastManager;
    private void GetCourseBroad() {
        RefreshNotify();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RefreshNotify();
            }
        };
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.Receive_Course));
//        getActivity().registerReceiver(broadcastReceiver, filter);
        //LocalBroadcastManager 是基于Handler实现的，拥有更高的效率与安全性。安全性主要体现在数据仅限于应用内部传输，避免广播被拦截、伪造、篡改的风险
        localBroadcastManager.registerReceiver(broadcastReceiver, filter);

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

    private void getData() {
//		tv_customer.setEnabled(false);
//		tv_healthcl.setEnabled(false);
//		tv_map_mode.setEnabled(false);
        final LoginGet loginget = new LoginGet();
        loginget.setLoginGetListener(new LoginGet.LoginGetListener() {

            @Override
            public void getMember(ArrayList<Customer> list) {
                // TODO Auto-generated method stub
                /*healthlist = new ArrayList<Customer>();
                healthlist.addAll(list);
                hAdapter = new HealthAdapter(getActivity(), healthlist);*/
//				tv_customer.setEnabled(true);
//				tv_healthcl.setEnabled(true);
            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                cuslist = new ArrayList<Customer>();
                cuslist.addAll(list);
                cAdapter = new CustomerAdapter(getActivity(), cuslist);
                lv_contact.setAdapter(cAdapter);
                lv_contact.setOnItemClickListener(new MyOnitem());
                VisibleGoneList(cuslist);
//                loginget.MemberRequest(getActivity(), 0);
//                tv_map_mode.setEnabled(true);
//                ChangeList(0);
            }
        });
        loginget.CustomerRequest(getActivity());

    }

    private void getSide() {
        sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position;
                if (isCustomer) {
                    position = SideBarSortHelp.getPositionForSection(cuslist,
                            s.charAt(0));
                } else {
                    position = SideBarSortHelp.getPositionForSection(healthlist,
                            s.charAt(0));
                }
                if (position != -1) {
                    lv_contact.setSelection(position);
                } else {
                    lv_contact
                            .setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                }
            }
        });

    }

    private void editCustomers() {
        inputManager = (InputMethodManager) et_search
                .getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                eList = new ArrayList<Customer>();
                if (isCustomer) {
                    mList = cuslist;
                } else {
                    mList = healthlist;
                }
                for (int i = 0; i < mList.size(); i++) {
                    Customer cus = mList.get(i);
                    String name = et_search.getText().toString();
                    String getName = cus.getName().toLowerCase();//转小写
                    String nameb = name.toLowerCase();//转小写
                    if (getName.contains(nameb.toString())
                            && name.length() > 0) {
                        Customer customer;
                        customer = cus;
                        eList.add(customer);
                    }
                }
                if (et_search.length() != 0) {
                    if (isCustomer) {
                        cAdapter = new CustomerAdapter(getActivity(), eList);
                    } else {
                        hAdapter = new HealthAdapter(getActivity(), eList);
                    }
                } else {
                    if (isCustomer) {
                        cAdapter = new CustomerAdapter(getActivity(), cuslist);
                    } else {
                        hAdapter = new HealthAdapter(getActivity(), healthlist);
                    }
                }

                if (isCustomer) {
                    lv_contact.setAdapter(cAdapter);
                } else {
                    lv_contact.setAdapter(hAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void RefreshRed(boolean flag_r) {
        Log.e("duang",flag_r + "");
        tv_red.setVisibility(flag_r ? View.VISIBLE : View.GONE);
    }

    private class MyOnitem implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Customer cus = isCustomer ? cuslist.get(position) : healthlist
                    .get(position);
            if (et_search.length() != 0) {
                cus = eList.get(position);
            }
            Intent intent = new Intent(getActivity(), isCustomer ? UserDetail.class : MemberDetail.class);
//			intent.putExtra("Customer", cus);
            intent.putExtra("UserID", cus.getUserID());
            startActivity(intent);

        }

    }

    protected class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.img_addCustomer:
                    mPopupWindow.showAsDropDown(v);
                    break;
                /*case tv_map_mode:
                    ChangeToMap();
                    break;
                case tv_customer:
                    ChangeList(0);
                    break;
                case tv_healthcl:
                    ChangeList(1);
                    break;*/
                case R.id.tv_cancel:
                    CancelSearch();
                    break;
                case R.id.lin_new:
                    AddNewUM();
                    break;
                case R.id.img_add:
                    CreateNewUM();
                    break;
                case R.id.lin_search:
                    GetSearch();
                    break;
                case R.id.img_notice:
                    GoToNotice();
                    break;

                default:
                    break;
            }
        }

        private void GoToNotice() {
            startActivity(new Intent(getActivity(), NotifyActivity.class));
        }


        private void AddNewUM() {
            Intent i = new Intent(getActivity(), NewFriendsActivity.class);
            i.putExtra("currentposition", isCustomer ? 0 : 1);
            startActivity(i);
        }

        public void CreateNewUM() {
            Intent i = new Intent(getActivity(), AddFriendsActivity.class);
            i.putExtra("type", isCustomer ? 1 : 2);
            startActivity(i);
        }
    }

    private void CancelSearch() {
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        et_search.setText("");
        lin_search.setVisibility(View.VISIBLE);
        lin_edit.setVisibility(View.GONE);
    }


    public void ChangeToMap() {
        Intent intent = new Intent(getActivity(), ContactMap.class);
        if (isCustomer) {
            intent.putExtra("mList", cuslist);
        } else {
            intent.putExtra("mList", healthlist);
        }
        intent.putExtra("isCustomer", isCustomer);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.tran_in, R.anim.tran_out);


    }

    private void GetSearch() {
        et_search.setFocusable(true);
        et_search.setFocusableInTouchMode(true);
        et_search.requestFocus();
        inputManager.showSoftInput(et_search, 0);
        lin_search.setVisibility(View.GONE);
        lin_edit.setVisibility(View.VISIBLE);
    }

    /*@SuppressLint("NewApi")
    public void ChangeList(int i) {
        switch (i) {
            case 0:
                img_newuser.setImageDrawable(getResources().getDrawable(R.drawable.new_user));
                tv_new.setText("我推荐的学生列表");
                if (1 == AppConfig.UserType) {
                    tv_new.setText("我的学生列表");
                }
                tv_add.setText("添加学生");
                VisibleGoneList(cuslist);
                isCustomer = true;
                lv_contact.setAdapter(cAdapter);
                tv_customer.setBackground(getResources().getDrawable(R.drawable.contact_shape_son_white_left));
                tv_customer.setTextColor(getResources().getColor(R.color.green));
                tv_healthcl.setBackground(getResources().getDrawable(R.drawable.contact_shape_son_green_right));
                tv_healthcl.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                img_newuser.setImageDrawable(getResources().getDrawable(R.drawable.add_user_haha));
                tv_new.setText("新的老师列表");
                tv_add.setText("添加老师");
                VisibleGoneList(healthlist);
                isCustomer = false;
                lv_contact.setAdapter(hAdapter);
                tv_customer.setBackground(getResources().getDrawable(R.drawable.contact_shape_son_green_left));
                tv_customer.setTextColor(getResources().getColor(R.color.white));
                tv_healthcl.setBackground(getResources().getDrawable(R.drawable.contact_shape_son_white_right));
                tv_healthcl.setTextColor(getResources().getColor(R.color.green));
                break;

            default:
                break;
        }

        if (mySelect != i) {
            LayoutAnimationController lac = new LayoutAnimationController(
                    AnimationUtils.loadAnimation(getActivity(),
                            0 == i ? R.anim.contact_left : R.anim.contact_right));
            lac.setInterpolator(new AccelerateInterpolator());
            lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
            lv_contact.setLayoutAnimation(lac);
            lv_contact.startLayoutAnimation();
        }
        mySelect = i;

    }*/

    private void VisibleGoneList(ArrayList<Customer> list) {
        // TODO Auto-generated method stub
        if (0 == list.size()) {
            lin_none.setVisibility(View.VISIBLE);
            lv_contact.setVisibility(View.GONE);
            sidebar.setVisibility(View.GONE);
        } else {
            lin_none.setVisibility(View.GONE);
            lv_contact.setVisibility(View.VISIBLE);
            sidebar.setVisibility(View.VISIBLE);
        }

    }

    /*
     * 获取PopupWindow实例
     */
    private void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    /*
     * 创建PopupWindow
     */
    @SuppressWarnings("deprecation")
    private void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        // View popupWindow = layoutInflater.inflate(R.layout.popup_window3,
        // null);
        View popupWindow = layoutInflater
                .inflate(R.layout.popup_contact, null);
        // View popupWindow = layoutInflater.inflate(R.layout.popup_window2,
        // null);
        LinearLayout lin_tonewuser = (LinearLayout) popupWindow
                .findViewById(R.id.lin_tonewuser);
        LinearLayout lin_tonewmember = (LinearLayout) popupWindow
                .findViewById(R.id.lin_tonewmember);

        lin_tonewuser.setOnClickListener(new MypopClick());
        lin_tonewmember.setOnClickListener(new MypopClick());

        WindowManager window = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        // 创建一个PopupWindow
        // 参数1：contentView 指定PopupWindow的内容
        // 参数2：width 指定PopupWindow的width
        // 参数3：height 指定PopupWindow的height
        mPopupWindow = new PopupWindow(popupWindow, width / 2, height / 5,
                false);

        // getWindowManager().getDefaultDisplay().getWidth();
        // getWindowManager().getDefaultDisplay().getHeight();
        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private class MypopClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lin_tonewuser:
                    Intent i = new Intent(getActivity(), AddUser.class);
                    startActivity(i);
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_tonewmember:
                    i = new Intent(getActivity(), AddFriendsActivity.class);
                    startActivity(i);
                    mPopupWindow.dismiss();
                    break;

                default:

            }

        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        isFragmentVisible = isVisibleToUser;
        if (isFirst && isVisibleToUser) {
            isFirst = false;
            initFunction();
        }
        RefreshInfo();
    }

    private void RefreshInfo() {

        if (AppConfig.isUpdateCustomer) {
            getData();
        }
        AppConfig.isUpdateCustomer = false;
    }

    private void initFunction() {
        getData();
        editCustomers();
        getSide();
        GetCourseBroad();
        img_addCustomer.setOnClickListener(new myOnClick());
//        tv_map_mode.setOnClickListener(new myOnClick());
//        tv_customer.setOnClickListener(new myOnClick());
//        tv_healthcl.setOnClickListener(new myOnClick());
        tv_cancel.setOnClickListener(new myOnClick());
        lin_new.setOnClickListener(new myOnClick());
        img_add.setOnClickListener(new myOnClick());
        lin_search.setOnClickListener(new myOnClick());
        img_notice.setOnClickListener(new myOnClick());
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (isFragmentVisible) {
            RefreshInfo();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null && getActivity() != null) {
//            getActivity().unregisterReceiver(broadcastReceiver);
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

}
