package com.kloudsync.techexcel2.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.bean.EventClose;
import com.kloudsync.techexcel2.bean.MeetingConfig;
import com.kloudsync.techexcel2.tool.MeetingSettingCache;

import org.greenrobot.eventbus.EventBus;


public class MeetingSettingDialog implements View.OnClickListener{

    private Activity host;
    private Dialog settingDialog;
    private View view;
    private TextView startText;
    private ImageView microImage,cameraImage;
    private TextView microText,cameraText;
    private boolean isStartMeeting;
    private TextView closeText;
    private MeetingConfig meetingConfig;

    private LinearLayout tabTitlesLayout;
    private LinearLayout recordingLayout;

    private int currentSelectStatus=0;// 0 麦克风  1摄像头  2确定

    public boolean isStartMeeting() {
        return isStartMeeting;
    }

    public void setStartMeeting(boolean startMeeting) {
        isStartMeeting = startMeeting;
    }

    public interface OnUserOptionsListener{
        void onUserStart();
        void onUserJoin();
    }

    private OnUserOptionsListener onUserOptionsListener;

    public void setOnUserOptionsListener(OnUserOptionsListener onUserOptionsListener) {
        this.onUserOptionsListener = onUserOptionsListener;
    }

    public MeetingSettingDialog(Activity host) {
        this.host = host;
        getPopupWindowInstance(host);
    }


    public void getPopupWindowInstance(Activity host) {
        if (null != settingDialog) {
            settingDialog.dismiss();
            return;
        } else {
            initPopuptWindow(host);
        }
    }

    public void initPopuptWindow(Activity host) {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.dialog_meeting_setting, null);
        microImage = view.findViewById(R.id.image_micro);
        microImage.setOnClickListener(this);
        closeText = view.findViewById(R.id.txt_cancel);
        closeText.setOnClickListener(this);
        tabTitlesLayout = view.findViewById(R.id.layout_tab_titles);
        recordingLayout = view.findViewById(R.id.layout_recording);
        microText = view.findViewById(R.id.txt_micro);
        cameraImage = view.findViewById(R.id.image_camera);
        cameraImage.setOnClickListener(this);
        cameraText = view.findViewById(R.id.txt_camera);
        startText = view.findViewById(R.id.txt_start);
        startText.setOnClickListener(this);
        settingDialog = new Dialog(host, R.style.my_dialog);
        settingDialog.setContentView(view);
        settingDialog.setCancelable(false);

