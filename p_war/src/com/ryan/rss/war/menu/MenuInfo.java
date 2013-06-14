package com.ryan.rss.war.menu;

/**
 * @author Ryan
 * 
 *         2013-4-24
 */
public class MenuInfo {

	public int menuId;
	public int title;
	public int imgSrc;
	public boolean isHide;

	/**
	 * 
	 * @param id
	 *            序号
	 * @param title
	 *            标题
	 * @param img
	 *            图片源
	 * @param isHide
	 *            是否隐藏
	 */
	public MenuInfo(int id, int title, int img, boolean isHide) {
		this.menuId = id;
		this.title = title;
		this.imgSrc = img;
		this.isHide = isHide;
	}

}
