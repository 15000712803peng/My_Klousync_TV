package com.kloudsync.techexcel2.tool;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

public class MediaControlUtils {

    public static void sendMediaKeyCode(Context context, int keyCode) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Log.e("haha", audioManager.isMusicActive() + "");
        if (audioManager.isMusicActive()) {
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
            Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
            context.sendOrderedBroadcast(intent, null);

            keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
            intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
            context.sendOrderedBroadcast(intent, null);
        }
    }

    public static void pauseMusic(Context context) {
        Intent freshIntent = new Intent();
        freshIntent.setAction("com.android.music.musicservicecommand.pause");
        freshIntent.putExtra("command", "pause");
        context.sendBroadcast(freshIntent);
    }
}
