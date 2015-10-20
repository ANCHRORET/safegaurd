package com.tp.safeguard.activities;

import com.tp.safeguard.R;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SjfdSetupInfoActivity extends Activity {

	private ImageView iv_toggle;
	private TextView tv_safeNum;
	private boolean isSjfdOepn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupinfo_activity);
		initView();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//每次显示页面都要初始化数据,所以放在此处来初始化
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		isSjfdOepn = PreferenceUtils.getBoolean(this, Constants.SJFD_TOGGLE, false);
		iv_toggle.setImageResource(isSjfdOepn?R.drawable.lock:R.drawable.unlock);
		String text=PreferenceUtils.getString(this, Constants.SJFD_SAFENUM, "");
		tv_safeNum.setText(text);
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		iv_toggle = (ImageView) findViewById(R.id.setupInfo_iv_toggle);
		tv_safeNum = (TextView) findViewById(R.id.setupInfo_tv_safeNum);
		
		
	}

	public void toggleSjfd(View v) {
		//控制手机防盗开启的开关
		isSjfdOepn=!isSjfdOepn;
		PreferenceUtils.putBoolean(this, Constants.SJFD_TOGGLE, isSjfdOepn);
		iv_toggle.setImageResource(isSjfdOepn?R.drawable.lock:R.drawable.unlock);
	}

	public void enterSetup(View v) {
		// 进入设置向导
		startActivity(new Intent(this,SjfdSetup1Activity.class));
		
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_exit_anim);
	}
}
