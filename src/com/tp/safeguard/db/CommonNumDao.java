package com.tp.safeguard.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tp.safeguard.bean.CommonNumChildBean;
import com.tp.safeguard.bean.CommonNumGroupBean;

/**
 * 访问常用号码数据库的dao类
 * 
 */
public class CommonNumDao {

	public static List<CommonNumGroupBean> queryAll(Context context) {
		List<CommonNumGroupBean> list = new ArrayList<CommonNumGroupBean>();
		File file = new File(context.getFilesDir(), "commonnum.db");
		if (!file.exists()) {
			return list;
		}

		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(),
				null, SQLiteDatabase.OPEN_READONLY);
		String groupSql = "select name,idx from classlist";
		Cursor cursor = db.rawQuery(groupSql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String title = cursor.getString(0);
				String index = cursor.getString(1);
				CommonNumGroupBean groupBean = new CommonNumGroupBean();
				groupBean.title = title;
				groupBean.list = new ArrayList<CommonNumChildBean>();
				list.add(groupBean);

				String sql = "select number,name from table" + index;
				Cursor query = db.rawQuery(sql, null);
				if (query != null) {
					while (query.moveToNext()) {
						String number = query.getString(0);
						String name = query.getString(1);
						CommonNumChildBean childBean = new CommonNumChildBean();
						childBean.name = name;
						childBean.number = number;
						groupBean.list.add(childBean);
					}
					query.close();
				}
			}
			cursor.close();
		}
		db.close();
		return list;
	}
}
