package com.kloudsync.techexcel2.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel2.R;

public class PopAlbums {

    public Context mContext;

    private static PopAlbumsDismissListener popAlbumsDismissListener;

    private boolean flag;

    public interface PopAlbumsDismissListener {
        void PopDismiss(boolean isAdd);
    }

    public void setPoPDismissListener(PopAlbumsDismissListener popAlbumsDismissListener) {
        this.popAlbumsDismissListener = popAlbumsDismissListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;

        getPopupWindowInstance();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation4);
    }


    public PopupWindow mPopupWindow;

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private TextView tv_album, tv_cancel;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_album, null);

        tv_album = (TextView) popupWindow.findViewById(R.id.tv_album);
        tv_cancel = (TextView) popupWindow.findViewById(R.id.tv_cancel);


        mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popAlbumsDismissListener != null) {
                    popAlbumsDismissListener.PopDismiss(flag);
                }
            }
        });

        tv_album.setOnClickListener(new myOnClick());
        tv_cancel.setOnClickListener(new myOnClick());


        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_album:
                    DissmissPop(true);
                    break;
                case R.id.tv_cancel:
                    DissmissPop(false);
                    break;

                default:
                    break;
            }

        }


    }

    private void DissmissPop(boolean isupdate) {
        flag = isupdate;
        mPopupWindow.dismiss();
    }

    public void StartPop(View v) {
        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }


}
