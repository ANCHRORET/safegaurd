package com.tp.safeguard.business;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.tp.safeguard.bean.ProcessBean;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

/**
 * 进程相关的方法
 * 
 */
public class ProcessProvider {

	private static final String TAG = "ProcessProvider";

	/**
	 * 获得正在运行的进程数
	 * 
	 * @param context
	 * @return
	 */
	public static int getRunningProcessCount(Context context) {
		ActivityManager aManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningProcesses = aManager
				.getRunningAppProcesses();

		if (runningProcesses != null) {
			return runningProcesses.size();
		}
		return 0;
	}

	/**
	 * 获得所有潜在的能运行的及已经运行的进程
	 * 
	 * @param context
	 * @return
	 */
	public static int getTotalProcessCount(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		Log.d(TAG, (packages == null) + "");
		if (packages != null) {
			HashSet<Object> set = new HashSet<Object>();
			for (PackageInfo packageInfo : packages) {
				ApplicationInfo applicationInfo = packageInfo.applicationInfo;
				set.add(applicationInfo.processName);

				ActivityInfo[] activities = packageInfo.activities;
				if (activities != null) {
					for (ActivityInfo activityInfo : activities) {
						set.add(activityInfo.processName);
					}
				}
				ActivityInfo[] receivers = packageInfo.receivers;
				if (receivers != null) {
					for (ActivityInfo receiverInfo : receivers) {
						set.add(receiverInfo.processName);
					}
				}
				ServiceInfo[] services = packageInfo.services;

				if (services != null) {
					for (ServiceInfo serviceInfo : services) {
						set.add(serviceInfo.processName);
					}
				}
				ProviderInfo[] providers = packageInfo.providers;
				if (providers != null) {
					for (ProviderInfo providerInfo : providers) {
						set.add(providerInfo.processName);
					}
				}
			}
			return set.size();
		}
		return 0;
	}

	/**
	 * 返回进程已经使用的内存
	 * 
	 * @param context
	 * @return
	 */
	public static long getFreeRaw(Context context) {

		ActivityManager aManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		aManager.getMemoryInfo(outInfo);

		return outInfo.availMem;
	}

	/**
	 * 所有可用运行内存
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static long getTotalRaw(Context context) {

		ActivityManager aManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		aManager.getMemoryInfo(outInfo);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			return outInfo.totalMem;
		} else {
			return getLowLevelTotalMem();
		}
	}

	/**
	 * 低版本的情况下获得总的内存大小
	 * 
	 * @return
	 */
	private static long getLowLevelTotalMem() {
		// proc\\meminfo
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/proc/meminfo"));
			// MemTotal: 510704 kB
			String info = reader.readLine();
			String totalMem = info.split(" ")[1];
			return Long.parseLong(totalMem) * 1000;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader = null;
			}
		}
	}

	public static List<ProcessBean> getAllProcess(Context context) {

		List<ProcessBean> list = new ArrayList<ProcessBean>();

		ActivityManager aManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();

		List<RunningAppProcessInfo> processes = aManager
				.getRunningAppProcesses();
		Log.d(TAG, "" + processes.size());
		if (list != null) {
			for (RunningAppProcessInfo processInfo : processes) {
				// 进程名就是包名,包名是一个程序的唯一标识
				String processName = processInfo.processName;
				int pid = processInfo.pid;
				Log.d(TAG, "" + processName);

				ProcessBean bean = new ProcessBean();
				try {
					// flag=0获得所有的应用数据
					ApplicationInfo applicationInfo = pm.getApplicationInfo(
							processName, 0);
					Drawable icon = applicationInfo.loadIcon(pm);
					String loadLabel = applicationInfo.loadLabel(pm).toString();
					bean.icon = icon;
					bean.name = loadLabel;

					int flag = applicationInfo.flags;
					if ((flag & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
						bean.isSystemProcess = true;
					} else {
						bean.isSystemProcess = false;
					}

				} catch (NameNotFoundException e) {
					e.printStackTrace();
					bean.name = processName;
					bean.isSystemProcess = true;
				}

				android.os.Debug.MemoryInfo[] memoryInfo = aManager
						.getProcessMemoryInfo(new int[] { pid });
				int totalPss = memoryInfo[0].getTotalPss() * 1024;
				bean.content = totalPss;
				bean.packageName = processName;
				list.add(bean);
			}
		}
		return list;
	}

	/**
	 * 根据给定的包名,删除其所有相关的进程
	 * 
	 * @param context
	 * @param packageName
	 */
	public static void killProcess(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(packageName);
	}
}
