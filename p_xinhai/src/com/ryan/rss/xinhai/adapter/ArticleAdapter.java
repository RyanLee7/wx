package com.ryan.rss.xinhai.adapter;

import java.util.List;

import com.ryan.rss.xinhai.R;
import com.ryan.rss.xinhai.util.RSSItem;

import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ArticleAdapter extends BaseAdapter {

	private Context context;
	private List<RSSItem> list;

	public ArticleAdapter(Context context, List<RSSItem> list) {

		this.context = context;
		this.list = list;

	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.article_list_item, null);

			holder = new ViewHolder();
			convertView.setTag(holder);
			holder.titleTextView = (TextView) convertView
					.findViewById(R.id.title);
			holder.dateTextView = (TextView) convertView
					.findViewById(R.id.date);
			holder.summaryTextView = (TextView) convertView
					.findViewById(R.id.txt_summary);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.titleTextView.setText(list.get(position).title);
		holder.dateTextView.setText(list.get(position).pubDate);
		holder.summaryTextView.setText(list.get(position).getSummary());

		return convertView;
	}

	static class ViewHolder {

		TextView titleTextView;
		TextView dateTextView;
		TextView summaryTextView;
	}

}
