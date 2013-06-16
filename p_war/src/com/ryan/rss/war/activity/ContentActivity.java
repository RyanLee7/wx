package com.ryan.rss.war.activity;

import java.util.List;

import cn.guomob.android.GuomobAdView;
import cn.guomob.android.OnBannerAdListener;

import com.ryan.rss.war.menu.MenuAdapter;
import com.ryan.rss.war.menu.MenuInfo;
import com.ryan.rss.war.menu.MenuUtils;
import com.ryan.rss.war.util.Globals;
import com.ryan.rss.war.R;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;

import android.view.GestureDetector;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ContentActivity extends Activity implements OnTouchListener {

	private final static String LOGTAG = "showDescription";
	private final static boolean DEBUG_MODE = false;

	private final static int LOOP = 0x1; // 字幕循环
	private final static int SCROLLTO = 0x2;// 滚动到指定位置

	private TextView txt_title;
	private TextView txt_descript;

	private String theStory;// 文章内容
	private String title; // 文章标题

	// 文字属性
	private int scrollSpeed;// 滚动速度
	ScrollView scroll;
	private int scrollPosition = 0;// 滚动位置

	// 记录用户的选项.双击改变
	private boolean isScroll = Globals.prefs_autoScroll;
	private GestureDetector gestureDetector;
	private SharedPreferences setting;
	private String link;
	// 菜单
	private PopupWindow popWindow;
	private MenuAdapter menuAdapter;
	private List<MenuInfo> menuList;
	private GridView menuGridView;

	/* 使用广告 */
	GuomobAdView m_adView;
	RelativeLayout m_Relative;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.v(LOGTAG, "onCreate()");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.content_layout);

		if (Globals.AD_MODE && Globals.NETWORK_ENABLE) {
			initADView();

			
		}
		initContent();
		initTouch();
		initPopWindows();// 初始化菜单
		readPreference();
		// if (DebugTest.DEBUG_MODE == true) {
		// // 打印当前时间与启动时间的差
		// Log.v(LOGTAG, "ArticleActivity:" + DebugTest.getCurrentTimePass());
		// }

	}

	/**
	 * 初始化显示内容
	 */
	private void initContent() {

		scroll = (ScrollView) findViewById(R.id.scroll);
		scrollSpeed = Globals.prefs_scrollSpeed;

		// 读取数据
		Intent startIntent = getIntent();

		if (startIntent != null) {
			Bundle b = startIntent.getBundleExtra("feed");

			if (b == null) {
				if (DEBUG_MODE) {
					Log.v(LOGTAG, "bundle error");
				}

				theStory = "data error!";
			} else {
				Globals.itemId = (int) b.getLong("id");
				Globals.prefs_recent_link = b.getString("link");
				link = b.getString("link");
				title = b.getString("title");
				theStory = "\n" + b.getString("description");
			}

		} else {
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "intent error");
			}

			theStory = "Information Not Found";
		}

		txt_descript = (TextView) findViewById(R.id.storybox);
		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/hy.ttf");
		txt_descript.setTypeface(face);// 设置字体
		// 设定字号
		txt_descript.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				Globals.prefs_textSize);
		txt_descript.setText(theStory);
		txt_descript.setOnTouchListener(this);

		txt_title = (TextView) findViewById(R.id.txt_title);
		txt_title.setText(title);
		txt_title.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				txt_title.setVisibility(View.GONE);
				return false;
			}
		});

	}

	private void initTouch() {
		gestureDetector = new GestureDetector(this, new SimpleGesture());

	}

	private Handler scrollHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case LOOP:
				doScroll();

				// 循环
				scrollHandler.sendEmptyMessageDelayed(LOOP,
						55 - scrollSpeed * 5);

				break;
			case SCROLLTO:
				// scroll.scrollTo(0, scrollPosition);
				scroll.smoothScrollTo(0, scrollPosition);
				break;
			}

		}

	};

	/**
	 * 使用handler来处理退出动作,可以产生延迟让动画进行完毕.
	 */
	private Handler stopHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			setResult(msg.what);
			finish();
		}

	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Log.v(LOGTAG, "onTouch()");
		return gestureDetector.onTouchEvent(event);
	}

	private class SimpleGesture extends SimpleOnGestureListener {
		private static final String LOGTAG = "SimpleGesture";

		// 双击的第二下,touch down时触发
		public boolean onDoubleTap(MotionEvent e) {

			if (isScroll) {
				scrollHandler.removeMessages(LOOP);
				isScroll = false;
				Globals.prefs_autoScroll = false;
			} else {
				scrollHandler.sendEmptyMessage(LOOP);
				isScroll = true;
				Globals.prefs_autoScroll = true;
			}

			if (DEBUG_MODE) {
				Log.v(LOGTAG, "onDoubleTap:");
			}

			return super.onDoubleTap(e);
		}

	}

	/**
	 * 字幕滚动
	 */
	private void doScroll() {

		// int now = scroll.getScrollY();

		scroll.smoothScrollBy(0, 1);
		// scroll.scrollBy(0, 10);
		// int now1 = scroll.getScrollY();
		// Log.v(LOGTAG, "now:" + now1);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			finish();
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		// ument
		MobclickAgent.onPause(this);
		Log.v(LOGTAG, "onPause()");
		scrollHandler.removeMessages(LOOP);

		// 记录阅读进度
		scrollPosition = scroll.getScrollY();
		writePreference();
		Log.v(LOGTAG, "scrollPosition: " + scrollPosition);
		if (DEBUG_MODE) {
			Log.v(LOGTAG, "scrollPosition: " + scrollPosition);
		}
		super.onPause();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		// ument
		MobclickAgent.onResume(this);
		Log.v(LOGTAG, "onResume()");

		// 恢复进度
		scroll.setScrollY(scrollPosition);
		if (isScroll) {
			scrollHandler.sendEmptyMessage(LOOP);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
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
		popWindow = new PopupWindow(menuGridView, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		popWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.yfb870e));

		popWindow.setFocusable(true);
		popWindow.setAnimationStyle(R.style.menushow);
		popWindow.update();

		// 触摸获取焦点
		menuGridView.setFocusableInTouchMode(true);

		// 设置键盘事件,如果按下菜单键则隐藏菜单
		menuGridView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if ((keyCode == KeyEvent.KEYCODE_MENU)
						&& (popWindow.isShowing())) {

					popWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		menuGridView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MenuInfo info = menuList.get(position);

				popWindow.dismiss();
				if (info.isHide) {
					return;
				}

				switch (info.menuId) {

				case MenuUtils.MENU_CONTENT_PREVIOUS:

					stopHandler.sendEmptyMessageDelayed(
							MenuUtils.MENU_CONTENT_PREVIOUS, 200);

					break;

				case MenuUtils.MENU_CONTENT_NEXT:
					stopHandler.sendEmptyMessageDelayed(
							MenuUtils.MENU_CONTENT_NEXT, 200);
					break;

				case MenuUtils.MENU_CONTENT_TOP:
					// 回到顶部

					// Toast.makeText(ContentActivity.this, "MENU_CONTENT_TOP",
					// Toast.LENGTH_LONG).show();
					scroll.fullScroll(ScrollView.FOCUS_UP);
					break;

				case MenuUtils.MENU_CONTENT_BOTTOM:
					// 回到底部

					// Toast.makeText(ContentActivity.this,
					// "MENU_CONTENT_BOTTOM",
					// Toast.LENGTH_LONG).show();
					scroll.fullScroll(ScrollView.FOCUS_DOWN);
					break;

				case MenuUtils.MENU_CONTENT_TXTUP:
					// 字号+
					if (Globals.prefs_textSize >= 250) {

						Toast.makeText(
								ContentActivity.this,

								getResources().getString(R.string.menu_txt_max)
										+ "--" + Globals.prefs_textSize,
								Toast.LENGTH_LONG).show();
					} else {
						Globals.prefs_textSize++;
						txt_descript.setTextSize(TypedValue.COMPLEX_UNIT_SP,
								Globals.prefs_textSize);
						Toast.makeText(
								ContentActivity.this,
								getResources().getString(R.string.menu_txtup)
										+ "--" + Globals.prefs_textSize,
								Toast.LENGTH_LONG).show();

					}

					break;

				case MenuUtils.MENU_CONTENT_TXTDOWN:
					// 字号-
					if (Globals.prefs_textSize <= 1) {
						Toast.makeText(
								ContentActivity.this,
								getResources().getString(R.string.menu_txt_min)
										+ "--" + Globals.prefs_textSize,
								Toast.LENGTH_LONG).show();
					} else {
						Globals.prefs_textSize--;
						txt_descript.setTextSize(TypedValue.COMPLEX_UNIT_SP,
								Globals.prefs_textSize);
						Toast.makeText(
								ContentActivity.this,
								getResources().getString(R.string.menu_txtup)
										+ "--" + Globals.prefs_textSize,
								Toast.LENGTH_LONG).show();
					}

					break;

				case MenuUtils.MENU_CONTENT_FAST:
					// 滚动速度+

					if (scrollSpeed >= 10) {
						Toast.makeText(
								ContentActivity.this,
								getResources().getString(
										R.string.menu_scroll_max)
										+ "--" + scrollSpeed, Toast.LENGTH_LONG)
								.show();
						// 最大速度.
					} else {
						scrollSpeed++;
						Globals.prefs_scrollSpeed = scrollSpeed;
						Toast.makeText(
								ContentActivity.this,
								getResources().getString(R.string.menu_scroll)
										+ "--" + scrollSpeed, Toast.LENGTH_LONG)
								.show();
					}

					break;

				case MenuUtils.MENU_CONTENT_SLOW:
					// 滚动速度-
					if (scrollSpeed <= 1) {

						Toast.makeText(
								ContentActivity.this,
								getResources().getString(
										R.string.menu_scroll_max)
										+ "--" + scrollSpeed, Toast.LENGTH_LONG)
								.show();
					} else {
						scrollSpeed--;
						Globals.prefs_scrollSpeed = scrollSpeed;
						Toast.makeText(
								ContentActivity.this,
								getResources().getString(R.string.menu_scroll)
										+ "--" + scrollSpeed, Toast.LENGTH_LONG)
								.show();

					}

					break;

				}

			}
		});

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

		if (popWindow != null) {

			menuList = MenuUtils.getContentMenuList();
			menuAdapter = new MenuAdapter(this, menuList);
			menuGridView.setAdapter(menuAdapter);
			popWindow.showAtLocation(
					this.findViewById(R.id.content_linearlayout),
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
	 * 读取设置
	 */
	private void readPreference() {

		setting = this.getSharedPreferences("content", 0);

		scrollPosition = setting.getInt(link, -1);
		Log.v(LOGTAG, "read preferenct:scrollPosition  " + scrollPosition);
		scrollHandler.sendEmptyMessageDelayed(SCROLLTO, 1000);

	}

	/**
	 * 写入设置
	 */
	private void writePreference() {

		SharedPreferences.Editor editor = setting.edit();

		editor.putInt(link, scrollPosition);

		editor.commit();

	}

	private void initADView() {

		m_adView = new GuomobAdView(this, Globals.AD_KEY);
		m_Relative = (RelativeLayout) findViewById(R.id.content_banner);
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
