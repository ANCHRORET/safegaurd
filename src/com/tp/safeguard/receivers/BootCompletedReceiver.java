package com.tp.safeguard.receivers;

import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompletedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d(TAG, "手机开机了");

		boolean toggle = PreferenceUtils.getBoolean(context,
				Constants.SJFD_TOGGLE, false);
		if (!toggle) {
			Log.d(TAG, "没有开启手机防盗");
			return;
		}

		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String newSimNum = manager.getSimSerialNumber();
		String oldSimNum = PreferenceUtils.getString(context,
				Constants.SJFD_SAFENUM, "");
		if (newSimNum.equals(oldSimNum)) {
			Log.d(TAG, "手机没换");
			return;
		}

		String safeNum = PreferenceUtils.getString(context,
				Constants.SJFD_SAFENUM, "");

		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(safeNum, null, "现在这个人用着你的手机", null, null);
	}
}
