package com.ryan.rss.xinhai.adapter;

import java.util.List;

import com.ryan.rss.xinhai.R;

import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter {

	private Context context;
	private List<String> list;

	public CategoryAdapter(Context context, List<String> list) {

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
					R.layout.category_item, null);

			holder = new ViewHolder();
			convertView.setTag(holder);
			holder.textView = (TextView) convertView
					.findViewById(R.id.catetory_item);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textView.setText(list.get(position));

		return convertView;
	}

	static class ViewHolder {

		TextView textView;
	}

}
