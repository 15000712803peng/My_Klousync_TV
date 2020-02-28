package com.ub.service.activity;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.adapter.AgoraCameraAdapter;
import com.kloudsync.techexcel2.adapter.BottomFileAdapter;
import com.kloudsync.techexcel2.adapter.FullAgoraCameraAdapter;
import com.kloudsync.techexcel2.adapter.MeetingMembersAdapter;
import com.kloudsync.techexcel2.bean.AgoraMember;
import com.kloudsync.techexcel2.bean.BookNote;
import com.kloudsync.techexcel2.bean.DocumentPage;
import com.kloudsync.techexcel2.bean.EventClose;
import com.kloudsync.techexcel2.bean.EventCloseNoteView;
import com.kloudsync.techexcel2.bean.EventCloseShare;
import com.kloudsync.techexcel2.bean.EventDisableTvFollow;
import com.kloudsync.techexcel2.bean.EventExit;
import com.kloudsync.techexcel2.bean.EventHeartBeat;
import com.kloudsync.techexcel2.bean.EventHighlightNote;
import com.kloudsync.techexcel2.bean.EventInviteUsers;
import com.kloudsync.techexcel2.bean.EventMeetingDocuments;
import com.kloudsync.techexcel2.bean.EventMute;
import com.kloudsync.techexcel2.bean.EventNote;
import com.kloudsync.techexcel2.bean.EventNoteErrorShowDocument;
import com.kloudsync.techexcel2.bean.EventNotePageActions;
import com.kloudsync.techexcel2.bean.EventOpenNote;
import com.kloudsync.techexcel2.bean.EventPageActions;
import com.kloudsync.techexcel2.bean.EventPageNotes;
import com.kloudsync.techexcel2.bean.EventPlaySoundtrack;
import com.kloudsync.techexcel2.bean.EventPresnterChanged;
import com.kloudsync.techexcel2.bean.EventRefreshDocs;
import com.kloudsync.techexcel2.bean.EventRefreshMembers;
import com.kloudsync.techexcel2.bean.EventSelectNote;
import com.kloudsync.techexcel2.bean.EventSetPresenter;
import com.kloudsync.techexcel2.bean.EventShareScreen;
import com.kloudsync.techexcel2.bean.EventShowMenuIcon;
import com.kloudsync.techexcel2.bean.EventShowNotePage;
import com.kloudsync.techexcel2.bean.EventSocketMessage;
import com.kloudsync.techexcel2.bean.EventTopTipsGone;
import com.kloudsync.techexcel2.bean.EventTvJoin;
import com.kloudsync.techexcel2.bean.MeetingConfig;
import com.kloudsync.techexcel2.bean.MeetingDocument;
import com.kloudsync.techexcel2.bean.MeetingMember;
import com.kloudsync.techexcel2.bean.MeetingType;
import com.kloudsync.techexcel2.bean.Note;
import com.kloudsync.techexcel2.bean.NoteDetail;
import com.kloudsync.techexcel2.bean.NoteId;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.config.RealMeetingSetting;
import com.kloudsync.techexcel2.dialog.AddFileFromDocumentDialog;
import com.kloudsync.techexcel2.dialog.AddFileFromFavoriteDialog;
import com.kloudsync.techexcel2.dialog.PopBottomFile;
import com.kloudsync.techexcel2.dialog.PopBottomMenu;
import com.kloudsync.techexcel2.dialog.TeamSpaceInterfaceListener;
import com.kloudsync.techexcel2.dialog.TeamSpaceInterfaceTools;
import com.kloudsync.techexcel2.help.ApiTask;
import com.kloudsync.techexcel2.help.BottomMenuManager;
import com.kloudsync.techexcel2.help.MeetingKit;
import com.kloudsync.techexcel2.help.NoteViewManager;
import com.kloudsync.techexcel2.help.PageActionsAndNotesMgr;
import com.kloudsync.techexcel2.info.Uploadao;
import com.kloudsync.techexcel2.service.ConnectService;
import com.kloudsync.techexcel2.tool.DocumentModel;
import com.kloudsync.techexcel2.tool.DocumentPageCache;
import com.kloudsync.techexcel2.tool.MeetingSettingCache;
import com.kloudsync.techexcel2.tool.SocketMessageManager;
import com.kloudsync.techexcel2.tool.UserData;

import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.FavoriteVideoPopup;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.internal.XWalkViewBridge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import Decoder.BASE64Encoder;
import butterknife.Bind;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


