package com.ryan.rss.https;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.ryan.rss.util.RSSItem;
import com.ryan.rss.xml.RSSHandler;

public class XmlParseUtil {

	public static List<RSSItem> getRssDate(InputStream in) {

		InputSource is = new InputSource(in);

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parse;
		try {
			parse = factory.newSAXParser();
			XMLReader xmlReader = parse.getXMLReader();
			RSSHandler rssHandler = new RSSHandler();
			xmlReader.setContentHandler(rssHandler);
			xmlReader.parse(is);

			return rssHandler.getList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
