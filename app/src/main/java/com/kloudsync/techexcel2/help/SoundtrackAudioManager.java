package com.kloudsync.techexcel2.help;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;


import com.kloudsync.techexcel2.bean.EventCloseSoundtrack;
import com.kloudsync.techexcel2.bean.SoundtrackMediaInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;


public class SoundtrackAudioManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static SoundtrackAudioManager instance;
    private MediaPlayer audioPlayer;
    private volatile long playTime;
    private Context context;
    private SoundtrackMediaInfo mediaInfo;

    private SoundtrackAudioManager(Context context) {
        this.context = context;
        audioPlayer = new MediaPlayer();

    }

    public static SoundtrackAudioManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SoundtrackAudioManager.class) {
                if (instance == null) {
                    instance = new SoundtrackAudioManager(context);
                }
            }
        }
        return instance;
    }

    public void setSoundtrackAudio(SoundtrackMediaInfo mediaInfo) {
        Log.e("check_play","mediaInfo:" + mediaInfo);
        this.mediaInfo = mediaInfo;
        prepareAudioAndPlay(mediaInfo);

    }

    public void prepareAudioAndPlay(SoundtrackMediaInfo audioData) {
        try {

            try {
                if(audioPlayer.isPlaying()){
                    return;
                }
            }catch (IllegalStateException exception){

            }
            audioPlayer.setOnPreparedListener(this);
            audioPlayer.setOnCompletionListener(this);
            audioPlayer.setOnErrorListener(this);
            Log.e("check_play","set_data_source:" + audioData.getAttachmentUrl());
            audioPlayer.setDataSource(context, Uri.parse(audioData.getAttachmentUrl()));
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                audioPlayer.prepareAsync();
            }catch (IllegalStateException e){
                Log.e("check_play","IllegalStateException," + e.getMessage());
                reinit(audioData);
            }

        } catch (IOException e) {
            Log.e("check_play","IOException," + e.getMessage());
            e.printStackTrace();
            audioData.setPreparing(false);
        }

    }

    private void reinit(SoundtrackMediaInfo mediaInfo){
        audioPlayer = null;
        audioPlayer = new MediaPlayer();
        try {
            audioPlayer.setDataSource(context, Uri.parse(mediaInfo.getAttachmentUrl()));
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("check_play","onPrepared");
        if (mediaInfo != null) {
            Log.e("check_play", "on prepared,id:" + mediaInfo.getAttachmentUrl());
            mp.start();

        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
        EventBus.getDefault().post(new EventCloseSoundtrack());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public boolean isPlaying(){
        if(audioPlayer != null){
            return audioPlayer.isPlaying();
        }
        return false;
    }

    public long getPlayTime(){
        return audioPlayer.getCurrentPosition();
    }


    public void release(){
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.reset();
            audioPlayer.release();
            audioPlayer = null;
        }

        mediaInfo = null;
        instance = null;
    }

    public long getDuration(){
        return audioPlayer.getDuration();
    }

    public void pause(){
        if(audioPlayer != null){
            Log.e("vedio_check","pause_begin");
            audioPlayer.pause();
            Log.e("vedio_check","pause_");
        }
    }

    public void restart(){
        if(audioPlayer != null){
            audioPlayer.start();
        }
    }

    public void seekTo(int time){

        if(audioPlayer != null){
            audioPlayer.seekTo(time);
            Log.e("vedio_check","seek_to,time:" + time);
        }
    }

}
