package com.tp.safeguard.services;

import java.util.List;

import com.tp.safeguard.bean.ProcessBean;
import com.tp.safeguard.business.ProcessProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class LockScreenCleanProcess extends Service {

	private static final String TAG = "LockScreenCleanProcess";
	private LockScreenReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mReceiver = new LockScreenReceiver();

		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Log.d(TAG, "LockScreenCleanProcessonDestroy");
		unregisterReceiver(mReceiver);
	}

	private class LockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "锁屏时清理了");
			List<ProcessBean> processes = ProcessProvider
					.getAllProcess(context);
			for (ProcessBean bean : processes) {
				if (!"com.tp.safeguard".equals(bean.packageName)) {
					ProcessProvider.killProcess(context, bean.packageName);
				}
			}
		}
	}
}
