package com.tp.safeguard.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;

public class ServiceUtils {

	/**
	 * 判断服务是否开启
	 * 
	 * @param context 上下文变量
	 * @param clazz 服务的Class对象
	 * @return true 则代表服务开启
	 */
	public static boolean isServiceRunning(Context context,
			Class<? extends Service> clazz) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am
				.getRunningServices(Integer.MAX_VALUE);
		if (runningServices != null) {
			for (RunningServiceInfo rSInfo : runningServices) {
				ComponentName service = rSInfo.service;
				String className = service.getClassName();
				if (className.equals(clazz.getName())) {
					return true;
				}
			}
		}
		return false;
	}
}
