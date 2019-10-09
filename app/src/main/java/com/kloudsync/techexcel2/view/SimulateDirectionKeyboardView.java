package com.kloudsync.techexcel2.view;

import android.content.Context;
import android.icu.text.IDNA;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.keyboard.KeyboardEventDispatherListener;
import com.kloudsync.techexcel2.keyboard.KeyboardEventReceiveListener;


/**
 * Created by tonyan on 2019/9/30.
 */



public class SimulateDirectionKeyboardView extends RelativeLayout implements View.OnClickListener{


    private ImageView left,right,top,bottom,enter,menu;

    private KeyboardEventReceiveListener keyboardEventReceiveListener;


    public void setKeyboardEventReceiveListener(KeyboardEventReceiveListener keyboardEventReceiveListener) {
        this.keyboardEventReceiveListener = keyboardEventReceiveListener;
    }

    public SimulateDirectionKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.direction_keybord,this,true);
        left = view.findViewById(R.id.image_direction_left);
        right = view.findViewById(R.id.image_direction_right);
        top = view.findViewById(R.id.image_direction_up);
        bottom = view.findViewById(R.id.image_direction_down);
        enter = view.findViewById(R.id.image_direction_enter);
        menu = view.findViewById(R.id.image_direction_menu);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        top.setOnClickListener(this);
        bottom.setOnClickListener(this);
        enter.setOnClickListener(this);
        menu.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_direction_down:
                if(keyboardEventReceiveListener != null){
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN);
                    keyboardEventReceiveListener.onKeyEventReceive(keyEvent);

                }
                break;
            case R.id.image_direction_up:
                if(keyboardEventReceiveListener != null){
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP);
                    keyboardEventReceiveListener.onKeyEventReceive(keyEvent);
                }
                break;
            case R.id.image_direction_left:
                if(keyboardEventReceiveListener != null){
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT);
                    keyboardEventReceiveListener.onKeyEventReceive(keyEvent);
                }
                break;
            case R.id.image_direction_right:
                if(keyboardEventReceiveListener != null){
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT);
                    keyboardEventReceiveListener.onKeyEventReceive(keyEvent);
                }
                break;

            case R.id.image_direction_enter:
                if(keyboardEventReceiveListener != null){
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_CENTER);
                    keyboardEventReceiveListener.onKeyEventReceive(keyEvent);
                }
                break;

            case R.id.image_direction_menu:
                if(keyboardEventReceiveListener != null){
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MENU);
                    keyboardEventReceiveListener.onKeyEventReceive(keyEvent);
                }
                break;
        }
    }
}
