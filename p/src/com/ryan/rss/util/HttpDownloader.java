package com.ryan.rss.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.util.Log;

/**
 * http下载远程文件的类,使用handler向主线程发送状态
 * 
 * @author Ryan
 * 
 *         2013-4-11
 */
public class HttpDownloader {

	// 下载状态通知的handler1
	private Handler downloadHandler;

	private static final int DOWNLOAD_START = 0x01;
	private static final int DOWNLOAD_PROGRESS = 0x02;
	private static final int DOWNLOAD_FINISH = 0x03;

	public HttpDownloader(Handler handler) {
		downloadHandler = handler;
	}

	/**
	 * @param urlStr
	 *            资源路径
	 * @param path
	 *            存储在本地的路径
	 * @param fileName
	 *            文件命名
	 * @return 1:文件存在 -1:下载文件出错 0:下载成功
	 */
	public int download(String urlStr, String path, String fileName) {
		InputStream is = null;

		try {

			is = getInputStreamFromUrl(urlStr);
			boolean result = FileUtil.write2SDcardFromInput(path, fileName, is);
			if (!result) {
				return -1;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	public InputStream getInputStreamFromUrl(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		InputStream is = null;
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		is = urlConn.getInputStream();
		
		return is;
	}
}
