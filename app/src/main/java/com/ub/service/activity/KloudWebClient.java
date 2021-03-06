package com.ub.service.activity;

import android.util.Log;

import com.kloudsync.techexcel2.config.AppConfig;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class KloudWebClient extends WebSocketClient{

    public interface OnClientEventListener{
        void onMessage(String message);
        void onReconnect();
    }
    private OnClientEventListener onClientEventListener;
    private static KloudWebClient instance;


    public void setOnClientEventListener(OnClientEventListener onClientEventListener) {
        this.onClientEventListener = onClientEventListener;
    }

    public KloudWebClient(URI serverUri) {
        super(serverUri);
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLSocketFactory factory = sslContext.getSocketFactory();
        try {
            setSocket(factory.createSocket());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("KloudWebClient","onOpen");
       AppConfig.webSocketClient = this;
    }


    @Override
    public void onMessage(String message) {
//        Log.e("KloudWebClient","onMessage:" + message);
        if(onClientEventListener != null){
            onClientEventListener.onMessage(message);
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("KloudWebClient","onClose");
        if(onClientEventListener != null){
            onClientEventListener.onReconnect();
        }
    }

    @Override
    public void onError(Exception ex) {
        Log.e("KloudWebClient","onClose");
    }

    public static KloudWebClient getDefault(final URI serverUri) {
        if (instance == null) {
            synchronized(KloudWebClient.class) {
                if (instance == null) {
                    instance = new KloudWebClient(serverUri);
                }
            }
        }
        return instance;
    }
}
