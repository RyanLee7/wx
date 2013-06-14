package com.ryan.rss.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ryan.rss.util.RSSItem;

import android.util.Log;

public class RSSHandler extends DefaultHandler {

	private final static boolean DEBUG_MODE = false;
	private final static String LOGTAG = "RSSHandler";

	private final String TAG_CATEGORY = "category";
	private final String TAG_ITEM = "item";
	private final String TAG_TITLE = "title";
	private final String TAG_DESCRIPTION = "description";
	private final String TAG_PUBDATE = "pubDate";
	private final String TAG_LINK = "link";

	private RSSItem item = null;
	private List<RSSItem> itemList = null;

	private long start;

	private StringBuilder sb = new StringBuilder();

	public RSSHandler() {

	}

	public List<RSSItem> getList() {
		return itemList;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();

		start = System.currentTimeMillis();
		itemList = new ArrayList<RSSItem>();
		item = new RSSItem();
	}

	@Override
	public void endDocument() throws SAXException {

		super.endDocument();
		long end = System.currentTimeMillis();
		Log.v(LOGTAG, "parse xml cost time:" + (end - start));
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		sb.setLength(0);

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);

		String value = sb.toString();

		if (TAG_TITLE.equals(localName)) {
			item.title = value;
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "title:" + value);
			}

		} else if (TAG_DESCRIPTION.equals(localName)) {
			item.description = value;
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "description:" + value);
			}

		} else if (TAG_PUBDATE.equals(localName)) {
			item.setPubDate(value);
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "pubdate:" + value);
			}

		} else if (TAG_LINK.equals(localName)) {
			item.link = value;
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "link:" + value);
			}

		} else if (TAG_CATEGORY.equals(localName)) {

			item.category = value;
			if (DEBUG_MODE) {
				Log.v(LOGTAG, "category:" + value);
			}
		}

		// 放在最后.
		if (TAG_ITEM.equals(localName)) {
			itemList.add(item);
			item = new RSSItem();
		}

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		sb.append(ch, start, length);

	}

}
