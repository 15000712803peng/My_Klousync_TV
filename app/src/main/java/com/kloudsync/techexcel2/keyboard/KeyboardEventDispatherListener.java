package com.kloudsync.techexcel2.keyboard;

import android.view.KeyEvent;
import android.view.View;

public interface KeyboardEventDispatherListener {

    boolean onKeyEventDispather(KeyEvent keyEvent,int viewId);

    boolean onMenuEventDispather();

    boolean onKeyEventEnterDispather(View targetView);

}
