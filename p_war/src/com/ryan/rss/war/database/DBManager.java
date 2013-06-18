package com.ryan.rss.war.database;

import java.util.ArrayList;
import java.util.List;

import com.ryan.rss.war.util.RSSItem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库管理类
 * 
 * @author Ryan
 * 
 *         2013-4-15
 */
/**
 * @author ryanlee
 * 
 */
public class DBManager {

	private static final String LOGTAG = "DBManager";
	private static final boolean DEBUG_MODE = false;

	private DBHelper helper;
	private SQLiteDatabase db;
	private boolean dbState;

	public DBManager(Context context) {

		helper = new DBHelper(context);
		dbState = false;

	}

	/**
	 * @param context
	 * @param name
	 *            数据库名称 data.db
	 * @param path
	 *            数据库文件在SDcard上的路径 "/war/"
	 */
	public DBManager(Context context, String name, String path) {

		helper = new DBHelper(context, name, path);
		dbState = false;

	}

	private synchronized void openDataBase() {

		// 自动管理数据库的打开与关闭. 外部调用增删改查时,先打开数据库,函数执行完毕后关闭数据库.
		// 下次打开前检查数据库有没有打开,如果已经打开则不用再次打开.
		if (dbState == true) {
			// 已经打开
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "database has open ");
			}

		} else {
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "openDataBase() ");
			}

			db = helper.getWritableDatabase();
			dbState = true;
		}

	}

	private synchronized void closeDataBase() {
		if (dbState) {
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "closeDataBase()");
			}

			db.close();
			dbState = false;

		} else {
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "database already close.");
			}

		}
	}

	/**
	 * 插入数据
	 * 
	 * @param itemList
	 */
	public void intert(List<RSSItem> itemList) {

		openDataBase();
		// 开始事务
		db.beginTransaction();

		try {

			// item(_id,title,description,link,pubDate,category)
			for (RSSItem item : itemList) {
				db.execSQL("INSERT INTO item VALUES(null,?,?,?,?,?)",
						new Object[] { item.title, item.description, item.link,
								item.pubDate, item.category });
			}
			// 事务成功完成
			db.setTransactionSuccessful();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// 结束事务
			db.endTransaction();
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "insert success.");
			}

			closeDataBase();

		}
	}

	/**
	 * 删除所有数据
	 */
	public void deleteAll() {
		openDataBase();
		db.delete("item", "_id>=?", new String[] { String.valueOf(0) });
		closeDataBase();
	}

	/**
	 * 返回所有数据
	 * 
	 * @return
	 */
	public List<RSSItem> queryAll() {
		openDataBase();

		ArrayList<RSSItem> itemList = new ArrayList<RSSItem>();
		Cursor c = db.rawQuery("SELECT * FROM item ORDER BY _id DESC", null);

		// item(_id,title,description,link,pubDate,category)
		while (c.moveToNext()) {

			RSSItem item = new RSSItem();
			item.id = c.getInt(c.getColumnIndex("_id"));
			item.title = c.getString(c.getColumnIndex("title"));
			item.description = c.getString(c.getColumnIndex("description"));
			item.link = c.getString(c.getColumnIndex("link"));
			item.pubDate = c.getString(c.getColumnIndex("pubDate"));
			item.category = c.getString(c.getColumnIndex("category"));

			if (DEBUG_MODE) {
				Log.v(LOGTAG, "category-" + item.category);
			}

			itemList.add(item);

		}

		c.close();
		c = null;
		closeDataBase();
		return itemList;
	}

	/**
	 * 查询目录信息
	 * 
	 * @return 目录集合
	 */
	public List<String> queryCategory() {

		openDataBase();
		ArrayList<String> categorys = new ArrayList<String>();

		// item(_id,title,description,link,pubDate,category)
		Cursor c = db.rawQuery(
				"SELECT DISTINCT category FROM item ORDER BY _id DESC", null);
		while (c.moveToNext()) {
			categorys.add(c.getString(c.getColumnIndex("category")));
		}
		c.close();
		c = null;
		closeDataBase();

		if (DEBUG_MODE) {
			Log.v(LOGTAG, "category:" + categorys.toString());
		}

		return categorys;
	}

	/**
	 * 根据目录查询相关文章
	 * 
	 * @param arg
	 *            目录
	 * @return
	 */
	public List<RSSItem> queryFromCategory(String arg) {
		openDataBase();

		ArrayList<RSSItem> itemList = new ArrayList<RSSItem>();
		Cursor c = db.rawQuery(
				"SELECT * FROM item WHERE category=? ORDER BY _id DESC",
				new String[] { arg });

		// item(_id,title,description,link,pubDate,category)
		while (c.moveToNext()) {

			RSSItem item = new RSSItem();
			item.id = c.getInt(c.getColumnIndex("_id"));
			item.title = c.getString(c.getColumnIndex("title"));
			item.description = c.getString(c.getColumnIndex("description"));
			item.link = c.getString(c.getColumnIndex("link"));
			item.pubDate = c.getString(c.getColumnIndex("pubDate"));
			item.category = c.getString(c.getColumnIndex("category"));

			if (DEBUG_MODE) {
				Log.v(LOGTAG, "queryFromCategory" + item.title);
			}

			itemList.add(item);

		}

		c.close();
		c = null;
		closeDataBase();
		return itemList;
	}

}
