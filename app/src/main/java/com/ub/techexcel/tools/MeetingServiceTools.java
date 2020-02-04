package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kloudsync.techexcel2.bean.DocumentPage;
import com.kloudsync.techexcel2.bean.EventNote;
import com.kloudsync.techexcel2.bean.EventNotePageActions;
import com.kloudsync.techexcel2.bean.EventPageActions;
import com.kloudsync.techexcel2.bean.EventPageNotes;
import com.kloudsync.techexcel2.bean.MeetingConfig;
import com.kloudsync.techexcel2.bean.MeetingType;
import com.kloudsync.techexcel2.bean.Note;
import com.kloudsync.techexcel2.bean.NoteDetail;
import com.kloudsync.techexcel2.config.AppConfig;
import com.ub.techexcel.bean.LineItem;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MeetingServiceTools {

    public static final int ERRORMESSAGE = 0x1105;
    public static final int GETPDFLIST = 0x1101;
    public static final int GETGETPAGEOBJECTS = 0x1102;
    public static final int UPLOADFILEWITHHASH = 0x1104;
    public static final int GETTOPICATTACHMENT = 0x1106;

    private ConcurrentHashMap<Integer, ServiceInterfaceListener> hashMap = new ConcurrentHashMap<>();

    private static MeetingServiceTools meetingServiceTools;

    public static MeetingServiceTools getInstance() {
        if (meetingServiceTools == null) {
            synchronized (MeetingServiceTools.class) {
                if (meetingServiceTools == null) {
                    meetingServiceTools = new MeetingServiceTools();
                }
            }
        }
        return meetingServiceTools;
    }

    private void putInterface(int code, ServiceInterfaceListener serviceInterfaceListener) {
        ServiceInterfaceListener serviceInterfaceListener2 = hashMap.get(code);
        if (serviceInterfaceListener2 == null) {
            hashMap.put(code, serviceInterfaceListener);
        }

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

    public void getSyncroomDetail(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnJson.getJSONObject("RetData").getJSONArray("FileList");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setTopicId(lineitem.getInt("TopicID"));
                            item.setSyncRoomCount(lineitem.getInt("SyncCount"));
                            item.setFileName(lineitem.getString("Title"));
                            item.setUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setHtml5(false);
                            item.setItemId(lineitem.getString("ItemID"));
                            item.setAttachmentID(lineitem.getString("AttachmentID"));
                            item.setCreatedDate(lineitem.getString("CreatedDate"));
                            String attachmentUrl = lineitem.getString("AttachmentUrl");
                            if (!TextUtils.isEmpty(attachmentUrl)) {
                                String newPath = attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/"));
                                item.setNewPath(newPath);
                            }
                            item.setFlag(0);
                            if (lineitem.getInt("Status") == 0) {
                                items.add(item);
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void getPdfList(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url+returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONObject service = returnJson.getJSONObject("RetData");
                        JSONArray lineitems = service.getJSONArray("AttachmentList");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setFileName(lineitem.getString("Title"));
                            item.setUrl(lineitem.getString("AttachmentUrl"));
                            item.setHtml5(false);
                            item.setItemId(lineitem.getString("ItemID"));
                            item.setAttachmentID(lineitem.getString("AttachmentID"));
                            item.setNewPath(lineitem.getString("NewPath"));
                            item.setFlag(0);
                            if (lineitem.getInt("Status") == 0) {
                                items.add(item);
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getTopicAttachment(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url + returnJson.toString());
                try {
                    if (returnJson.getInt("RetCode") == 0) {
                        JSONArray lineitems = returnJson.getJSONArray("RetData");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setTopicId(lineitem.getInt("TopicID"));
                            item.setSyncRoomCount(lineitem.getInt("SyncCount"));
                            item.setFileName(lineitem.getString("Title"));
                            item.setUrl(lineitem.getString("AttachmentUrl"));
                            item.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                            item.setHtml5(false);
                            item.setItemId(lineitem.getString("ItemID"));
                            item.setAttachmentID(lineitem.getString("AttachmentID"));
                            item.setCreatedDate(lineitem.getString("CreatedDate"));
                            String attachmentUrl = lineitem.getString("AttachmentUrl");
                            if (!TextUtils.isEmpty(attachmentUrl)) {
                                String newPath = attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/"));

                                item.setNewPath(newPath);
                            }

                            Log.e("LineItem","url:" + item.getUrl() + ",----new path:" + item.getNewPath());
                            item.setFlag(0);
                            if (lineitem.getInt("Status") == 0) {
                                items.add(item);
                            }
                        }
                        Message msg = Message.obtain();
                        msg.obj = items;
                        msg.what = code;
                        handler.sendMessage(msg);
                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getGetPageObjects(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener){
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
                Log.e("meetingservicrtools", url+"   "+returnJson.toString());
                try {

                    if (returnJson.getInt("RetCode") == 0) {
                        JSONArray data = returnJson.getJSONArray("RetData");
                        String mmm = "";
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject1 = data.getJSONObject(i);
                            String ddd = jsonObject1.getString("Data");
                            if (!TextUtil.isEmpty(ddd)) {
                                String dd = "'" + Tools.getFromBase64(ddd) + "'";
                                if (i == 0) {
                                    mmm += "[" + dd;
                                } else {
                                    mmm += "," + dd;
                                }
                                if (i == data.length() - 1) {
                                    mmm += "]";
                                }
                            }
                        }


                        Message msg = Message.obtain();
                        msg.what = code;
                        msg.obj = mmm;
                        handler.sendMessage(msg);

                    } else {
                        Message msg3 = Message.obtain();
                        msg3.what = ERRORMESSAGE;
                        msg3.obj = returnJson.getString("ErrorMessage");
                        handler.sendMessage(msg3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }



    public void uploadFileWithHash(final String url, final int code, ServiceInterfaceListener serviceInterfaceListener) {
        putInterface(code, serviceInterfaceListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject returnJson = com.ub.techexcel.service.ConnectService.submitDataByJson(url, null);
                Log.e("meetingservicrtools",url+ returnJson.toString());
                Message msg = Message.obtain();
                msg.obj = returnJson;
                msg.what = code;
                handler.sendMessage(msg);
            }
        }).start();

    }

    public EventPageActions syncGetPageActions(MeetingConfig config) {
        String url = "";
        switch (config.getType()) {
            case MeetingType.DOC:
                url = "https://api.peertime.cn/peertime/V1/PageObject/GetPageObjects?lessonID=0&itemID=" + 0 + "&pageNumber=" + config.getPageNumber() +
                        "&attachmentID=" + config.getDocument().getAttachmentID() + "&soundtrackID=0&displayDrawingLine=0";
                break;
            case MeetingType.MEETING:
                url = AppConfig.URL_PUBLIC + "PageObject/GetPageObjects?lessonID=" + config.getLessionId() + "&itemID=" +
                        config.getDocument().getItemID() + "&pageNumber=" + config.getPageNumber();
                break;
            case MeetingType.SYNCBOOK:
                break;
            case MeetingType.SYNCROOM:
                break;
            default:
        }


        JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetPageActions", url + "   " + returnJson.toString());
        EventPageActions pageActions = new EventPageActions();
        pageActions.setPageNumber(config.getPageNumber());
        try {
            if (returnJson.getInt("RetCode") == 0) {
                JSONArray data = returnJson.getJSONArray("RetData");
                String dataJson = "";
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject1 = data.getJSONObject(i);
                    String _data = jsonObject1.getString("Data");
                    if (!TextUtil.isEmpty(_data)) {
                        String dd = "'" + Tools.getFromBase64(_data) + "'";
                        if (i == 0) {
                            dataJson += "[" + dd;
                        } else {
                            dataJson += "," + dd;
                        }
                        if (i == data.length() - 1) {
                            dataJson += "]";
                        }
                    }
                }

                pageActions.setData(dataJson);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageActions;

    }

    public EventPageNotes syncGetPageNotes(MeetingConfig meetingConfig) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + 0 + "&documentItemID=" + meetingConfig.getDocument().getAttachmentID() +
                "&pageNumber=" + meetingConfig.getPageNumber() + "&userID=" + AppConfig.UserID;
        JSONObject returnJson = com.kloudsync.techexcel2.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetPageNotes", url + "   " + returnJson.toString());
        EventPageNotes pageNotes = new EventPageNotes();
        pageNotes.setPageNumber(meetingConfig.getPageNumber());
        try {
            if (returnJson.getInt("RetCode") == 0) {
                JSONArray _notes = returnJson.getJSONArray("RetData");
                List<NoteDetail> notes = new ArrayList<NoteDetail>();
                for (int j = 0; j < _notes.length(); j++) {
                    JSONObject note = _notes.getJSONObject(j);
                    notes.add(new Gson().fromJson(note.toString(), NoteDetail.class));
                }
                pageNotes.setNotes(notes);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return pageNotes;
    }

    public EventNotePageActions syncGetPageActions(MeetingConfig config, Note note) {
        String url = "";
        switch (config.getType()) {
            case MeetingType.DOC:
                url = "https://api.peertime.cn/peertime/V1/PageObject/GetPageObjects?lessonID=0&itemID=" + 0 + "&pageNumber=" + config.getPageNumber() +
                        "&attachmentID=" + note.getAttachmentID() + "&soundtrackID=0&displayDrawingLine=0";
                break;
            case MeetingType.MEETING:
                url = AppConfig.URL_PUBLIC + "PageObject/GetPageObjects?lessonID=" + config.getLessionId() + "&itemID=" +
                        note.getNoteID() + "&pageNumber=" + 1;
                break;
            case MeetingType.SYNCBOOK:
                break;
            case MeetingType.SYNCROOM:
                break;
            default:
        }


        JSONObject returnJson = com.ub.techexcel.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetPageActions", url + "   " + returnJson.toString());
        EventNotePageActions pageActions = new EventNotePageActions();
        pageActions.setPageNumber(config.getPageNumber());
        try {
            if (returnJson.getInt("RetCode") == 0) {
                JSONArray data = returnJson.getJSONArray("RetData");
                String dataJson = "";
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject1 = data.getJSONObject(i);
                    String _data = jsonObject1.getString("Data");
                    if (!TextUtil.isEmpty(_data)) {
                        String dd = "'" + Tools.getFromBase64(_data) + "'";
                        if (i == 0) {
                            dataJson += "[" + dd;
                        } else {
                            dataJson += "," + dd;
                        }
                        if (i == data.length() - 1) {
                            dataJson += "]";
                        }
                    }
                }

                pageActions.setData(dataJson);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageActions;

    }

    public EventNote syncGetNoteByLinkId(int linkId) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/NoteByLinkID?linkID=" + linkId;
        JSONObject returnjson = com.kloudsync.techexcel2.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("getNoteByLinkID", url + "  " + returnjson.toString());
        EventNote eventNote = new EventNote();
        eventNote.setLinkId(linkId);
        try {
            if (returnjson.getInt("RetCode") == 0) {
                JSONObject lineitem = returnjson.getJSONObject("RetData");
                Note note = new Note();
                String attachmentUrl = lineitem.getString("AttachmentUrl");
                note.setLocalFileID(lineitem.getString("LocalFileID"));
                note.setNoteID(lineitem.getInt("NoteID"));
                note.setLinkID(lineitem.getInt("LinkID"));
                note.setPageNumber(lineitem.getInt("PageNumber"));
                note.setDocumentItemID(lineitem.getInt("DocumentItemID"));
                note.setFileName(lineitem.getString("Title"));
                note.setAttachmentUrl(attachmentUrl);
                note.setPageCount(lineitem.getInt("PageCount"));
                note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                note.setAttachmentID(lineitem.getInt("AttachmentID"));
                String noteUrl = attachmentUrl.substring(0, attachmentUrl.lastIndexOf("<")) + 1 + attachmentUrl.substring(attachmentUrl.lastIndexOf("."));
                note.setUrl(noteUrl);

                String preUrl = "";
                String endUrl = "";
                if (!TextUtils.isEmpty(attachmentUrl)) {
                    int index = attachmentUrl.lastIndexOf("<");
                    int index2 = attachmentUrl.lastIndexOf(">");
                    if (index > 0) {
                        preUrl = attachmentUrl.substring(0, index);
                    }
                    if (index2 > 0) {
                        endUrl = attachmentUrl.substring(index2 + 1, attachmentUrl.length());
                    }
                }

                note.setNewPath(attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/")));

                List<DocumentPage> pages = new ArrayList<>();
                for (int j = 0; j < note.getPageCount(); ++j) {
                    String pageUrl = "";
                    DocumentPage page = new DocumentPage();
                    page.setLocalFileId(note.getLocalFileID());
                    page.setPageNumber(j + 1);
                    page.setDocumentId(note.getDocumentItemID());
                    if (TextUtils.isEmpty(preUrl)) {
                        page.setPageUrl(pageUrl);
                    } else {
                        page.setPageUrl(preUrl + (j + 1) + endUrl);
                    }
                    pages.add(page);
                }
                note.setDocumentPages(pages);
                eventNote.setNote(note);
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventNote;
    }


    public EventNote syncGetNoteByNoteId(int noteId) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/Item?noteID=" + noteId;
        JSONObject returnjson = com.kloudsync.techexcel2.service.ConnectService.getIncidentbyHttpGet(url);
        Log.e("syncGetNoteByNoteId", url + "  " + returnjson.toString());
        EventNote eventNote = new EventNote();
        eventNote.setNoteId(noteId);
        try {
            if (returnjson.getInt("RetCode") == 0) {
                JSONObject lineitem = returnjson.getJSONObject("RetData");
                Note note = new Note();
                String attachmentUrl = lineitem.getString("AttachmentUrl");
                note.setLocalFileID(lineitem.getString("LocalFileID"));
                note.setNoteID(lineitem.getInt("NoteID"));
//                note.setLinkID(lineitem.getInt("LinkID"));

                note.setDocumentItemID(lineitem.getInt("AttachmentFileID"));
                note.setFileName(lineitem.getString("Title"));
                note.setAttachmentUrl(attachmentUrl);
                note.setPageCount(lineitem.getInt("PageCount"));
                note.setSourceFileUrl(lineitem.getString("SourceFileUrl"));
                note.setAttachmentID(lineitem.getInt("AttachmentID"));
                String noteUrl = attachmentUrl.substring(0, attachmentUrl.lastIndexOf("<")) + 1 + attachmentUrl.substring(attachmentUrl.lastIndexOf("."));
                note.setUrl(noteUrl);

                String preUrl = "";
                String endUrl = "";
                if (!TextUtils.isEmpty(attachmentUrl)) {
                    int index = attachmentUrl.lastIndexOf("<");
                    int index2 = attachmentUrl.lastIndexOf(">");
                    if (index > 0) {
                        preUrl = attachmentUrl.substring(0, index);
                    }
                    if (index2 > 0) {
                        endUrl = attachmentUrl.substring(index2 + 1, attachmentUrl.length());
                    }
                }

                note.setNewPath(attachmentUrl.substring(attachmentUrl.indexOf(".com") + 5, attachmentUrl.lastIndexOf("/")));

                List<DocumentPage> pages = new ArrayList<>();
                for (int j = 0; j < note.getPageCount(); ++j) {
                    String pageUrl = "";
                    DocumentPage page = new DocumentPage();
                    page.setLocalFileId(note.getLocalFileID());
                    page.setPageNumber(j + 1);
                    page.setDocumentId(note.getDocumentItemID());
                    if (TextUtils.isEmpty(preUrl)) {
                        page.setPageUrl(pageUrl);
                    } else {
                        page.setPageUrl(preUrl + (j + 1) + endUrl);
                    }
                    pages.add(page);
                }
                note.setDocumentPages(pages);
                eventNote.setNote(note);
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventNote;
    }



}
