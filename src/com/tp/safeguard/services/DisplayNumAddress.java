package com.tp.safeguard.services;

import com.tp.safeguard.db.NumAddressDao;
import com.tp.safeguard.view.DisplayNumToast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DisplayNumAddress extends Service {

	private static final String TAG = "DisplayNumAddress";
	private TelephonyManager mTelephonyManager;
	private PhoneStateListener mListener;
	private BroadcastReceiver mReceiver;
	private DisplayNumToast mDNToast;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		mDNToast = new DisplayNumToast(this);
		// 接收电话监听
		mListener = new MyTPListener();
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(mListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// 拨打电话监听
		mReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		if (mDNToast != null) {
			mDNToast.hide();
		}
		mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(mReceiver);
	}

	private class MyTPListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			// TelephonyManager#CALL_STATE_IDLE
			// TelephonyManager#CALL_STATE_RINGING
			// TelephonyManager#CALL_STATE_OFFHOOK
			Log.d(TAG, "incomingNumber");
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				String numAddress = NumAddressDao.queryNumAddress(
						DisplayNumAddress.this, incomingNumber);
				// 显示归属地
				mDNToast.show(numAddress);
			} else if (state == TelephonyManager.CALL_STATE_IDLE) {
				if (mDNToast != null) {
					mDNToast.hide();
				}
				mDNToast.hide();
			}
		}
	}

	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			mDNToast.show(number);
		}
	}
}
