package com.kloudsync.techexcel2.help;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.bean.SectionVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyan on 2019/11/21.
 */

public class UserVedioManager {

    private static boolean needRefresh = true;
    private static UserVedioManager instance;
    private MediaPlayer audioPlayer;
    private volatile long playTime;
    private Context context;
    private UserVedioAdapter adapter;
    //
    private List<UserVedioData> userVedioDatas = new ArrayList<>();

    private UserVedioManager(Context context) {
        this.context = context;
        audioPlayer = new MediaPlayer();

    }

    public void setAdapter(RecyclerView userList){
        if(adapter == null){
            adapter = new UserVedioAdapter();
        }
        userList.setAdapter(adapter);
    }

    public static UserVedioManager getInstance(Context context) {
        if (instance == null) {
            synchronized (UserVedioManager.class) {
                if (instance == null) {
                    instance = new UserVedioManager(context);
                }
            }
        }
        return instance;
    }

    public void addUserVedios(String userId,List<SectionVO> vediosData) {
        UserVedioData _user = new UserVedioData(userId);
        if(userVedioDatas.contains(_user)){
            UserVedioData userVedioData = userVedioDatas.get(userVedioDatas.indexOf(_user));
            userVedioData.setVedios(vediosData);
        }else {
            UserVedioData vedioData = new UserVedioData(userId);
            vedioData.setVedios(vediosData);
            userVedioDatas.add(vedioData);
        }

        Log.e("userVedioDatas",userVedioDatas+"");

    }

    public void refreshUserInfo(String userId,String userName,String avatarUrl){
        UserVedioData _user = new UserVedioData(userId);
        if(userVedioDatas.contains(_user)){
            UserVedioData userVedioData = userVedioDatas.get(userVedioDatas.indexOf(_user));
            userVedioData.setAvatarUrl(avatarUrl);
            userVedioData.setUserName(userName);
        }else {
            UserVedioData vedioData = new UserVedioData(userId);
            vedioData.setAvatarUrl(avatarUrl);
            vedioData.setUserName(userName);
            userVedioDatas.add(vedioData);
        }

        Log.e("userVedioDatas",userVedioDatas+"");

    }

    SectionVO checkData;

    public  void setPlayTime(long playTime) {

        this.playTime = playTime;

        Log.e("check_play","step_one");
        if(userVedioDatas.size() < 0 ){
            return;
        }

        SectionVO data  = getNearestVedioData(playTime);

        if(data == null){
            return;
        }
        Log.e("check_play","step_two");

        if(playTime < data.getStartTime() || playTime > data.getEndTime()){
            return;
        }

        Log.e("check_play","step_three");

        if(checkData != null && !checkData.equals(data) || checkData == null){
            needRefresh = true;
        }

        Log.e("check_play","step_four:" + needRefresh);

        if(needRefresh){
            if(adapter != null){
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("UserVedioManager","adapter_refresh");
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            needRefresh = false;
            checkData = data;
        }


    }



    public void release(){
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.reset();
            audioPlayer.release();
            audioPlayer = null;
        }
        checkData = null;
        instance = null;
    }

    public class UserVedioData{
        private int type;
        private String userId;
        private String userName;
        private String avatarUrl;
        private List<SectionVO> vedios;
        private boolean isPlaying;
        private boolean isPreparing;
        private boolean isPrepared;
        private MediaPlayer mediaPlayer;

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }

        public void setMediaPlayer(MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
        }

        public boolean isPrepared() {
            return isPrepared;
        }

        public void setPrepared(boolean prepared) {
            isPrepared = prepared;
        }

        public boolean isPreparing() {
            return isPreparing;
        }

        public void setPreparing(boolean preparing) {
            isPreparing = preparing;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }

        public UserVedioData(String userId) {
            this.userId = userId;
        }

        public UserVedioData() {

        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public List<SectionVO> getVedios() {
            return vedios;
        }

        public void setVedios(List<SectionVO> vedios) {
            this.vedios = vedios;
        }

        @Override
        public String toString() {
            return "UserVedioData{" +
                    "type=" + type +
                    ", userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", avatarUrl='" + avatarUrl + '\'' +
                    ", vedios=" + vedios +
                    ", isPlaying=" + isPlaying +
                    ", isPreparing=" + isPreparing +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserVedioData that = (UserVedioData) o;

            return userId != null ? userId.equals(that.userId) : that.userId == null;
        }

        @Override
        public int hashCode() {
            return userId != null ? userId.hashCode() : 0;
        }
    }

