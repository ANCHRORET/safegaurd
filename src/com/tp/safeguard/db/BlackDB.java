package com.tp.safeguard.db;

public interface BlackDB {

	String DBNAME = "black.db";
	int VERSION = 1;

	public interface TableBlack {

		String TABLE_NAME = "black";
		String COLUMN_ID = "_id";
		String COLUMN_TYPE = "type";
		String COLUMN_NUMBER = "number";

		String SQL_CREATE_TABLE = "create table " + TABLE_NAME + "("
				+ COLUMN_ID + " Integer primary key autoincrement, "
				+ COLUMN_NUMBER + " text unique, " + COLUMN_TYPE
				+ " Integer)";
	}
}
