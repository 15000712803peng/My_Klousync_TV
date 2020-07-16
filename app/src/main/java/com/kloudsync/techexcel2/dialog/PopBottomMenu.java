package com.kloudsync.techexcel2.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.bean.MeetingConfig;
import com.kloudsync.techexcel2.bean.MeetingType;

import java.util.ArrayList;
import java.util.List;


public class PopBottomMenu implements PopupWindow.OnDismissListener, OnClickListener {

    int width;
    private Context mContext;
    private PopupWindow bottomMenuWindow;
    //--
    private ImageView menuIcon;

    private RelativeLayout menuClose;
    private RelativeLayout menuFile;
    private RelativeLayout menuNote;
    private RelativeLayout menuMember;
    private RelativeLayout menuChat;
    private RelativeLayout menuSetting;
    //----
    private MeetingConfig meetingConfig;

    private int currentMeetStatus=0;//0 file  1 用户笔记  2 参会者  3 chat 4 设置

    private int currentDocStatus=0;//0 file  1 用户笔记  2 关闭

    private List<View> meetViews=new ArrayList<>();
    private List<View> docViews=new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_menu_close:
                doMenuClose();
                break;
            case R.id.bottom_menu_file:
                doMenuFile();
                break;


            case R.id.bottom_menu_notes:
                doMenuNote();
                break;


            case R.id.bottom_menu_members:
                doMenuMember();
                break;

