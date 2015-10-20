package com.tp.safeguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackDBHelper extends SQLiteOpenHelper {

	public BlackDBHelper(Context context) {
		super(context, BlackDB.DBNAME, null, BlackDB.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BlackDB.TableBlack.SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
