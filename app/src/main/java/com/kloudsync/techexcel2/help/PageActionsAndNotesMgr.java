package com.kloudsync.techexcel2.help;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.kloudsync.techexcel2.bean.EventNote;
import com.kloudsync.techexcel2.bean.EventNotePageActions;
import com.kloudsync.techexcel2.bean.EventOpenNote;
import com.kloudsync.techexcel2.bean.EventPageActions;
import com.kloudsync.techexcel2.bean.EventPageNotes;
import com.kloudsync.techexcel2.bean.EventSelectNote;
import com.kloudsync.techexcel2.bean.MeetingConfig;
import com.kloudsync.techexcel2.bean.Note;
import com.kloudsync.techexcel2.bean.NoteDetail;
import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.service.ConnectService;
import com.kloudsync.techexcel2.tool.NoteImageCache;
import com.ub.service.activity.ThreadManager;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tonyan on 2019/11/28.
 */

public class PageActionsAndNotesMgr {

    public static void requestActionsAndNote(MeetingConfig config) {
        Observable.just(config).observeOn(Schedulers.io()).map(new Function<MeetingConfig, EventPageActions>() {
            @Override
            public EventPageActions apply(MeetingConfig config) throws Exception {
                return MeetingServiceTools.getInstance().syncGetPageActions(config);
            }
        }).doOnNext(new Consumer<EventPageActions>() {
            @Override
            public void accept(EventPageActions eventPageActions) throws Exception {
                EventBus.getDefault().post(eventPageActions);
            }
        }).subscribe();

        Observable.just(config).observeOn(Schedulers.io()).map(new Function<MeetingConfig, EventPageNotes>() {
            @Override
            public EventPageNotes apply(MeetingConfig config) throws Exception {
                return MeetingServiceTools.getInstance().syncGetPageNotes(config);
            }
        }).doOnNext(new Consumer<EventPageNotes>() {
            @Override
            public void accept(EventPageNotes eventPageNotes) throws Exception {
                EventBus.getDefault().post(eventPageNotes);
            }
        }).subscribe();

    }

    public static void requestActionsForNotePage(MeetingConfig config, final Note note) {
        Observable.just(config).observeOn(Schedulers.io()).map(new Function<MeetingConfig, EventNotePageActions>() {
            @Override
            public EventNotePageActions apply(MeetingConfig config) throws Exception {
                return MeetingServiceTools.getInstance().syncGetPageActions(config, note);
            }
        }).doOnNext(new Consumer<EventNotePageActions>() {
            @Override
            public void accept(EventNotePageActions eventPageActions) throws Exception {
                EventBus.getDefault().post(eventPageActions);
            }
        }).subscribe();


    }

