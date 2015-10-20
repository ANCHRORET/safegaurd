package com.tp.safeguard.activities;

import com.tp.safeguard.R;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SjfdSetup2Activity extends BaseSetupActivity {

	private ImageView ivBind;
	private boolean isBindsm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup2_activity);
		initView();

	}

	private void initView() {
		ivBind = (ImageView) findViewById(R.id.setup2_iv_bindsm);
		isBindsm = PreferenceUtils.getBoolean(this, Constants.SJFD_BINDSM,
				false);
		ivBind.setImageResource(isBindsm ? R.drawable.lock : R.drawable.unlock);
	}

	public void clickBind(View v) {
		isBindsm = !isBindsm;
		PreferenceUtils.putBoolean(this, Constants.SJFD_BINDSM, isBindsm);
		ivBind.setImageResource(isBindsm ? R.drawable.lock : R.drawable.unlock);
		if (isBindsm) {
			TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String bindNum = manager.getSimSerialNumber();
			PreferenceUtils.putString(this, Constants.SJFD_BINDNUM, bindNum);

		}
	}

	@Override
	public boolean performNext() {
		if (isBindsm) {
			Intent intent = new Intent(this, SjfdSetup3Activity.class);
			startActivity(intent);
			return false;
		}else {
			Toast.makeText(this, "必须要绑定当前的sim卡,才能完成手机防盗", Toast.LENGTH_SHORT).show();
			return true;
		}
	}

	@Override
	public boolean performPre() {
		Intent intent = new Intent(this, SjfdSetup1Activity.class);
		startActivity(intent);
		return false;
	}
}
