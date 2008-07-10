package com.imcode.imcms.util.rss;

import org.w3c.dom.Document;

public interface RssDocumentFactory {

    Document createRssDocument(Channel channel) ;
}