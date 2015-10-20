package com.tp.safeguard.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtils {

	private static SharedPreferences sp;

	private static SharedPreferences getSharedPreferences(Context context) {
		if (sp == null) {
			sp = context.getSharedPreferences("sjws", Context.MODE_PRIVATE);
		}
		return sp;
	}

	/*
	 * public static String getString(Context context, String key, String
	 * defValue) { SharedPreferences sp = getPreferences(context); return
	 * sp.getString(key, defValue); }
	 */

	public static String getString(Context context, String key, String defValue) {
		SharedPreferences sp = getSharedPreferences(context);
		return sp.getString(key, defValue);
	}

	// 存储String数据
	public static void putString(Context context, String key, String value) {
		SharedPreferences sp = getSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.apply();

	}

	/**
	 * 获得boolean型数据
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		SharedPreferences sp = getSharedPreferences(context);
		return sp.getBoolean(key, defValue);
	}

	/**
	 * 存储Boolean数据
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = getSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	/**
	 * 获得int型数据
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Context context, String key, int defValue) {
		SharedPreferences sp = getSharedPreferences(context);
		return sp.getInt(key, defValue);
	}

	/**
	 * 存储int数据
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sp = getSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.apply();
	}
}
