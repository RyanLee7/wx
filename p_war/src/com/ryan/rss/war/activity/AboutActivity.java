package com.ryan.rss.war.activity;



import com.ryan.rss.war.R;
import com.ryan.rss.war.util.Globals;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutActivity extends Activity {

	private TextView about_web_txt;
	private LinearLayout linearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);

		linearLayout = (LinearLayout) findViewById(R.id.about_linearlayout);
		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		about_web_txt = (TextView) findViewById(R.id.about_textview);
		String htmlLinkTextWeibo = "<a href=\"#\"> "
				+ getResources().getString(R.string.about_web) + "</a>";
		about_web_txt.setText(Html.fromHtml(htmlLinkTextWeibo));
		about_web_txt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(Globals.WEB_URL);
				intent.setData(content_url);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		finish();
		overridePendingTransition(R.anim.alpha_show, R.anim.pophidden_anim);
		return true;
	}

	@Override
	protected void onResume() {
		// ument
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// ument
		MobclickAgent.onPause(this);
		super.onPause();
	}

}
