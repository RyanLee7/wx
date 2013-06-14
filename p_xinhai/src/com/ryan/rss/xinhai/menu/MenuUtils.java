package com.ryan.rss.xinhai.menu;

import java.util.ArrayList;
import java.util.List;

import com.ryan.rss.xinhai.R;

public class MenuUtils {

	// ArticleActivity使用到的菜单项
	// public static final int MENU_ARTICLE_RECENT = 1;// 最近阅读
	public static final int MENU_ARTICLE_LISTREFLESH = 2;// 列表更新
	public static final int MENU_ARTICLE_APPUPDATE = 3;// 应用更新
	public static final int MENU_ARTICLE_ABOUT = 4;// 关于
	public static final int MENU_EXIT = 5;// 退出
	public static final int MENU_FEEDBACK = 6;// 反馈问题

	// ContentActivity使用到的菜单项
	public static final int MENU_CONTENT_PREVIOUS = 1;// 上一篇
	public static final int MENU_CONTENT_NEXT = 2;// 下一篇
	public static final int MENU_CONTENT_TOP = 3;// 回到顶部
	public static final int MENU_CONTENT_BOTTOM = 4;// 回到底部
	public static final int MENU_CONTENT_TXTUP = 5;// 字体+
	public static final int MENU_CONTENT_TXTDOWN = 6;// 字体-
	public static final int MENU_CONTENT_FAST = 7;// 滚动速度+
	public static final int MENU_CONTENT_SLOW = 8;// 滚动速度-

	/**
	 * ArticleActivity的菜单项
	 * 
	 * @return
	 */
	public static List<MenuInfo> getArticleMenuList() {

		// list.add方法时序影响菜单项的排序顺序
		List<MenuInfo> list = new ArrayList<MenuInfo>();

		list.add(new MenuInfo(MENU_ARTICLE_LISTREFLESH,
				R.string.menu_listreflesh, R.drawable.menu_article_update,
				false));
		list.add(new MenuInfo(MENU_FEEDBACK, R.string.menu_feedback,
				R.drawable.menu_feedback, false));

		list.add(new MenuInfo(MENU_ARTICLE_ABOUT, R.string.menu_about,
				R.drawable.menu_about, false));

		list.add(new MenuInfo(MENU_EXIT, R.string.menu_exit,
				R.drawable.menu_exit, false));

		return list;

	}

	/**
	 * ContentActivity的菜单
	 * 
	 * @return
	 */
	public static List<MenuInfo> getContentMenuList() {

		List<MenuInfo> list = new ArrayList<MenuInfo>();
		// list.add方法时序影响菜单项的排序顺序

		list.add(new MenuInfo(MENU_CONTENT_PREVIOUS, R.string.menu_previous,
				R.drawable.menu_previous, false));
		list.add(new MenuInfo(MENU_CONTENT_TOP, R.string.menu_top,
				R.drawable.menu_home, false));
		list.add(new MenuInfo(MENU_CONTENT_FAST, R.string.menu_fast,
				R.drawable.menu_fast, false));
		list.add(new MenuInfo(MENU_CONTENT_TXTUP, R.string.menu_txtup,
				R.drawable.menu_font_add, false));

		list.add(new MenuInfo(MENU_CONTENT_NEXT, R.string.menu_next,
				R.drawable.menu_next, false));
		list.add(new MenuInfo(MENU_CONTENT_BOTTOM, R.string.menu_bottom,
				R.drawable.menu_end, false));
		list.add(new MenuInfo(MENU_CONTENT_SLOW, R.string.menu_slow,
				R.drawable.menu_slow, false));
		list.add(new MenuInfo(MENU_CONTENT_TXTDOWN, R.string.menu_txtdown,
				R.drawable.menu_font_del, false));

		return list;

	}
}
