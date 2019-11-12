package com.ub.service.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kloudsync.techexcel2.config.AppConfig;
import com.kloudsync.techexcel2.tool.Md5Tool;
import com.ub.techexcel.tools.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KloudWebClientManager implements KloudWebClient.OnClientEventListener{

    private static KloudWebClientManager instance;
    private KloudWebClient kloudWebClient;
    private URI uri;
    private boolean heartBeatStarted = false;
    private Context context;

    public interface OnMessageArrivedListener{
        void onMessage(String message);
    }

    private List<OnMessageArrivedListener> onMessageArrivedListeners = new ArrayList<>();

    public void addOnMessageArrivedListener(OnMessageArrivedListener onMessageArrivedListener) {
        this.onMessageArrivedListeners.add(onMessageArrivedListener);
    }

    public KloudWebClient getKloudWebClient() {
        return kloudWebClient;
    }

    public static KloudWebClientManager getDefault(Context context,URI uri) {
        if (instance == null) {
            synchronized(KloudWebClientManager.class) {
                if (instance == null) {
                    instance = new KloudWebClientManager(context,uri);
                }
            }
        }
        return instance;
    }

    public static KloudWebClientManager getDefault() {
        return instance;
    }

    private KloudWebClientManager(Context context, URI uri) {
        this.context = context;
        this.uri = uri;
        kloudWebClient = new KloudWebClient(uri);
        kloudWebClient.setOnClientEventListener(this);
    }

    public synchronized void connect(){
        if(kloudWebClient != null){
            try{
                kloudWebClient.connect();
            }catch (Exception e){
                reconnect();
            }

        }
    }

    public synchronized void reconnect() {
        if (this.uri != null) {
            Log.e("KloundWebClientManager", "reconnect");
            AppConfig.UserToken = context.getSharedPreferences(AppConfig.LOGININFO,
                    Context.MODE_PRIVATE).getString("UserToken", null);
            AppConfig.DEVICE_ID =  context.getSharedPreferences(AppConfig.LOGININFO,
                    Context.MODE_PRIVATE).getString("DeviceId", null);
            try {
                kloudWebClient = new KloudWebClient(new URI(AppConfig.COURSE_SOCKET + "/" + AppConfig.UserToken + "/" + 3 + "/" +AppConfig.DEVICE_ID));
                kloudWebClient.setOnClientEventListener(this);
                kloudWebClient.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();

            }
        }

    }

    @Override
    public void onMessage(String message) {
        if(this.onMessageArrivedListeners != null && this.onMessageArrivedListeners.size() > 0){
            for(OnMessageArrivedListener onMessageArrivedListener: this.onMessageArrivedListeners){
                if(onMessageArrivedListener != null){
                    onMessageArrivedListener.onMessage(message);
                }

            }
        }

    }

    @Override
    public synchronized void onReconnect() {
        reconnect();
    }


    class HeartBeatTask extends TimerTask {
        @Override
        public void run() {

            JSONObject heartBeatMessage = new JSONObject();
            try {
                heartBeatMessage.put("action", "HELLO");
                heartBeatMessage.put("sessionId", AppConfig.UserToken);
                heartBeatMessage.put("changeNumber", 0);
                if (AppConfig.isPresenter) {
//                    heartBeatMessage.put("status", AppConfig.status);
//                    heartBeatMessage.put("currentLine", AppConfig.currentLine);
//                    heartBeatMessage.put("currentMode", AppConfig.currentMode);
                    heartBeatMessage.put("currentPageNumber", AppConfig.currentPageNumber);
                    heartBeatMessage.put("currentItemId", AppConfig.currentDocId);
                }

                if(kloudWebClient != null){
                    Log.e("web_client","send heart beat message:" + heartBeatMessage.toString());
                    kloudWebClient.send(heartBeatMessage.toString());
                }
                heartBeatStarted = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){

            }

        }
    }

    private  Timer heartBeatTimer;
    private  HeartBeatTask heartBeatTask;

    public void startHeartBeat(){
        heartBeatTimer = new Timer();
        heartBeatTask = new HeartBeatTask();
        if(!heartBeatStarted && heartBeatTimer != null && heartBeatTask != null){
            heartBeatTimer.schedule(heartBeatTask, 3000, 5000);
        }
    }

    public void release(){
        if(heartBeatTimer != null && heartBeatTask != null){
            heartBeatStarted = false;
            heartBeatTask.cancel();
            heartBeatTimer.cancel();
            heartBeatTimer = null;
            heartBeatTask = null;
            instance = null;
        }
    }

    public void removeMessageArrivedLinster(OnMessageArrivedListener onMessageArrivedListener){
        Log.e("KloudWebClientManager","removeMessageArrivedLinster");
        if(this.onMessageArrivedListeners != null){
            this.onMessageArrivedListeners.remove(onMessageArrivedListener);
        }
    }


}
