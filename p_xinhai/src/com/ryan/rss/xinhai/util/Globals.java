package com.ryan.rss.xinhai.util;

public class Globals {

	public static boolean prefs_isFirstRun = true;
	public static boolean prefs_autoScroll = true;// 自动滚动
	public static int prefs_categoryIndex = 0;// 当前阅读的目录索引.默认为0
	public static int prefs_textSize = 19;// 默认字号
	public static int prefs_scrollSpeed = 5;// 默认滚动速度
	public static String prefs_recent_link = "";// 最近阅读url,写入系统设置
	// 本地内容的rss源更新时间,在用户点击刷新按钮时,用来判断本地更新时间与服务器上rss的更新时间
	public static String prefs_last_modify_date = "Tue, 14 May 2013 09:12:46 GMT";

	// 05-15 17:53:25.414: V/MainActivity(7987): has update.Tue, 14 May 2013
	// 09:12:46 GMT

	public static int itemId;// 正在阅读文章的ID,用来实现"上一篇,下一篇"功能

	public final static String RSS_URL = "http://www.60hy.com/xinhai/?feed=rss2";
	public final static String WEB_URL = "http://tieba.baidu.com/f?kw=%D0%C4%B7%A8";

}
