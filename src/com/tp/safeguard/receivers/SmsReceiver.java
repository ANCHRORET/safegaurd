package com.tp.safeguard.receivers;

import com.tp.safeguard.services.GPSService;
import com.tp.safeguard.services.PlayAlarmService;
import com.tp.safeguard.utils.Constants;
import com.tp.safeguard.utils.PreferenceUtils;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean toggle = PreferenceUtils.getBoolean(context,
				Constants.SJFD_TOGGLE, false);
		if (!toggle) {
			return;
		}
		// 到底要不要判断手机丢没丢,不用了算了
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objs) {
			SmsMessage msg = SmsMessage.createFromPdu((byte[]) obj);
			String address = msg.getOriginatingAddress();
			String safeNum = PreferenceUtils.getString(context,
					Constants.SJFD_SAFENUM, "");

			// if (safeNum.equals(address)) { TODO:
			String msgBody = msg.getMessageBody();
			if (msgBody.contains("#*location*#")) {
				// 发送位置信息给安全号码
				context.startService(new Intent(context, GPSService.class));
				abortBroadcast();
			} else if (msgBody.contains("#*wipedata*#")) {
				// 擦除数据
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName who = new ComponentName(context,
						SafeAdminReceiver.class);
				if (dpm.isAdminActive(who)) {
					dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
				}
				abortBroadcast();
			} else if (msgBody.contains("#*alarm*#")) {
				// 播放报警音乐
				context.startService(new Intent(context, PlayAlarmService.class));
				abortBroadcast();
			} else if (msgBody.contains("#*lockscreen*#")) {
				// 锁屏
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName who = new ComponentName(context,
						SafeAdminReceiver.class);
				if (dpm.isAdminActive(who)) {
					dpm.lockNow();
					dpm.resetPassword("753146", 0);
				}
				abortBroadcast();
			}
		}
	}
}
