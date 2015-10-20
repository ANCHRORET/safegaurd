package com.tp.safeguard.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.TextView;

import com.tp.safeguard.R;
import com.tp.safeguard.fragments.AppLockEnterFragment;
import com.tp.safeguard.fragments.AppLockSetFragment;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;

public class ApplockEntranceActivity extends FragmentActivity {

	private TextView mTvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applockentrance);

		initView();
		initData();
	}

	private void initView() {
		mTvTitle = (TextView) findViewById(R.id.ale_tv_title);
	}

	private void initData() {
		String haveAppLockPwd = PreferenceUtils.getString(this,
				Constants.CYGJ_APPLOCKPWD, "");
		if (TextUtils.isEmpty(haveAppLockPwd)) {
			// 没设置密码
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			Fragment fragment = new AppLockSetFragment();
			transaction.replace(R.id.ale_fl_container, fragment);
			transaction.commit();
		} else {
			// 设置了密码
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.replace(R.id.ale_fl_container,
					new AppLockEnterFragment());
			transaction.commit();
		}
	}

	public void setTitleText(String title) {
		mTvTitle.setText(title);
	}
}
