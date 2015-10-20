package com.tp.safeguard.bean;

import android.graphics.drawable.Drawable;

public class AppLockBean {

	/**
	 * 应用的图标
	 */
	public Drawable icon;
	/**
	 * 安装的应用名字
	 */
	public String appName;
	/**
	 * app的包名
	 */
	public String packageName;
	/**
	 * app是否加锁,代表枷锁的状态,true代表加锁了
	 */
	public boolean lockState;
}
