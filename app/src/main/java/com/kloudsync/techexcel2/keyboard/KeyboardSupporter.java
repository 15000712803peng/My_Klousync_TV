package com.kloudsync.techexcel2.keyboard;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.kloudsync.techexcel2.R;

import java.util.ArrayList;
import java.util.List;

public class KeyboardSupporter implements KeyboardEventReceiveListener {

    @Override
    public boolean onKeyEventReceive(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if(keyEvent.getAction() != KeyEvent.ACTION_DOWN){
            return false;
        }

        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case  KeyEvent.KEYCODE_DPAD_DOWN:
                break;
            case  KeyEvent.KEYCODE_DPAD_LEFT:
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            case  KeyEvent.KEYCODE_DPAD_CENTER:
                break;
            case KeyEvent.KEYCODE_MENU:
                break;
        }

        return false;
    }


    private View rootView;
    List<KeyboardView> myViews = new ArrayList<>();

    public void setRootView(View rootView){
        this.rootView = rootView;
    }

    public void addKeyboardView(KeyboardView keyboardView){
        myViews.add(keyboardView);
    }

    private KeyboardView getSelectedView(){
        for(KeyboardView keyboardView : myViews){
            if(keyboardView.isSelected()){
                return keyboardView;
            }
        }
        return null;
    }






}
