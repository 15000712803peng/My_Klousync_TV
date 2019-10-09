package com.kloudsync.techexcel2.view.spinner;

import android.text.Spannable;
import android.text.SpannableString;


public class DeviceTextFormatter implements SpinnerTextFormatter<AudioDeviceData> {

    @Override
    public Spannable format(AudioDeviceData device) {
        return new SpannableString(device.name);
    }
}
