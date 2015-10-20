package com.tp.safeguard.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.tp.safeguard.bean.AppInfoBean;

/**
 * 查询手机上安装的包信息
 * 
 */
public class AppInfoProvider {

	/**
	 * 查询手机上安装的所有的包的信息
	 * 
	 * @param context
	 * @return 没有则返回空的list
	 */
	public static List<AppInfoBean> getAllInstalled(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		List<AppInfoBean> list = new ArrayList<AppInfoBean>();
		for (PackageInfo packageInfo : installedPackages) {
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			String sourceDir = applicationInfo.sourceDir;
			File file = new File(sourceDir);
			long size = file.length();
			String appName = applicationInfo.loadLabel(pm).toString();
			Drawable icon = applicationInfo.loadIcon(pm);

			AppInfoBean bean = new AppInfoBean();
			if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				bean.location = AppInfoBean.SDINSTALLED;
			} else {
				bean.location = AppInfoBean.RAWINSTALLED;
			}
			boolean isSystemApp = false;
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				isSystemApp = true;
			}
			bean.size = size;
			bean.appName = appName;
			bean.icon = icon;
			bean.isSystemApp = isSystemApp;
			bean.packageName = packageInfo.packageName;
			list.add(bean);
		}
		return list;
	}
}
