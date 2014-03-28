package com.xiaoenai.rss.v2;

import java.io.File;
import java.net.URLEncoder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xiaoenai.rss.v1.UrlUtil;


public class AppV2 {
	
	public static final String CHROME_UE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31 AlexaToolbar/alxg-3.1";
	
	public static void main(String[] args) throws Exception {
		parse();
	}
	
	public static void parse() throws Exception {
		long startTime = System.currentTimeMillis();
		File input = new File(
				"html/bookmarks_14-3-27.html");

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
					System.out.print(i + "----------------------[");
					System.out.print(htmlUrl);
					System.out.print("]----------------------[");
					System.out.print(xmlUrl);
					System.out.print("]----------------------[");
					System.out.println(title + "]");
					String outline = gen(htmlUrl, xmlUrl, desc, title);
					sb.append(outline);
				}
			} catch(Exception e) {
				System.err.println(i+" ------>parse error url is "+htmlUrl+",error is "+e.getMessage());
			}
		}
		sb.append("</body>").append("</opml>");
		System.out.println(sb);
		System.out.println("cost time :" + (System.currentTimeMillis()-startTime)/1000 + " s.");
		FileUtils.write(new File("html/vienna-rss.opml"), sb);
	}
	
	public static String[] parseUrl2FeedUrl(String url) throws Exception {
		String[] str = new String[2];
        Document doc = Jsoup.connect(url).userAgent(CHROME_UE).get();
        Elements el = doc.select("link[rel=alternate]");
        for (Element element : el) {
        	String type = element.attr("type");
        	String href = element.absUrl("href");
        	String strf = "";
        	if ("application/rss+xml".equalsIgnoreCase(type)) {
                strf = UrlUtil.completeUrl(url.toString(), href);
            } else if ("application/atom+xml".equalsIgnoreCase(type)) {
                strf = UrlUtil.completeUrl(url.toString(), href);
            }
        	if (StringUtils.isNotBlank(strf)) {
        		str[0] = strf;
        		break;
        	}
		}
        Elements descs = doc.select("meta[name=description]");
        for (Element element : descs) {
        	String content = element.attr("content");
        	if (StringUtils.isNotBlank(content)) {
        		str[1] = content;
        		break;
        	}
		}
        return str;
	}
	
	public static String gen(String htmlUrl, String xmlUrl, String desc, String title) {
		String rtn = "<outline htmlUrl=\""+URLEncoder.encode(htmlUrl)+"\" xmlUrl=\""+URLEncoder.encode(xmlUrl)+"\" type=\"rss\" description=\""+desc+"\" text=\""+title+"\"/>";
		return rtn;
	}
}
