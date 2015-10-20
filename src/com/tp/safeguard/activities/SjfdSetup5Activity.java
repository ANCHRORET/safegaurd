package com.tp.safeguard.activities;

import com.tp.safeguard.R;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SjfdSetup5Activity extends BaseSetupActivity {

	private CheckBox cb_sjfdToggle;
	private boolean isSjfdOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup5_activity);

		initView();
		initData();
		
	}
	private void initData() {
		cb_sjfdToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// 改变是否开启手机防盗
				isSjfdOpen=!isSjfdOpen;
				PreferenceUtils.putBoolean(SjfdSetup5Activity.this, Constants.SJFD_TOGGLE, isSjfdOpen);
			}
		});
	}
	private void initView() {
		cb_sjfdToggle = (CheckBox) findViewById(R.id.cb_sjfdToggle);
		isSjfdOpen = PreferenceUtils.getBoolean(this, Constants.SJFD_TOGGLE, false);
		cb_sjfdToggle.setChecked(isSjfdOpen);
	}
	@Override
	public boolean performNext() {
		if (!isSjfdOpen) {
			Toast.makeText(this, "请打开手机防盗保护", Toast.LENGTH_SHORT).show();
			return true;
		}
		PreferenceUtils.putBoolean(this, Constants.SJFD_SETUP, true);
		PreferenceUtils.putBoolean(this, Constants.SJFD_TOGGLE, true);
		//设置完成,显示设置完成的信息
		Intent intent = new Intent(this, SjfdSetupInfoActivity.class);
		startActivity(intent);
		return false;
	}

	@Override
	public boolean performPre() {
		Intent intent = new Intent(this, SjfdSetup4Activity.class);
		startActivity(intent);
		return false;
	}
}
