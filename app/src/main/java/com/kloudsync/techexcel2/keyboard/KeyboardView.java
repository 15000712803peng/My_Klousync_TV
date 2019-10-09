package com.kloudsync.techexcel2.keyboard;

import android.graphics.drawable.Drawable;
import android.view.View;

public class KeyboardView {
    private View targeview;
    private KeyboardView leftView;
    private KeyboardView rightView;
    private KeyboardView topView;
    private KeyboardView bottomView;
    private boolean isSelected;
    private Drawable selectedBackgroud;
    private Drawable unSelectedBackgroud;

    public View getTargeview() {
        return targeview;
    }

    public void setTargeview(View targeview) {
        this.targeview = targeview;
    }

    public KeyboardView getLeftView() {
        return leftView;
    }

    public void setLeftView(KeyboardView leftView) {
        this.leftView = leftView;
    }

    public KeyboardView getRightView() {
        return rightView;
    }

    public void setRightView(KeyboardView rightView) {
        this.rightView = rightView;
    }

    public KeyboardView getTopView() {
        return topView;
    }

    public void setTopView(KeyboardView topView) {
        this.topView = topView;
    }

    public KeyboardView getBottomView() {
        return bottomView;
    }

    public void setBottomView(KeyboardView bottomView) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyboardView that = (KeyboardView) o;

        return targeview != null ? targeview.equals(that.targeview) : that.targeview == null;
    }

    @Override
    public int hashCode() {
        return targeview != null ? targeview.hashCode() : 0;
    }
}
