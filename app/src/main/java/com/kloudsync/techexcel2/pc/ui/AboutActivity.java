package com.kloudsync.techexcel2.pc.ui;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel2.R;
import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends Activity {

	private LinearLayout img_back;
	private TextView tv_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pc_about);
		initView();
		
	}

	private void initView() {
		img_back = (LinearLayout) findViewById(R.id.img_back);
		tv_name = (TextView) findViewById(R.id.tv_name);
		
		tv_name.setText(R.string.about_title);
		
		
		img_back.setOnClickListener(new myOnClick());
	}
	
	private class myOnClick implements OnClickListener {
		Intent intent = new Intent();
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.img_back:
				finish();
				break;
			default:
				break;
			}

		}
		
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("AboutActivity"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("AboutActivity");
	    MobclickAgent.onPause(this);
	}
}
