package com.tp.safeguard.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Switch;

/**
 * 电话号码归属地对应数据库的访问
 */
public class NumAddressDao {

	/**
	 * 通过给定的号码,查询到归属地
	 * 
	 * @param context
	 *            上下文,用来获取数据库文件地址
	 * @param number
	 *            号码
	 * @return 返回归属地
	 */
	public static String queryNumAddress(Context context, String number) {
		// 如果数据库没有的话,则会得到什么,null吗?
		// SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(new File(
		// context.getFilesDir(), "address.db"), null);
		String path = new File(context.getFilesDir(), "address.db")
				.getAbsolutePath();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		String address = "未知号码";
		String sql = null;
		if (number.matches("^1[34578]\\d{5,9}")) {
			String phoneNumber = number.substring(0, 7);
			sql = "select cardtype from info where mobileprefix=?";
			Cursor cursor = db.rawQuery(sql, new String[] { phoneNumber });
			if (cursor != null) {
				if (cursor.moveToNext()) {
					address = cursor.getString(0);
				}
				cursor.close();
			}
		}

		if ("未知号码".equals(address)) {
			switch (number.length()) {
			case 3:
				address = "紧急电话";
				break;
			case 4:
				address = "模拟器号码";
				break;
			case 5:
				address = "服务号码";
				break;
			case 6:
				address = "公司短号";
				break;
			case 7:
			case 8:
				address = "本地座机";
				break;
			case 10:
			case 11:
			case 12:
				String areaNum = number.substring(0, 3);
				sql = "select city from info where area=?";
				Cursor cursor = db.rawQuery(sql, new String[] { areaNum });
				if (cursor != null) {
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}
					cursor.close();
				}

				if ("未知号码".equals(address)) {
					areaNum = number.substring(0, 4);
					sql = "select city from info where area=?";
					cursor = db.rawQuery(sql, new String[] { areaNum });
					if (cursor != null) {
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
					}
				}
			}
		}
		db.close();
		return address;
	}
}
