package com.ryan.rss.util;

import android.content.Context;
import android.util.Log;

/**
 * @author Ryan
 * 
 *         2013-4-7
 */
public class UpdateManager {

	private Context mContext;
	private static final String packName = "com.ryan.rss";
	private static final String LOGTAG = "UpdateManager";

	public UpdateManager(Context context) {
		mContext = context;
	}

	/**
	 * 与本地版本比较,判断是否需要升级
	 * 
	 * @param version
	 *            远程服务器上的版本号
	 * @return
	 */
	public boolean needUpdate(String version) {

		int ver = Integer.parseInt(version);
		int versionCode = getVersionCode(mContext);
		if (ver > versionCode) {
			return true;
		}

		return false;
	}

	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(packName,
					0).versionCode;
			Log.v(LOGTAG, packName + "-version:" + versionCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionCode;
	}
}
