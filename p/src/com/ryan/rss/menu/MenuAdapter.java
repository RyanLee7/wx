package com.ryan.rss.menu;

import java.util.List;

import com.ryan.rss.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

	private final List<MenuInfo> list;
	private final LayoutInflater inflater;;

	public MenuAdapter(Context context, List<MenuInfo> list) {
		this.list = list;
		inflater = LayoutInflater.from(context);
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
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.menu_item, null);
		}

		MenuInfo info = list.get(position);
		ImageView imageView = (ImageView) view.findViewById(R.id.menu_image);
		TextView textView = (TextView) view.findViewById(R.id.menu_text);
		imageView.setImageResource(info.imgSrc);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		textView.setText(info.title);
		if (info.isHide) {
			imageView.setAlpha(80);
		}
		return view;
	}

}
