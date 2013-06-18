package com.ryan.rss.war.util;

public class Globals {

	public static boolean prefs_isFirstRun = true;
	public static boolean prefs_autoScroll = true;// 自动滚动
	public static int prefs_categoryIndex = 0;// 当前阅读的目录索引.默认为0
	public static int prefs_textSize = 19;// 默认字号
	public static int prefs_scrollSpeed = 5;// 默认滚动速度
	public static String prefs_recent_link = "";// 最近阅读url,写入系统设置

	// 本地内容的rss源更新时间,在用户点击刷新按钮时,用来判断本地更新时间与服务器上rss的更新时间
	public static String prefs_last_modify_date = "Sun, 16 Jun 2013 08:46:13 GMT";

	// 06-16 16:51:14.507: V/MainActivity(12218): Sun, 16 Jun 2013 08:46:13 GMT

	public static int itemId;// 正在阅读文章的ID,用来实现"上一篇,下一篇"功能

	public static final boolean AD_MODE = true;// 是否开启广告
	public static boolean NETWORK_ENABLE = false;// 网络状态

	public static final String AD_KEY = "zr2lu80qpxc818";// 果盟广告KEY

	//
	public final static String DATABASE_PATH = "/war_story/";// 数据库路径
	public final static String DATABASE_NAME = "war.db";// 数据库名称

	public final static String RSS_URL = "http://www.60hy.com/war/?feed=rss2";
	// 百度魔兽同人小说吧
	public final static String WEB_URL = "http://tieba.baidu.com/f?kw=%C4%A7%CA%DE%CD%AC%C8%CB%D0%A1%CB%B5";

}
