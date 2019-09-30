package com.kloudsync.techexcel2.dialog;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.kloudsync.techexcel2.R;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

public class SubConversationListActivtiy extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subconversationlist);

		if (RongIM.getInstance() != null)
			RongIM.getInstance().startSubConversationList(this,
					Conversation.ConversationType.GROUP);
	}
}
