package com.tp.safeguard.activities;


import com.tp.safeguard.R;
import com.tp.safeguard.receivers.SafeAdminReceiver;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SjfdSetup4Activity extends BaseSetupActivity {

	private static final int ACTIVATE_REQUESTCODE = 101;
	private static final String TAG = "SjfdSetup4Activity";
	private ImageView mIvActivate;
	private DevicePolicyManager mDpm;
	private boolean isActivateSjfd;
	private ComponentName who;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup4_activity);

		initView();
		initData();
	}

	private void initView() {
		mIvActivate = (ImageView) findViewById(R.id.setup4_iv_activate);
		mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		who = new ComponentName(this, SafeAdminReceiver.class);
	}

	private void initData() {
		isActivateSjfd = mDpm.isAdminActive(who);
		setIvImage();
	}

	@Override
	public boolean performNext() {
		isActivateSjfd = mDpm.isAdminActive(who);
		if (isActivateSjfd) {
			Intent intent = new Intent(this, SjfdSetup5Activity.class);
			startActivity(intent);
			return false;
		} else {
			Toast.makeText(this, "如果要开启防盗保护,必须激活设备管理员", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
	}

	@Override
	public boolean performPre() {
		Intent intent = new Intent(this, SjfdSetup3Activity.class);
		startActivity(intent);
		return false;
	}

	public void activateManager(View v) {
		/*dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		ComponentName who = new ComponentName(this, MyReceiver.class);
		// 如果有激活设备管理员，才去锁屏，没有就提示激活
		if (!dpm.isAdminActive(who)) {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					getString(R.string.add_admin_extra_app_text));
			startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);*/

//		ComponentName who1=new ComponentName(this, SafeAdminReceiver.class);
		isActivateSjfd = mDpm.isAdminActive(who);
		if (isActivateSjfd) {
			Log.d(TAG, "激活了,要取消激活");
			mDpm.removeActiveAdmin(who);
			mIvActivate.setImageResource(R.drawable.admin_inactivated);
		} else {
			Log.d(TAG, "没有激活,去激活");
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					getString(R.string.add_admin_extra_app_text));
			startActivityForResult(intent, ACTIVATE_REQUESTCODE);
		}
	}

	private void setIvImage() {
		mIvActivate
				.setImageResource(isActivateSjfd ? R.drawable.admin_activated
						: R.drawable.admin_inactivated);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVATE_REQUESTCODE) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				// 成功激活
				mIvActivate.setImageResource(R.drawable.admin_activated);
				break;
			case Activity.RESULT_CANCELED:
				// 取消激活
				mIvActivate.setImageResource(R.drawable.admin_inactivated);
				break;
			default:
				break;
			}
		}
	}
}
