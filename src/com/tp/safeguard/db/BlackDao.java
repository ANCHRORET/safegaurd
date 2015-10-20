package com.tp.safeguard.db;

import java.util.ArrayList;
import java.util.List;

import com.tp.safeguard.bean.BlackBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BlackDao {

	private static final String TAG = "BlackDao";
	private BlackDBHelper mHelper;

	public BlackDao(Context context) {
		mHelper = new BlackDBHelper(context);
	}

	/**
	 * 添加一条数据
	 * 
	 * @param number
	 * @param type
	 * @return 为false代表添加失败
	 */
	public boolean insert(String number, int type) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackDB.TableBlack.COLUMN_NUMBER, number);
		values.put(BlackDB.TableBlack.COLUMN_TYPE, type);
		long rowID = db.insert(BlackDB.TableBlack.TABLE_NAME, null, values);

		db.close();
		return rowID != -1;
	}

	/**
	 * 删除一条数据
	 * 
	 * @param number
	 * @return true 代表删除成功
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		String whereClause = BlackDB.TableBlack.COLUMN_NUMBER + "=?";
		String[] whereArgs = { number };
		int num = db.delete(BlackDB.TableBlack.TABLE_NAME, whereClause,
				whereArgs);

		db.close();

		return num != 0;

	}

	/**
	 * 更新一条数据
	 * 
	 * @param number
	 * @param type
	 * @return true 代表更新成功
	 */
	public boolean update(String number, int type) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackDB.TableBlack.COLUMN_TYPE, type);
		String whereClause = BlackDB.TableBlack.COLUMN_NUMBER + "=?";
		String[] whereArgs = { number };
		int num = db.update(BlackDB.TableBlack.TABLE_NAME, values, whereClause,
				whereArgs);

		db.close();

		return num != 0;
	}

	/**
	 * 通过电话号码来查询类型type
	 * 
	 * @param number
	 * @return 如果为-1,则代表没有记录
	 */
	public int findType(String number) {
		SQLiteDatabase db = mHelper.getReadableDatabase();

		String selection = BlackDB.TableBlack.COLUMN_NUMBER + "=?";
		String[] selectionArgs = { number };
		String[] columns = { BlackDB.TableBlack.COLUMN_TYPE };
		Cursor cursor = db.query(BlackDB.TableBlack.TABLE_NAME, columns,
				selection, selectionArgs, null, null, null);

		int type = -1;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				type = cursor.getInt(0);
			}
			cursor.close();
		}

		db.close();

		return type;
	}

	/**
	 * 查询所有的黑名单数据
	 * 
	 * @return 包含所有信息的list集合
	 */
	public List<BlackBean> queryAll() {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String[] columns = { BlackDB.TableBlack.COLUMN_NUMBER,
				BlackDB.TableBlack.COLUMN_TYPE };
		Cursor cursor = db.query(BlackDB.TableBlack.TABLE_NAME, columns, null,
				null, null, null, null);
		List<BlackBean> list = new ArrayList<BlackBean>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(0);
				int type = cursor.getInt(1);
				BlackBean bean = new BlackBean();
				bean.number = number;
				bean.type = type;
				list.add(bean);
			}
			cursor.close();
		}
		db.close();
		return list;
	}

	/**
	 * 从指定位置开始,查询固定条目的记录
	 * 
	 * @param size
	 *            查询的条目
	 * @param offset
	 *            查询的起始位置
	 * @return 查询的结果
	 */
	public List<BlackBean> queryPart(int size, int offset) {

		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select " + BlackDB.TableBlack.COLUMN_NUMBER + ","
				+ BlackDB.TableBlack.COLUMN_TYPE + " from "
				+ BlackDB.TableBlack.TABLE_NAME + " limit " + size + " offset "
				+ offset;
		Cursor cursor = db.rawQuery(sql, null);

		List<BlackBean> list = new ArrayList<BlackBean>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(0);
				int type = cursor.getInt(1);
				BlackBean bean = new BlackBean();
				bean.number = number;
				bean.type = type;
				list.add(bean);
			}
			cursor.close();
		}
		db.close();
		return list;
	}

}