    public static void requestActionsSaved(final MeetingConfig config) {

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC +
                        "Lesson/SaveInstantLesson?lessonID=" + config.getLessionId(), null);
                Log.e("save_changed", "jsonObject:" + jsonObject);

            }
        }).start(ThreadManager.getManager());
    }




    public static void handleNoteActions(Context context, String action, JSONObject data, MeetingConfig meetingConfig) throws JSONException {
        switch (action) {
            case "BookNoteSelect":
                if(data.has("LinkID")){
                    final int linkId = data.getInt("LinkID");
                    final JSONObject linkProperty = data.getJSONObject("LinkProperty");
                    EventSelectNote selectNote = new EventSelectNote();
                    selectNote.setLinkId(linkId);
                    selectNote.setLinkProperty(linkProperty);
                }
                break;
            case "BookNoteView":
                if (data.has("LinkID")) {
                    int linkId = data.getInt("LinkID");
                    if(linkId <=0){
                        return;
                    }
                    getNoteDetail(linkId);
                }

                break;
            case "BookNoteMove":
                break;
            case "BookNoteDelete":
                break;
            case "BookNoteEdit":
                if (data.has("LinkID")) {
                    try {
                        int linkId = data.getInt("LinkID");
                        if(linkId < 0){
                            return;
                        }
                        meetingConfig.setCurrentLinkProperty(data.getJSONObject("LinkProperty"));
                        final EventOpenNote eventOpenNote = new EventOpenNote();
                        if (linkId == 0) {
                            EventBus.getDefault().post(eventOpenNote);
                        } else {
                            Observable.just(linkId).observeOn(Schedulers.io()).map(new Function<Integer, EventOpenNote>() {
                                @Override
                                public EventOpenNote apply(Integer _linkId) throws Exception {
                                    JSONObject result = ServiceInterfaceTools.getinstance().syncGetSimpleNoteInfoByLinkId(_linkId + "");
                                    if (result.has("RetCode")) {
                                        if (result.getInt("RetCode") == 0) {
                                            JSONObject _noteData = result.getJSONObject("RetData");
                                            eventOpenNote.setNoteId(_noteData.getString("LocalFileID"));
                                            EventBus.getDefault().post(eventOpenNote);
                                        }
                                    }
                                    return eventOpenNote;
                                }
                            }).subscribe();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    public static synchronized void getNoteDetail(int linkId) {

        Observable.just(linkId).observeOn(Schedulers.io()).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer _linkId) throws Exception {
                EventBus.getDefault().post(MeetingServiceTools.getInstance().syncGetNoteByLinkId(_linkId));
            }
        }).subscribe();
    }

    public static synchronized void getNoteDetail(Context context, final NoteDetail note) {

        final NoteImageCache noteCache = NoteImageCache.getInstance(context);
        EventNote eventNote = new EventNote();
        eventNote.setLinkId(note.getLinkID());
        Observable.just(eventNote).observeOn(Schedulers.io()).map(new Function<EventNote, EventNote>() {
            @Override
            public EventNote apply(EventNote _note) throws Exception {
                _note.setNote(parseNote(note));
                return _note;
            }

        }).doOnNext(new Consumer<EventNote>() {
            @Override
            public void accept(EventNote eventNote) throws Exception {
                Note note = eventNote.getNote();
                Log.e("check_note", "one_event_note:" + eventNote);
                if (note != null && !TextUtils.isEmpty(note.getUrl())) {
//                    Log.e("getNoteDetail","note url:" + note.getAttachmentUrl());
                    if (noteCache.containFile(note.getUrl())) {
                        if (new File(noteCache.getNoteImage(note.getUrl())).exists()) {
                            note.setLocalFilePath(noteCache.getNoteImage(note.getUrl()));
                            Log.e("check_note", "post note:" + note);
                            EventBus.getDefault().post(eventNote);
                            return;
                        } else {
                            noteCache.removeNoteImage(note.getUrl());
                        }
                    }
                }
            }
        }).doOnNext(new Consumer<EventNote>() {
            @Override
            public void accept(EventNote eventNote) throws Exception {
                Log.e("check_note", "two_event_note:" + eventNote);
                Note note = eventNote.getNote();
                if (note != null && !TextUtils.isEmpty(note.getLocalFilePath())) {
                    return;
                }
                note.setLocalFilePath(FileUtils.getBaseDir() + note.getUrl().substring(note.getUrl().lastIndexOf("/")));
                safeDownloadNote(eventNote, noteCache, true);

            }
        }).subscribe();
    }


    private synchronized static void safeDownloadNote(final EventNote note, final NoteImageCache imageCache, final boolean needRedownload) {


        final ThreadLocal<EventNote> localNote = new ThreadLocal<>();
        localNote.set(note);
//      DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(localNote.get(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {
                localNote.get().getNote().setLocalFilePath(note.getNote().getLocalFilePath());
                Log.e("safeDownloadFile", "onDownloadSuccess:" + localNote.get());
                imageCache.cacheNoteImage(localNote.get().getNote().getUrl(), localNote.get().getNote().getLocalFilePath());
                EventBus.getDefault().post(note);

            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + localNote.get());
                if (needRedownload) {
                    safeDownloadNote(note, imageCache, false);
                }
            }
        });
    }

    public static Note parseNote(NoteDetail noteDetail) {
        Note note = new Note();
        note.setAttachmentUrl(noteDetail.getAttachmentUrl());
        note.setLocalFileID(noteDetail.getLocalFileID());
        note.setNoteID(noteDetail.getNoteID());
        note.setLinkID(noteDetail.getLinkID());
        note.setPageNumber(noteDetail.getPageNumber());
        note.setDocumentItemID(noteDetail.getDocumentItemID());
        note.setFileName(noteDetail.getTitle());
        note.setSourceFileUrl(noteDetail.getSourceFileUrl());
        note.setAttachmentID(noteDetail.getAttachmentID());
        note.setUrl(noteDetail.getUrl());
        return note;
    }

}
