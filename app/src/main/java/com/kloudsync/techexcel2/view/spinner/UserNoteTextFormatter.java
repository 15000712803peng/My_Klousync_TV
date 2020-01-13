package com.kloudsync.techexcel2.view.spinner;

import android.text.Spannable;
import android.text.SpannableString;

import com.kloudsync.techexcel2.bean.UserNotes;


public class UserNoteTextFormatter implements SpinnerTextFormatter<UserNotes> {

    @Override
    public Spannable format(UserNotes user) {
        return new SpannableString(user.getUserName()  +"  (" + (user.getNoteCount()) + ")");
    }
}
