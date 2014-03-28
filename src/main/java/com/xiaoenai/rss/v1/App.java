package com.xiaoenai.rss.v1;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App {
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		File input = new File(
				"/Users/maiyang/Documents/workspace/jsoup-rss/html/bookmarks_14-3-27.html");

		Document doc = Jsoup.parse(input, "UTF-8");
		Elements links = doc.getElementsByTag("a");
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
		.append("<opml version=\"1.0\">")
		.append("<head>")
		.append("<title>Vienna Subscriptions</title>")
		.append("<dateCreated>2014-03-28 09:16:45 +0800</dateCreated>")
		.append("</head>")
		.append("<body>");
		int i = 0;
		for (Element link : links) {
			i++;
			String htmlUrl = link.attr("href");
			String title = link.text();
			try {
				String[] feed = parseUrl2FeedUrl(htmlUrl);
				String xmlUrl = feed[0];
				String desc = feed[1];
				if (StringUtils.isNotBlank(xmlUrl)) {
					System.err.print(i + "----------------------[");
					System.err.print(htmlUrl);
					System.err.print("]----------------------[");
					System.err.print(xmlUrl);
					System.err.print("]----------------------[");
					System.err.println(title + "]");
					String outline = gen(htmlUrl, xmlUrl, desc, title);
					sb.append(outline);
				}
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println(i+" ------>parse error url is "+htmlUrl+",error is "+e);
			}
		}
		sb.append("</body>").append("</opml>");
		System.out.println(sb);
		System.out.println("cost time :" + (System.currentTimeMillis()-startTime)/1000 + " s.");
	}
	
	public static String[] parseUrl2FeedUrl(String url) throws Exception {
		String str[] = new String[2];
		final RssExtractor extractor = new RssExtractor(url);
        new ReaderHttpClient() {
            
            @Override
            public void process(InputStream is) throws Exception {
                extractor.readPage(is);
            }
        }.open(new URL(url));
        List<String> feedList = extractor.getFeedList();
        str[0] = feedList == null || feedList.size()<1 ? null:feedList.get(0);
        str[1] = extractor.getDesc();
        return str;
	}
	
	public static String gen(String htmlUrl, String xmlUrl, String desc, String title) {
		String rtn = "<outline htmlUrl=\""+htmlUrl+"\" xmlUrl=\""+xmlUrl+"\" type=\"rss\" description=\""+desc+"\" text=\""+title+"\"/>";
		return rtn;
	}
}
