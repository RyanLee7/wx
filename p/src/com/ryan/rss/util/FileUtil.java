package com.ryan.rss.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

/**
 * 文件处理类,包括读取,写入功能
 * 
 * @author Ryan
 * 
 *         2013-4-11
 */
public class FileUtil {

	private final static boolean DEBUG_MODE = true;
	private static final String LOGTAG = "FileUtil";

	private static String appDir;
	private static String sdcardDir;

	/**
	 * 检测sdcard状态,sdcard存在时设置sdcard路径
	 * 
	 * @return
	 */
	public static boolean checkSDCard() {
		boolean externalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			sdcardDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/";

			if (DEBUG_MODE) {
				Log.v(LOGTAG, "---sdcard root dir:" + sdcardDir + "---");
			}

			externalStorageWriteable = true;
		} else {
			Log.v(LOGTAG, "----------sdcard not writeable-------------");
			externalStorageWriteable = false;
		}
		return externalStorageWriteable;
	}

	public static void createSDFile(String fileName) throws IOException {

		File file = new File(fileName);
		file.createNewFile();

	}

	public static void createSDDir(String dirName) {
		File dir = new File(dirName);
		dir.mkdirs();

	}

	/**
	 * 文件如果存在则删除
	 * 
	 * @param fileName
	 */
	public static void checkFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			Log.v(LOGTAG, fileName + ":exists,delete.");
			file.delete();
		}

	}

	/**
	 * InputStream流写入本地
	 * 
	 * @param path
	 *            路径,如 filedown/
	 * @param fileName
	 * @param is
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static boolean write2SDcardFromInput(String path, String fileName,
			InputStream is) {

		boolean result = true;
		File file = null;
		OutputStream output = null;
		int readBytes = 0;

		try {
			createSDDir(sdcardDir + path);// 创建目录
			checkFile(sdcardDir + path + fileName);
			createSDFile(sdcardDir + path + fileName);
			file = new File(sdcardDir + path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[1024];
			while ((readBytes = is.read(buffer)) > 0) {
				output.write(buffer, 0, readBytes);
			}

			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (result) {
			Log.v(LOGTAG, sdcardDir + path + fileName + ":write success.");
		} else {
			Log.v(LOGTAG, sdcardDir + path + fileName + ":write failed.");
		}
		return result;
	}
}
