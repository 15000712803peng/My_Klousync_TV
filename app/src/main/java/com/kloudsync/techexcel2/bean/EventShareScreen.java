package com.kloudsync.techexcel2.bean;

import android.view.SurfaceView;

/**
 * Created by tonyan on 2019/12/22.
 */

public class EventShareScreen {
    private int uid;
    private SurfaceView shareView;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public SurfaceView getShareView() {
        return shareView;
    }

    public void setShareView(SurfaceView shareView) {
        this.shareView = shareView;
    }
}
