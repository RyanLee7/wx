package com.ryan.rss.war.activity;

import java.io.InputStream;
import java.util.List;

import cn.guomob.android.GuomobAdView;
import cn.guomob.android.OnBannerAdListener;

import com.ryan.rss.war.adapter.ArticleAdapter;
import com.ryan.rss.war.adapter.CategoryAdapter;
import com.ryan.rss.war.database.DBManager;
import com.ryan.rss.war.https.HttpUtil;
import com.ryan.rss.war.https.XmlParseUtil;
import com.ryan.rss.war.menu.MenuAdapter;
import com.ryan.rss.war.menu.MenuInfo;
import com.ryan.rss.war.menu.MenuUtils;
import com.ryan.rss.war.util.Globals;
import com.ryan.rss.war.util.RSSItem;
import com.ryan.rss.war.R;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;
import com.umeng.update.UmengUpdateAgent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * @author Ryan
 * 
 *         2013-4-7
 */
public class ArticleActivity extends Activity implements OnItemClickListener {

	private final static String LOGTAG = "MainActivity";
	private final static boolean DEBUG_MODE = false;

	private DBManager dbManager; // 数据库管理对象
	private List<RSSItem> rssList;// http下载rss xml后解析出来的数据,写入数据库

	private ImageButton recentBtn;// 最近阅读按钮
	private ImageButton directoryBtn;
	ProgressDialog progressDialog;

	// 文章
	private ListView articleView;// 文章列表
	private List<RSSItem> dbList;// 从数据库中根据目录读取出来的数据,用于ListView显示.

	// 目录
	private ListView categoryListView;// 目录列表
	private List<String> categoryList;// 从数据库中检出的目录集合
	private PopupWindow categoryWindow;
	private View categoryView;

	// 菜单
	private PopupWindow menuWindow;
	private MenuAdapter menuAdapter;
	private List<MenuInfo> menuList;
	private GridView menuGridView;
	private SharedPreferences setting;
	private FrameLayout mFrameTv;
	private ImageView mImgTv;

	/* 使用广告 */

	GuomobAdView m_adView;
	RelativeLayout m_Relative;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UmengUpdateAgent.update(this);
		Log.v(LOGTAG, "onCreate");

		setContentView(R.layout.article_layout);

		if (Globals.AD_MODE) {
			initADView();
		}
		readPreference();
		initUI();
		initPopWindows();// 初始化菜单

		// 初始化数据库管理对象
		dbManager = new DBManager(ArticleActivity.this);