public class TvKeyActivity extends BaseDocAndMeetingActivity implements PopBottomMenu.BottomMenuOperationsListener, PopBottomFile.BottomFileOperationsListener, AddFileFromFavoriteDialog.OnFavoriteDocSelectedListener,
        BottomFileAdapter.OnDocumentClickListener, View.OnClickListener, AddFileFromDocumentDialog.OnDocSelectedListener, MeetingMembersAdapter.OnMemberClickedListener, AgoraCameraAdapter.OnCameraOptionsListener {

    public static MeetingConfig meetingConfig;
    private SocketMessageManager messageManager;
    //---
    private BottomMenuManager menuManager;
    private PopBottomFile bottomFilePop;
    private MeetingKit meetingKit;
    //---
    @Bind(R.id.layout_real_meeting)
    RelativeLayout meetingLayout;
    @Bind(R.id.layout_toggle_camera)
    LinearLayout toggleCameraLayout;
    @Bind(R.id.image_toggle_camera)
    ImageView toggleCameraImage;
    @Bind(R.id.member_camera_list)
    RecyclerView cameraList;
    @Bind(R.id.full_camera_list)
    RecyclerView fullCameraList;
    @Bind(R.id.meeting_menu)
    ImageView meetingMenu;
    @Bind(R.id.layout_note)
    RelativeLayout noteLayout;
    @Bind(R.id.layout_full_camera)
    RelativeLayout fullCamereLayout;
    @Bind(R.id.icon_back_full_screen)
    ImageView backFullCameraImage;
    @Bind(R.id.layout_vedio)
    RelativeLayout vedioLayout;
    @Bind(R.id.image_vedio_close)
    ImageView closeVedioImage;

    @Bind(R.id.layout_meeting_default_document)
    RelativeLayout meetingDefaultDocument;

    @Bind(R.id.layout_remote_share)
    RelativeLayout remoteShareLayout;
    @Bind(R.id.frame_remote_share)
    FrameLayout remoteShareFrame;

    @Bind(R.id.layout_create_blank_page)
    LinearLayout createBlankPageLayout;

    @Bind(R.id.layout_role_host)
    LinearLayout roleHostLayout;

    @Bind(R.id.layout_role_member)
    LinearLayout roleMemberLayout;

    @Bind(R.id.layout_invite)
    LinearLayout inviteLayout;

    @Bind(R.id.layout_share)
    LinearLayout shareLayout;

    @Bind(R.id._layout_invite)
    LinearLayout _inviteLayout;

    @Bind(R.id._layout_share)
    LinearLayout _shareLayout;

    @Bind(R.id.txt_role)
    TextView roleText;

    AgoraCameraAdapter cameraAdapter;
    FullAgoraCameraAdapter fullCameraAdapter;

    /**MeetingSettingDialog是否dismiss*/
    private boolean mIsSettingDialogCancel=false;

    @Override
    public void showErrorPage() {

    }

    @SuppressLint("WrongConstant")
    @Override
    public void initData() {

        boolean createSuccess = FileUtils.createFileSaveDir(this);
        if (!createSuccess) {
            Toast.makeText(getApplicationContext(), "文件系统异常，打开失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        writeNoteBlankPageImage();
        initViews();
        //----
        RealMeetingSetting realMeetingSetting = MeetingSettingCache.getInstance(this).getMeetingSetting();
        meetingConfig = getConfig();
        messageManager = SocketMessageManager.getManager(this);
        messageManager.registerMessageReceiver();
        if (meetingConfig.getType() != MeetingType.MEETING) {
            messageManager.sendMessage_JoinMeeting(meetingConfig);
        } else {
            MeetingKit.getInstance().prepareJoin(this, meetingConfig);
            MeetingKit.getInstance().setOnDialogDismissListener(new MeetingKit.OnDialogDismissListener() {
                @Override
                public void onDialogDismiss() {
                    mIsSettingDialogCancel=true;
                }
            });
        }

        pageCache = DocumentPageCache.getInstance(this);
        //--
        menuManager = BottomMenuManager.getInstance(this, meetingConfig);
        menuManager.setBottomMenuOperationsListener(this);
        menuManager.setMenuIcon(menuIcon);
        initWeb();
        bottomFilePop = new PopBottomFile(this);

    }

    private void writeNoteBlankPageImage() {
        File localNoteFile = new File(FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg");
        if (localNoteFile.exists()) {
            return;
        }
        new File(FileUtils.getBaseDir() + "note").mkdirs();
        Observable.just(localNoteFile).observeOn(Schedulers.io()).doOnNext(new Consumer<File>() {
            @Override
            public void accept(File file) throws Exception {
                copyAssetsToDst("blank_note_1.jpg", file);
            }
        }).subscribe();

    }

    private void copyAssetsToDst(String srcPath, File dstPath) {
        try {
            InputStream is = getAssets().open(srcPath);
            Log.e("copy_file", "is:" + is);
            FileOutputStream fos = new FileOutputStream(dstPath);
            Log.e("copy_file", "fos:" + fos);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("copy_file", "Exception:" + e.getMessage());

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (bottomFilePop != null && bottomFilePop.isShowing()) {
//            bottomFilePop.hide();
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onResume() {
        Log.e("TvKeyActivity", "on_resume");
        if (menuManager != null) {
            menuManager.setMenuIcon(menuIcon);
        }
        if (bottomFilePop != null && !bottomFilePop.isShowing()) {
            menuIcon.setImageResource(R.drawable.icon_menu);
            menuIcon.setEnabled(true);
        }
        Tools.keepSocketServiceOn(this);
        super.onResume();
    }


    private void initWeb() {
        web.setZOrderOnTop(false);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        web.addJavascriptInterface(this, "AnalyticsWebInterface");

        noteWeb.setZOrderOnTop(true);
        noteWeb.getSettings().setJavaScriptEnabled(true);
        noteWeb.getSettings().setDomStorageEnabled(true);
        noteWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        noteWeb.addJavascriptInterface(new NoteJavascriptInterface(), "AnalyticsWebInterface");

        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
        loadWebIndex();

    }

    private void loadWebIndex() {
        String indexUrl = "file:///android_asset/index.html";
        web.load(indexUrl, null);
        web.load("javascript:ShowToolbar(" + false + ")", null);
        web.load("javascript:Record()", null);

        noteWeb.load(indexUrl, null);
        noteWeb.load("javascript:ShowToolbar(" + false + ")", null);
        noteWeb.load("javascript:Record()", null);
    }

    private MeetingConfig getConfig() {
        Intent data = getIntent();
        if (meetingConfig == null) {
            meetingConfig = new MeetingConfig();
        }
        meetingConfig.setType(data.getIntExtra("meeting_type", MeetingType.DOC));
        meetingConfig.setMeetingId(data.getStringExtra("meeting_id"));
        meetingConfig.setLessionId(data.getIntExtra("lession_id", 0));
        meetingConfig.setDocumentId(data.getStringExtra("document_id"));
        meetingConfig.setRole(data.getIntExtra("meeting_role", MeetingConfig.MeetingRole.MEMBER));
        meetingConfig.setUserToken(UserData.getUserToken(this));
        meetingConfig.setFromMeeting(data.getBooleanExtra("from_meeting", false));
        meetingConfig.setSpaceId(getIntent().getIntExtra("spaceId", 0));
        return meetingConfig;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TvKeyActivity", "on_destroy");
        if (messageManager != null) {
            messageManager.sendMessage_LeaveMeeting(meetingConfig);
            messageManager.release();
        }

        if (menuManager != null) {
            menuManager.release();
        }

        if (wakeLock != null) {
            wakeLock.release();
        }

        MeetingKit.getInstance().release();
        if (web != null) {
            web.removeAllViews();
            web.onDestroy();
            web = null;
        }

        if (meetingConfig != null) {
            meetingConfig.reset();
        }
        meetingConfig = null;

    }

    private synchronized void getMeetingMembers(JSONArray users) {
        List<MeetingMember> allMembers = (List<MeetingMember>) new Gson().fromJson(users.toString(), new TypeToken<List<MeetingMember>>() {
        }.getType());
        List<MeetingMember> auditors = new ArrayList<>();
        List<MeetingMember> members = new ArrayList<>();
        for (MeetingMember member : allMembers) {
            if (member.getRole() == 3) {
                //旁听生
                auditors.add(member);
            } else {
                members.add(member);
            }

            if (member.getRole() == 2) {
                meetingConfig.setMeetingHostId(member.getUserId() + "");
            }

            if (member.getPresenter() == 1) {
                meetingConfig.setPresenterId(member.getUserId() + "");
            }
        }

        Log.e("getMeetingMembers", "members_size:" + members.size());
        meetingConfig.setMeetingAuditor(auditors);
        meetingConfig.setMeetingMembers(members);
//        EventRefreshMembers refreshMembers = new EventRefreshMembers();
//        refreshMembers.setMeetingConfig(meetingConfig);
//        EventBus.getDefault().post(refreshMembers);
    }

    private void requestDocumentsAndShowPage() {
        Log.e("TvKeyActivity","requestDocumentsAndShowPage");
        DocumentModel.asyncGetDocumentsInDocAndShowPage(meetingConfig, true);
    }

    private void requestDocuments() {
        DocumentModel.asyncGetDocumentsInDocAndShowPage(meetingConfig, false);
    }

    // ------- @Subscribe
    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDocuments(EventMeetingDocuments documents) {
        // 所有文档的data
        Log.e("receiverDocuemnts", "documents:" + documents);
        this.documents.clear();
        this.documents.addAll(documents.getDocuments());
        if (this.documents != null && this.documents.size() > 0) {
            int index = this.documents.indexOf(new MeetingDocument(meetingConfig.getFileId()));
            if (index < 0) {
                index = 0;
            }
            meetingConfig.setDocument(this.documents.get(index));
            downLoadDocumentPageAndShow();
        } else {
            hideEnterLoading();
            menuIcon.setVisibility(View.VISIBLE);
            if (meetingConfig.getType() == MeetingType.MEETING) {
                meetingDefaultDocument.setVisibility(View.VISIBLE);
                handleMeetingDefaultDocument();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDocuments(EventRefreshDocs refreshDocs) {
        // 所有文档的data
        this.documents.clear();
        this.documents.addAll(refreshDocs.getDocuments());
        Log.e("refreshDocuments", "documents:" + documents.size());
        int index = documents.indexOf(new MeetingDocument(refreshDocs.getItemId()));
        if (index < 0 || refreshDocs.getPageNumber() < 0) {
            return;
        }
        changeDocument(documents.get(index), refreshDocs.getPageNumber());
    }

    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDocumentPage(DocumentPage page) {
        Log.e("showDocumentPage", "page:" + page);
        hideEnterLoading();
        MeetingDocument document = getDocument(page);
        Log.e("showDocumentPage", "current_document:" + document);
        if (document != null) {
            meetingConfig.setDocument(document);
            meetingConfig.setDocumentId(document.getItemID() + "");
            meetingConfig.setPageNumber(meetingConfig.getDocument().getDocumentPages().indexOf(page) + 1);
        }

        //notify change file
        notifyDocumentChanged();
        meetingDefaultDocument.setVisibility(View.GONE);
        web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        web.load("javascript:ShowPDF('" + page.getShowingPath() + "'," + (page.getPageNumber()) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")", null);
        web.load("javascript:Record()", null);
        if (bottomFilePop != null && bottomFilePop.isShowing()) {
            bottomFilePop.setDocuments(this.documents, meetingConfig.getDocument().getItemID(), this);
            bottomFilePop.removeTempDoc();
        } else {
            menuIcon.setVisibility(View.VISIBLE);
        }
    }

    private long currentNoteId;


    class TempNoteData {
        private String data;
        private long noteId;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public long getNoteId() {
            return noteId;
        }

        public void setNoteId(long noteId) {
            this.noteId = noteId;
        }
    }

    private CopyOnWriteArrayList<TempNoteData> newNoteDatas = new CopyOnWriteArrayList<>();

    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showNotePage(final EventShowNotePage page) {
        Log.e("showNotePage", "page:" + page);
        noteWeb.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(page.getNotePage().getLocalFileId())) {
            if (page.getNotePage().getLocalFileId().contains(".")) {
                currentNoteId = page.getNoteId();
                String localNoteBlankPage = FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg";
                Log.e("show_PDF", "javascript:ShowPDF('" + localNoteBlankPage + "'," + (page.getNotePage().getPageNumber()) + ",''," + page.getAttachmendId() + "," + false + ")");
                noteWeb.load("javascript:ShowPDF('" + localNoteBlankPage + "'," + (page.getNotePage().getPageNumber()) + ",''," + page.getAttachmendId() + "," + false + ")", null);
                noteWeb.load("javascript:Record()", null);
                handleBluetoothNote(page.getNotePage().getPageUrl());
                return;
            }
        }
        Log.e("show_PDF", "javascript:ShowPDF('" + page.getNotePage().getShowingPath() + "'," + (page.getNotePage().getPageNumber()) + ",''," + page.getAttachmendId() + "," + false + ")");
        noteWeb.load("javascript:ShowPDF('" + page.getNotePage().getShowingPath() + "'," + (page.getNotePage().getPageNumber()) + ",''," + page.getAttachmendId() + "," + false + ")", null);
        noteWeb.load("javascript:Record()", null);

    }

    private void handleBluetoothNote(final String url) {
        Observable.just(url).observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                String newUrl = "";
                int index = url.lastIndexOf("/");
                if (index > 0 && index < url.length() - 2) {
                    newUrl = url.substring(0, index + 1) + "book_page_data.json";
                }
                return newUrl;
            }
        }).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String url) throws Exception {
                JSONObject jsonObject = new JSONObject();
                if (!TextUtils.isEmpty(url)) {
                    Log.e("check_url", "url:" + url);
                    jsonObject = ServiceInterfaceTools.getinstance().syncGetNotePageJson(url);
                }
                return jsonObject;
            }
        }).observeOn(AndroidSchedulers.mainThread()).delay(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                String key = "ShowDotPanData";
                Log.e("ShowDotPanData", "javascript:FromApp('" + key + "'," + jsonObject + ")");
                JSONObject _data = new JSONObject();
                _data.put("LinesData", jsonObject);
                _data.put("ShowInCenter", false);
                _data.put("TriggerEvent", false);
                noteWeb.setZOrderOnTop(true);
                noteWeb.setVisibility(View.VISIBLE);
                noteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);
            }
        }).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (!newNoteDatas.isEmpty()) {
                    Observable.fromIterable(newNoteDatas).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<TempNoteData>() {
                        @Override
                        public void accept(TempNoteData tempNoteData) throws Exception {
                            Log.e("draw_new_note", "temp_note_note");
                            if (tempNoteData.getNoteId() == currentNoteId) {
                                String key = "ShowDotPanData";
                                JSONObject _data = new JSONObject();
                                _data.put("LinesData", tempNoteData.getData());
                                _data.put("ShowInCenter", true);
                                _data.put("TriggerEvent", true);
                                noteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);
                            }
                            newNoteDatas.remove(tempNoteData);
                        }
                    }).subscribe();
                }
            }
        }).observeOn(Schedulers.io()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetNoteP1Item(currentNoteId);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        JSONArray dataArray = result.getJSONArray("data");
                        Observable.just(dataArray).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONArray>() {
                            @Override
                            public void accept(JSONArray _jsonArray) throws Exception {
                                for (int i = 0; i < _jsonArray.length(); ++i) {
                                    JSONObject data = _jsonArray.getJSONObject(i);
                                    addLinkBorderForDTNew(data);
                                }

                            }
                        }).subscribe();


                    }
                }
            }
        }).subscribe();
    }

    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void showNote(EventNote note) {
        Note _note = note.getNote();
        Log.e("show_note", "note:" + _note);
        if (meetingConfig.getType() == MeetingType.MEETING) {
            noteUsersLayout.setVisibility(View.GONE);
        } else {
            noteUsersLayout.setVisibility(View.VISIBLE);
        }
        showNoteView();
        NoteViewManager.getInstance().setContent(this, noteLayout, _note, noteWeb, meetingConfig);
        notifyViewNote(note.getNote());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void showSelectedNote(EventSelectNote selectNote) {
        Observable.just(selectNote).observeOn(Schedulers.io()).doOnNext(new Consumer<EventSelectNote>() {
            @Override
            public void accept(EventSelectNote selectNote) throws Exception {
                JSONObject response = ServiceInterfaceTools.getinstance().syncImportNote(meetingConfig, selectNote);
                if (response != null && response.has("RetCode")) {
                    if (response.getInt("RetCode") == 0) {
                        selectNote.setNewLinkId(response.getInt("RetData"));
                    }
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<EventSelectNote>() {
            @Override
            public void accept(EventSelectNote selectNote) throws Exception {
                if (selectNote.getLinkId() > 0) {
                    deleteNote(selectNote.getLinkId());
                }
                if (selectNote.getNewLinkId() > 0) {
                    drawNote(selectNote.getNewLinkId(), selectNote.getLinkProperty(), 0);
                }
            }
        }).subscribe();
    }


    @SuppressLint("WrongConstant")
    public synchronized void followShowNote(int noteId) {

        if (meetingConfig.getType() == MeetingType.MEETING) {
            noteUsersLayout.setVisibility(View.GONE);
        } else {
            noteUsersLayout.setVisibility(View.VISIBLE);
        }
        hideEnterLoading();
        showNoteView();
        NoteViewManager.getInstance().followShowNote(this, noteLayout, noteWeb, noteId, meetingConfig, menuIcon);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDocumentIfRequestNoteError(EventNoteErrorShowDocument showDocument) {
        requestDocumentsAndShowPage();
    }

    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showMenuIcon(EventShowMenuIcon showMenuIcon) {
        if (menuIcon != null) {
            Log.e("showMenuIcon", "show");
            menuIcon.setImageResource(R.drawable.icon_menu);
            menuIcon.setEnabled(true);
            Log.e("showMenuIcon", "menu visible:  " + (menuIcon.getVisibility() == View.VISIBLE));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exit(EventExit exit) {
//        handleExit(exit.isEnd());
        finish();
    }

    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void receiveSocketMessage(EventSocketMessage socketMessage) {
        Log.e("TvKeyActivity", "socket_message:" + socketMessage);
        String action = socketMessage.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }

        switch (action) {
            case SocketMessageManager.MESSAGE_LEAVE_MEETING:
                //handleMessageLeaveMeeting(socketMessage.getData());
                break;

            case SocketMessageManager.MESSAGE_JOIN_MEETING:
                handleMessageJoinMeeting(socketMessage.getData());
                break;

            case SocketMessageManager.MESSAGE_BROADCAST_FRAME:

                if (socketMessage.getData() == null) {
                    return;
                }
                if (socketMessage.getData().has("data")) {
                    try {
                        String _frame = Tools.getFromBase64(socketMessage.getData().getString("data"));
                        if (noteLayout.getVisibility() == View.VISIBLE) {
                            if (noteWeb != null) {
                                noteWeb.load("javascript:PlayActionByTxt('" + _frame + "','" + 1 + "')", null);
                            }
                        } else {
                            if (web != null) {
                                web.load("javascript:PlayActionByTxt('" + _frame + "','" + 1 + "')", null);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_SEND_MESSAGE:
                if (socketMessage.getData() == null) {
                    return;
                }

                if (socketMessage.getData().has("data")) {
                    try {
                        handleMessageSendMessage(new JSONObject(Tools.getFromBase64(socketMessage.getData().getString("data"))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SocketMessageManager.MESSAGE_MAKE_PRESENTER:
                if (socketMessage.getData() == null) {
                    return;
                }

                try {
                    if (socketMessage.getData().has("presenterId")) {
                        meetingConfig.setPresenterId(socketMessage.getData().getString("presenterId"));
                    }
                    if (socketMessage.getData().has("presenterSessionId")) {
                        meetingConfig.setPresenterSessionId(socketMessage.getData().getString("presenterSessionId"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (MeetingMember member : meetingConfig.getMeetingMembers()) {
                    if ((member.getUserId() + "").equals(meetingConfig.getPresenterId())) {
                        member.setPresenter(1);
                    } else {
                        member.setPresenter(0);
                    }
                }
                EventRefreshMembers refreshMembers = new EventRefreshMembers();
                refreshMembers.setMeetingConfig(meetingConfig);
                EventBus.getDefault().post(refreshMembers);
                break;
            case SocketMessageManager.MESSAGE_ATTACHMENT_UPLOADED:
                if (socketMessage.getData() == null) {
                    return;
                }
                handleMessageAttchmentUploadedAndShow(socketMessage.getData());
                break;
            case SocketMessageManager.MESSAGE_END_MEETING:
                finish();
                break;
            case SocketMessageManager.MESSAGE_MEMBER_LIST_CHANGE:
                MeetingKit.getInstance().requestMeetingMembers(meetingConfig);
                break;
            case SocketMessageManager.MESSAGE_AGORA_STATUS_CHANGE:
//                handleMessageAgoraStatusChange(socketMessage.getData());
                break;

            case SocketMessageManager.MESSAGE_NOTE_DATA:
                if (socketMessage.getData().has("retData")) {
                    try {
                        JSONObject retData = socketMessage.getData().getJSONObject("retData");
                        String noteData = retData.getString("data");
                        long noteId = retData.getInt("noteId");
                        if (currentNoteId != noteId) {
                            TempNoteData _noteData = new TempNoteData();
                            _noteData.setData(Tools.getFromBase64(noteData));
                            _noteData.setNoteId(noteId);
                            newNoteDatas.add(_noteData);
                            return;
                        }
                        String key = "ShowDotPanData";
                        if (noteLayout.getVisibility() == View.VISIBLE) {
                            if (noteWeb != null) {
                                JSONObject _data = new JSONObject();
                                _data.put("LinesData", Tools.getFromBase64(noteData));
                                _data.put("ShowInCenter", true);
                                _data.put("TriggerEvent", true);
                                noteWeb.load("javascript:FromApp('" + key + "'," + _data + ")", null);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_NOTE_CHANGE:
                if (socketMessage.getData().has("retData")) {
                    try {
                        int noteId = socketMessage.getData().getJSONObject("retData").getInt("noteId");
                        followShowNote(noteId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SocketMessageManager.MESSAGE_START_RECOGNIZING_NOTE:
                Log.e("TvKeyActivity", "showTips");
                topTipText.showTips();
                break;

            case SocketMessageManager.MESSAGE_BIND_TV_LEAVE_MEETING:

                Observable.just("leave").delay(1000,TimeUnit.MILLISECONDS).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if(noNeedLeave){
                            return;
                        }
                        SocketMessageManager.getManager(TvKeyActivity.this).sendMessage_LeaveMeeting(meetingConfig,1);
                        finish();
                    }
                });

                break;

        }
    }

    @Subscribe
    public void setTopTipsGone() {
        topTipText.setVisibility(View.GONE);
    }

    @Subscribe
    public void receiveHeartBeatMessage(EventHeartBeat heartBeat) {
        Log.e("receiveHeartBeatMessage", "heartBeat:" + heartBeat);
        try {
            JSONObject heartMessageData = new JSONObject(heartBeat.getMessage());
            handleMessageHeartBeat(heartMessageData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ;

    }

    @Subscribe
    public void receiveEventClose(EventClose close) {
        Log.e("receiveEventClose", "close");
//        finish();
        requestNotFollow();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivePageActions(EventPageActions pageActions) {
        String data = pageActions.getData();
        if (!TextUtils.isEmpty(data)) {
            if (pageActions.getPageNumber() == meetingConfig.getPageNumber()) {
//                Log.e("check_play_txt","PlayActionByArray:" + data);
                web.load("javascript:PlayActionByArray(" + data + "," + 0 + ")", null);
            }
        }
    }


    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveNotePageActions(EventNotePageActions pageActions) {
        String data = pageActions.getData();
        if (!TextUtils.isEmpty(data)) {
            if (noteLayout.getVisibility() == View.VISIBLE) {
                noteWeb.load("javascript:PlayActionByArray(" + data + "," + 0 + ")", null);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivePageNotes(EventPageNotes pageNotes) {
        Log.e("receivePageNotes", "page_notes:" + pageNotes);
        List<NoteDetail> notes = pageNotes.getNotes();
        if (notes != null && notes.size() > 0) {
            if (pageNotes.getPageNumber() == meetingConfig.getPageNumber()) {
                if (messageManager != null) {
                    for (NoteDetail note : notes) {

                        try {
                            JSONObject message = new JSONObject();
                            message.put("type", 38);
                            message.put("LinkID", note.getLinkID());
                            message.put("IsOther", false);
                            if (!TextUtils.isEmpty(note.getLinkProperty())) {
                                message.put("LinkProperty", new JSONObject(note.getLinkProperty()));
                            }
                            Log.e("check_play_txt", "notes_PlayActionByTxt:" + message);
                            web.load("javascript:PlayActionByTxt('" + message + "')", null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showMemberCamera(AgoraMember member) {
        Log.e("showMemeberCamera", "member:" + member);
        if ((member.getUserId() + "").equals(AppConfig.UserID) && member.isAdd()) {
            //自己开启会议成功
            if (documents != null && documents.size() > 0) {
                notifyDocumentChanged();
            }
        }

        if (member.isAdd()) {
            meetingConfig.addAgoraMember(member);
        } else {
            //delete user
            meetingConfig.deleteAgoraMember(member);
        }

        checkAgoraMemberName();
        refreshAgoraMember(member);

        if (cameraAdapter != null) {
            cameraAdapter.setOnCameraOptionsListener(this);
        }

        Log.e("check_send_agora_status", "user_id:" + AppConfig.UserID + ",agora_id:" + member.getUserId());
        if ((member.getUserId() + "").equals(AppConfig.UserID)) {
            messageManager.sendMessage_AgoraStatusChange(meetingConfig, member);
        }

    }

    private EventShareScreen shareScreen;

    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showShareScreen(EventShareScreen shareScreen) {
        Log.e("showShareScreen", "show_screen");
        this.shareScreen = shareScreen;
        remoteShareLayout.setVisibility(View.VISIBLE);
        if (remoteShareFrame.getChildCount() == 0) {
            ViewParent parent = shareScreen.getShareView().getParent();
            if (parent != null) {
                ((FrameLayout) parent).removeView(shareScreen.getShareView());
            }
        }
        MeetingKit.getInstance().setShareScreenStream(shareScreen);
        remoteShareFrame.removeAllViews();
        remoteShareFrame.addView(shareScreen.getShareView(), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        reloadAgoraMember();
    }

    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeShareScreen(EventCloseShare closeShare) {
        SurfaceView surfaceView = (SurfaceView) remoteShareFrame.getChildAt(0);
        if (surfaceView != null) {
            surfaceView.setVisibility(View.GONE);
        }
        remoteShareFrame.removeAllViews();
        remoteShareLayout.setVisibility(View.GONE);
        shareScreen = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void inviteUsers(EventInviteUsers inviteUsers) {
        messageManager.sendMessage_InviteToMeeting(meetingConfig, inviteUsers.getUsers());
        MeetingKit.getInstance().requestMeetingMembers(meetingConfig);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadNodeSuccess(NoteId noteId) {
        Log.e("event_bus", "draw_note_by_id:" + noteId);
        if (noteId.getLinkID() == 0) {
            return;
        }
        deleteTempNote();
        drawNote(noteId.getLinkID(), meetingConfig.getCurrentLinkProperty(), 0);
    }

    private void drawNote(int linkId, JSONObject linkProperty, int isOther) {
        JSONObject noteData = new JSONObject();
        try {
            noteData.put("type", 38);
            noteData.put("LinkID", linkId);
            noteData.put("IsOther", isOther);
            noteData.put("LinkProperty", linkProperty);
            Log.e("drawNote", "note:" + noteData.toString());
            if (web != null) {
                web.load("javascript:PlayActionByTxt('" + noteData + "')", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawTempNote() {
        drawNote(-1, meetingConfig.getCurrentLinkProperty(), 0);
    }

    private void deleteNote(int linkId) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/RemoveNote?linkIDs=" + linkId;
        JSONObject noteData = new JSONObject();
        try {
            noteData.put("type", 102);
            noteData.put("id", "BooXNote_" + linkId);
            Log.e("deleteTempNote", "note:" + noteData.toString());
            if (web != null) {
                web.load("javascript:PlayActionByTxt('" + noteData + "')", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServiceInterfaceTools.getinstance().removeNote(url, ServiceInterfaceTools.REMOVENOTE, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {

            }
        });
    }

    private void deleteTempNote() {
        deleteNote(-1);
    }

    @SuppressLint("WrongConstant")
    public void refreshAgoraMember(AgoraMember member) {
        if (cameraList.getVisibility() == View.VISIBLE) {
            if (cameraAdapter != null) {
                if (member.isAdd()) {
                    cameraAdapter.addUser(member);
                } else {
                    cameraAdapter.removeUser(member);
                }
            } else {
                List<AgoraMember> copyMembers = new ArrayList<>();
                for (AgoraMember _member : meetingConfig.getAgoraMembers()) {
                    copyMembers.add(_member);
                }
                cameraAdapter = new AgoraCameraAdapter(this);
                cameraAdapter.setMembers(copyMembers);
                cameraAdapter.setOnCameraOptionsListener(this);
//            fitCameraList();
                cameraList.setAdapter(cameraAdapter);
                MeetingKit.getInstance().setCameraAdapter(cameraAdapter);
            }
        }

        if (fullCameraList.getVisibility() == View.VISIBLE) {
            if (fullCameraAdapter != null) {
                if (member.isAdd()) {
                    fullCameraAdapter.addUser(member);
                } else {
                    fullCameraAdapter.removeUser(member);
                }
            } else {
                List<AgoraMember> copyMembers = new ArrayList<>();
                for (AgoraMember _member : meetingConfig.getAgoraMembers()) {
                    copyMembers.add(_member);
                }
                fullCameraAdapter = new FullAgoraCameraAdapter(this);
                fullCameraAdapter.setMembers(copyMembers);
                fitFullCameraList();
                fullCameraList.setAdapter(fullCameraAdapter);
                MeetingKit.getInstance().setFullCameraAdaptero(fullCameraAdapter);
            }
        }
    }


    @SuppressLint("WrongConstant")
    private void reloadAgoraMember() {
        List<AgoraMember> copyMembers = new ArrayList<>();
        for (AgoraMember member : meetingConfig.getAgoraMembers()) {
            copyMembers.add(member);
        }

        if (cameraList.getVisibility() == View.VISIBLE) {
            if (cameraAdapter != null) {
                cameraAdapter.reset();
                cameraAdapter = null;
            }

            cameraAdapter = new AgoraCameraAdapter(this);
            cameraAdapter.setMembers(copyMembers);
            cameraAdapter.setOnCameraOptionsListener(this);
//            fitCameraList();
            cameraList.setAdapter(cameraAdapter);
            MeetingKit.getInstance().setCameraAdapter(cameraAdapter);
        }

        if (fullCameraList.getVisibility() == View.VISIBLE) {
            if (fullCameraAdapter != null) {
                fullCameraAdapter.reset();
                fullCameraAdapter = null;
            }
            fullCameraAdapter = new FullAgoraCameraAdapter(this);
            fullCameraAdapter.setMembers(copyMembers);
            fitFullCameraList();
            fullCameraList.setAdapter(fullCameraAdapter);
            MeetingKit.getInstance().setFullCameraAdaptero(fullCameraAdapter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void highLightNote(final EventHighlightNote note) {

        if (note.getPageNumber() == meetingConfig.getPageNumber()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("LinkID", note.getNote().getLinkID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String key = "TwinkleBookNote";
            web.load("javascript:FromApp('" + key + "'," + jsonObject + ")", null);

        } else {

//            if (note.getNote().getAttachmentID() != meetingConfig.getDocument().getAttachmentID()) {
//                Log.e("highLightNote", "in_different_document");
//                final int index = documents.indexOf(new MeetingDocument(note.getNote().getDocumentItemID()));
//                changeDocument(note.getNote().getDocumentItemID(), note.getPageNumber());
//
//            } else

            {
                Log.e("highLightNote", "in_same_document");
                final String notifyUrl = meetingConfig.getNotifyUrl();
                if (TextUtils.isEmpty(notifyUrl)) {
                    return;
                }
                if (note.getPageNumber() - 1 < 0 || note.getPageNumber() > meetingConfig.getDocument().getPageCount()) {
                    return;
                }
                final DocumentPage page = meetingConfig.getDocument().getDocumentPages().get(note.getPageNumber() - 1);
                if (page == null) {
                    return;
                }
                final String pathLocalPath = notifyUrl.substring(0, notifyUrl.lastIndexOf("<")) +
                        note.getPageNumber() + notifyUrl.substring(notifyUrl.lastIndexOf("."));
                if (!TextUtils.isEmpty(page.getPageUrl())) {
                    DocumentPage _page = pageCache.getPageCache(page.getPageUrl());
                    Log.e("check_cache_page", "_page:" + _page + "，page:" + page);

                    if (_page != null && page.getPageUrl().equals(_page.getPageUrl())) {
                        if (!TextUtils.isEmpty(_page.getSavedLocalPath())) {
                            File localFile = new File(_page.getSavedLocalPath());
                            if (localFile.exists()) {
                                if (!pathLocalPath.equals(localFile.getAbsolutePath())) {
                                    if (localFile.renameTo(new File(pathLocalPath))) {
                                        Log.e("highLightNote", "uncorrect_file_name,rename");
                                        notifyWebFilePrepared(notifyUrl, note.getPageNumber());
                                        page.setSavedLocalPath(pathLocalPath);
                                        page.setShowingPath(notifyUrl);
                                        pageCache.cacheFile(page);
                                        highLightNoteInDifferentPage(page, note);
                                        return;
                                    } else {
                                        Log.e("highLightNote", "uncorrect_file_name,delete");
                                        localFile.delete();
                                    }
                                } else {
                                    Log.e("highLightNote", "correct_file_name,notify");
                                    page.setSavedLocalPath(pathLocalPath);
                                    page.setShowingPath(notifyUrl);
                                    pageCache.cacheFile(page);
                                    notifyWebFilePrepared(notifyUrl, note.getPageNumber());
                                    highLightNoteInDifferentPage(page, note);
                                    return;
                                }

                            } else {
                                //清楚缓存
                                pageCache.removeFile(_page.getPageUrl());
                            }

                        }
                    }
                }

                Observable.just(meetingConfig.getDocument()).observeOn(Schedulers.io()).map(new Function<MeetingDocument, Object>() {
                    @Override
                    public Object apply(MeetingDocument document) throws Exception {
                        safeDownloadFile(page, pathLocalPath, note.getPageNumber(), true);
                        return document;
                    }
                }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        highLightNoteInDifferentPage(page, note);

                    }
                }).subscribe();
            }


        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMeetingMembers(EventRefreshMembers refreshMembers) {

        if (meetingConfig.getMeetingMembers() == null || meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
//        if (meetingMembersDialog != null && meetingMembersDialog.isShowing()) {
//            Log.e("refreshMeetingMembers", "dialog_is_show");
//            meetingMembersDialog.refresh(refreshMembers);
//        }
        checkAgoraMemberName();

    }

    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void muteAgoraMember(EventMute eventMute) {
        Log.e("muteAgoraMember", "eventMute:" + eventMute);
        if (cameraList.getVisibility() == View.VISIBLE) {
            if (cameraAdapter != null) {
                if (eventMute.getType() == EventMute.TYPE_MUTE_VEDIO) {
                    Log.e("muteAgoraMember", "muteVideo");
                    cameraAdapter.muteVideo(eventMute.getAgoraMember(), eventMute.isMuteVedio());
                } else if (eventMute.getType() == EventMute.TYPE_MUTE_AUDIO) {
                    Log.e("muteAgoraMember", "muteAudio");
                    cameraAdapter.muteAudio(eventMute.getAgoraMember(), eventMute.isMuteAudio());
                }

            }
        }

        if (fullCameraList.getVisibility() == View.VISIBLE) {
            if (fullCameraAdapter != null) {
                if (eventMute.getType() == EventMute.TYPE_MUTE_VEDIO) {
                    fullCameraAdapter.muteVideo(eventMute.getAgoraMember(), eventMute.isMuteVedio());
                } else if (eventMute.getType() == EventMute.TYPE_MUTE_AUDIO) {
                    fullCameraAdapter.muteAudio(eventMute.getAgoraMember(), eventMute.isMuteAudio());

                }

            }
        }

        if (eventMute.getAgoraMember() != null) {
            if ((eventMute.getAgoraMember().getUserId() + "").equals(AppConfig.UserID)) {
                int index = meetingConfig.getAgoraMembers().indexOf(eventMute.getAgoraMember());
                if (index >= 0) {
                    messageManager.sendMessage_AgoraStatusChange(meetingConfig, meetingConfig.getAgoraMembers().get(index));
                }

            }
        }

    }

    private void checkAgoraMemberName() {
        for (MeetingMember member : meetingConfig.getMeetingMembers()) {
            for (AgoraMember agoraMember : meetingConfig.getAgoraMembers()) {
                if ((member.getUserId() + "").equals(agoraMember.getUserId() + "")) {
                    agoraMember.setUserName(member.getUserName());
                    agoraMember.setIconUrl(member.getAvatarUrl());
                    break;
                }
            }
        }

        for (MeetingMember member : meetingConfig.getMeetingAuditor()) {
            for (AgoraMember agoraMember : meetingConfig.getAgoraMembers()) {
                if ((member.getUserId() + "").equals(agoraMember.getUserId() + "")) {
                    agoraMember.setUserName(member.getUserName());
                    agoraMember.setIconUrl(member.getAvatarUrl());
                    break;
                }
            }
        }
    }


    private void highLightNoteInDifferentPage(DocumentPage page, EventHighlightNote note) {
        showDocumentPage(page);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LinkID", note.getNote().getLinkID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Observable.just(page).delay(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<DocumentPage>() {
            @Override
            public void accept(DocumentPage page) throws Exception {
                String key = "TwinkleBookNote";
                Log.e("TwinkleBookNote", "delay");
                web.load("javascript:FromApp('" + key + "'," + jsonObject + ")", null);
            }
        }).subscribe();

    }

    private MeetingDocument getDocument(DocumentPage page) {
        Log.e("check_page", "current_page:" + page);
        for (MeetingDocument document : documents) {
//            if(document.getDocumentPages().contains(page)){
//                return document;
//            }
            for (DocumentPage _page : document.getDocumentPages()) {
                Log.e("check_page", "page:" + _page);
                if (_page.equals(page)) {
                    return document;
                }
            }
        }
        return null;
    }

    private void downLoadDocumentPageAndShow() {
        Observable.just(meetingConfig.getDocument()).observeOn(Schedulers.io()).map(new Function<MeetingDocument, Object>() {
            @Override
            public Object apply(MeetingDocument document) throws Exception {
                int pageNumber = 1;
                if (meetingConfig.getPageNumber() == 0) {
                    pageNumber = 1;
                } else if (meetingConfig.getPageNumber() > 0) {
                    pageNumber = meetingConfig.getPageNumber();
                }
                DocumentPage page = document.getDocumentPages().get(pageNumber - 1);
                queryAndDownLoadPageToShow(page, true);
                return page;
            }
        }).subscribe();
    }

    private synchronized void downLoadDocumentPageAndShow(MeetingDocument document, final int pageNumber) {
        Observable.just(document).observeOn(Schedulers.io()).map(new Function<MeetingDocument, Object>() {
            @Override
            public Object apply(MeetingDocument document) throws Exception {
                queryAndDownLoadPageToShow(document, pageNumber, true);
                return document;
            }
        }).subscribe();
    }

    private CopyOnWriteArrayList<MeetingDocument> documents = new CopyOnWriteArrayList<>();
    private DocumentPageCache pageCache;

    private Uploadao parseQueryResponse(final String jsonstring) {
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject data = returnjson.getJSONObject("Data");

                JSONObject bucket = data.getJSONObject("Bucket");
                Uploadao uploadao = new Uploadao();
                uploadao.setServiceProviderId(bucket.getInt("ServiceProviderId"));
                uploadao.setRegionName(bucket.getString("RegionName"));
                uploadao.setBucketName(bucket.getString("BucketName"));
                return uploadao;
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    private void queryAndDownLoadPageToShow(final DocumentPage documentPage, final boolean needRedownload) {
        String pageUrl = documentPage.getPageUrl();
        DocumentPage page = pageCache.getPageCache(pageUrl);
        Log.e("-", "get cach page:" + page + "--> url:" + documentPage.getPageUrl());
        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
                && !TextUtils.isEmpty(page.getSavedLocalPath()) && !TextUtils.isEmpty(page.getShowingPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                page.setDocumentId(documentPage.getDocumentId());
                page.setPageNumber(documentPage.getPageNumber());
                pageCache.cacheFile(page);
                EventBus.getDefault().post(page);
                return;
            } else {
                pageCache.removeFile(pageUrl);
            }
        }

        MeetingDocument document = meetingConfig.getDocument();
        String meetingId = meetingConfig.getMeetingId();

        JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                document.getNewPath());
        if (queryDocumentResult != null) {
            Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
            String fileName = pageUrl.substring(pageUrl.lastIndexOf("/") + 1);
            String part = "";
            if (1 == uploadao.getServiceProviderId()) {
                part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + document.getNewPath()
                        + "/" + fileName;
            } else if (2 == uploadao.getServiceProviderId()) {
                part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + document.getNewPath() + "/" + fileName;
            }

            String pathLocalPath = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_" + (documentPage.getPageNumber()) +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            final String showUrl = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_<" + document.getPageCount() + ">" +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            int pageIndex = 1;
            if (meetingConfig.getPageNumber() == 0) {
                pageIndex = 1;
            } else if (meetingConfig.getPageNumber() > 0) {
                pageIndex = meetingConfig.getPageNumber();
            }

            Log.e("-", "showUrl:" + showUrl);

            documentPage.setSavedLocalPath(pathLocalPath);

            Log.e("-", "page:" + documentPage);
            //保存在本地的地址

            DownloadUtil.get().download(pageUrl, pathLocalPath, new DownloadUtil.OnDownloadListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDownloadSuccess(int arg0) {
                    documentPage.setShowingPath(showUrl);
                    Log.e("queryAndDownLoadCurrentPageToShow", "onDownloadSuccess:" + documentPage);
                    pageCache.cacheFile(documentPage);
                    EventBus.getDefault().post(documentPage);
                }

                @Override
                public void onDownloading(final int progress) {

                }

                @Override
                public void onDownloadFailed() {

                    Log.e("-", "onDownloadFailed:" + documentPage);
                    if (needRedownload) {
                        queryAndDownLoadPageToShow(documentPage, false);
                    }
                }
            });
        }
    }

    private synchronized void queryAndDownLoadPageToShow(final MeetingDocument document, final int pageNumber, final boolean needRedownload) {
        if (pageNumber <= 0) {
            return;
        }
        final DocumentPage _page = document.getDocumentPages().get(pageNumber - 1);
        String pageUrl = _page.getPageUrl();
        final DocumentPage page = pageCache.getPageCache(pageUrl);
        Log.e("-", "get cach page:" + page + "--> url:" + pageUrl);
        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
                && !TextUtils.isEmpty(page.getSavedLocalPath()) && !TextUtils.isEmpty(page.getShowingPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                page.setDocumentId(_page.getDocumentId());
                page.setPageNumber(_page.getPageNumber());
                pageCache.cacheFile(page);
                EventBus.getDefault().post(page);
                return;
            } else {
                pageCache.removeFile(pageUrl);
            }
        }

        String meetingId = meetingConfig.getMeetingId();
        JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                document.getNewPath());
        if (queryDocumentResult != null) {
            Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
            String fileName = pageUrl.substring(pageUrl.lastIndexOf("/") + 1);
            String part = "";
            if (1 == uploadao.getServiceProviderId()) {
                part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + document.getNewPath()
                        + "/" + fileName;
            } else if (2 == uploadao.getServiceProviderId()) {
                part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + document.getNewPath() + "/" + fileName;
            }

            String pathLocalPath = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_" + (_page.getPageNumber()) +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            final String showUrl = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_<" + document.getPageCount() + ">" +
                    pageUrl.substring(pageUrl.lastIndexOf("."));

            Log.e("-", "showUrl:" + showUrl);

            _page.setSavedLocalPath(pathLocalPath);

            Log.e("-", "page:" + _page);
            //保存在本地的地址

            DownloadUtil.get().download(pageUrl, pathLocalPath, new DownloadUtil.OnDownloadListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDownloadSuccess(int arg0) {
                    _page.setShowingPath(showUrl);
                    Log.e("queryAndDownLoadCurrentPageToShow", "onDownloadSuccess:" + page);
                    pageCache.cacheFile(_page);
                    EventBus.getDefault().post(_page);
                }

                @Override
                public void onDownloading(final int progress) {

                }

                @Override
                public void onDownloadFailed() {

                    Log.e("-", "onDownloadFailed:" + _page);
                    if (needRedownload) {
                        queryAndDownLoadPageToShow(document, pageNumber, false);
                    }
                }
            });
        }
    }

    private synchronized void changeDocument(MeetingDocument document, int pageNumber) {
        Log.e("changeDocument", "document:" + document);
        downLoadDocumentPageAndShow(document, pageNumber);
    }

    private synchronized void changeDocument(int itemId, int pageNumber) {
        Log.e("changeDocument", "itemId:" + itemId + ",pageNumber:" + pageNumber);
        if (hasLoadedFile) {
            int index = documents.indexOf(new MeetingDocument(itemId));
            if (index < 0) {
//                DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, itemId, pageNumber);
                return;
            }
            MeetingDocument _document = documents.get(index);
            if (meetingConfig.getDocument().equals(_document)) {
                return;
            }
            changeDocument(_document, pageNumber);

        } else {
            DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, itemId, pageNumber);
        }

    }


    private synchronized void safeDownloadFile(final DocumentPage page, final String localPath, final int pageNumber, final boolean needRedownload) {

        Log.e("safeDownloadFile", "start down load:" + page);
        final String url = meetingConfig.getNotifyUrl();

        page.setSavedLocalPath(localPath);
        final ThreadLocal<DocumentPage> localPage = new ThreadLocal<>();
        localPage.set(page);
//      DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(localPage.get(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {
                localPage.get().setShowingPath(url);
                Log.e("safeDownloadFile", "onDownloadSuccess:" + localPage.get());
                pageCache.cacheFile(localPage.get());
                notifyWebFilePrepared(url, pageNumber);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + localPage.get());
                if (needRedownload) {
                    safeDownloadFile(page, localPath, pageNumber, false);
                }
            }
        });
    }


    private synchronized void safeDownloadFile(final String pathLocalPath, final DocumentPage page, final String notifyUrl, final int index, final boolean needRedownload) {

        Log.e("safeDownloadFile", "start down load:" + page);

        page.setSavedLocalPath(pathLocalPath);
        final ThreadLocal<DocumentPage> localPage = new ThreadLocal<>();
        localPage.set(page);
//      DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(localPage.get(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {
                localPage.get().setShowingPath(notifyUrl);
                Log.e("safeDownloadFile", "onDownloadSuccess:" + localPage.get());
                pageCache.cacheFile(localPage.get());
                notifyWebFilePrepared(notifyUrl, index);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + localPage.get());
                if (needRedownload) {
                    safeDownloadFile(pathLocalPath, page, notifyUrl, index, false);
                }
            }
        });
    }

    private void notifyWebFilePrepared(final String url, final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("WebView_Load", "javascript:AfterDownloadFile('" + url + "', " + index + ")");
                web.load("javascript:AfterDownloadFile('" + url + "', " + index + ")", null);

            }
        });
    }

    public String encoderByMd5(String str) {
        try {
            //确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            //加密后的字符串
            String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
            return newstr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }


    //-----  JavascriptInterface ----
    @org.xwalk.core.JavascriptInterface
    public void afterLoadPageFunction() {
        Log.e("JavascriptInterface", "afterLoadPageFunction");
    }

    @org.xwalk.core.JavascriptInterface
    public void userSettingChangeFunction(final String option) {
        Log.e("JavascriptInterface", "userSettingChangeFunction,option:  " + option);

    }

    @org.xwalk.core.JavascriptInterface
    public synchronized void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        Log.e("JavascriptInterface", "preLoadFileFunction,url:  " + url + ", currentpageNum:" + currentpageNum + ",showLoading:" + showLoading);
        meetingConfig.setNotifyUrl(url);
        if (currentpageNum - 1 < 0) {
            return;
        }

        final DocumentPage page = meetingConfig.getDocument().getDocumentPages().get(currentpageNum - 1);

        final String pathLocalPath = url.substring(0, url.lastIndexOf("<")) +
                currentpageNum + url.substring(url.lastIndexOf("."));

        if (page != null && !TextUtils.isEmpty(page.getPageUrl())) {
            DocumentPage _page = pageCache.getPageCache(page.getPageUrl());
            Log.e("check_cache_page", "_page:" + _page + "，page:" + page);
            if (_page != null && page.getPageUrl().equals(_page.getPageUrl())) {
                if (!TextUtils.isEmpty(_page.getSavedLocalPath())) {
                    File localFile = new File(_page.getSavedLocalPath());
                    if (localFile.exists()) {
                        if (!pathLocalPath.equals(localFile.getAbsolutePath())) {
                            if (localFile.renameTo(new File(pathLocalPath))) {
                                Log.e("preLoadFileFunction", "uncorrect_file_name,rename");
                                notifyWebFilePrepared(url, currentpageNum);
                                return;
                            } else {
                                Log.e("preLoadFileFunction", "uncorrect_file_name,delete");
                                localFile.delete();
                            }
                        } else {
                            Log.e("preLoadFileFunction", "correct_file_name,notify");
                            notifyWebFilePrepared(url, currentpageNum);
                            return;
                        }

                    } else {
                        //清楚缓存
                        pageCache.removeFile(_page.getPageUrl());
                    }

                }
            }
        }

        Log.e("JavascriptInterface", "preLoadFileFunction,page:  " + page);

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                safeDownloadFile(pathLocalPath, page, url, currentpageNum, true);
            }
        }).start(ThreadManager.getManager());

    }


    private boolean hasLoadedFile = false;

    @org.xwalk.core.JavascriptInterface
    public void afterLoadFileFunction() {
        Log.e("JavascriptInterface", "afterLoadFileFunction");
        hasLoadedFile = true;

    }

    @org.xwalk.core.JavascriptInterface
    public void showErrorFunction(final String error) {
        Log.e("JavascriptInterface", "showErrorFunction,error:  " + error);

    }

    @org.xwalk.core.JavascriptInterface
    public void afterChangePageFunction(final int pageNum, int type) {
        Log.e("JavascriptInterface", "afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
        meetingConfig.setPageNumber(pageNum);
        PageActionsAndNotesMgr.requestActionsAndNote(meetingConfig);
    }

    @org.xwalk.core.JavascriptInterface
    public void reflect(String result) {
        Log.e("JavascriptInterface", "reflect,result:  " + result);
        meetingConfig.setDocModifide(checkIfModifyDoc(result));
        notifyMyWebActions(result);
//        DocVedioManager.getInstance(this).prepareVedio(result);
    }

    private boolean checkIfModifyDoc(String result) {
        if (meetingConfig.isDocModifide()) {
            return true;
        }
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject _result = new JSONObject(result);
                if (_result.has("type")) {

                    int type = _result.getInt("type");
                    if (type == 22 || type == 24 || type == 25 || type == 103) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void notifyMyWebActions(String actions) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager == null) {
                messageManager = SocketMessageManager.getManager(this);
                messageManager.registerMessageReceiver();
            }
            messageManager.sendMessage_MyActionFrame(actions, meetingConfig);

        } else {
            Log.e("notifyMyWebActions", "role:" + meetingConfig.getRole());
            if (!AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                return;
            }
            if (messageManager == null) {
                messageManager = SocketMessageManager.getManager(this);
                messageManager.registerMessageReceiver();
            }
            messageManager.sendMessage_MyActionFrame(actions, meetingConfig);
        }
    }

    private void notifyMyNoteWebActions(String actions, Note note) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager == null) {
                messageManager = SocketMessageManager.getManager(this);
                messageManager.registerMessageReceiver();
            }
            messageManager.sendMessage_MyNoteActionFrame(actions, meetingConfig, note);

        } else {
            Log.e("notifyMyWebActions", "role:" + meetingConfig.getRole());
            if (!AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                return;
            }
            if (messageManager == null) {
                messageManager = SocketMessageManager.getManager(this);
                messageManager.registerMessageReceiver();
            }
            messageManager.sendMessage_MyNoteActionFrame(actions, meetingConfig, note);
        }
    }

    private void notifyDocumentChanged() {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager != null) {
                messageManager.sendMessage_DocumentShowed(meetingConfig);
            }
        } else {
            if (!TextUtils.isEmpty(meetingConfig.getPresenterSessionId())) {
                if (AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                    if (meetingConfig.isInRealMeeting()) {
                        if (messageManager != null) {
                            messageManager.sendMessage_DocumentShowed(meetingConfig);
                        }

                    }
                }
            }
        }

    }

    private void notifyViewNote(Note note) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager != null) {
                messageManager.sendMessage_ViewNote(meetingConfig, note);
            }
        } else {
            if (!TextUtils.isEmpty(meetingConfig.getPresenterSessionId())) {
                if (AppConfig.UserToken.equals(meetingConfig.getPresenterSessionId())) {
                    if (meetingConfig.isInRealMeeting()) {
                        if (messageManager != null) {
                            messageManager.sendMessage_ViewNote(meetingConfig, note);
                        }

                    }
                }
            }
        }

    }

    @org.xwalk.core.JavascriptInterface
    public synchronized void autoChangeFileFunction(int diff) {
        Log.e("JavascriptInterface", "autoChangeFileFunction,diff:  " + diff);
        if (documents.size() <= 1) {
            return;
        }
        if (diff == 1) {
            _changeToNextDocument();
        } else if (diff == -1) {
            _changeToPreDocument();
        }
    }

    private void _changeToNextDocument() {
        MeetingDocument document = meetingConfig.getDocument();
        int index = documents.indexOf(document);
        Log.e("check_file_index", "index:" + index + ",documents size:" + documents.size());
        if (index + 1 < documents.size()) {
            document = documents.get(index + 1);
            changeDocument(document, 1);
        }
    }

    private void _changeToPreDocument() {
        MeetingDocument document = meetingConfig.getDocument();
        int index = documents.indexOf(document);
        if (index - 1 < documents.size() && (index - 1 >= 0)) {
            document = documents.get(index - 1);
            changeDocument(document, document.getPageCount());
        }
    }

    // 播放视频
    @org.xwalk.core.JavascriptInterface
    public void videoPlayFunction(final int vid) {
        Log.e("JavascriptInterface", "videoPlayFunction,vid:  " + vid);
//        DocVedioManager.getInstance(this).play(this, vedioLayout, meetingConfig, vid);

    }

    private FavoriteVideoPopup selectVideoDialog;

    //打开
    @org.xwalk.core.JavascriptInterface
    public void videoSelectFunction(String video) {
        Log.e("JavascriptInterface", "videoSelectFunction,id:  " + video);
        if (selectVideoDialog != null) {
            selectVideoDialog.dismiss();
            selectVideoDialog = null;
        }
//        selectVideoDialog = new FavoriteVideoPopup(this);
    }

    // 录制
    @org.xwalk.core.JavascriptInterface
    public void audioSyncFunction(final int id, final int isRecording) {
        Log.e("JavascriptInterface", "audioSyncFunction,id:  " + id + ",isRecording:" + isRecording);

    }

    @org.xwalk.core.JavascriptInterface
    public synchronized void callAppFunction(final String action, final String data) {
        Log.e("JavascriptInterface", "callAppFunction,action:  " + action + ",data:" + data);
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(data)) {
            return;
        }


        Observable.just(data).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                PageActionsAndNotesMgr.handleNoteActions(TvKeyActivity.this, action, new JSONObject(data), meetingConfig);
            }
        }).subscribe();

    }

    // ---- Bottom Menu


    @Override
    public void menuClosedClicked() {
//        handleExit(false);
        finish();
    }

//    ExitDialog exitDialog;
//    private void handleExit(boolean isEnd) {
//        if (exitDialog != null) {
//            if (exitDialog.isShowing()) {
//                exitDialog.dismiss();
//            }
//            exitDialog = null;
//        }
//
//        exitDialog = new ExitDialog(this, meetingConfig);
//        exitDialog.setEndMeeting(isEnd);
//        exitDialog.setDialogClickListener(new ExitDialog.ExitDialogClickListener() {
//            @Override
//            public void onSaveAndLeaveClick() {
//
//                if (exitDialog.isEndMeeting() && meetingConfig.isInRealMeeting()) {
//                    messageManager.sendMessage_EndMeeting(meetingConfig);
//                }
//                if (messageManager != null) {
//                    messageManager.sendMessage_UpdateAttchment(meetingConfig);
//                }
//                PageActionsAndNotesMgr.requestActionsSaved(meetingConfig);
//                finish();
//            }
//
//            @Override
//            public void onLeaveClick() {
//                if (exitDialog.isEndMeeting() && meetingConfig.isInRealMeeting()) {
//                    messageManager.sendMessage_EndMeeting(meetingConfig);
//                }
//                finish();
//            }
//        });
//        exitDialog.show();
//    }

    @Override
    public void menuFileClicked() {
        if (bottomFilePop == null) {
            bottomFilePop = new PopBottomFile(this);
        }
        if (documents != null && documents.size() > 0) {
            bottomFilePop.setDocuments(documents, meetingConfig.getDocument().getItemID(), this);
        }
        if (menuManager != null) {
            menuManager.setMenuIcon(menuIcon);
            menuManager.totalHideMenu();
        }
        menuIcon.setImageResource(R.drawable.shape_transparent);
        menuIcon.setEnabled(false);
        bottomFilePop.show(web, this);
    }


    @Override
    public void menuNoteClicked() {
//        showNotesDialog();
    }


    @Override
    public void menuMeetingMembersClicked() {
        if (meetingConfig.getMeetingMembers() == null || meetingConfig.getMeetingMembers().size() < 0) {
            return;
        }
//        showMembersDialog();
    }


    @Override
    public void menuChatClicked() {
//        showChatPop();
    }

//    private UserSoundtrackDialog soundtrackDialog;
//
//    private void showSoundtrackDialog() {
//        if (soundtrackDialog != null) {
//            if (soundtrackDialog.isShowing()) {
//                soundtrackDialog.dismiss();
//                soundtrackDialog = null;
//            }
//        }
//        soundtrackDialog = new UserSoundtrackDialog(this);
//        soundtrackDialog.show(meetingConfig);
//    }

    //-----
    @Override
    public void addFromTeam() {
        openTeamDocument();

    }

    @Override
    public void addFromCamera() {
        openCameraForAddDoc();
    }

    @Override
    public void addFromPictures() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICTURE_ADD_DOC);
    }

    @Override
    public void addBlankFile() {

        reqeustNewBlankPage();
    }

    AddFileFromDocumentDialog addFileFromDocumentDialog;

    private void openTeamDocument() {

        if (addFileFromDocumentDialog != null) {
            addFileFromDocumentDialog.dismiss();
        }
        addFileFromDocumentDialog = new AddFileFromDocumentDialog(this);
        addFileFromDocumentDialog.setOnSpaceSelectedListener(this);
        addFileFromDocumentDialog.show();

    }

    @Override
    public void onDocSelected(String docId) {
        TeamSpaceInterfaceTools.getinstance().uploadFromSpace(AppConfig.URL_PUBLIC + "EventAttachment/UploadFromSpace?lessonID=" + meetingConfig.getLessionId() + "&itemIDs=" + docId, TeamSpaceInterfaceTools.UPLOADFROMSPACE, new TeamSpaceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                Log.e("add_success", "response:" + object);
                try {
                    JSONObject data = new JSONObject(object.toString());
                    if (data.getInt("RetCode") == 0) {
                        JSONObject document = data.getJSONArray("RetData").getJSONObject(0);
                        if (document != null && document.has("ItemID")) {
                            addDocSucc(document.getInt("ItemID"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    //------

    private AddFileFromFavoriteDialog addFileFromFavoriteDialog;

    @Override
    public void addFromFavorite() {
        if (addFileFromFavoriteDialog != null) {
            if (addFileFromFavoriteDialog.isShowing()) {
                addFileFromFavoriteDialog.dismiss();
            }
            addFileFromFavoriteDialog = null;
        }
        addFileFromFavoriteDialog = new AddFileFromFavoriteDialog(this);
        addFileFromFavoriteDialog.setOnFavoriteDocSelectedListener(this);
        addFileFromFavoriteDialog.show();
    }


    @Override
    public void onFavoriteDocSelected(String docId) {
        TeamSpaceInterfaceTools.getinstance().uploadFromSpace(AppConfig.URL_PUBLIC + "EventAttachment/UploadFromFavorite?lessonID=" +
                        meetingConfig.getLessionId() + "&itemIDs=" + docId, TeamSpaceInterfaceTools.UPLOADFROMSPACE,
                new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        Log.e("add_success", "response:" + object);
                        try {
                            JSONObject data = new JSONObject(object.toString());
                            if (data.getInt("RetCode") == 0) {
                                JSONObject document = data.getJSONArray("RetData").getJSONObject(0);
                                if (document != null && document.has("ItemID")) {
                                    addDocSucc(document.getInt("ItemID"));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    //-----
    @Override
    public void onDocumentClick(MeetingDocument document) {
        changeDocument(document, 1);
    }

    @SuppressLint("WrongConstant")
    private void initRealMeeting() {
        Log.e("DocAndMeetigActivity", "initRealMeeting");
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
        keepScreenWake();
        MeetingKit.getInstance().startMeeting();
        meetingLayout.setVisibility(View.VISIBLE);
        if (messageManager != null && meetingConfig.getRole() == MeetingConfig.MeetingRole.HOST) {
            messageManager.sendMessage_MeetingStatus(meetingConfig);
        }

//        ChatManager.getManager().joinChatRoom(getResources().getString(R.string.Classroom) + meetingConfig.getLessionId());
    }

    @SuppressLint("WrongConstant")
    private void initViews() {
        toggleCameraLayout.setOnClickListener(this);
        cameraList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        cameraList.setDrawingCacheEnabled(true);
        cameraList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        fullCameraList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        fullCameraList.setDrawingCacheEnabled(true);
        fullCameraList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        meetingMenu.setOnClickListener(this);
        backFullCameraImage.setOnClickListener(this);
        closeVedioImage.setOnClickListener(this);
        createBlankPageLayout.setOnClickListener(this);
        inviteLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        _inviteLayout.setOnClickListener(this);
        _shareLayout.setOnClickListener(this);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_toggle_camera:
                meetingConfig.setMembersCameraToggle(!meetingConfig.isMembersCameraToggle());
                toggleMembersCamera(meetingConfig.isMembersCameraToggle());
                break;
            case R.id.meeting_menu:
                if (meetingKit == null) {
                    meetingKit = MeetingKit.getInstance();
                }
                meetingKit.showMeetingMenu(meetingMenu, this, meetingConfig);
                break;
            case R.id.icon_back_full_screen:
                fullCamereLayout.setVisibility(View.GONE);
                fullCameraList.setVisibility(View.GONE);
                cameraList.setVisibility(View.VISIBLE);
                reloadAgoraMember();
                if (shareScreen != null) {
                    showShareScreen(shareScreen);
                }
                break;
            case R.id.image_vedio_close:
//                DocVedioManager.getInstance(this).close();
                break;
            case R.id._layout_invite:
            case R.id.layout_invite:
//                Intent intent = new Intent(this, AddMeetingMemberActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                break;
            case R.id.layout_share:
            case R.id._layout_share:
                break;
            case R.id.layout_create_blank_page:
                reqeustNewBlankPage();
                break;
        }
    }


    @SuppressLint("WrongConstant")
    private void toggleMembersCamera(boolean isToggle) {
        fullCameraList.setVisibility(View.GONE);
        if (cameraList.getVisibility() != View.VISIBLE) {
            cameraList.setVisibility(View.VISIBLE);
            reloadAgoraMember();
            toggleCameraImage.setImageResource(R.drawable.eyeclose);
        } else {
            if (cameraAdapter != null) {
                cameraAdapter.reset();
            }
            cameraList.setVisibility(View.GONE);
            toggleCameraImage.setImageResource(R.drawable.eyeopen);
        }

    }


//    UserNotesDialog notesDialog;
//
//    private void showNotesDialog() {
//        Log.e("showNotesDialog", "meeting_config:" + meetingConfig);
//        if (notesDialog != null) {
//            if (notesDialog.isShowing()) {
//                notesDialog.dismiss();
//                notesDialog = null;
//            }
//        }
//        notesDialog = new UserNotesDialog(this);
//        notesDialog.show(AppConfig.UserID, meetingConfig);
//    }
//
//
//    MeetingMembersDialog meetingMembersDialog;
//
//    private void showMembersDialog() {
//        MeetingKit.getInstance().requestMeetingMembers(meetingConfig);
//        if (meetingMembersDialog != null) {
//            if (meetingMembersDialog.isShowing()) {
//                meetingMembersDialog.dismiss();
//                meetingMembersDialog = null;
//            }
//        }
//        meetingMembersDialog = new MeetingMembersDialog();
//        meetingMembersDialog.init(this, meetingConfig);
//        meetingMembersDialog.show(getSupportFragmentManager());
//    }
//
//
//    PopBottomChat chatBottomPop;
//
//    private void showChatPop() {
//        String chatRoomId = getResources().getString(R.string.Classroom) + meetingConfig.getLessionId();
//        if (chatBottomPop != null) {
//            if (chatBottomPop.isShowing()) {
//                chatBottomPop.hide();
//                chatBottomPop = null;
//            }
//        }
//        chatBottomPop = new PopBottomChat(this);
//        chatBottomPop.show(web, chatRoomId);
//        ChatManager.getManager().setPopBottomChat(chatBottomPop, chatRoomId);
//    }

    //--------
    private boolean isCameraCanUse() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return false;
        } else {
            return true;
        }
    }

    private File cameraFile;

    @SuppressLint("WrongConstant")
    private void openCameraForAddDoc() {
        if (!isCameraCanUse()) {
            Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String mFilePath = FileUtils.getBaseDir();
        // 文件名
        String fileName = "Kloud_" + DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".jpg";
        cameraFile = new File(mFilePath, fileName);
        //Android7.0文件保存方式改变了
        if (Build.VERSION.SDK_INT < 24) {
            Uri uri = Uri.fromFile(cameraFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, REQUEST_CAMEIA_ADD_DOC);
    }

    private static final int REQUEST_CAMEIA_ADD_DOC = 1;
    private static final int REQUEST_PICTURE_ADD_DOC = 2;
    private static final int REQUEST_SCAN = 3;
    private static final int REQUEST_CODE_ADD_NOTE = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMEIA_ADD_DOC:
                    if (cameraFile != null && cameraFile.exists()) {
                        Log.e("onActivityResult", "camera_file:" + cameraFile);
//                        uploadFileWhenAddDoc(cameraFile);

                    }
                    break;
                case REQUEST_PICTURE_ADD_DOC:
                    if (data.getData() != null) {
                        File picture = new File(FileUtils.getPath(this, data.getData()));
                        if (picture != null && picture.exists()) {
//                            uploadFileWhenAddDoc(picture);
                        }
                    }
                    break;
                case REQUEST_SCAN:
                    if (meetingConfig.getType() == MeetingType.MEETING) {
                        MeetingKit.getInstance().restoreLocalVedeo();
                    }
                    break;

            }
        }
    }


    private void reqeustNewBlankPage() {
        Observable.just(meetingConfig).observeOn(Schedulers.io()).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {
                final JSONObject data = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "EventAttachment/AddBlankPage?lessonID=" +
                        meetingConfig.getLessionId(), null);
//                Log.e("blank_page","result:" + jsonObject);
                if (data.getInt("RetCode") == 0) {
                    JSONObject document = data.getJSONObject("RetData");
                    if (document != null && document.has("ItemID")) {
                        addDocSucc(document.getInt("ItemID"));
                    }
                }
            }
        }).subscribe();
    }

    private void addDocSucc(int newItemid) {
        DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, newItemid, 1);
        if (bottomFilePop != null && bottomFilePop.isShowing()) {


        }
    }


//    SetPresenterDialog setPresenterDialog;

    // --set presenter
    @Override
    public void onMemberClicked(MeetingMember meetingMember) {
//        if (setPresenterDialog != null) {
//            setPresenterDialog.dismiss();
//        }
//        setPresenterDialog = new SetPresenterDialog(this);
//        setPresenterDialog.show(meetingMember, this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPresenter(EventSetPresenter setPresenter) {
        messageManager.sendMessage_MakePresenter(meetingConfig, setPresenter.getMeetingMember());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void playSoundtrack(EventPlaySoundtrack soundtrack) {
        Log.e("check_play", "playSoundtrack");
//        showSoundtrackPlayDialog(soundtrack.getSoundtrackDetail());
    }

    @SuppressLint("WrongConstant")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeViewNote(EventCloseNoteView closeNoteView) {
        Log.e("closeViewNote", "closeNoteView");
        currentNoteId = 0;
        newNoteDatas.clear();
        menuIcon.setVisibility(View.VISIBLE);
        hideNoteView();
        notifyDocumentChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void presenterChanged(EventPresnterChanged presnterChanged) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
        Log.e("check_id", "tv_user_id:" + AppConfig.UserID + ",tv_bind_user_id:" + AppConfig.BINDUSERID);
        if (presnterChanged.getPresenterId().equals(AppConfig.UserID)) {
            Log.e("EventPresnterChanged", "presenter is me");
            web.load("javascript:ShowToolbar(" + true + ")", null);
            web.load("javascript:Record()", null);
            noteWeb.load("javascript:ShowToolbar(" + true + ")", null);
            noteWeb.load("javascript:Record()", null);

        } else {
            web.load("javascript:ShowToolbar(" + false + ")", null);
            web.load("javascript:Record()", null);
            noteWeb.load("javascript:ShowToolbar(" + false + ")", null);
            noteWeb.load("javascript:Record()", null);
            Log.e("EventPresnterChanged", "presenter is not me");
        }
    }


//    private SoundtrackPlayDialog soundtrackPlayDialog;
//
//    private void showSoundtrackPlayDialog(SoundtrackDetail soundtrackDetail) {
//        if (soundtrackPlayDialog != null) {
//            if (soundtrackPlayDialog.isShowing()) {
//                soundtrackPlayDialog.dismiss();
//                soundtrackPlayDialog = null;
//            }
//        }
//        soundtrackPlayDialog = new SoundtrackPlayDialog(this, soundtrackDetail, meetingConfig);
//        soundtrackPlayDialog.show();
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        handleExit(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            handleExit(false);
        }
        return true;

    }

    //------Camera vedio options

    @Override
    public void onCameraFrameClick(AgoraMember member) {
        handleFullScreenCamera(cameraAdapter);
    }

    private void handleFullScreenCamera(AgoraCameraAdapter cameraAdapter) {
        if (cameraAdapter == null || cameraAdapter.getUsers().size() == 0) {
            return;
        }
        showFullCameraScreen();
    }

    @SuppressLint("WrongConstant")
    private void showFullCameraScreen() {
        fullCamereLayout.setVisibility(View.VISIBLE);
        fullCameraList.setVisibility(View.VISIBLE);
        cameraList.setVisibility(View.GONE);
        reloadAgoraMember();

    }

    private void fitFullCameraList() {

        int size = meetingConfig.getAgoraMembers().size();

        if (size == 1) {
            fullCameraList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));

        } else if (size > 1 && size <= 4) {

            fullCameraList.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));

        } else if (size > 4 && size <= 6) {

            fullCameraList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

        } else if (size > 6 && size <= 8) {

            fullCameraList.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));

        } else {
            fullCameraList.setLayoutManager(new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false));

        }
        GridLayoutManager s = (GridLayoutManager) fullCameraList.getLayoutManager();
        int currentSpanCount = s.getSpanCount();

        Log.e("fitFullCameraList", "span:" + currentSpanCount);
    }

//    private void fitCameraList() {
//
//        int size = meetingConfig.getAgoraMembers().size();
//
//        if (size > 0 && size <= 8) {
//            cameraList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//
//        } else if (size > 8 && size <= 16) {
//
//            cameraList.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
//
//        } else if (size > 16 ) {
//
//            cameraList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false));
//
//        }
//        GridLayoutManager s = (GridLayoutManager) fullCameraList.getLayoutManager();
//        int currentSpanCount = s.getSpanCount();
//    }

    // ----handle_message

    @SuppressLint("WrongConstant")
    private void handleMessageSendMessage(JSONObject data) throws JSONException {
        if (!data.has("actionType")) {
            return;
        }

        switch (data.getInt("actionType")) {
            case 8:
                if (data.has("docType")) {
                    int docType = data.getInt("docType");
                    switch (docType) {
                        case 1:
                            // 切换笔记
                            followShowNote(data.getInt("itemId"));
                            break;
                        case 2:
                            // 切换文档
                        default:
                            if (noteLayout.getVisibility() == View.VISIBLE) {
                                hideNoteView();
                            }
                            changeDocument(data.getInt("itemId"), (int) (Float.parseFloat(data.getString("pageNumber"))));
                            break;
                    }

                } else {
                    if (noteLayout.getVisibility() == View.VISIBLE) {
//                        noteWeb.load("javascript:ClearPath()", null);
////                        noteWeb.setVisibility(View.GONE);
//                        noteLayout.setVisibility(View.GONE);
                        hideNoteView();
                    }
                    changeDocument(data.getInt("itemId"), Integer.parseInt(data.getString("pageNumber")));
                }
                break;
            case 9:
                Log.e("check_share_screen", "data:" + data + "，uid:" + meetingConfig.getShareScreenUid());
                if (data.has("videoMode")) {
                    String mode = data.getString("videoMode");
                    meetingConfig.setMode(Integer.parseInt(mode));
                    if (meetingConfig.getMode() == 3) {
                        Log.e("check_share_screen", "data:" + data + "，uid:" + meetingConfig.getShareScreenUid() + ",mode:" + meetingConfig.getMode() + ",post_share_screen");
                        MeetingKit.getInstance().postShareScreen(meetingConfig.getShareScreenUid());
                    } else {
                        meetingConfig.setShareScreenUid(0);
                    }
                }
                break;
        }
    }

    private void handleMessageLeaveMeeting(JSONObject data) {
        Log.e("handle_leave_meeting", "data:" + data);
        if (data == null) {
            return;
        }

        if (data.has("retCode")) {
            try {
                if (data.getInt("retCode") == 0) {
                    JSONArray usersList = data.getJSONObject("retData").getJSONArray("usersList");
                    if (usersList != null) {
                        getMeetingMembers(usersList);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        MeetingKit.getInstance().requestMeetingMembers(meetingConfig);

    }

    private void handleMessageAttchmentUploadedAndShow(JSONObject data) {
        Log.e("handle_attchment_upload", "data;" + data);
        String newDocumentId = "";
        try {
            newDocumentId = data.getString("itemId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(newDocumentId)) {
            return;
        }
        final String _id = newDocumentId;
        Observable.just(meetingConfig).observeOn(Schedulers.io()).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {
                DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, Integer.parseInt(_id), 1);
            }
        }).subscribe();
    }

    @SuppressLint("WrongConstant")
    public void handleMessageJoinMeeting(JSONObject data) {
        if (data == null) {
            return;
        }

        if (data.has("retCode")) {
            try {

                if (data.getInt("retCode") == 0) {
                    // 成功收到JOIN_MEETING的返回
                    noNeedLeave = false;
                    JSONObject dataJson = data.getJSONObject("retData");
//                    if (!dataJson.has("CurrentDocumentPage")) {
//                        Toast.makeText(this, "join meeting failed", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    String pageData = dataJson.getString("CurrentDocumentPage");
                    String[] datas = pageData.split("-");
                    meetingConfig.setFileId(Integer.parseInt(datas[0]));
                    float page = Float.parseFloat(datas[1]);
                    meetingConfig.setPageNumber((int) page);
                    meetingConfig.setType(dataJson.getInt("type"));
                    if (dataJson.has("lessonId")) {
                        String lessionId = dataJson.getString("lessonId");
                        if (!TextUtils.isEmpty(lessionId) && !lessionId.equals("0")) {
                            meetingConfig.setLessionId(Integer.parseInt(lessionId));
                        }
                    }
                    if (dataJson.has("currentMode")) {
                        meetingConfig.setMode(dataJson.getInt("currentMode"));
                    }

                    if (documents == null || documents.size() <= 0) {
                        requestDocumentsAndShowPage();
                    }else {
                        requestDocuments();
                    }

                    if (dataJson.has("noteId") && dataJson.getInt("noteId") > 0) {
                        if(dataJson.has("noteUserId")&& !TextUtils.isEmpty(data.getString("noteUserId"))){
                            followShowNote(dataJson.getInt("noteId"));
                        }
                    }


                    if (meetingConfig.getType() == MeetingType.DOC) {
                        meetingLayout.setVisibility(View.GONE);
                    } else if (meetingConfig.getType() == MeetingType.MEETING) {
                        if (dataJson.has("presenterSessionId")) {
                            meetingConfig.setPresenterSessionId(dataJson.getString("presenterSessionId"));
                        }

                        if (dataJson.has("usersList")) {
                            JSONArray users = dataJson.getJSONArray("usersList");
                            if (users.length() >= 0) {
                                getMeetingMembers(users);
                            }
                        }

                        MeetingKit.getInstance().requestMeetingMembers(meetingConfig);
                        if (meetingConfig.isInRealMeeting()) {
                            return;
                        }
                        //
                        initRealMeeting();
                    }
//                    Log.e("MeetingConfig","MeetingConfig:" + meetingConfig);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessageAgoraStatusChange(JSONObject data) {
        Log.e("agora_status_change", "data:" + data);
        if (data.has("retData")) {
            try {
                JSONObject _data = data.getJSONObject("retData");
                if (_data.has("usersList")) {
                    JSONArray userList = _data.getJSONArray("usersList");

                    for (int i = 0; i < userList.length(); ++i) {
                        JSONObject user = userList.getJSONObject(i);
                        Log.e("check_list", "user:" + user);
                        int index = meetingConfig.getAgoraMembers().indexOf(new AgoraMember(Integer.parseInt(user.getString("userId"))));
                        if (index >= 0) {
                            AgoraMember member = meetingConfig.getAgoraMembers().get(index);
                            member.setUserName(user.getString("userName"));
                            member.setMuteAudio(!(user.getInt("microphoneStatus") == 2));
                            member.setMuteVideo(!(user.getInt("cameraStatus") == 2));
                        } else {
                            AgoraMember agoraMember = new AgoraMember();
                            agoraMember.setUserId(Integer.parseInt(user.getString("userId")));
                            agoraMember.setUserName(user.getString("userName"));
                            agoraMember.setIconUrl(user.getString("avatarUrl"));
                            agoraMember.setMuteAudio(!(user.getInt("microphoneStatus") == 2));
                            agoraMember.setMuteVideo(!(user.getInt("cameraStatus") == 2));
                            meetingConfig.getAgoraMembers().add(agoraMember);
                        }

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void handleMeetingDefaultDocument() {
        if (meetingConfig.getRole() != MeetingConfig.MeetingRole.HOST) {
            roleMemberLayout.setVisibility(View.VISIBLE);
            roleHostLayout.setVisibility(View.GONE);
            roleText.setText(R.string.shAcan);
        } else {
            roleMemberLayout.setVisibility(View.GONE);
            roleHostLayout.setVisibility(View.VISIBLE);
            roleText.setText(R.string.shYcan);
        }
    }

    public class NoteJavascriptInterface {
        @org.xwalk.core.JavascriptInterface
        public void afterChangePageFunction(final int pageNum, int type) {
//            Log.e("JavascriptInterface", "note_afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
            NoteViewManager.getInstance().getNotePageActionsToShow(meetingConfig);
        }

        @org.xwalk.core.JavascriptInterface
        public void reflect(String result) {
            Log.e("JavascriptInterface", "reflect,result:  " + result);
            Note note = NoteViewManager.getInstance().getNote();
            if (note != null) {
                notifyMyNoteWebActions(result, note);
            }
        }

        public void afterLoadPageFunction() {

        }


    }

    private PowerManager.WakeLock wakeLock;

    @SuppressLint("WrongConstant")
    private void keepScreenWake() {
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "TEST");
        wakeLock.acquire();
    }

    //------ message
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventMessageDisableTvFollow(EventDisableTvFollow disableTvFollow) {
        finish();
    }

    private boolean noNeedLeave = false;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventMessageTvJoin(EventTvJoin tvJoin) {
        noNeedLeave = true;
        String newMeetingId = "";
        try {
            JSONObject jsonObject = new JSONObject(tvJoin.getMessage());

            JSONObject data = jsonObject.getJSONObject("retData");
            if (data.has("meetingId")) {
                newMeetingId = data.getString("meetingId");
                if (!meetingConfig.getMeetingId().equals(newMeetingId)) {
                    meetingConfig.setMeetingId(newMeetingId);
                    if (data.has("type")) {
                        meetingConfig.setType(data.getInt("type"));
                    }
                    refreshMeeting();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refreshMeeting() {

        if (meetingConfig.getType() == MeetingType.MEETING) {
            MeetingKit.getInstance().prepareJoin(this, meetingConfig);
        } else {
            SocketMessageManager.getManager(this).sendMessage_JoinMeeting(meetingConfig);
        }
    }

    private void addLinkBorderForDTNew(JSONObject p1Created) throws JSONException {
        if (p1Created.has("noteId")) {
            if (currentNoteId == p1Created.getInt("noteId")) {
//                noteWeb.load("javascript:whiteboard");
                JSONArray positionArray = new JSONArray(p1Created.getString("position"));
                Log.e("addLinkBorderForDTNew", "positionArray:" + positionArray);
                JSONObject info = new JSONObject();
                info.put("ProjectID", p1Created.getInt("projectId"));
                info.put("TaskID", p1Created.getInt("itemId"));
                for (int i = 0; i < positionArray.length(); ++i) {
                    JSONObject position = positionArray.getJSONObject(i);
                    doDrawDTNewBorder(position.getInt("left"), position.getInt("top"), position.getInt("width"), position.getInt("height"), info);
                }
            }
        }
    }

    private void doDrawDTNewBorder(int x, int y, int width, int height, JSONObject info) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("type", 40);
        message.put("CW", 678);
        message.put("x", x);
        message.put("y", y);
        message.put("width", width);
        message.put("height", height);
        message.put("info", info);

        JSONObject clearLastMessage = new JSONObject();
        clearLastMessage.put("type", 40);

        Log.e("doDrawDTNewBorder", "border_PlayActionByTxt:" + message);
        noteWeb.load("javascript:PlayActionByTxt('" + message + "')", null);
    }

    private void hideNoteView() {
        Observable.just("hide_note_view").observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

                noteWeb.load("javascript:ClearPath()", null);
                View childView = (ViewGroup) noteWeb.getChildAt(0);
                while (childView instanceof ViewGroup) {
                    childView = ((ViewGroup) childView).getChildAt(0);
                }

                if (childView instanceof SurfaceView) {
                    SurfaceView surfaceView = (SurfaceView) childView;
                    surfaceView.setZOrderMediaOverlay(false);
                    Log.e("note_view", "setZOrderMediaOverlay,false");
                }
                noteWeb.setZOrderOnTop(false);
                noteWeb.setVisibility(View.GONE);
                noteLayout.setVisibility(View.GONE);

            }
        }).subscribe();


    }

    private void showNoteView() {
        Observable.just("show_note").observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                noteLayout.setVisibility(View.VISIBLE);
                noteWeb.setVisibility(View.VISIBLE);
                View childView = (ViewGroup) noteWeb.getChildAt(0);
                while (childView instanceof ViewGroup) {
                    childView = ((ViewGroup) childView).getChildAt(0);
                }
                if (childView instanceof SurfaceView) {
                    SurfaceView surfaceView = (SurfaceView) childView;
                    surfaceView.setZOrderMediaOverlay(true);
                    Log.e("note_view", "setZOrderMediaOverlay,true");
                }
                Log.e("note_view", "childView:" + childView);
            }
        }).subscribe();

    }

    private void handleMessageHeartBeat(JSONObject message) throws JSONException {
        Log.e("followAccordingHeartBeat", "message:" + message);
        if (message.has("tvOwnerMeetingId")) {
            if (TextUtils.isEmpty(message.getString("tvOwnerMeetingId"))) {
                //finish();
            } else {

                if (message.has("currentItemId")) {
                    if (documents.isEmpty() || TextUtil.isEmpty(meetingConfig.getDocumentId())) {
                        return;
                    }
                    int currentItemId = message.getInt("currentItemId");
                    int page = message.getInt("currentPageNumber");
                    if (page == 0) {
                        return;
                    }
                    if (!(currentItemId + "").equals(meetingConfig.getDocumentId())) {
                        // 如果在不同的文档，切换到相同的问题
                        changeDocument(currentItemId, page);
                    } else {
                        // 在同一个文档，不在同一页
                        if (meetingConfig.getPageNumber() != message.getInt("currentPageNumber")) {
                            changeDocument(meetingConfig.getDocument(), message.getInt("currentPageNumber"));
                        }
                    }
                }

                if (message.has("noteId")) {

                    if (message.getInt("noteId") <= 0 || TextUtils.isEmpty(message.getString("noteUserId"))) {
                        if (noteLayout.getVisibility() == View.VISIBLE) {
                            hideNoteView();
                        }
                    }

                    if (message.getInt("noteId") > 0 && !TextUtils.isEmpty(message.getString("noteUserId"))) {
                        if (noteLayout.getVisibility() != View.VISIBLE) {
                            showNoteView();
                        }
                    }
                }
            }
        }
    }

    private void requestNotFollow() {
        getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE).edit().putString("tv_bind_user", "").commit();
        AppConfig.BINDUSERID = "";

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                        AppConfig.meetingServer + "/tv/logout",
                        new JSONObject());
                finish();

            }
        }).start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }
        if (keyCode == KeyEvent.KEYCODE_MENU&&mIsSettingDialogCancel) {//dialog消失
            if(menuManager!=null){
                menuManager.handleMenuClicked();
            }
            return false;
        }
        if(!mIsSettingDialogCancel){//点击遥控器按键作用于dialog 0 1 2 3分别代表上下左右
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_UP:
                    MeetingKit.getInstance().remoteDirectionDown(0);
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    MeetingKit.getInstance().remoteDirectionDown(1);
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    MeetingKit.getInstance().remoteDirectionDown(2);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    MeetingKit.getInstance().remoteDirectionDown(3);
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    MeetingKit.getInstance().remoteEnterDown();
                    break;
            }
        }else {//点击遥控器按键作用于menu
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_UP:
                    if(menuManager!=null)
                        menuManager.remoteUPOrDown(true);//向上
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(menuManager!=null)
                        menuManager.remoteUPOrDown(false);//向下
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if(menuManager!=null)
                        menuManager.remoteEnter();
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
