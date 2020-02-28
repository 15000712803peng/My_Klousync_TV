package com.kloudsync.techexcel2.help;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel2.adapter.AgoraCameraAdapter;
import com.kloudsync.techexcel2.adapter.FullAgoraCameraAdapter;
import com.kloudsync.techexcel2.app.App;
import com.kloudsync.techexcel2.bean.AgoraMember;
import com.kloudsync.techexcel2.bean.EventCloseShare;
import com.kloudsync.techexcel2.bean.EventExit;
import com.kloudsync.techexcel2.bean.EventMute;
import com.kloudsync.techexcel2.bean.EventRefreshMembers;
import com.kloudsync.techexcel2.bean.EventShareScreen;
import com.kloudsync.techexcel2.bean.MeetingConfig;
import com.kloudsync.techexcel2.bean.MeetingMember;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.dialog.MeetingSettingDialog;
import com.kloudsync.techexcel2.dialog.PopMeetingMenu;
import com.kloudsync.techexcel2.service.ConnectService;
import com.kloudsync.techexcel2.tool.MeetingSettingCache;
import com.kloudsync.techexcel2.tool.SocketMessageManager;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import java.util.List;
import io.agora.openlive.model.AGEventHandler;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.internal.RtcEngineImpl;
import io.agora.rtc.video.VideoCanvas;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER;


public class MeetingKit implements MeetingSettingDialog.OnUserOptionsListener, AGEventHandler, View.OnClickListener, PopMeetingMenu.MeetingMenuOperationsListener {

    private static MeetingKit kit;

    private MeetingConfig meetingConfig;

    private MeetingSettingDialog settingDialog;

    private Activity host;

    private String newMeetingId;

    //----
    private RtcManager rtcManager;
    private ImageView menu;
    private PopMeetingMenu popMeetingMenu;
    private boolean isStarted;
    private AgoraCameraAdapter cameraAdapter;
    private FullAgoraCameraAdapter fullCameraAdapter;

    public void setCameraAdapter(AgoraCameraAdapter cameraAdapter) {
        this.cameraAdapter = cameraAdapter;
    }

    public void setFullCameraAdaptero(FullAgoraCameraAdapter fullCameraAdapter) {
        this.fullCameraAdapter = fullCameraAdapter;
    }

    public void setMenu(ImageView menu) {
        this.menu = menu;
    }

    private MeetingKit() {

    }

    public static MeetingKit getInstance() {
        if (kit == null) {
            synchronized (MeetingKit.class) {
                if (kit == null) {
                    kit = new MeetingKit();
                }
            }
        }
        return kit;
    }



    private RtcManager getRtcManager() {
        if (rtcManager == null) {
            return RtcManager.getDefault(host);
        }
        return rtcManager;
    }

    public void prepareStart(Activity host, MeetingConfig meetingConfig, String newMeetingId) {
        Log.e("prepareStart","role:" + meetingConfig.getRole());
        this.host = host;
        this.newMeetingId = newMeetingId;
        this.meetingConfig = meetingConfig;
        ((App) host.getApplication()).initWorkerThread();
        getRtcManager().setHost(host);
        getRtcManager().addEventHandler(this);
        if (settingDialog != null) {
            if (settingDialog.isShowing()) {
                settingDialog.dismiss();
            }
            settingDialog = null;
        }
        settingDialog = new MeetingSettingDialog(host);
        settingDialog.setOnUserOptionsListener(this);
        settingDialog.setStartMeeting(true);
        if (settingDialog.isShowing()) {
            return;
        }
        settingDialog.show(host,meetingConfig);
    }

    public void remoteDirectionDown(int direction){
        if(settingDialog!=null&&settingDialog.isShowing()){
            settingDialog.remoteWayDown(direction);
        }
    }

    public void remoteEnterDown(){
        if(settingDialog!=null&&settingDialog.isShowing()){
            settingDialog.remoteEnter();
        }
    }

