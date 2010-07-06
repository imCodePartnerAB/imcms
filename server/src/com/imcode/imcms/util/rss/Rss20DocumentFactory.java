package com.imcode.imcms.util.rss;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.StringUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Locale;

import com.imcode.imcms.util.rss.dc.DublinCoreTerms;
import com.imcode.imcms.util.rss.dc.DublinCoreEntity;

public class Rss20DocumentFactory implements RssDocumentFactory {

    private static final String RFC822_DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";
	private static final Locale EN_LOCALE = Locale.US;

    public Document createRssDocument(Channel channel) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document xmlDocument = documentBuilder.newDocument();

            appendRssElement(xmlDocument, channel);

            return xmlDocument;
        } catch ( ParserConfigurationException e ) {
            throw new UnhandledException(e);
        }
    }

    private Element appendRssElement(Document xmlDocument,
                                     Channel channel) {

        Element rssElement = xmlDocument.createElement("rss");
        xmlDocument.appendChild(rssElement);
        rssElement.setAttribute("version", "2.0");

        appendChannelElement(rssElement, channel);

        return rssElement;
    }

    private Element appendChannelElement(Element rssElement,
                                         Channel channel) {
        Element channelElement = rssElement.getOwnerDocument().createElement("channel");
        rssElement.appendChild(channelElement);

        appendTextElement(channelElement, "title", channel.getTitle());
        appendTextElement(channelElement, "link", channel.getLink());
        appendTextElement(channelElement, "description", channel.getDescription());
        appendNameSpaceStrings(channelElement, channel.getNameSpaceStrings());

        appendItems(channelElement, channel);

        return channelElement;
    }

    private void appendItems(Element channelElement,
                             Channel channel) {

        for ( Item item : channel.getItems() ) {
            appendItem(channelElement, item);
        }
    }

    private void appendItem(Element channelElement, Item item) {
        channelElement.appendChild(createItemElement(channelElement.getOwnerDocument(), item));
    }

    private Element createItemElement(Document xmlDocument,
                                      Item item) {
        DateFormat dateFormat = new SimpleDateFormat(RFC822_DATE_PATTERN, EN_LOCALE);

        Element itemElement = xmlDocument.createElement("item");
        appendTextElement(itemElement, "link", item.getLink());
        appendTextElement(itemElement, "title", item.getTitle());
        appendTextElement(itemElement, "description", item.getDescription());

        try {
            DublinCoreEntity dublinCoreEntity = (DublinCoreEntity) item.getNameSpaceBeans().get(DublinCoreTerms.DUBLIN_CORE_ELEMENTS_NAME_SPACE).get("creator");
            appendTextElement(itemElement, "author", dublinCoreEntity.getEmailAddress());
        } catch ( NullPointerException npe ) {
        }

        try {
            Date issued = (Date) item.getNameSpaceBeans().get(DublinCoreTerms.DUBLIN_CORE_TERMS_NAME_SPACE).get("issued");
            appendTextElement(itemElement, "pubDate", dateFormat.format(issued));
        } catch ( NullPointerException npe ) {
        }

        Map<NameSpace, Map<String, String>> nameSpaces = item.getNameSpaceStrings();
        appendNameSpaceStrings(itemElement, nameSpaces);
        return itemElement;
    }

    private void appendNameSpaceStrings(Element itemElement, Map<NameSpace, Map<String, String>> nameSpaces
    ) {
        for ( Map.Entry<NameSpace, Map<String, String>> nameSpaceEntry : nameSpaces.entrySet() ) {
            NameSpace nameSpace = nameSpaceEntry.getKey();
            String nameSpaceUri = nameSpace.getNameSpaceUri();
            String prefix = lookupPrefix(itemElement, nameSpaceUri, nameSpace);
            Map<String, String> nameSpaceValues = nameSpaceEntry.getValue();
            for ( Map.Entry<String, String> nameSpaceValueEntry : nameSpaceValues.entrySet() ) {
                String name = nameSpaceValueEntry.getKey();
                if ( null != prefix ) {
                    name = prefix + ":" + name;
                }
                String value = nameSpaceValueEntry.getValue();
                appendTextElementNS(itemElement, nameSpaceUri, name, value);
            }
        }
    }

    private String lookupPrefix(Element element, String nameSpaceUri, NameSpace nameSpace) {
        String prefix = element.lookupPrefix(nameSpaceUri);
        if ( null == prefix ) {
            String suggestedPrefix = nameSpace.getSuggestedPrefix();
            if ( null == element.lookupNamespaceURI(suggestedPrefix) ) {
                prefix = suggestedPrefix;
                element.getOwnerDocument().getDocumentElement().setAttribute("xmlns:" + prefix, nameSpaceUri);
            }
        }
        return prefix;
    }

    private void appendTextElement(Element parentElement, String tagName, String text) {
        appendTextElementNS(parentElement, null, tagName, text);
    }

    private void appendTextElementNS(Element parentElement, String namespaceUri, String qualifiedName,
                                     String text) {
        if ( StringUtils.isNotBlank(text) ) {
            parentElement.appendChild(createTextElementNS(parentElement.getOwnerDocument(), namespaceUri, qualifiedName, text));
        }
    }

    private Element createTextElementNS(Document xmlDocument, String namespaceUri, String qualifiedName,
                                        String text) {
        Element treeKeyElement = xmlDocument.createElementNS(namespaceUri, qualifiedName);
        treeKeyElement.appendChild(xmlDocument.createTextNode(text));
        return treeKeyElement;
    }

}
