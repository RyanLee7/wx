package com.ryan.rss.war.https;

import java.io.InputStream;
import java.net.HttpURLConnection;

import java.net.URL;

public class HttpUtil {

	public static final String LOGTAG = "HttpUtil";

	/**
	 * 获得http流
	 * 
	 * @param urlStr
	 * @return
	 */
	public static InputStream getHttpUrlStream(String urlStr) {

		HttpURLConnection connection = null;
		InputStream is = null;

		try {

			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			is = connection.getInputStream();

			return is;

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取最后更新的时间信息
	 * 
	 * @param urlStr
	 * @return
	 */
	public static String getHttpLastModify(String urlStr) {

		String result = "";
		HttpURLConnection connection = null;
		// InputStream is = null;
		// int fileSize = 0;

		try {

			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			result = connection.getHeaderField("Last-Modified");
			// fileSize = connection.getContentLength();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return result;

	}
}