		if (Globals.prefs_isFirstRun) {

			updateLocalContent();
			// 第一次运行时更新数据
			// updateListContent();
			Globals.prefs_isFirstRun = false;
		}
		uiHandler.sendEmptyMessage(0);// 更新UI

	}

	private void initUI() {
		mFrameTv = (FrameLayout) findViewById(R.id.fl_off);
		mImgTv = (ImageView) findViewById(R.id.iv_off);
		recentBtn = (ImageButton) findViewById(R.id.recent_btn);
		recentBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openRecentRead();

			}
		});

		initCategoryWindow();// 初始化目录选项
		directoryBtn = (ImageButton) findViewById(R.id.directory_btn);
		directoryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showCategoryWindow(v);
			}
		});

		articleView = (ListView) findViewById(R.id.itemlist);

	}

	/**
	 * 解析url上的RSS数据到数据库中
	 */
	private void updateListContent() {
		progressDialog = ProgressDialog.show(ArticleActivity.this, "",
				getResources().getString(R.string.loading));
		progressDialog.setCancelable(true);

		// XXX 添加标记,当用户多次点击按钮时,只下载一次.

		new Thread() {

			@Override
			public void run() {

				try {
					// 获取服务器rss更新时间
					String lastModifyDate = HttpUtil
							.getHttpLastModify(Globals.RSS_URL);

					if (lastModifyDate.equals(Globals.prefs_last_modify_date)) {

						// 时间相同,不需要更新
						progressDialog.cancel();
						uiHandler.sendEmptyMessage(1);
						if (DEBUG_MODE) {
							Log.v(LOGTAG, lastModifyDate);
						}

						return;
					}

					// 时间不相同，需要更新
					Globals.prefs_last_modify_date = lastModifyDate;

					Log.v(LOGTAG, "has update." + lastModifyDate);

					InputStream is = HttpUtil.getHttpUrlStream(Globals.RSS_URL);
					rssList = XmlParseUtil.getRssDate(is);

					dbManager.deleteAll();
					dbManager.intert(rssList);// 写入到数据库中
					progressDialog.cancel();
					uiHandler.sendEmptyMessage(0);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}.start();
	}

	public void updateLocalContent() {
		try {
			InputStream is = getAssets().open("data/war.xml");
			rssList = XmlParseUtil.getRssDate(is);

			dbManager.deleteAll();
			dbManager.intert(rssList);// 写入到数据库中

			uiHandler.sendEmptyMessage(0);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				Log.v(LOGTAG, "uiHandler.handleMessage()");
				updateData();
				updateDisplay();
				break;
			case 1:
				Toast.makeText(ArticleActivity.this, R.string.data_is_new,
						Toast.LENGTH_LONG).show();
				break;
			case 2:
				// finish
				mFrameTv.setVisibility(0);
				mImgTv.setVisibility(0);
				Animation anim = AnimationUtils.loadAnimation(
						ArticleActivity.this, R.anim.tv_off);
				anim.setAnimationListener(new tvOffAnimListener());
				mImgTv.startAnimation(anim);
				break;

			}

		}

	};

	/**
	 * 从数据库中读取数据
	 */
	private void updateData() {

		Log.v(LOGTAG, "updateData()");

		// 获得目录信息
		categoryList = dbManager.queryCategory();
		// dbList = dbManager.queryAll();
		if (categoryList.size() > 0) {

			// 如果默认目录选项的长度比列表的长度大,表示默认目录选项没有更新正确.
			if (Globals.prefs_categoryIndex >= categoryList.size()) {
				Globals.prefs_categoryIndex = 0;
			}
			dbList = dbManager.queryFromCategory(categoryList
					.get(Globals.prefs_categoryIndex));
			Log.v(LOGTAG, dbList.toString());
		}

	}

	/**
	 * 从数据库中读取数据 刷新界面
	 */
	private void updateDisplay() {

		if (dbList == null) {

			return;
		}

		ArticleAdapter articleAdapter = new ArticleAdapter(this, dbList);
		articleView.setAdapter(articleAdapter);
		articleView.setSelection(0);
		articleView.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

		// if (DebugTest.DEBUG_MODE == true) {
		// // 打印当前时间与启动时间的差
		// Log.v(LOGTAG, "onItemClick:" + DebugTest.getCurrentTimePass());
		// }

		startContentActivity(dbList.get(position));

	}

	/**
	 * 启动ContentActivity
	 * 
	 * @param item
	 *            显示的文章内容
	 */
	private void startContentActivity(RSSItem item) {

		Intent itemIntent = new Intent(this, ContentActivity.class);

		Bundle b = new Bundle();
		b.putLong("id", item.id);
		b.putString("title", item.title);
		b.putString("description", item.description);
		b.putString("link", item.link);

		itemIntent.putExtra("feed", b);
		startActivityForResult(itemIntent, 0);
		overridePendingTransition(R.anim.zoom_enter, R.anim.push_left_out);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == MenuUtils.MENU_CONTENT_PREVIOUS) {
			// 上一篇的功能
			Log.v(LOGTAG, "onActivityResult" + "id" + Globals.itemId
					+ "PREVIOUS_PREVIOUS");
			RSSItem item = searchItem(dbList, Globals.itemId + 1);

			if (item != null) {
				startContentActivity(item);

			} else {
				Toast.makeText(ArticleActivity.this,
						R.string.menu_previous_no_data, Toast.LENGTH_LONG)
						.show();
			}
		}

		if (resultCode == MenuUtils.MENU_CONTENT_NEXT) {
			// 下一篇的功能
			Log.v(LOGTAG, "onActivityResult" + "id" + Globals.itemId
					+ "MENU_CONTENT_NEXT");
			RSSItem item = searchItem(dbList, Globals.itemId - 1);

			if (item != null) {
				startContentActivity(item);

			} else {
				Toast.makeText(ArticleActivity.this,
						R.string.menu_next_no_data, Toast.LENGTH_LONG).show();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 返回List中含有相应url字段的RSSItem对象
	 * 
	 * @param dbList
	 * @param url
	 * @return
	 */
	private RSSItem searchItem(List<RSSItem> dbList, String url) {

		if (dbList == null || url.length() == 0) {
			return null;
		}

		for (RSSItem temp : dbList) {
			if ((temp.link).equals(url)) {
				return temp;
			}
		}

		return null;

	}

	/**
	 * 返回List中含有相应id字段的RSSItem对象
	 * 
	 * @param dbList
	 * @param id
	 * @return
	 */
	private RSSItem searchItem(List<RSSItem> dbList, int id) {

		if (dbList == null) {
			return null;
		}

		for (RSSItem temp : dbList) {
			if (temp.id == id) {
				return temp;
			}
		}

		return null;

	}

	/**
	 * 显示目录
	 * 
	 * @param parent
	 */
	private void showCategoryWindow(View parent) {

		CategoryAdapter categoryAdapter = new CategoryAdapter(this,
				categoryList);
		categoryListView.setAdapter(categoryAdapter);
		// 显示在中间位置.
		categoryWindow.showAtLocation(findViewById(R.id.linearlayout),
				Gravity.CENTER, 0, 0);

	}

	/**
	 * 初始化目录窗口
	 */
	private void initCategoryWindow() {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		categoryView = inflater.inflate(R.layout.category_list, null);
		categoryListView = (ListView) categoryView
				.findViewById(R.id.category_listview);

		categoryWindow = new PopupWindow(categoryView,
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		categoryWindow.setFocusable(true);
		categoryWindow.setOutsideTouchable(true);
		categoryWindow.setBackgroundDrawable(new BitmapDrawable());
		categoryWindow.setAnimationStyle(R.style.popwindow_anim_style);

		// 当点击popwindow时隐藏窗口
		RelativeLayout relativelayout = (RelativeLayout) categoryView
				.findViewById(R.id.category_list_relativelayout);
		relativelayout.setFocusable(true);
		relativelayout.setFocusableInTouchMode(true);
		relativelayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (DEBUG_MODE) {
					Log.v(LOGTAG, "linearLayout onClick");
				}

				if (categoryWindow != null) {
					categoryWindow.dismiss();
				}
			}
		});

		// ListView响应事件.
		categoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Globals.prefs_categoryIndex = position;
				uiHandler.sendEmptyMessage(0);// 更新UI
				// Toast.makeText(ArticleActivity.this,
				// categoryList.get(position), Toast.LENGTH_LONG).show();

				if (categoryWindow != null) {
					categoryWindow.dismiss();
				}
			}
		});
	}

	@Override
	protected void onPause() {
		// ument
		MobclickAgent.onPause(this);
		Log.v(LOGTAG, "onPause()");
		super.onPause();

	}

	@Override
	protected void onResume() {
		// ument
		MobclickAgent.onResume(this);
		Log.v(LOGTAG, "onResume()");
		super.onResume();

	}

	@Override
	protected void onDestroy() {

		writePreference();
		Log.v(LOGTAG, "onDestroy()");
		super.onDestroy();

	}

	@Override
	protected void onRestart() {

		Log.v(LOGTAG, "onRestart()");

		super.onRestart();
	}

	@Override
	protected void onStart() {

		Log.v(LOGTAG, "onStart()");

		super.onStart();
	}

	@Override
	protected void onStop() {

		Log.v(LOGTAG, "onStop()");

		super.onStop();

	}

	// 菜单项
	private void initPopWindows() {

		menuGridView = (GridView) View.inflate(this, R.layout.menu_gridview,
				null);

		menuWindow = new PopupWindow(menuGridView, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		menuWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.b00b0f0));
		menuWindow.setFocusable(true);
		menuWindow.setAnimationStyle(R.style.menushow);
		menuWindow.update();

		// 触摸获取焦点
		menuGridView.setFocusableInTouchMode(true);
		// 设置键盘事件,如果按下菜单键则隐藏菜单
		menuGridView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if ((keyCode == KeyEvent.KEYCODE_MENU)
						&& (menuWindow.isShowing())) {

					menuWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		menuGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				MenuInfo info = menuList.get(position);
				menuWindow.dismiss();

				if (info.isHide) {
					return;
				}

				switch (info.menuId) {
				case MenuUtils.MENU_ARTICLE_LISTREFLESH:
					// 列表更新
					updateListContent();
					break;

				case MenuUtils.MENU_EXIT:
					// 应用更新
					// Toast.makeText(ArticleActivity.this,
					// "MENU_ARTICLE_APPUPDATE", Toast.LENGTH_LONG).show();

					uiHandler.sendEmptyMessageDelayed(2, 300);
					break;

				case MenuUtils.MENU_ARTICLE_ABOUT:
					// 关于
					// Toast.makeText(ArticleActivity.this,
					// "MENU_ARTICLE_ABOUT",
					// Toast.LENGTH_LONG).show();

					Intent intent = new Intent();
					intent.setClass(ArticleActivity.this, AboutActivity.class);
					startActivity(intent);
					break;

				case MenuUtils.MENU_FEEDBACK:
					// 反馈
					UMFeedbackService
							.openUmengFeedbackSDK(ArticleActivity.this);

					break;

				default:
					break;

				}

			}
		});

	}

	/**
	 * 打开最近阅读的文章
	 */
	private void openRecentRead() {

		/**
		 * 最近阅读的逻辑思路: 使用static prefs_recent_read_url记录最近阅读的url.
		 * 用户从内容页面返回时同时设置url. ArticleActivity onPause写入setting. onCreate时读取设置
		 */

		RSSItem item = searchItem(dbList, Globals.prefs_recent_link);

		if (item != null) {
			startContentActivity(item);

		} else {
			Toast.makeText(ArticleActivity.this, R.string.recent_no_read,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		showPopWindow();
		return false;
	}

	/**
	 * 显示菜单.
	 */
	private void showPopWindow() {

		if (menuWindow != null) {

			menuList = MenuUtils.getArticleMenuList();
			menuAdapter = new MenuAdapter(this, menuList);
			menuGridView.setAdapter(menuAdapter);
			menuWindow.showAtLocation(this.findViewById(R.id.linearlayout),
					Gravity.BOTTOM, 0, 0);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		// 随便加一个显示的项目.并不会显示出来
		menu.add("menu");
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * 模仿电视关机的动画
	 * 
	 * 
	 * 2013-5-15
	 */
	class tvOffAnimListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * 读取设置
	 */
	private void readPreference() {

		setting = this.getSharedPreferences("setting", 0);

		Globals.prefs_isFirstRun = setting.getBoolean("isfirstrun",
				Globals.prefs_isFirstRun);
		Globals.prefs_autoScroll = setting.getBoolean("autoscroll",
				Globals.prefs_autoScroll);
		Globals.prefs_categoryIndex = setting.getInt("category",
				Globals.prefs_categoryIndex);
		Globals.prefs_recent_link = setting.getString("link",
				Globals.prefs_recent_link);
		Globals.prefs_textSize = setting.getInt("textsize",
				Globals.prefs_textSize);
		Globals.prefs_scrollSpeed = setting.getInt("scrollspeed",
				Globals.prefs_scrollSpeed);
		Globals.prefs_last_modify_date = setting.getString("lastmodify",
				Globals.prefs_last_modify_date);
	}

	/**
	 * 写入设置
	 */
	private void writePreference() {

		SharedPreferences.Editor editor = setting.edit();

		editor.putBoolean("isfirstrun", Globals.prefs_isFirstRun);
		editor.putBoolean("autoscroll", Globals.prefs_autoScroll);
		editor.putInt("category", Globals.prefs_categoryIndex);
		editor.putString("link", Globals.prefs_recent_link);
		editor.putInt("textsize", Globals.prefs_textSize);
		editor.putInt("scrollspeed", Globals.prefs_scrollSpeed);
		editor.putString("lastmodify", Globals.prefs_last_modify_date);
		editor.commit();

	}

	private void initADView() {

		m_adView = new GuomobAdView(this, Globals.AD_KEY);
		m_Relative = (RelativeLayout) findViewById(R.id.banner);
		m_Relative.addView(m_adView);

		m_adView.setOnBannerAdListener(new OnBannerAdListener() {

			// 无网络连接
			@Override
			public void onNetWorkError() {
				// TODO Auto-generated method stub
				Log.e("GuomobLog", "onNetWorkError");
			}

			// 加载广告成功
			@Override
			public void onLoadAdOk() {
				// TODO Auto-generated method stub
				Log.e("GuomobLog", "onLoadAdOk");
			}

			// 加载广告失败 arg0：失败原因
			@Override
			public void onLoadAdError(String arg0) {
				// TODO Auto-generated method stub
				Log.e("GuomobLog", "onLoadAdError" + arg0);
			}
		});
	}
}
