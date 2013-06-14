package com.ryan.rss.war.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.util.Log;

public class RSSItem {

	private static final String LOGTAG = "RSSItem";
	public String title;
	public String description;
	public String link;
	public String category;
	public String pubDate;
	public int id = 0;// 在数据库中的id号,从数据库查询时自动生成.数字是连续的

	public RSSItem() {

	}

	/**
	 * 写入日期时,转化显示方式
	 * 
	 * @param d
	 */
	public void setPubDate(String d) {

		// <pubDate>Fri, 19 Apr 2013 10:35:07 +0000</pubDate>
		pubDate = formatDate(d);
		Log.v(LOGTAG, pubDate);
	}

	/**
	 * 返回格式化的字符串 "yyyy-MM-dd"
	 * 
	 * @param d
	 *            "EEE, dd MMM yyyy HH:mm:ss Z"
	 * @return "yyyy-MM-dd"
	 */
	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("finally")
	private String formatDate(String d) {
		String dateFormat = "";
		Date date = null;
		SimpleDateFormat inFormat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {

			// 读入时间
			date = inFormat.parse(d);
			dateFormat = outFormat.format(date);

		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			return dateFormat;
		}

	}

	/**
	 * 获取摘要
	 * 
	 * @return
	 */
	public String getSummary() {

		if (description.length() <= 80) {
			return description;
		} else {
			return description.substring(0, 80) + "... ...";
		}
	}

	/**
	 * 测试用.
	 * 
	 * @param title
	 * @param description
	 * @param link
	 * @param category
	 * @param pubDate
	 */
	public RSSItem(String title, String description, String link,
			String category, String pubDate) {

		this.title = title;
		this.description = description;
		this.link = link;
		this.category = category;
		this.pubDate = pubDate;
	}

	public String toString() {
		return title;
	}

}