    public void prepareJoin(Activity host, MeetingConfig meetingConfig) {
        this.host = host;
        this.meetingConfig = meetingConfig;
        ((App) host.getApplication()).initWorkerThread();
        getRtcManager().setHost(host);
        getRtcManager().addEventHandler(this);
        if (settingDialog != null) {
            if (settingDialog.isShowing()) {
                settingDialog.dismiss();
            }
            settingDialog = null;
        }
        settingDialog = new MeetingSettingDialog(host);
        settingDialog.setOnUserOptionsListener(this);
        settingDialog.setStartMeeting(false);
        if (settingDialog.isShowing()) {
            return;
        }
        settingDialog.show(host,meetingConfig);
        settingDialog.setOnDialogDismissListener(new MeetingSettingDialog.OnDialogDismissListener() {
            @Override
            public void onDialogDismiss() {
                if(onDialogDismissListener!=null){
                    onDialogDismissListener.onDialogDismiss();
                }
            }
        });
    }

    public void startMeeting() {
        Log.e("MeetingKit", "start_meeting");
//        meetingConfig.setRole(MeetingConfig.MeetingRole.HOST);
        if(!TextUtils.isEmpty(newMeetingId)){
            meetingConfig.setMeetingId(newMeetingId);
        }
        rtcManager = RtcManager.getDefault(host);
        rtcManager.doConfigEngine(CLIENT_ROLE_BROADCASTER);
        Log.e("MeetingKit", "joinChannel:" + meetingConfig.getMeetingId());
        getRtcManager().rtcEngine().enableWebSdkInteroperability(true);
        rtcManager.joinRtcChannle(meetingConfig.getMeetingId());

    }

