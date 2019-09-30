package com.kloudsync.techexcel2.help;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.adapter.FavouriteAdapter;
import com.kloudsync.techexcel2.dialog.Document;
import com.kloudsync.techexcel2.dialog.message.SendFileMessage;
import com.kloudsync.techexcel2.info.Favorite;
import com.kloudsync.techexcel2.start.LoginGet;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class DialogSendFile {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private RecyclerView rv_pc;
    private Context mContext;
    Conversation.ConversationType conversationType;
    String targetId;
    private ArrayList<Favorite> mlist = new ArrayList<Favorite>();
    private FavouriteAdapter fAdapter;

    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void DialogDismiss();
    }

    public void setPoPDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogSendFile.dialogdismissListener = dialogdismissListener;
    }

    public void EditCancel(Context context, Conversation.ConversationType conversationType,
                           String targetId) {
        this.mContext = context;
        this.conversationType = conversationType;
        this.targetId = targetId;

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        window.setContentView(R.layout.sendfile);

        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
        layoutParams.width = width * 5 / 7;
        layoutParams.height = height * 2 / 3;
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        rv_pc = (RecyclerView) window.findViewById(R.id.rv_pc);

        GetData();

    }

    private void GetData() {

        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rv_pc.setLayoutManager(manager);
        fAdapter = new FavouriteAdapter(mlist);
        fAdapter.setOnItemClickListener(new FavouriteAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Favorite fav = mlist.get(position);
                SendFileMessage msg = new SendFileMessage(fav.getLinkedKWProjectID() + "", fav.getAttachmentID() + "",
                        fav.getIncidentID() + "", fav.getTitle(), fav.getFileID() + "", fav.getFileName(),
                        fav.getFileDownloadURL(), fav.getAttachmentTypeID() + "", fav.getCreatedDate(),
                        fav.getStatus() + "");
                io.rong.imlib.model.Message myMessage = io.rong.imlib.model.Message.obtain(targetId, conversationType, msg);
                RongIM.getInstance()
                        .sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
                            @Override
                            public void onAttached(io.rong.imlib.model.Message message) {

                            }

                            @Override
                            public void onSuccess(io.rong.imlib.model.Message message) {
                                Log.e("lalala", "sendMessage onError");

                            }

                            @Override
                            public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
                                Log.e("lalala", "sendMessage onError");

                            }
                        });
                dlgGetWindow.dismiss();
            }
        });
        rv_pc.setAdapter(fAdapter);

        LoginGet lg = new LoginGet();
        lg.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
            @Override
            public void getFavorite(ArrayList<Document> list) {
//                mlist = new ArrayList<Favorite>();
//                mlist.addAll(list);
//                fAdapter.UpdateRV(mlist);
            }
        });
        lg.MyFavoriteRequest(mContext,1);
    }


}
