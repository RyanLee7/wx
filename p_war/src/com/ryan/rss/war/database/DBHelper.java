package com.ryan.rss.war.database;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库助手类
 * 
 * @author Ryan
 * 
 *         2013-4-12
 */
public class DBHelper extends SDcardSQLiteOpenHelper {

	private static final String DEFAULT_DATABASE_NAME = "data.db";
	private static final int DATABASE_VERSION = 1;

	/**
	 * 默认存储在手机中
	 * 
	 * @param context
	 */
	public DBHelper(Context context) {
		super(context, DEFAULT_DATABASE_NAME, null, DATABASE_VERSION);

	}

	/**
	 * 优先储存在sdcard中，需指定路径和数据库名
	 * 
	 * @param context
	 * @param name
	 *            "data.db"
	 * @param path
	 *            "/path/"
	 */
	public DBHelper(Context context, String name, String path) {
		super(context, name, null, DATABASE_VERSION, null, path);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// item(_id,title,description,link,pubDate,category)
		db.execSQL("CREATE TABLE IF NOT EXISTS item"
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,title VARCHAR,description TEXT,link VARCHAR,pubDate VARCHAR,category VARCHAR)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
