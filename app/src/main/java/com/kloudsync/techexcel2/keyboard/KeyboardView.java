package com.kloudsync.techexcel2.keyboard;

import android.graphics.drawable.Drawable;
import android.view.View;

public class KeyboardView {
    private int viewId;
    private View leftView;
    private View rightView;
    private View topView;
    private View bottomView;
    private boolean isSelected;
    private Drawable selectedBackgroud;
    private Drawable unSelectedBackgroud;

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public View getLeftView() {
        return leftView;
    }

    public void setLeftView(View leftView) {
        this.leftView = leftView;
    }

    public View getRightView() {
        return rightView;
    }

    public void setRightView(View rightView) {
        this.rightView = rightView;
    }

    public View getTopView() {
        return topView;
    }

    public void setTopView(View topView) {
        this.topView = topView;
    }

    public View getBottomView() {
        return bottomView;
    }

    public void setBottomView(View bottomView) {
        this.bottomView = bottomView;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Drawable getSelectedBackgroud() {
        return selectedBackgroud;
    }

    public void setSelectedBackgroud(Drawable selectedBackgroud) {
        this.selectedBackgroud = selectedBackgroud;
    }

    public Drawable getUnSelectedBackgroud() {
        return unSelectedBackgroud;
    }

    public void setUnSelectedBackgroud(Drawable unSelectedBackgroud) {
        this.unSelectedBackgroud = unSelectedBackgroud;
    }
}
