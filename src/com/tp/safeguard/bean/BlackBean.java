package com.tp.safeguard.bean;

public class BlackBean {

	/**
	 * 选中了电话拦截
	 */
	public static final int BLACK_TYPE_CALL = 0;
	/**
	 * 选中了短信拦截
	 */
	public static final int BLACK_TYPE_SMS = 1;
	/**
	 * 选中了拦截全部
	 */
	public static final int BLACK_TYPE_ALL = 2;

	/**
	 * 黑名单的号码
	 */
	public String number;
	/**
	 * 黑名单的拦截类型
	 */
	public int type = -1;
}