            case R.id.bottom_menu_chat:
                doMenuChat();
                break;
        }
    }

    public interface BottomMenuOperationsListener {
        void menuClosedClicked();
        void menuFileClicked();
        void menuNoteClicked();
        void menuMeetingMembersClicked();
        void menuChatClicked();

    }

    private BottomMenuOperationsListener bottomMenuOperationsListener;

    public PopBottomMenu(Context context, MeetingConfig meetingConfig) {
        this.mContext = context;
        this.meetingConfig = meetingConfig;
        getPopupWindow();
    }


    public void getPopupWindow() {
        if (null != bottomMenuWindow) {
            bottomMenuWindow.dismiss();
            return;
        } else {
            init();
        }
    }

    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_bottom_menu, null);
        menuClose = popupWindow.findViewById(R.id.bottom_menu_close);
        menuClose.setOnClickListener(this);
        menuFile = popupWindow.findViewById(R.id.bottom_menu_file);
        menuFile.setOnClickListener(this);

        menuNote = popupWindow.findViewById(R.id.bottom_menu_notes);
        menuNote.setOnClickListener(this);
        menuMember = popupWindow.findViewById(R.id.bottom_menu_members);
        menuMember.setOnClickListener(this);
        menuChat = popupWindow.findViewById(R.id.bottom_menu_chat);
        menuChat.setOnClickListener(this);


        menuSetting = popupWindow.findViewById(R.id.bottom_menu_setting);
        menuSetting.setOnClickListener(this);

        popupWindow.setFocusableInTouchMode(true);
        popupWindow.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    int keyCode = keyEvent.getKeyCode();
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_UP:
                            remoteUPOrDown(true);
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            remoteUPOrDown(false);
                            break;
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            remoteEnter();
                            break;
                    }
                }
                return false;
            }
        });
        width = (int) (mContext.getResources().getDisplayMetrics().widthPixels);
        bottomMenuWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);
        initMenu(meetingConfig.getType());
        bottomMenuWindow.getWidth();
        bottomMenuWindow.getHeight();
        bottomMenuWindow.setFocusable(true);
        bottomMenuWindow.setOnDismissListener(this);
        // 设置允许在外点击消失
        bottomMenuWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        bottomMenuWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    @SuppressLint("WrongConstant")
    private void initMenu(int meetingType) {
        switch (meetingType) {
            case MeetingType.DOC:
                menuFile.setVisibility(View.VISIBLE);
                menuNote.setVisibility(View.VISIBLE);
                menuClose.setVisibility(View.VISIBLE);
                //------
                menuMember.setVisibility(View.GONE);
                menuChat.setVisibility(View.GONE);
                menuSetting.setVisibility(View.GONE);

                docViews.add(menuFile);
                docViews.add(menuNote);
                docViews.add(menuClose);
                setCurrentDocAction(currentDocStatus);

                break;
            case MeetingType.MEETING:
                menuFile.setVisibility(View.VISIBLE);
                menuNote.setVisibility(View.VISIBLE);
                menuClose.setVisibility(View.GONE);
                //------
                menuMember.setVisibility(View.VISIBLE);
                menuChat.setVisibility(View.VISIBLE);
                menuSetting.setVisibility(View.VISIBLE);

                meetViews.add(menuFile);
                meetViews.add(menuNote);
                meetViews.add(menuMember);
                meetViews.add(menuChat);
                meetViews.add(menuSetting);
                setCurrentMeetAction(currentMeetStatus);
                break;
            case MeetingType.SYNCBOOK:
                break;
            case MeetingType.SYNCROOM:
                break;
        }
    }


    public void show(ImageView menu,PopBottomMenu.BottomMenuOperationsListener bottomMenuOperationsListener) {
        this.menuIcon = menu;
        this.bottomMenuOperationsListener = bottomMenuOperationsListener;
        bottomMenuWindow.showAtLocation(menu, Gravity.BOTTOM | Gravity.LEFT,
                width - mContext.getResources().getDimensionPixelSize(R.dimen.fab_margin),
                mContext.getResources().getDimensionPixelSize(R.dimen.menu_bottom_margin));
    }

    public boolean isShowing() {
        if (bottomMenuWindow != null) {
            return bottomMenuWindow.isShowing();
        }
        return false;
    }

    public void hide() {
        if (bottomMenuWindow != null) {
            bottomMenuWindow.dismiss();
        }
        bottomMenuWindow = null;
    }

    @Override
    public void onDismiss() {
        Log.e("PopBottomMenu", "on_dismiss");
        if (menuIcon != null) {
            Log.e("PopBottomMenu", "on_dismiss_menu_icon");
            menuIcon.setImageResource(R.drawable.icon_menu);
        }
        bottomMenuWindow = null;
    }

    /**接收遥控器的上下按键*/
    public void remoteUPOrDown(boolean mIsUp){
        Toast.makeText(mContext,"pop->"+meetingConfig.getType(),Toast.LENGTH_LONG).show();
        if(meetingConfig.getType()==MeetingType.MEETING){
            if(mIsUp){//向上按键遥控器
                if(currentMeetStatus>0){
                    currentMeetStatus-=1;
                    setCurrentMeetAction(currentMeetStatus);
                }
            }else {//向下按键遥控器
                if(currentMeetStatus<4){
                    currentMeetStatus+=1;
                    setCurrentMeetAction(currentMeetStatus);
                }
            }
        }else {//文档
            if(mIsUp){//向上按键遥控器
                if(currentDocStatus>0){
                    currentDocStatus-=1;
                    setCurrentDocAction(currentDocStatus);
                }
            }else {//向下按键遥控器
                if(currentDocStatus<2){
                    currentDocStatus+=1;
                    setCurrentDocAction(currentDocStatus);
                }
            }
        }
    }

    /**接收遥控器的enter按键,执行功能*/
    public void remoteEnter(){
        if(meetingConfig.getType()==MeetingType.MEETING){//会议
            doCurrentMeetAction();
        }else {//文档
            doCurrentDocAction();
        }
    }

    /**遥控器当前选中的会议选项*/
    public void setCurrentMeetAction(int status){
        currentMeetStatus=status;
        for(int i=0;i<meetViews.size();i++){
            meetViews.get(i).setBackground(null);
        }
        meetViews.get(status).setBackgroundResource(R.drawable.bg_remote_doc_select);
    }

    /**遥控器当前选中的文档选项*/
    public void setCurrentDocAction(int status){
        currentDocStatus=status;
        for(int i=0;i<docViews.size();i++){
            docViews.get(i).setBackground(null);
        }
        docViews.get(status).setBackgroundResource(R.drawable.bg_remote_doc_select);
    }

    /**执行遥控器当前选中的会议选项操作*/
    public void doCurrentMeetAction(){
        switch (currentMeetStatus){
            case 0://File
                doMenuFile();
                break;
            case 1://用户笔记
                doMenuNote();
                break;
            case 2://参会者
                doMenuMember();
                break;
            case 3://chat
                doMenuChat();
                break;
            case 4://设置
                break;
        }
    }

    /**执行遥控器当前选中的会议选项操作*/
    public void doCurrentDocAction(){
        switch (currentDocStatus){
            case 0://File
                doMenuFile();
                break;
            case 1://用户笔记
                doMenuNote();
                break;
            case 2://关闭
                doMenuClose();
                break;
        }
    }

    private void doMenuFile(){
        hide();
        if (bottomMenuOperationsListener != null) {
            bottomMenuOperationsListener.menuFileClicked();
        }
    }

    private void doMenuNote(){
        hide();
        if(bottomMenuOperationsListener != null){
            bottomMenuOperationsListener.menuNoteClicked();
        }
    }

    private void doMenuMember(){
        hide();
        if(bottomMenuOperationsListener != null){
            bottomMenuOperationsListener.menuMeetingMembersClicked();
        }
    }

    private void doMenuChat(){
        hide();
        if(bottomMenuOperationsListener != null){
            bottomMenuOperationsListener.menuChatClicked();
        }
    }

    private void doMenuClose(){
        hide();
        if(bottomMenuOperationsListener != null){
            bottomMenuOperationsListener.menuClosedClicked();
        }
    }

}
