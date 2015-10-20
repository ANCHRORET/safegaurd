package com.tp.safeguard.bean;

import android.graphics.drawable.Drawable;

public class AppInfoBean {

	public static final String RAWINSTALLED = "内存安装";
	public static final String SDINSTALLED = "sd卡安装";

	/**
	 * 应用的图标
	 */
	public Drawable icon;
	/**
	 * 安装的应用名字
	 */
	public String appName;
	/**
	 * 安装在那个位置,link$RAWINSTALLED link$SDINSTALLED
	 */
	public String location;
	/**
	 * app占用的内存大小
	 */
	public long size;
	/**
	 * true 代表是系统的应用
	 */
	public boolean isSystemApp;

	/**
	 * app的包名
	 */
	public String packageName;
}
