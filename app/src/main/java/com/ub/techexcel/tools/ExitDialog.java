package com.ub.techexcel.tools;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.kloudsync.techexcel2.R;
public class ExitDialog implements View.OnClickListener{//extends Dialog implements DialogInterface.OnDismissListener,View.OnClickListener{
    private Context context;
    private TextView tv_confirm;
    private TextView tv_cancel;
    private View view;
    private Dialog confirmDialog;
    private ExitDialogClickListener dialogClickListener;
    private int currentSelectStatus=0;// 0 确定 1取消

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confirm:
                doConfirm();
                break;
            case R.id.tv_cancel:
                doCancel();
                break;
        }
    }

    private void doConfirm(){
        if(dialogClickListener != null){
            dialogClickListener.onLeaveClick();
            confirmDialog.dismiss();
        }
    }

    private void doCancel(){
        if(dialogClickListener != null){
            confirmDialog.dismiss();
        }
    }

    public void setDialogClickListener(ExitDialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
    }

    public interface ExitDialogClickListener {
        void onLeaveClick();
    }

    public ExitDialog(Context context) {
        this.context = context;
        getInstance();
    }

    public void getInstance(){
        if (null != confirmDialog) {
            confirmDialog.dismiss();
            return;
        } else {
            init();
        }
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_exit_doc, null);
        tv_confirm = view.findViewById(R.id.tv_confirm);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        confirmDialog = new Dialog(context, R.style.confirmDialog);
        confirmDialog.setContentView(view);
        confirmDialog.setCancelable(false);
        confirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                    int keyCode = keyEvent.getKeyCode();
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            remoteWayDown(0);
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            remoteWayDown(1);
                            break;
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            remoteEnter();
                            break;
                    }
                }
                return false;
            }
        });
        setCurrentSelectAction(currentSelectStatus);
        Window window = confirmDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = context.getResources().getDimensionPixelSize(R.dimen.meeting_setting_dialog_width);
        confirmDialog.getWindow().setAttributes(lp);
        confirmDialog.show();
    }

    /**遥控器当前选中的按钮*/
    public void setCurrentSelectAction(int status){
        currentSelectStatus=status;
        switch (status){
            case 1://取消
                tv_confirm.setBackground(null);
                tv_cancel.setBackgroundResource(R.drawable.bg_remote_doc_select);
                break;
            case 0://确定
                tv_cancel.setBackground(null);
                tv_confirm.setBackgroundResource(R.drawable.bg_remote_doc_select);
                break;
        }
    }

    /**接收遥控器的上下左右按键*/
    public void remoteWayDown(int way){// 0 向左  1向右
        switch (way){
            case 0:
                if(currentSelectStatus==1){
                    currentSelectStatus=0;
                    setCurrentSelectAction(currentSelectStatus);
                }
                break;
            case 1:
                if(currentSelectStatus==0){
                    currentSelectStatus=1;
                    setCurrentSelectAction(currentSelectStatus);
                }
                break;
        }
    }

    /**接收遥控器的enter按键,执行功能*/
    public void remoteEnter(){
        switch (currentSelectStatus){
            case 0://确定
                doConfirm();
                break;
            case 1://取消
                doCancel();
                break;
        }
    }
}