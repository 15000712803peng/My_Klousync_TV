package com.kloudsync.techexcel2.keyboard;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.kloudsync.techexcel2.R;

import java.util.ArrayList;
import java.util.List;

public class KeyboardSupporter implements KeyboardEventReceiveListener {
    private Context context;

    private KeyboardEventDispatherListener keyboardEventDispatherListener;


    public void setKeyboardEventDispatherListener(KeyboardEventDispatherListener keyboardEventDispatherListener) {
        this.keyboardEventDispatherListener = keyboardEventDispatherListener;
    }

    @Override
    public boolean onKeyEventReceive(KeyEvent keyEvent) {
        Log.e("KeyboardSupport","onKeyEventReceive,action:" + keyEvent.getAction());
        int keyCode = keyEvent.getKeyCode();
        if(keyEvent.getAction() != KeyEvent.ACTION_DOWN){
            return false;
        }



        KeyboardView currentSelectedView = getSelectedView();



        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
            if(currentSelectedView != null && currentSelectedView.getTopView() != null){
                setCurrentSelectedKeyboardView(currentSelectedView.getTopView());
            }
                break;
            case  KeyEvent.KEYCODE_DPAD_DOWN:
                if(currentSelectedView != null && currentSelectedView.getBottomView() != null){
                    setCurrentSelectedKeyboardView(currentSelectedView.getBottomView());
                }
                break;
            case  KeyEvent.KEYCODE_DPAD_LEFT:
                if(currentSelectedView != null && currentSelectedView.getLeftView() != null){
                    setCurrentSelectedKeyboardView(currentSelectedView.getLeftView());
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(currentSelectedView != null && currentSelectedView.getRightView() != null){
                    setCurrentSelectedKeyboardView(currentSelectedView.getRightView());
                }
                break;
            case  KeyEvent.KEYCODE_DPAD_CENTER:
                if(currentSelectedView != null){
                    if(keyboardEventDispatherListener != null){
                        keyboardEventDispatherListener.onKeyEventEnterDispather(currentSelectedView.getTargeview());
                    }
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                if(keyboardEventDispatherListener != null){
                    keyboardEventDispatherListener.onMenuEventDispather();
                }
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

    private void reset(){
        for(KeyboardView keyboardView : myViews){

                keyboardView.setSelected(false);
                keyboardView.getTargeview().setBackground(null);
        }
    }

    private void setCurrentSelectedKeyboardView(KeyboardView keyboardView){
        reset();
        int index = myViews.indexOf(keyboardView);

        if(index >= 0){
            KeyboardView view = myViews.get(index);
            view.setSelected(true);
            view.getTargeview().setBackground(view.getSelectedBackgroud());
        }

    }

    public void init(){

        setCurrentSelectedKeyboardView(getSelectedView());
//        currentSelectedView.getTargeview().setBackground(currentSelectedView.getSelectedBackgroud());
    }

    public void init(Context context){
        this.context = context;
        setCurrentSelectedKeyboardView(getSelectedView());
//        currentSelectedView.getTargeview().setBackground(currentSelectedView.getSelectedBackgroud());
    }


}