        Window window = settingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = host.getResources().getDimensionPixelSize(R.dimen.meeting_setting_dialog_width);
        settingDialog.getWindow().setAttributes(lp);


    }

    @SuppressLint("WrongConstant")
    private void init(MeetingSettingCache settingCache){

        if(settingCache.getMeetingSetting().isMicroOn()){
            microImage.setImageResource(R.drawable.sound_on1);
            microText.setText(R.string.satOn);
        }else {
            microImage.setImageResource(R.drawable.sound_off1);
            microText.setText(R.string.satOff);
        }

        if(settingCache.getMeetingSetting().isCameraOn()){
            cameraImage.setImageResource(R.drawable.cam_on2);
            cameraText.setText(R.string.satOn);
        }else {
            cameraImage.setImageResource(R.drawable.cam_off2);
            cameraText.setText(R.string.satOff);
        }

        if(meetingConfig.getRole() != MeetingConfig.MeetingRole.HOST){
            recordingLayout.setVisibility(View.GONE);
            tabTitlesLayout.setVisibility(View.GONE);
            closeText.setVisibility(View.GONE);
        }else {
            recordingLayout.setVisibility(View.VISIBLE);
            tabTitlesLayout.setVisibility(View.VISIBLE);
            closeText.setVisibility(View.VISIBLE);
        }

    }

    @SuppressLint("NewApi")
    public void show(Activity host,MeetingConfig meetingConfig) {
        this.host = host;
        this.meetingConfig = meetingConfig;
        if (settingDialog != null && !settingDialog.isShowing()) {
            settingDialog.show();
        }
        settingCache = getSettingCache(host);
        init(settingCache);
    }

    public boolean isShowing() {
        if(settingDialog != null){
            return settingDialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (settingDialog != null) {
            settingDialog.dismiss();
            settingDialog = null;
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_start://确定
                doConfirm();
                break;
            case R.id.image_micro://音频
                doMicrophone();
                break;
            case R.id.image_camera://视频
                doCamera();
                break;
            case R.id.txt_cancel:
                if(meetingConfig.isFromMeeting() && meetingConfig.getRole() == MeetingConfig.MeetingRole.HOST){
                    EventBus.getDefault().post(new EventClose());
                }
                dismiss();
                break;
        }
    }

    private MeetingSettingCache settingCache;

    private MeetingSettingCache getSettingCache(Activity host) {
        if (settingCache == null) {
            settingCache = MeetingSettingCache.getInstance(host);
        }
        return settingCache;
    }

    /**接收遥控器的上下左右按键*/
    public void remoteWayDown(int way){// 0 向上  1 向下  2 向左  3向右
        switch (way){
            case 0:
                if(currentSelectStatus==2){
                    currentSelectStatus=0;
                    setCurrentSelectAction(currentSelectStatus);
                }
                break;
            case 1:
                if(currentSelectStatus==0||currentSelectStatus==1){
                    currentSelectStatus=2;
                    setCurrentSelectAction(currentSelectStatus);
                }
                break;
            case 2:
                if(currentSelectStatus==1){
                    currentSelectStatus=0;
                    setCurrentSelectAction(currentSelectStatus);
                }
                break;
            case 3:
                if(currentSelectStatus==0){
                    currentSelectStatus=1;
                    setCurrentSelectAction(currentSelectStatus);
                }
                break;
        }
    }

    /**接收遥控器的enter按键,执行功能*/
    public void remoteEnter(){
        doCurrentSelectAction();
    }


    /**遥控器当前选中的按钮*/
    public void setCurrentSelectAction(int status){
        currentSelectStatus=status;
        switch (status){
            case 0://麦克风
                microImage.setBackgroundResource(R.drawable.bg_remote_meeting_select);
                cameraImage.setBackground(null);
                startText.setBackgroundResource(R.drawable.bg_remote_meeting_confirm_nor);
                break;
            case 1://摄像头
                microImage.setBackground(null);
                cameraImage.setBackgroundResource(R.drawable.bg_remote_meeting_select);
                startText.setBackgroundResource(R.drawable.bg_remote_meeting_confirm_nor);
                break;
            case 2://确定
                microImage.setBackground(null);
                cameraImage.setBackground(null);
                startText.setBackgroundResource(R.drawable.bg_remote_meeting_confirm_activite);
                break;
        }
    }

    /**执行当前遥控器选中对应按钮的事件*/
    public void doCurrentSelectAction(){
        switch (currentSelectStatus){
            case 0://麦克风
                doMicrophone();
                break;
            case 1://摄像头
                doCamera();
                break;
            case 2://确定
                doConfirm();
                break;
        }
    }

    private void doMicrophone(){
        boolean isMicroOn = getSettingCache(host).getMeetingSetting().isMicroOn();
        if(!isMicroOn){
            microImage.setImageResource(R.drawable.sound_on1);
            microText.setText(R.string.satOn);
        }else {
            microImage.setImageResource(R.drawable.sound_off1);
            microText.setText(R.string.satOff);
        }
        getSettingCache(host).setMicroOn(!isMicroOn);
    }

    private void doCamera(){
        boolean isCameraOn = getSettingCache(host).getMeetingSetting().isCameraOn();
        if(!isCameraOn){
            cameraImage.setImageResource(R.drawable.cam_on2);
            cameraText.setText(R.string.satOn);
        }else {
            cameraImage.setImageResource(R.drawable.cam_off2);
            cameraText.setText(R.string.satOff);
        }
        getSettingCache(host).setCameraOn(!isCameraOn);
    }

    private void doConfirm(){
        if(isStartMeeting){
            if(onUserOptionsListener != null){
                onUserOptionsListener.onUserStart();
            }
        }else {
            if(onUserOptionsListener != null){
                onUserOptionsListener.onUserJoin();
            }
        }
        if(onDialogDismissListener!=null){
            onDialogDismissListener.onDialogDismiss();
        }
        dismiss();
    }

    private OnDialogDismissListener onDialogDismissListener;

    public void setOnDialogDismissListener(OnDialogDismissListener onDialogDismissListener){
        this.onDialogDismissListener=onDialogDismissListener;
    }

    public interface OnDialogDismissListener{
        void onDialogDismiss();
    }

}
