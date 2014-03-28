package com.xiaoenai.rss.v1;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * HTML parser used to look for RSS / Atom feeds.
 *
 * @author jtremeaux
 */
public class RssExtractor extends DefaultHandler {

    /**
     * Original page URL.
     */
    private final URL url;

    /**
     * List of extracted feed URLs.
     */
    private List<String> feedList;
    
    private String desc;
    
    /**
     * Constructor of RssExtractor.
     * 
     * @param url Url of the html page
     * @throws MalformedURLException
     */
    public RssExtractor(String url) throws MalformedURLException {
        this.url = new URL(url);
        feedList = new ArrayList<String>();
    }

    /**
     * Reads an HTML page and extracts RSS / Atom feeds.
     * 
     * @param is Input stream
     * @throws Exception
     */
    public void readPage(InputStream is) throws Exception {
        SAXParserImpl parser = SAXParserImpl.newInstance(null);
        parser.setFeature("http://xml.org/sax/features/namespaces", true);    
        parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);

        parser.parse(is, this);
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("link".equalsIgnoreCase(localName)) {
            String rel = StringUtils.trim(attributes.getValue("rel"));
            String type = StringUtils.trim(attributes.getValue("type"));
            String href = StringUtils.trim(attributes.getValue("href"));
            
            if ("alternate".equalsIgnoreCase(rel)) {
                try {
                    if ("application/rss+xml".equalsIgnoreCase(type)) {
                        feedList.add(UrlUtil.completeUrl(url.toString(), href));
                    } else if ("application/atom+xml".equalsIgnoreCase(type)) {
                        feedList.add(UrlUtil.completeUrl(url.toString(), href));
                    }
                } catch (MalformedURLException e) {
                    System.err.println(MessageFormat.format("Error parsing URL, extracted href: {0}", href));
                }
            }
            return;
        }
        if ("meta".equalsIgnoreCase(localName)) {
            String name = StringUtils.trim(attributes.getValue("name"));
            String content = StringUtils.trim(attributes.getValue("content"));
            
            if ("description".equalsIgnoreCase(name)) {
            	desc = content;
            }
            return;
        }
    }
    
    /**
     * Getter of feedList.
     *
     * @return feedList
     */
    public List<String> getFeedList() {
        return feedList;
    }
    
    public String getDesc() {
    	return desc;
    }
}
