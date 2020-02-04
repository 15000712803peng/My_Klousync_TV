package com.ub.service.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.view.KloudLoadingView;
import com.kloudsync.techexcel2.view.TipTextView;

import org.greenrobot.eventbus.EventBus;
import org.xwalk.core.XWalkView;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseDocAndMeetingActivity extends FragmentActivity {

    @Bind(R.id.layout_enter_loading)
    protected LinearLayout enterLoading;

    @Bind(R.id.enter_loading)
    protected KloudLoadingView loadingView;

    @Bind(R.id.web)
    protected XWalkView web;

    @Bind(R.id.web_note)
    protected XWalkView noteWeb;

    @Bind(R.id.menu)
    protected ImageView menuIcon;

    @Bind(R.id.layout_note_users)
    LinearLayout noteUsersLayout;

    @Bind(R.id.txt_top_tip)
    TipTextView topTipText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("BaseDocAndMeetingActivity","on_create");
        setContentView(R.layout.activity_tv_key);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        showEnterLoading();
        initData();
    }

    public void showEnterLoading(){
        enterLoading.setVisibility(View.VISIBLE);
        loadingView.smoothToShow();
    }

    public  void hideEnterLoading(){
        loadingView.hide();
        enterLoading.setVisibility(View.GONE);
    }

    public abstract void showErrorPage();

    public abstract void initData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
