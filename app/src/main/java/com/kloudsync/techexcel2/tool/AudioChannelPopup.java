package com.kloudsync.techexcel2.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel2.R;
import com.kloudsync.techexcel2.keyboard.KeyboardSupporter;
import com.kloudsync.techexcel2.keyboard.KeyboardView;
import com.kloudsync.techexcel2.view.spinner.AudioDeviceData;
import com.kloudsync.techexcel2.view.spinner.DeviceTextFormatter;
import com.kloudsync.techexcel2.view.spinner.NiceSpinner;

import java.util.ArrayList;
import java.util.List;


public class AudioChannelPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    private NiceSpinner microSpinner;
    private NiceSpinner speakerSpinner;
    private List<AudioDeviceData> outPutDevices = new ArrayList<>();
    private List<AudioDeviceData> inPutDevices = new ArrayList<>();
    private KeyboardSupporter keyboardSupporter;
    private RelativeLayout keyboardSpinnerSpeaker;
    private RelativeLayout keyboardSpinnerMicro;
    private RelativeLayout keyboardCancel;
    private RelativeLayout keyboardSave;
    private LinearLayout parentView;

    public RelativeLayout getKeyboardSpinnerSpeaker() {
        return keyboardSpinnerSpeaker;
    }


    public AudioChannelPopup(Context context, LinearLayout parentView) {
        this.mContext = context;
        this.parentView = parentView;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
        getVudioDevices(context);
    }

    public void getPopupWindowInstance() {
        initPopuptWindow();

    }


    public void initPopuptWindow() {

        microSpinner = parentView.findViewById(R.id.spinner_micro);
        speakerSpinner = parentView.findViewById(R.id.spinner_speaker);
        keyboardSpinnerSpeaker = parentView.findViewById(R.id.keyboard_sppinner_speaker);
        keyboardSpinnerMicro = parentView.findViewById(R.id.keyboard_sppinner_micro);
        keyboardCancel = parentView.findViewById(R.id.keyboard_cancel);
        keyboardSave = parentView.findViewById(R.id.keyboard_save);

    }

    public KeyboardSupporter getKeyboardSupporter() {
        return keyboardSupporter;
    }


    public void show() {
        initKeyboardSupporter();
        parentView.setVisibility(View.VISIBLE);

    }

    private void initKeyboardSupporter(){

        keyboardSupporter = new KeyboardSupporter();
        KeyboardView speakerSpinnerView = new KeyboardView();
        speakerSpinnerView.setSelected(true);
        speakerSpinnerView.setTargeview(keyboardSpinnerSpeaker);
        speakerSpinnerView.setSelectedBackgroud(mContext.getResources().getDrawable(R.drawable.rect_keybord_selected));


        KeyboardView microSpinnerView = new KeyboardView();
        microSpinnerView.setTargeview(keyboardSpinnerMicro);
        microSpinnerView.setSelected(false);
        microSpinnerView.setSelectedBackgroud(mContext.getResources().getDrawable(R.drawable.rect_keybord_selected));


        KeyboardView cancelView = new KeyboardView();
        cancelView.setSelected(false);
        cancelView.setTargeview(keyboardCancel);
        cancelView.setSelectedBackgroud(mContext.getResources().getDrawable(R.drawable.rect_keybord_selected2));

        KeyboardView saveView = new KeyboardView();
        saveView.setSelected(false);
        saveView.setTargeview(keyboardSave);
        saveView.setSelectedBackgroud(mContext.getResources().getDrawable(R.drawable.rect_keybord_selected2));

        microSpinnerView.setBottomView(speakerSpinnerView);

        speakerSpinnerView.setTopView(microSpinnerView);
        speakerSpinnerView.setBottomView(cancelView);

        cancelView.setTopView(microSpinnerView);
        cancelView.setRightView(saveView);

        saveView.setTopView(microSpinnerView);
        saveView.setLeftView(cancelView);


        keyboardSupporter.addKeyboardView(speakerSpinnerView);
        keyboardSupporter.addKeyboardView(microSpinnerView);
        keyboardSupporter.addKeyboardView(cancelView);
        keyboardSupporter.addKeyboardView(saveView);
        Log.e("AudioChannelPopup","keyboard supporter init");
        keyboardSupporter.init(mContext);
    }


    @Override
    public void onClick(View view) {

    }

    private void getVudioDevices(Context context){
        PackageManager packageManager = context.getPackageManager();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // Check whether the device has a speaker.

        Toast.makeText(mContext,"SDK VERSION:"+Build.VERSION.SDK_INT + "，has feature audio output:" + packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT),Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                // Check FEATURE_AUDIO_OUTPUT to guard against false positives.
                packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
            AudioDeviceInfo[] outputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : outputDevices) {

                if (device.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {

                    AudioDeviceData audioDevice = new AudioDeviceData();
                    audioDevice.name = device.getProductName() + "(Buildin_Speaker)";
                    audioDevice.id = device.getId();
                    audioDevice.type = "TYPE_BUILTIN_SPEAKER";
                    this.outPutDevices.add(audioDevice);

                }else if(device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO){
                    AudioDeviceData audioDevice = new AudioDeviceData();
                    audioDevice.name = device.getProductName() + "(Type_Bluetooth)";
                    audioDevice.id = device.getId();
                    audioDevice.type = "TYPE_BLUETOOTH_A2DP";
                    this.outPutDevices.add(audioDevice);

                }else if(device.getType() == AudioDeviceInfo.TYPE_HDMI){

                    AudioDeviceData audioDevice = new AudioDeviceData();
                    audioDevice.name = device.getProductName() + "(Type_HDMI)";
                    audioDevice.id = device.getId();
                    audioDevice.type = "TYPE_HDMI";
                    this.outPutDevices.add(audioDevice);

                }else if(device.getType() == AudioDeviceInfo.TYPE_USB_DEVICE || device.getType() == AudioDeviceInfo.TYPE_USB_HEADSET){

                    AudioDeviceData audioDevice = new AudioDeviceData();
                    audioDevice.name = device.getProductName() + "(Type_Usb)";
                    audioDevice.id = device.getId();
                    audioDevice.type = "TYPE_USB";
                    this.outPutDevices.add(audioDevice);
                }

//                Log.e("getVudioDevices","device,type:" + device.getType() +",name:" + device.getProductName());


            }

            Log.e("getVudioDevices","outPutDevices:" + this.outPutDevices);
            Toast.makeText(mContext,"output devices:" + this.outPutDevices,Toast.LENGTH_SHORT).show();
            speakerSpinner.attachDataSource(this.outPutDevices,new DeviceTextFormatter());
            if(this.outPutDevices != null && this.outPutDevices.size() > 0){
                speakerSpinner.setSelectedIndex(0);
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                // Check FEATURE_AUDIO_OUTPUT to guard against false positives.
                packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
            AudioDeviceInfo[] outputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
            for (AudioDeviceInfo device : outputDevices) {
                if (device.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {

                }

                Log.e("getVudioDevices","device,type:" + device.getType() +",name:" + device.getProductName());

            }

        }


    }

}
