package com.tp.safeguard.services;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.tp.safeguard.bean.BlackBean;
import com.tp.safeguard.db.BlackDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsSafeService extends Service {

	private static final String TAG = "CallSmsSafeService";
	private CallSmsSafeReceiver safeReceiver;
	private BlackDao mBlackDao;
	private CallListener mListener;
	private TelephonyManager mTpManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		mBlackDao = new BlackDao(this);

		// 拦截短信
		safeReceiver = new CallSmsSafeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(safeReceiver, filter);

		//拦截电话
		mTpManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mListener = new CallListener();
		mTpManager.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		unregisterReceiver(safeReceiver);
		mTpManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
	}

	private class CallListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			// 有以下三种状态
			// TelephonyManager#CALL_STATE_IDLE
			// TelephonyManager#CALL_STATE_RINGING
			// TelephonyManager#CALL_STATE_OFFHOOK
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				int findType = mBlackDao.findType(incomingNumber);
				if (findType == BlackBean.BLACK_TYPE_ALL
						|| findType == BlackBean.BLACK_TYPE_CALL) {
					Log.d(TAG, "挂断电话");

					// 挂断电话 ITelephony
					// ITelephony.Stub.asInterface(
					// ServiceManager.getService(Context.TELEPHONY_SERVICE));
					// public boolean endCall() throws
					// android.os.RemoteException;
					/*
					 * try { Method method = mTpManager.getClass().getMethod(
					 * "getITelephony", null); method.setAccessible(true);
					 * ITelephony iTelephony = (ITelephony) method.invoke(
					 * mTpManager, null); iTelephony.endCall(); } catch
					 * (Exception e) { e.printStackTrace(); }
					 */
					try {
						Class<?> clazz = Class
								.forName("android.os.ServiceManager");
						Method method = clazz.getMethod("getService",
								String.class);
						IBinder iBinder = (IBinder) method.invoke(null,
								Context.TELEPHONY_SERVICE);
						ITelephony iTelephony = ITelephony.Stub
								.asInterface(iBinder);
						iTelephony.endCall();
					} catch (Exception e) {
						e.printStackTrace();
					}

					/*
					 * ContentResolver resolver = getContentResolver(); String
					 * where = "number=?"; Uri url = CallLog.CONTENT_URI;
					 * String[] selectionArgs = { incomingNumber }; int delete =
					 * resolver.delete(url, where, selectionArgs); Log.d(TAG,
					 * "delete:" + delete);
					 */

					final ContentResolver resolver = getContentResolver();
					final Uri uri = CallLog.Calls.CONTENT_URI;
					ContentObserver observer = new ContentObserver(
							new Handler()) {
						@Override
						public void onChange(boolean selfChange) {
							super.onChange(selfChange);
							String where = CallLog.Calls.NUMBER + "=?";
							String[] selectionArgs = { incomingNumber };
							int delete = resolver.delete(uri, where,
									selectionArgs);
							Log.d(TAG, "delete:" + delete);
						}
					};
					resolver.registerContentObserver(uri, true, observer);
				}
			}
		}
	}

	/**
	 * 用来拦截短信
	 * 
	 */
	private class CallSmsSafeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// 拦截短信
			Object[] objects = (Object[]) intent.getExtras().get("pdus");

			// 选择一个就行了把,不用遍历,因为号码都是一样的
			for (Object object : objects) {
				SmsMessage msg = SmsMessage.createFromPdu((byte[]) object);
				String address = msg.getOriginatingAddress();
				int findType = mBlackDao.findType(address);
				if (findType == BlackBean.BLACK_TYPE_SMS
						|| findType == BlackBean.BLACK_TYPE_ALL) {
					abortBroadcast();
					Log.d(TAG, "拦截短信:" + address);
					break;
				}
			}
		}
	}
}