    @Override
    public void onUserStart() {
        Observable.just(newMeetingId).observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                JSONObject response = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/UpgradeToNormalLesson?lessonID=" + newMeetingId, null);
                if (response != null && response.getInt("RetCode") == 0) {
                    SocketMessageManager.getManager(host).sendMessage_LeaveMeeting(meetingConfig);
                    SocketMessageManager.getManager(host).sendMessage_startMeeting(meetingConfig, newMeetingId);
                }
            }
        }).subscribe();

    }

    @Override
    public void onUserJoin() {
        SocketMessageManager.getManager(host).sendMessage_JoinMeeting(meetingConfig);
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {

    }

    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        isStarted = true;
        Log.e("MeetingKit", "onJoinChannelSuccess:" + channel);
        if (meetingConfig != null) {
            meetingConfig.setInRealMeeting(true);
            meetingConfig.setAgoraChannelId(channel);
        }

        try {
            getRtcManager().worker().getEngineConfig().mUid = uid;
            initAgora(host);
        } catch (Exception e) {
            Log.e("MeetingKit", "mUid = uid:" + e);
        }
        refreshMembersAndPost(meetingConfig,uid,true);




//        Log.e("MeetingKit", "onJoinChannelSuccess,uid:" + uid + ",elapsed:" + elapsed);
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.e("MeetingKit", "onUserOffline:" + uid);
        if (uid > 1000000000 && uid < 1500000000) {
            meetingConfig.setShareScreenUid(0);
            EventBus.getDefault().post(new EventCloseShare());
        }else {
            AgoraMember member = new AgoraMember();
            member.setUserId(uid);
            member.setMuteAudio(true);
            member.setMuteVideo(true);
            EventBus.getDefault().post(member);
        }

    }

    @Override
    public void onAudioRouteChanged(int routing) {
        if (popMeetingMenu != null && popMeetingMenu.isShowing()) {
            popMeetingMenu.onAudioRouteChanged(routing);
        }
    }

    @Override
    public void onUserMuteVideo(int uid, boolean muted) {
        Log.e("MeetingKit", "onUserMuteVideo:" + uid);
        AgoraMember member = new AgoraMember();

        member.setUserId(uid);
        EventMute eventMute = new EventMute();
        eventMute.setType(EventMute.TYPE_MUTE_VEDIO);
        eventMute.setMuteVedio(muted);
        eventMute.setAgoraMember(member);
        EventBus.getDefault().post(eventMute);


    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {
        Log.e("MeetingKit", "onUserMuteAudio:" + uid);
        AgoraMember member = new AgoraMember();
        member.setUserId(uid);
        EventMute eventMute = new EventMute();
        eventMute.setType(EventMute.TYPE_MUTE_AUDIO);
        eventMute.setMuteAudio(muted);
        eventMute.setAgoraMember(member);
        EventBus.getDefault().post(eventMute);

    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {

    }

    public void postShareScreen(int uid){
        if(meetingConfig.getMode() != 3){
            return;
        }
        if(uid <= 1000000000 || uid > 1500000000){
            return;
        }

        getRtcManager().rtcEngine().enableWebSdkInteroperability(true);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(host.getBaseContext());
        surfaceView.setZOrderOnTop(true);
        surfaceView.setZOrderMediaOverlay(true);
        surfaceView.setTag(uid);
        getRtcManager().rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        EventShareScreen shareScreen = new EventShareScreen();
        shareScreen.setUid(uid);
        shareScreen.setShareView(surfaceView);
        EventBus.getDefault().post(shareScreen);
    }
    @Override
    public void onUserJoined(final int uid, int elapsed) {
        Log.e("MeetingKit", "onUserJoined,uid:" + uid);
        if (meetingConfig != null) {
            if (!meetingConfig.isInRealMeeting()) {
                return;
            }

            Observable.just(meetingConfig).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MeetingConfig>() {
                @Override
                public void accept(MeetingConfig meetingConfig) throws Exception {
                    // 屏幕共享
                    if (uid > 1000000000 && uid < 1500000000) {
                        Log.e("check_share_screen","uid:" + uid);
                        meetingConfig.setShareScreenUid(uid);
                        postShareScreen(meetingConfig.getShareScreenUid());

                    }else {
                     //  成员的camera
                        refreshMembersAndPost(meetingConfig,uid,false);
                    }
                }
            }).subscribe();

        }
    }

    public void setShareScreenStream(EventShareScreen eventShareScreen){

        getRtcManager().rtcEngine().setupRemoteVideo(new VideoCanvas(eventShareScreen.getShareView(), VideoCanvas.RENDER_MODE_HIDDEN, eventShareScreen.getUid()));

    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {

    }

    private AgoraMember createMemberCamera(int userId) {
        AgoraMember member = new AgoraMember();
        member.setUserId(userId);
        member.setAdd(true);
        SurfaceView surfaceV = RtcEngine.CreateRendererView(host.getApplicationContext());
        surfaceV.setZOrderOnTop(true);
        surfaceV.setZOrderMediaOverlay(true);
        getRtcManager().rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, userId));
        member.setSurfaceView(surfaceV);
        return member;
    }

    private AgoraMember createSelfCamera(int userId) {
        Log.e("check_agora","begin_create_camera");
        AgoraMember member = new AgoraMember();
        member.setUserId(userId);
        boolean isMuteVedio = !MeetingSettingCache.getInstance(host).getMeetingSetting().isCameraOn();
        Log.e("check_agora","one");
        boolean isMuteAudio = !MeetingSettingCache.getInstance(host).getMeetingSetting().isMicroOn();
        Log.e("check_agora","two");
        member.setMuteVideo(isMuteVedio);
        member.setMuteAudio(isMuteAudio);
        member.setAdd(true);
        Log.e("check_agora","three");
        SurfaceView surfaceV = RtcEngine.CreateRendererView(host.getApplicationContext());
        Log.e("check_agora","four");
        surfaceV.setZOrderOnTop(true);
        Log.e("check_agora","five");
        surfaceV.setZOrderMediaOverlay(true);
        Log.e("check_agora","six");
        getRtcManager().rtcEngine().setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, userId));
        Log.e("check_agora","seven");
        getRtcManager().worker().getRtcEngine().muteLocalVideoStream(isMuteVedio);
        Log.e("check_agora","eight");
        member.setSurfaceView(surfaceV);
        Log.e("check_agora","end_create_camera");
        return member;
    }

    public void release() {
        if (settingDialog != null) {
            if (settingDialog.isShowing()) {
                settingDialog.dismiss();
                settingDialog = null;
            }
        }
        try {
            getRtcManager().rtcEngine().leaveChannel();
            getRtcManager().event().removeEventHandler(this);
        } catch (Exception e) {
            Log.e("MeetingKit", "release exception:" + e);
        }
        isStarted = false;
        kit = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void showMeetingMenu(ImageView menu, Activity host, MeetingConfig meetingConfig) {
        Log.e("PopMeetingMenu", "showMeetingMenu");
        this.meetingConfig = meetingConfig;
        this.host = host;
        if (popMeetingMenu != null) {
            if (popMeetingMenu.isShowing()) {
                popMeetingMenu.hide();
                popMeetingMenu = null;
            }
        }

        popMeetingMenu = new PopMeetingMenu(host);
        popMeetingMenu.show(host, menu, meetingConfig, this);
    }

    // --- meeting menu

    @Override
    public void menuEndClicked() {
        EventExit exit = new EventExit();
        exit.setEnd(true);
        EventBus.getDefault().post(exit);
    }

    @Override
    public void menuLeaveClicked() {
        EventExit exit = new EventExit();
        exit.setEnd(false);
        EventBus.getDefault().post(exit);

    }

    @Override
    public void menuCameraClicked(boolean isCameraOn) {
        Log.e("menuCameraClicked", "mute_locacl_video:" + !isCameraOn);
        getRtcManager().worker().getRtcEngine().muteLocalVideoStream(!isCameraOn);
        MeetingSettingCache.getInstance(host).setCameraOn(isCameraOn);
        if (cameraAdapter != null) {
            cameraAdapter.muteOrOpenCamera(getRtcManager().worker().getEngineConfig().mUid, !isCameraOn);
        }

        if (fullCameraAdapter != null) {
            fullCameraAdapter.muteOrOpenCamera(getRtcManager().worker().getEngineConfig().mUid, !isCameraOn);
        }
        onUserMuteVideo(Integer.parseInt(AppConfig.UserID),!MeetingSettingCache.getInstance(host).getMeetingSetting().isCameraOn());

    }

    @Override
    public void menuSwitchCamera() {
        getRtcManager().worker().getRtcEngine().switchCamera();
    }

    @Override
    public void menuMicroClicked(boolean isMicroOn) {
        Log.e("menuMicroClicked", "mute_locacl_voice:" + !isMicroOn);
        getRtcManager().worker().getRtcEngine().muteLocalAudioStream(!isMicroOn);
        MeetingSettingCache.getInstance(host).setMicroOn(isMicroOn);
        onUserMuteAudio(Integer.parseInt(AppConfig.UserID),!MeetingSettingCache.getInstance(host).getMeetingSetting().isMicroOn());
    }

    @Override
    public void menuChangeVoiceStatus(int status) {
        if (status == 0) {
            getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
            getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(false);
            getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(false);
            MeetingSettingCache.getInstance(host).setVoiceStatus(1);
        } else if (status == 1) {
            getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(true);
            MeetingSettingCache.getInstance(host).setVoiceStatus(2);

        } else if (status == 2) {
            getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
            getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
            getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(true);
            MeetingSettingCache.getInstance(host).setVoiceStatus(0);
        }
    }




    public void initVoice(int status) {
        try {
            if (status == 0) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
                getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(true);

            } else if (status == 1) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(false);
                getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(false);
            } else if (status == 2) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(true);
            }
        } catch (Exception e) {

        }

    }

    private void initAgora(Activity host) {
        int status = getSettingCache(host).getMeetingSetting().getVoiceStatus();
        boolean isMicroOn = getSettingCache(host).getMeetingSetting().isMicroOn();
        boolean isCameraOn = getSettingCache(host).getMeetingSetting().isCameraOn();
        try {
            if (status == 0) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(true);
                getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(true);

            } else if (status == 1) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(false);
                getRtcManager().worker().getRtcEngine().setDefaultAudioRoutetoSpeakerphone(false);
                getRtcManager().worker().getRtcEngine().setEnableSpeakerphone(false);
            } else if (status == 2) {
                getRtcManager().worker().getRtcEngine().muteAllRemoteAudioStreams(true);
            }
            getRtcManager().worker().getRtcEngine().muteLocalAudioStream(!isMicroOn);
            getRtcManager().worker().getRtcEngine().muteLocalVideoStream(!isCameraOn);
        } catch (Exception e) {

        }

    }

    public void checkCameraForScan() {

        try {
            RtcEngineImpl engine = (RtcEngineImpl) getRtcManager().worker().getRtcEngine();
            engine.setVideoCamera(0);
        } catch (Exception e) {

        }
    }

    private MeetingSettingCache settingCache;

    private MeetingSettingCache getSettingCache(Activity host) {
        if (settingCache == null) {
            settingCache = MeetingSettingCache.getInstance(host);
        }
        return settingCache;
    }

    public void requestMeetingMembers(MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        Observable.just(meetingConfig).observeOn(Schedulers.io()).map(new Function<MeetingConfig, MeetingConfig>() {
            @Override
            public MeetingConfig apply(MeetingConfig meetingConfig) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.MEMBER);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("data").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        if(members != null){
                            for(MeetingMember member : members){

                                if(member.getRole() == 2){
                                    meetingConfig.setMeetingHostId(member.getUserId()+"");
                                }

                                if (member.getPresenter() == 1) {
                                    meetingConfig.setPresenterId(member.getUserId() + "");
                                    meetingConfig.setPresenterSessionId(member.getSessionId() + "");
                                }
                            }
                            meetingConfig.setMeetingMembers(members);
                        }
                    }
                }
                return meetingConfig;
            }

        }).map(new Function<MeetingConfig, MeetingConfig>() {
            @Override
            public MeetingConfig apply(MeetingConfig meetingConfig) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.AUDIENCE);
                Log.e("check_auditor","result:" + result);

                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        Log.e("check_auditor","json_array" + result.getJSONArray("data").toString());
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("data").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        Log.e("check_auditor","auditor" + members);
                        if(members != null){
                            for(MeetingMember member : members){
                                member.setRole(MeetingConfig.MeetingRole.AUDIENCE);
                            }
                            meetingConfig.setMeetingAuditor(members);
                        }
                    }
                }
                return meetingConfig;
            }

        }).map(new Function<MeetingConfig, MeetingConfig>() {
            @Override
            public MeetingConfig apply(MeetingConfig meetingConfig) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.BE_INVITED);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("data").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        if(members != null){
                            for(MeetingMember member : members){
                                member.setRole(MeetingConfig.MeetingRole.BE_INVITED);
                            }
                            meetingConfig.setMeetingInvitors(members);
                        }
                    }
                }
                EventRefreshMembers refreshMembers = new EventRefreshMembers();
                refreshMembers.setMeetingConfig(meetingConfig);
                Log.e("check_member","member:" + meetingConfig.getMeetingMembers().size() + "," + "auditor:" + meetingConfig.getMeetingAuditor().size());
                EventBus.getDefault().post(refreshMembers);
                return meetingConfig;
            }

        }).subscribe();
    }

    public void templeDisableLocalVideo(){
        try {
            getRtcManager().worker().getRtcEngine().disableVideo();
            Log.e("templeDisableLocalVideo","disableVideo");
        }catch (Exception e){
            Log.e("templeDisableLocalVideo","exception:" + e);

        }
    }

    public void restoreLocalVedeo(){
        boolean isCameraOn = MeetingSettingCache.getInstance(host).getMeetingSetting().isCameraOn();
        try {
            getRtcManager().worker().getRtcEngine().enableVideo();
            Log.e("restoreLocalVedeo","enableVideo:" + isCameraOn);
        }catch (Exception e){
            Log.e("restoreLocalVedeo","exception:" + e);

        }
    }

    private void refreshMembersAndPost(MeetingConfig meetingConfig, final int uid, final boolean isSelf) {
        this.meetingConfig = meetingConfig;
        Observable.just(meetingConfig).observeOn(Schedulers.io()).map(new Function<MeetingConfig, MeetingConfig>() {
            @Override
            public MeetingConfig apply(MeetingConfig meetingConfig) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetMeetingMembers(meetingConfig.getMeetingId(), MeetingConfig.MeetingRole.MEMBER);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        List<MeetingMember> members = new Gson().fromJson(result.getJSONArray("data").toString(), new TypeToken<List<MeetingMember>>() {
                        }.getType());
                        if(members != null){
                            for(MeetingMember member : members){

                                if(member.getRole() == 2){
                                    meetingConfig.setMeetingHostId(member.getUserId()+"");
                                }

                                if (member.getPresenter() == 1) {
                                    meetingConfig.setPresenterId(member.getUserId() + "");
                                }
                            }
                            meetingConfig.setMeetingMembers(members);
                        }
                    }
                }
                return meetingConfig;
            }

        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {
                if(isSelf){
                    EventBus.getDefault().post(createSelfCamera(uid));
                }else {
                    EventBus.getDefault().post(createMemberCamera(uid));
                }


            }
        }).subscribe();
    }

    private OnDialogDismissListener onDialogDismissListener;

    public void setOnDialogDismissListener(OnDialogDismissListener onDialogDismissListener){
        this.onDialogDismissListener=onDialogDismissListener;
    }

    public interface OnDialogDismissListener{
        void onDialogDismiss();
    }

}