    class UserVedioAdapter extends RecyclerView.Adapter<VedioHolder>{


        @NonNull
        @Override
        public VedioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_vedio, parent, false);
            return new VedioHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VedioHolder holder, int position) {
            final UserVedioData vedioData = userVedioDatas.get(position);
            holder.nameText.setText(vedioData.getUserName());
            if(vedioData.getVedios() != null){
                SectionVO data  = getNearestVedioData(playTime,vedioData.getVedios());
                toPlay(vedioData,data,holder.surface);

            }
        }

        @Override
        public int getItemCount() {
            return userVedioDatas.size();
        }
    }


    class VedioHolder extends RecyclerView.ViewHolder{
        SurfaceView surface;
        TextView nameText;
        public VedioHolder(View itemView) {
            super(itemView);
            surface = itemView.findViewById(R.id.user_surface);
            nameText = itemView.findViewById(R.id.txt_name);
        }
    }

    private SectionVO getNearestVedioData(long playTime,List<SectionVO> vedioDatas) {
        if (vedioDatas.size() > 0) {
            int index = 0;
            for (int i = 0; i < vedioDatas.size(); ++i) {
                //4591,37302
                long interval = vedioDatas.get(i).getStartTime() - playTime;
                if(interval > 0){
                    index = i;
                    break;
                }

            }
            return vedioDatas.get(index);

        }
        return null;
    }

    private SectionVO getNearestVedioData(long playTime) {

        if(userVedioDatas.size() <= 0){
            return null;
        }

        for(UserVedioData data : userVedioDatas){
            int index = 0;
            if(data.getVedios() == null){
                return null;
            }
            if (data.getVedios().size() > 0) {
                for (int i = 0; i < data.getVedios().size(); ++i) {
                    //4591,37302
                    long interval = data.getVedios().get(i).getStartTime() - playTime;
                    if(interval > 0){
                        index = i;
                        break;
                    }

                }
            }
            return data.getVedios().get(index);
        }

        return null;
    }

    SectionVO vedioData;

    private void toPlay(final UserVedioData userVedioData, SectionVO data, SurfaceView surfaceView) {

        if(data == null){
            return;
        }

        if(playTime < data.getStartTime() || playTime > data.getEndTime()){
            return;
        }

        if(userVedioData.isPlaying() || userVedioData.isPreparing()){
            return;
        }

        if(vedioData != null && vedioData.equals(data)){
            return;
        }

        if(vedioData != null && vedioData.isPlaying()){
            return;
        }

        this.vedioData = data;

        final  MediaPlayer vedioPlayer = new MediaPlayer();

        try {

            try {
                initSurface(surfaceView,vedioPlayer);
                userVedioData.setPreparing(true);
                vedioPlayer.reset();
                vedioPlayer.setDataSource(context, Uri.parse(data.getFileUrl()));
                vedioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                vedioPlayer.prepareAsync();
            }catch (IllegalStateException e){

            }

            vedioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    vedioPlayer.start();
                    vedioData.setPlaying(true);
                    userVedioData.setPreparing(true);

                }
            });
            vedioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });

            vedioPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            userVedioData.setPreparing(false);
        }


    }


    public void initSurface(SurfaceView surfaceView,MediaPlayer mediaPlayer){
        //给surfaceHolder设置一个callback
        surfaceView.setZOrderOnTop(true);
        surfaceView.setZOrderMediaOverlay(true);
        surfaceView.getHolder().addCallback(new SurfaceCallBack(mediaPlayer));
    }

    private class SurfaceCallBack implements SurfaceHolder.Callback {

        MediaPlayer mediaPlayer;
        public SurfaceCallBack(MediaPlayer mediaPlayer){
            this.mediaPlayer = mediaPlayer;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //调用MediaPlayer.setDisplay(holder)设置surfaceHolder，surfaceHolder可以通过surfaceview的getHolder()方法获得

            Log.e("WebVedioManager", "surfaceCreated");
            if(mediaPlayer != null){
                mediaPlayer.setDisplay(holder);
            }
        }

        /**
         * 当SurfaceHolder的尺寸发生变化的时候被回调
         *
         * @param holder
         * @param format
         * @param width
         * @param height
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            release();
        }
    }




}
