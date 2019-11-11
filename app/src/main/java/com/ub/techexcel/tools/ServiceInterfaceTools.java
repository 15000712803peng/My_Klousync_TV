package com.ub.techexcel.tools;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kloudsync.techexcel2.bean.NoteDetail;
import com.kloudsync.techexcel2.bean.SyncBook;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.help.ApiTask;
import com.kloudsync.techexcel2.info.ConvertingResult;
import com.kloudsync.techexcel2.info.Customer;
import com.kloudsync.techexcel2.info.Favorite;
import com.kloudsync.techexcel2.info.Uploadao;
import com.kloudsync.techexcel2.resp.NetworkResponse;
import com.kloudsync.techexcel2.resp.TeamsResponse;
import com.ub.service.activity.ThreadManager;
import com.ub.techexcel.bean.AudioActionBean;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.bean.PageActionBean;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.service.ConnectService;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceInterfaceTools {

    public static final int GETSOUNDITEM = 0x1101;
    public static final int GETSOUNDLIST = 0x1102;
    public static final int DELETESOUNDLIST = 0x1103;
    public static final int ADDSOUNDTOLESSON = 0x1104;
    public static final int ERRORMESSAGE = 0x1105;
    public static final int SHARESOUNDTOLESSON = 0x1106;
    public static final int CREATESOUNDTOLESSON = 0x1107;
    public static final int GETPAGEACTIONS = 0x1108;
    public static final int GETPAGEACTIONSTARTTIME = 0x1109;
    public static final int GETVIEWALLDOCUMENTS = 0x1110;
    public static final int QUERYCONVERTING = 0x0001;
    public static final int STARTCONVERTING = 0x1112;
    public static final int UPLOADNEWFILE = 0x1113;
    public static final int PREPAREDOWNLOADING = 0x1114;
    public static final int FINISHCONVERING = 0x1115;
    public static final int QUERYDOWNLOADING = 0x1116;
    public static final int QUERYDOCUMENT = 0x1117;
    public static final int NOTIFYUPLOADED = 0x1118;
    public static final int GETSOUNDTRACKACTIONS = 0x1119;
    public static final int TV_NOT_FOLLOW = 0x1120;
    public static final int GETNOTELIST = 0x1139;
    public static final int GETNOTEBYLOCALFILEID = 0x1140;
    public static final int GETNOTEBYNOTEID2 = 0x1141;
    public static final int GETSYNCROOMUSERLIST = 0x1142;
    public static final int IMPORTNOTE = 0x1143;
    public static final int GETNOTELISTV2 = 0x1144;
    public static final int GETNOTELISTV3 = 0x1145;
    public static final int REMOVENOTE = 0x1146;
    public static final int GETNOTEBYLINKID = 0x1147;

    private ConcurrentHashMap<Integer, ServiceInterfaceListener> hashMap = new ConcurrentHashMap<>();

    private static ServiceInterfaceTools serviceInterfaceTools;

    private ServiceInterfaceTools() {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(AppConfig.URL_PUBLIC)
                .build();
        request = retrofit.create(TempleteCourse_interface.class);
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int code = msg.what;
            if (code == ERRORMESSAGE) {
            } else {
                ServiceInterfaceListener serviceInterfaceListener = hashMap.get(code);
                if (serviceInterfaceListener != null) {
                    serviceInterfaceListener.getServiceReturnData(msg.obj);
                    hashMap.remove(code);
                }
            }
        }
    };


    public static ServiceInterfaceTools getinstance() {
        if (serviceInterfaceTools == null) {
            synchronized (ServiceInterfaceTools.class) {
                if (serviceInterfaceTools == null) {
                    serviceInterfaceTools = new ServiceInterfaceTools();
                }
            }
        }
        return serviceInterfaceTools;
    }

    public void getPageActions(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {

                        PageActionBean pageActionBean = new PageActionBean();
                        JSONObject retdata = returnjson.getJSONObject("RetData");
                        pageActionBean.setPageNumber(retdata.getString("PageNumber"));
                        JSONArray jsonArray = retdata.getJSONArray("Actions");
                        String mmm = "";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String ddd = jsonObject.getString("Data");
                            if (!TextUtil.isEmpty(ddd)) {
                                String dd = "'" + Tools.getFromBase64(ddd) + "'";
                                if (i == 0) {
                                    mmm += "[" + dd;
                                } else {
                                    mmm += "," + dd;
                                }
                                if (i == jsonArray.length() - 1) {
                                    mmm += "]";
                                }
                            }
                        }
                        pageActionBean.setActions(mmm);

                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = pageActionBean;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public void getSoundItem(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("hhh", url + "  获取音响item    " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject retdata = returnjson.getJSONObject("RetData");
                        SoundtrackBean soundtrackBean1 = new SoundtrackBean();
                        soundtrackBean1.setSoundtrackID(retdata.getInt("SoundtrackID"));
                        soundtrackBean1.setTitle(retdata.getString("Title"));
                        soundtrackBean1.setAttachmentId(retdata.getString("AttachmentID"));
                        soundtrackBean1.setCreatedDate(retdata.getString("CreatedDate"));
                        soundtrackBean1.setBackgroudMusicAttachmentID(retdata.getInt("BackgroudMusicAttachmentID"));
                        soundtrackBean1.setNewAudioAttachmentID(retdata.getInt("NewAudioAttachmentID"));
                        soundtrackBean1.setSelectedAudioAttachmentID(retdata.getInt("SelectedAudioAttachmentID"));

                        if (soundtrackBean1.getBackgroudMusicAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject = retdata.getJSONObject("BackgroudMusicInfo");
                                Favorite favoriteAudio = new Favorite();
                                favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject.getInt("ItemID"));
                                favoriteAudio.setTitle(jsonObject.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID"));
                                favoriteAudio.setDuration(jsonObject.getString("VideoDuration"));
                                soundtrackBean1.setBackgroudMusicInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean1.setBackgroudMusicInfo(new Favorite());
                                e.printStackTrace();
                            }
                        }
                        if (soundtrackBean1.getNewAudioAttachmentID() != 0) {
                            try {
                                if (!retdata.isNull("NewAudioInfo")) {

                                }
                                JSONObject jsonObject = retdata.getJSONObject("NewAudioInfo");
                                Favorite favoriteAudio = new Favorite();
                                favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject.getInt("ItemID"));
                                favoriteAudio.setTitle(jsonObject.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID"));
                                favoriteAudio.setDuration(jsonObject.getString("VideoDuration"));
                                soundtrackBean1.setNewAudioInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean1.setNewAudioInfo(new Favorite());
                                e.printStackTrace();
                            }
                        }
                        if (soundtrackBean1.getSelectedAudioAttachmentID() != 0) {
                            try {
                                JSONObject jsonObject = retdata.getJSONObject("SelectedAudioInfo");
                                Favorite favoriteAudio = new Favorite();
                                favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                                favoriteAudio.setItemID(jsonObject.getInt("ItemID"));
                                favoriteAudio.setTitle(jsonObject.getString("Title"));
                                favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID"));
                                favoriteAudio.setDuration(jsonObject.getString("VideoDuration"));
                                soundtrackBean1.setSelectedAudioInfo(favoriteAudio);
                            } catch (Exception e) {
                                soundtrackBean1.setSelectedAudioInfo(new Favorite());
                                e.printStackTrace();
                            }
                        }
                        soundtrackBean1.setBackgroudMusicTitle(retdata.getString("BackgroudMusicTitle"));

                        soundtrackBean1.setDuration(retdata.getString("Duration"));

                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = soundtrackBean1;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void getSoundList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener, final boolean isHidden, final boolean ishavepresenter) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        final JSONArray array = returnjson.getJSONArray("RetData");
                        List<SoundtrackBean> mlist = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            SoundtrackBean soundtrackBean = new SoundtrackBean();
                            soundtrackBean.setSoundtrackID(jsonObject.getInt("SoundtrackID"));
                            soundtrackBean.setTitle(jsonObject.getString("Title"));
                            soundtrackBean.setUserID(jsonObject.getString("UserID"));
                            soundtrackBean.setUserName(jsonObject.getString("UserName"));
                            soundtrackBean.setAvatarUrl(jsonObject.getString("AvatarUrl"));
                            soundtrackBean.setDuration(jsonObject.getString("Duration"));
                            soundtrackBean.setCreatedDate(jsonObject.getString("CreatedDate"));
                            soundtrackBean.setHidden(isHidden);
                            soundtrackBean.setHavePresenter(ishavepresenter);
                            mlist.add(soundtrackBean);
                        }
                        Message msg3 = Message.obtain();
                        msg3.obj = mlist;
                        msg3.what = code;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void getSoundtrackActions(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject1 = ConnectService.getIncidentbyHttpGet(url);
                Log.e("hhh", url +   jsonObject1.toString());
                try {
                    if (jsonObject1.getInt("RetCode") == 0) {
                        JSONObject retdata = jsonObject1.getJSONObject("RetData");
                        JSONArray jsonArray = retdata.getJSONArray("SoundtackActions");
                        List<AudioActionBean> audioActionBeanList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject audiojson = jsonArray.getJSONObject(i);
                            AudioActionBean audioActionBean = new AudioActionBean();
                            audioActionBean.setTime(audiojson.getInt("Time"));
                            String action = audiojson.getString("Data").replaceAll("\"", "");
                            String msg2 = Tools.getFromBase64(action);
                            audioActionBean.setData(msg2);
                            audioActionBeanList.add(audioActionBean);
                        }
                        Message msg3 = Message.obtain();
                        msg3.obj = audioActionBeanList;
                        msg3.what = code;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = jsonObject1.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void deleteSound(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentDataattachment(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void addSoundToLesson(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, null);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void shareDocument(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, null);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getString("RetData");
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    public void getPageActionStartTime(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentData(url);
                    Log.e("hhh", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {

                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getInt("RetData");
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private Retrofit retrofit;
    private TempleteCourse_interface request;


    private void putInterface(int code, ServiceInterfaceListener serviceInterfaceListener) {
        ServiceInterfaceListener serviceInterfaceListener2 = hashMap.get(code);
        if (serviceInterfaceListener2 == null) {
//            hashMap.remove(code);
            hashMap.put(code, serviceInterfaceListener);
        }

    }


    //创建音响
    public void createYinxiang(final String url, final int code, final String attachmentId, final String recordingId, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    if (TextUtils.isEmpty(attachmentId)) {
                        jsonObject.put("AttachmentID", 0);
                    } else {
                        jsonObject.put("AttachmentID", Integer.parseInt(attachmentId));
                    }
                    String time = new SimpleDateFormat("yyyyMMdd_hh:mm").format(new Date());
                    jsonObject.put("Title", AppConfig.UserName + "_" + time);
                    jsonObject.put("EnableBackgroud", 1);
                    jsonObject.put("EnableSelectVoice", 1);
                    jsonObject.put("EnableRecordNewVoice", 1);
                    jsonObject.put("Type", 1);
                    jsonObject.put("RecordingID", recordingId);

                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);

                    Log.e("Agora", jsonObject.toString() + "      " + returnjson.toString());

                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject jsonObject1 = returnjson.getJSONObject("RetData");
                        SoundtrackBean soundtrackBean = new SoundtrackBean();
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
                        msg3.what = code;
                        msg3.obj = soundtrackBean;
                        handler.sendMessage(msg3);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 查询转换进度
     *
     * @param url
     * @param code
     * @param uploadao
     */
    public void queryConverting(final String url, final int code, final Uploadao uploadao, final String key, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Key", key);
                    JSONObject buckjson = new JSONObject();
                    buckjson.put("ServiceProviderId", uploadao.getServiceProviderId());
                    buckjson.put("RegionName", uploadao.getRegionName());
                    buckjson.put("BucketName", uploadao.getBucketName());
                    jsonObject.put("Bucket", buckjson);
                    JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
                    Log.e("hhh", "   " + jsonObject.toString() + "      " + returnjson.toString());
                    if (returnjson.getBoolean("Success")) {
                        JSONObject data = returnjson.getJSONObject("Data");
                        ConvertingResult convertingResult = new ConvertingResult();
                        convertingResult.setCurrentStatus(data.getInt("CurrentStatus"));
                        convertingResult.setFinishPercent(data.getInt("FinishPercent"));
                        if (convertingResult.getCurrentStatus() == 5) {
                            JSONObject result = data.getJSONObject("Result");
                            convertingResult.setUrl(result.getString("Url"));
                            convertingResult.setFolderKey(result.getString("FolderKey"));
                            convertingResult.setCount(result.getInt("Count"));
                            convertingResult.setFileName(result.getString("FileName"));
                        }
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = convertingResult;
                        handler.sendMessage(msg3);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * @param url
     * @param code
     * @param uploadao
     */
    public void startConverting(final String url, final int code, final Uploadao uploadao, final String key, final String Title, final String targetFolderKey, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Key", key);
                    jsonObject.put("DocumentType", Title.substring(Title.lastIndexOf(".") + 1, Title.length()));
                    jsonObject.put("TargetFolderKey", targetFolderKey);
                    JSONObject buckjson = new JSONObject();
                    buckjson.put("ServiceProviderId", uploadao.getServiceProviderId());
                    buckjson.put("RegionName", uploadao.getRegionName());
                    buckjson.put("BucketName", uploadao.getBucketName());
                    jsonObject.put("Bucket", buckjson);
                    JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
                    Log.e("hhh", jsonObject.toString() + "      " + returnjson.toString() + "  " );
                    if (returnjson.getBoolean("Success")) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getBoolean("Success");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * @param url
     * @param code
     * @param uploadao
     */
    public void finishConvering(final String url, final int code, final Uploadao uploadao, final String key, final String targetFolderKey, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("SourceKey", key);
                    jsonObject.put("TargetFolderKey", targetFolderKey);
                    jsonObject.put("Callback", "");
                    JSONObject buckjson = new JSONObject();
                    buckjson.put("ServiceProviderId", uploadao.getServiceProviderId());
                    buckjson.put("RegionName", uploadao.getRegionName());
                    buckjson.put("BucketName", uploadao.getBucketName());
                    jsonObject.put("Bucket", buckjson);
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhh", url + "    " + jsonObject.toString() + "      " + returnjson.toString() + "  ");
                    if (returnjson.getBoolean("Success")) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = returnjson.getBoolean("Success");
                        handler.sendMessage(msg3);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }



    /**
     * 根据lessonid  获取 笔记的列表
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getNoteList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteList", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnjson.getJSONArray("RetData");
                        List<Note> items = new ArrayList<Note>();
                        for (int j = 0; j < lineitems.length(); j++) {

                            JSONObject lineitem = lineitems.getJSONObject(j);
                            Note item = new Note();
                            item.setLinkID(lineitem.getInt("LinkID"));
                            item.setNoteID(lineitem.getInt("NoteID"));
                            item.setLocalFileID(lineitem.getString("LocalFileID"));
                            item.setFileName(lineitem.getString("Title"));
                            item.setFileID(lineitem.getInt("FileID"));
                            item.setFileName(lineitem.getString("FileName"));
                            item.setAttachmentUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setAttachmentID(lineitem.getInt("AttachmentID"));
                            items.add(item);

                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    public void getNoteListV2(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("TwinkleBookNote", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnjson.getJSONArray("RetData");
                        List<NoteDetail> items = new ArrayList<NoteDetail>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            items.add(new Gson().fromJson(lineitem.toString(), NoteDetail.class));
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    public void getNoteListV3(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("TwinkleBookNote", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnjson.getJSONArray("RetData");
                        List<NoteDetail> items = new ArrayList<NoteDetail>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            items.add(new Gson().fromJson(lineitem.toString(), NoteDetail.class));
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 根据localFileID获取Note
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getNoteByLocalFileId(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteList note", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject lineitem = returnjson.getJSONObject("RetData");
                        Note note = new Note();
                        note.setLocalFileID(lineitem.getString("LocalFileID"));
                        note.setPageNumber(lineitem.getInt("PageNumber"));
                        note.setDocumentItemID(lineitem.getInt("DocumentItemID"));
                        note.setFileName(lineitem.getString("Title"));
                        note.setAttachmentUrl(lineitem.getString("AttachmentUrl"));
                        note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                        note.setAttachmentID(lineitem.getInt("AttachmentID"));
                        Message msg = Message.obtain();
                        msg.obj = note;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    /**
     * 根据NoteId获取Note
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getNoteByNoteId(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteList", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject lineitem = returnjson.getJSONObject("RetData");
                        Note note = new Note();
                        note.setLocalFileID(lineitem.getString("LocalFileID"));
                        note.setFileName(lineitem.getString("Title"));
                        note.setAttachmentUrl(lineitem.getString("AttachmentUrl"));
                        note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                        note.setAttachmentID(lineitem.getInt("AttachmentID"));
                        note.setNoteID(lineitem.getInt("NoteID"));
                        Message msg = Message.obtain();
                        msg.obj = note;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 获取SyncRoom中的用户的Note数量
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getSyncRoomUserList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteList note count ", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray userArray = returnjson.getJSONArray("RetData");
                        List<Customer> list = new ArrayList<>();
                        for (int i = 0; i < userArray.length(); i++) {
                            JSONObject userJson = userArray.getJSONObject(i);
                            Customer customer = new Customer();
                            customer.setUserID(userJson.getInt("UserID") + "");
                            customer.setName(userJson.getString("UserName"));
                            customer.setNoteCount(userJson.getInt("NoteCount"));
                            list.add(customer);
                        }
                        Message msg = Message.obtain();
                        msg.obj = list;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 获取用户的Note列表
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getUserNoteList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteList", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnjson.getJSONArray("RetData");
                        List<Note> items = new ArrayList<Note>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            Note item = new Note();
                            item.setNoteID(lineitem.getInt("NoteID"));
                            item.setLocalFileID(lineitem.getString("LocalFileID"));
                            item.setFileName(lineitem.getString("Title"));
                            item.setFileID(lineitem.getInt("FileID"));
                            item.setFileName(lineitem.getString("FileName"));
                            item.setAttachmentUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setAttachmentID(lineitem.getInt("AttachmentID"));
                            item.setCreatedDate(lineitem.getString("CreatedDate"));
                            items.add(item);
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    /**
     * 导入 Note
     */
    public void importNote(final String url, final int code, String syncroomid,
                           String documentItemID, String pagenumber, int noteid, String linkproperty,
                           ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("SyncRoomID", syncroomid);
            jsonObject.put("DocumentItemID", documentItemID);
            jsonObject.put("PageNumber", pagenumber);
            jsonObject.put("NoteID", noteid);
            jsonObject.put("LinkProperty", linkproperty);
            new ApiTask(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("importNote", url + "    " + jsonObject.toString() + "     " + returnjson.toString());
                    try {
                        int retCode = returnjson.getInt("RetCode");
                        if (retCode == 0) {
                            int linkid = returnjson.getInt("RetData");
                            Message msg3 = Message.obtain();
                            msg3.what = code;
                            msg3.obj = linkid;
                            handler.sendMessage(msg3);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).start(ThreadManager.getManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据LinkID获取Note信息
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void getNoteByLinkID(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(url);
                    Log.e("getNoteByLinkID", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        JSONObject lineitem = returnjson.getJSONObject("RetData");
                        Note note = new Note();
                        note.setLocalFileID(lineitem.getString("LocalFileID"));
                        note.setNoteID(lineitem.getInt("NoteID"));
                        note.setLinkID(lineitem.getInt("LinkID"));
                        note.setPageNumber(lineitem.getInt("PageNumber"));
                        note.setDocumentItemID(lineitem.getInt("DocumentItemID"));
                        note.setFileName(lineitem.getString("Title"));
                        note.setAttachmentUrl(lineitem.getString("AttachmentUrl"));
                        note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                        note.setAttachmentID(lineitem.getInt("AttachmentID"));
                        Message msg = Message.obtain();
                        msg.obj = note;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    /**
     * 根据LinkID删除Note信息
     *
     * @param url
     * @param code
     * @param serviceInterfaceListener
     */
    public void removeNote(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentDataattachment(url);
                    Log.e("removeNote", url + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg = Message.obtain();
                        msg.obj = 0;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnjson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }









    /**
     * @param url
     * @param code
     * @param uploadao
     */
    public void uploadNewFile(final String url, final int code, final String fileName, final Uploadao uploadao, final String lessonId,
                              final String key, final ConvertingResult convertingResult, final boolean isAddToFavorite, final int fieldId, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("LessonID", lessonId);
                    jsonObject.put("Title", fileName);
                    jsonObject.put("Hash", key);
                    jsonObject.put("IsAddToFavorite", isAddToFavorite ? 1 : 0);
                    jsonObject.put("SchoolID", AppConfig.SchoolID);
                    jsonObject.put("FileID", fieldId);
                    jsonObject.put("PageCount", convertingResult.getCount());
                    JSONObject buckjson = new JSONObject();
                    buckjson.put("ServiceProviderId", uploadao.getServiceProviderId());
                    buckjson.put("RegionName", uploadao.getRegionName());
                    buckjson.put("BucketName", uploadao.getBucketName());
                    jsonObject.put("Bucket", buckjson);
                    jsonObject.put("SourceKey", convertingResult.getFolderKey());
                    jsonObject.put("FileName", convertingResult.getFileName());
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhh", jsonObject.toString() + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        Message msg3 = Message.obtain();
                        msg3.what = code;
                        msg3.obj = "";
                        handler.sendMessage(msg3);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void queryDownloading(final String url, final int code, final Uploadao uploadao, final String newPath, ServiceInterfaceListener serviceInterfaceListener) {

        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            JSONObject keyJson = new JSONObject();
            keyJson.put("Option", 1);
            keyJson.put("Key", newPath);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(keyJson);
            jsonObject.put("Keys", jsonArray);
            JSONObject bucketJson = new JSONObject();
            bucketJson.put("ServiceProviderId", uploadao.getServiceProviderId());
            bucketJson.put("RegionName", uploadao.getRegionName());
            bucketJson.put("BucketName", uploadao.getBucketName());
            jsonObject.put("Bucket", bucketJson);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
                    Log.e("hhhhh", url + "     " + returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void queryDocument(final String url, final int code, final String newPath, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        try {
            final JSONObject jsonObject = new JSONObject();
            JSONObject keyJson = new JSONObject();
            keyJson.put("Option", 1);
            keyJson.put("Key", newPath);
            jsonObject.put("Key", keyJson);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
                    Log.e("hhhhh", url + "     " +jsonObject  + "   "+returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject syncQueryDocument(final String url, final String newPath) {
        try {
            final JSONObject jsonObject = new JSONObject();
            JSONObject keyJson = new JSONObject();
            keyJson.put("Option", 1);
            keyJson.put("Key", newPath);
            jsonObject.put("Key", keyJson);
            JSONObject returnjson = ConnectService.submitDataByJsonLive(url, jsonObject);
            return returnjson;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void tvNotFollow(final String url,final int code){
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("status","0");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject returnjson = ConnectService.submitDataByJson(url, jsonObject);
                    Log.e("hhhhh", url + "     " +jsonObject  + "   "+returnjson.toString());
                    Message msg3 = Message.obtain();
                    msg3.what = code;
                    msg3.obj = returnjson.toString();
                    handler.sendMessage(msg3);
                }
            }).start();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getNoteDetailByLinkId(final String url, final String linkId, final OnJsonResponseReceiver jsonResponseReceiver) {

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("linkID", linkId);
                    JSONObject returnjson = ConnectService.getIncidentData(url + "?linkId=" + linkId);
                    Log.e("getNoteDetailByLinkId", url + jsonObject.toString() + "  " + returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        if (jsonResponseReceiver != null) {
                            jsonResponseReceiver.jsonResponse(returnjson);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }


    public interface OnJsonResponseReceiver {
        void jsonResponse(JSONObject jsonResponse);
    }

    public Call<TeamsResponse> getCompanyTeams(String companyID) {
        return request.getCompanyTeams(AppConfig.SACN_USER_TOKEN, 1, companyID);
    }

    public Call<NetworkResponse<SyncBook>> getSyncbookOutline(String syncroomId){
        return request.getSyncbookOutline(AppConfig.UserToken,syncroomId);
    }


}
