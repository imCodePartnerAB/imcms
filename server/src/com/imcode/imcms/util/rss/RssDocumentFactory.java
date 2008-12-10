package com.imcode.imcms.util.rss;

import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;

public interface RssDocumentFactory {

    Document createRssDocument(Channel channel) ;
}