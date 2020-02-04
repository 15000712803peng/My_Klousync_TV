package com.kloudsync.techexcel2.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.kloudsync.techexcel2.bean.EventTopTipsGone;

import org.greenrobot.eventbus.EventBus;

public class TipTextView extends AppCompatTextView {
    private static final int START_TIME = 500;//动画显示时间
    private static final int END_TIME = 500;//动画移出时间
    private static final int SHOW_TIME = 1500;//动画显示时间
    private int titleHeight = 100;//标题栏默认的高度设置成100

    public TipTextView(Context context) {
        super(context);
    }

    public TipTextView(Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
    }

    public TipTextView(Context context, AttributeSet paramAttributeSet, int paramInt) {
        super(context, paramAttributeSet, paramInt);
    }

    public void showTips() {
        Log.e("TipTextView","showTips:" + getVisibility());
        if (getVisibility() == VISIBLE) {
            return;
        }
        Log.e("TipTextView","showTips");
        setVisibility(View.VISIBLE);
        shake();
        //向下移动动画
//        TranslateAnimation downTranslateAnimation = new TranslateAnimation(0, 0, 0, getMeasuredHeight());
//        downTranslateAnimation.setDuration(START_TIME);
//        downTranslateAnimation.setFillAfter(true);
//        startAnimation(downTranslateAnimation);
//        //动画监听
//        downTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                Log.e("TipTextView","downTranslateAnimation end");//向下移动动画结束
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
    }

    private void shake(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(END_TIME);
        alphaAnimation.setRepeatCount(4);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("TipTextView","shake_end");
                setVisibility(GONE);
                EventBus.getDefault().post(new EventTopTipsGone());
//                topTranslateAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void topTranslateAnimation() {
        TranslateAnimation topTranslateAnimation = new TranslateAnimation(0, 0, getMeasuredHeight(), 0);
        topTranslateAnimation.setDuration(100);
        topTranslateAnimation.setFillAfter(true);
        startAnimation(topTranslateAnimation);
        topTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("TipTextView","topTranslateAnimation end");//动画结束隐藏提示的TextView
                setVisibility(View.GONE);

            }
        });
    }

    /**
     * 设置标题栏高度
     *
     * @param titleHeight
     */
    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;
    }
}
