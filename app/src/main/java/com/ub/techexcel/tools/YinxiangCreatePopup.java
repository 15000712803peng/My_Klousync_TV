package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ub.techexcel.bean.SoundtrackBean;
import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.info.Favorite;
import com.kloudsync.techexcel2.service.ConnectService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wang on 2017/9/18.
 */

public class YinxiangCreatePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView close;
    private TextView addaudio, addrecord;
    private CheckBox checkBox1, checkBox2;
    private EditText edittext;

    private TextView recordsync, cancel;
    private Favorite favorite = new Favorite();
    private Favorite recordfavorite = new Favorite();
    private CheckBox checkBox;
    private String attachmentId;
    private static FavoritePoPListener mFavoritePoPListener;
    private TextView recordname, recordtime;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1001:
                    mFavoritePoPListener.syncorrecord(checkBox.isChecked(), soundtrackBean);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public interface FavoritePoPListener {

        void dismiss();

        void open();

        void addrecord(int isrecord);

        void addaudio(int isrecord);

        void syncorrecord(boolean checked, SoundtrackBean soundtrackBean);
    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }


    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.yinxiang_create_popup, null);
        close = (ImageView) view.findViewById(R.id.close);
        cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        addaudio = (TextView) view.findViewById(R.id.addaudio);
        addrecord = (TextView) view.findViewById(R.id.addrecord);
        recordname = (TextView) view.findViewById(R.id.recordname);
        recordtime = (TextView) view.findViewById(R.id.recordtime);
        edittext = (EditText) view.findViewById(R.id.edittext);
        String time = new SimpleDateFormat("yyyyMMdd_hh:mm").format(new Date());
        edittext.setText(AppConfig.UserName + "_" + time);
        checkBox = (CheckBox) view.findViewById(R.id.checkboxx);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                haha();
            }
        });

        recordsync = (TextView) view.findViewById(R.id.recordsync);
        recordsync.setOnClickListener(this);
        close.setOnClickListener(this);
        addaudio.setOnClickListener(this);
        addrecord.setOnClickListener(this);

        checkBox1 = (CheckBox) view.findViewById(R.id.checkbox1);
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addaudio.setAlpha(1.0f);
                    addaudio.setEnabled(true);
                } else {
                    addaudio.setAlpha(0.5f);
                    addaudio.setEnabled(false);
                }
                haha();
            }
        });
        addaudio.setAlpha(0.5f);
        addaudio.setEnabled(false);

        checkBox2 = (CheckBox) view.findViewById(R.id.checkbox2);
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addrecord.setAlpha(1.0f);
                    addrecord.setEnabled(true);
                    checkBox.setAlpha(1.0f);
                    checkBox.setEnabled(true);
                } else {
                    addrecord.setAlpha(0.5f);
                    addrecord.setEnabled(false);
                    checkBox.setAlpha(0.5f);
                    checkBox.setEnabled(false);

                    recordname.setVisibility(View.GONE);
                    recordtime.setVisibility(View.GONE);
                    checkBox.setChecked(false);
                    recordfavorite = new Favorite();
                }
                haha();
            }
        });

        addrecord.setEnabled(true);

        checkBox2.setChecked(true);
        checkBox.setChecked(true);

        recordsync.setText("Record & Sync");

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFavoritePoPListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
    }


    private void haha() {
        if ((!checkBox1.isChecked() && !checkBox2.isChecked()) ||
                (!checkBox1.isChecked() && checkBox2.isChecked() && recordfavorite.getAttachmentID() == 0 && !checkBox.isChecked()) ||
                (checkBox1.isChecked() && !checkBox2.isChecked() && favorite.getAttachmentID() == 0) ||
                (checkBox1.isChecked() && checkBox2.isChecked() && favorite.getAttachmentID() == 0 && recordfavorite.getAttachmentID() == 0 && !checkBox.isChecked())) {
            recordsync.setText("Start");
            recordsync.setAlpha(0.5f);
            recordsync.setEnabled(false);
        } else {
            recordsync.setAlpha(1.0f);
            recordsync.setEnabled(true);
            if (checkBox2.isChecked() && checkBox.isChecked()) {
                recordsync.setText("Record & Sync");
            } else {
                recordsync.setText("Sync");
            }
        }
    }

    public void setAudioBean(Favorite favorite) {
        this.favorite = favorite;
        addaudio.setText(favorite.getTitle());
        haha();
    }

    public void setRecordBean(Favorite favorite) {
        this.recordfavorite = favorite;
        if (recordfavorite != null) {
            recordname.setVisibility(View.VISIBLE);
            recordtime.setVisibility(View.VISIBLE);
            recordname.setText(recordfavorite.getTitle());
        }
        haha();

    }


    @SuppressLint("NewApi")
    public void StartPop(View v, String attachmentId) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            this.attachmentId = attachmentId;
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    private SoundtrackBean soundtrackBean = new SoundtrackBean();

    private void createSoundtrack() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("AttachmentID", Integer.parseInt(attachmentId));

                    if (recordfavorite == null || !checkBox2.isChecked()) {
                        recordfavorite = new Favorite();
                        recordfavorite.setAttachmentID(0);
                    }
                    jsonObject.put("SelectedAudioAttachmentID", recordfavorite.getAttachmentID());

                    if (favorite == null || !checkBox1.isChecked()) {
                        favorite = new Favorite();
                        favorite.setAttachmentID(0);
                    }

                    jsonObject.put("BackgroudMusicAttachmentID", favorite.getAttachmentID());

                    jsonObject.put("Title", edittext.getText().toString());
                    jsonObject.put("EnableBackgroud", checkBox1.isChecked() ? 1 : 0);
                    jsonObject.put("EnableSelectVoice", checkBox2.isChecked() ? 1 : 0);
                    jsonObject.put("EnableRecordNewVoice", checkBox.isChecked() ? 1 : 0);
                    jsonObject.put("SelectedAudioTitle", recordfavorite.getAttachmentID() == 0 ? "" : recordfavorite.getTitle());
                    jsonObject.put("BackgroudMusicTitle", favorite.getAttachmentID() == 0 ? "" : favorite.getTitle());
                    JSONObject returnjson = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Soundtrack/CreateSoundtrack", jsonObject);
                    Log.e("hhh", jsonObject.toString() + "      " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject jsonObject1 = returnjson.getJSONObject("RetData");
                        soundtrackBean = new SoundtrackBean();
                        soundtrackBean.setSoundtrackID(jsonObject1.getInt("SoundtrackID"));
                        soundtrackBean.setTitle(jsonObject1.getString("Title"));
                        soundtrackBean.setUserID(jsonObject1.getString("UserID"));
                        soundtrackBean.setUserName(jsonObject1.getString("UserName"));
                        soundtrackBean.setAvatarUrl(jsonObject1.getString("AvatarUrl"));
                        soundtrackBean.setDuration(jsonObject1.getString("Duration"));
                        soundtrackBean.setCreatedDate(jsonObject1.getString("CreatedDate"));

                        soundtrackBean.setBackgroudMusicAttachmentID(jsonObject1.getInt("BackgroudMusicAttachmentID"));
                        soundtrackBean.setNewAudioAttachmentID(jsonObject1.getInt("NewAudioAttachmentID"));
                        soundtrackBean.setSelectedAudioAttachmentID(jsonObject1.getInt("SelectedAudioAttachmentID"));

                        if (soundtrackBean.getBackgroudMusicAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("BackgroudMusicInfo");
                                Favorite favoriteAudio = new Favorite();
                                favoriteAudio.setFileDownloadURL(jsonObject2.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject2.getInt("ItemID"));
                                favoriteAudio.setTitle(jsonObject2.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject2.getInt("AttachmentID"));
                                favoriteAudio.setDuration(jsonObject2.getString("VideoDuration"));
                                soundtrackBean.setBackgroudMusicInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean.setBackgroudMusicInfo(new Favorite());
                                e.printStackTrace();
                            }
                        }
                        if (soundtrackBean.getSelectedAudioAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject3 = jsonObject1.getJSONObject("SelectedAudioInfo");
                                Favorite favoriteAudio = new Favorite();
                                favoriteAudio.setFileDownloadURL(jsonObject3.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject3.getInt("ItemID"));
                                favoriteAudio.setTitle(jsonObject3.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject3.getInt("AttachmentID"));
                                favoriteAudio.setDuration(jsonObject3.getString("VideoDuration"));
                                soundtrackBean.setSelectedAudioInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean.setSelectedAudioInfo(new Favorite());
                                e.printStackTrace();
                            }
                        }

                        Message msg3 = Message.obtain();
                        msg3.obj = soundtrackBean;
                        msg3.what = 0x1001;
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;
            case R.id.addaudio:
                mFavoritePoPListener.addaudio(0);
                break;
            case R.id.addrecord:
                mFavoritePoPListener.addrecord(1);
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.recordsync:
                dismiss();
                createSoundtrack();

                break;
            default:
                break;
        }
    }

}
