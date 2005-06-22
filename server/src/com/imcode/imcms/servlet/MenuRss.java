package com.imcode.imcms.servlet;

import com.imcode.imcms.api.*;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.time.DateFormatUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MenuRss extends HttpServlet {
    private static final String IMCMS_MENU_NAMESPACE_URI = "imcms:menu";
    private static final String DUBLIN_CORE_METADATA_TERMS_NAMESPACE_URI = "http://purl.org/dc/terms/";
    private static final String DUBLIN_CORE_METADATA_ELEMENTS_NAMESPACE_URI = "http://purl.org/dc/elements/1.1/";
    private static final String RFC822_DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        try {
            ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
            int documentId = Integer.parseInt(request.getParameter("meta_id"));
            int menuIndex = Integer.parseInt(request.getParameter("menu_index")) ;

            DocumentService documentService = cms.getDocumentService();
            TextDocument document = documentService.getTextDocument(documentId);
            if ( null == document ) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                Document xmlDocument = createRssDocument(request, document, menuIndex);
                Utility.outputXmlDocument(response, xmlDocument);
            }
        } catch ( NoPermissionException e ) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch ( ClassCastException nfe ) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch ( NumberFormatException nfe ) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch ( ParserConfigurationException e ) {
            throw new UnhandledException(e);
        }
    }

    private Document createRssDocument(HttpServletRequest request, TextDocument document,
                                       int menuIndex) throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        Document xmlDocument = documentBuilder.newDocument();

        Element rssElement = createRssElement(xmlDocument, document, menuIndex, request);

        xmlDocument.appendChild(rssElement);

        return xmlDocument;
    }

    private Element createRssElement(Document xmlDocument, TextDocument document, int menuIndex,
                                     HttpServletRequest request) {

        Element rssElement = xmlDocument.createElement("rss");
        rssElement.setAttribute("xmlns:imcms", IMCMS_MENU_NAMESPACE_URI);
        rssElement.setAttribute("xmlns:dcterms", DUBLIN_CORE_METADATA_TERMS_NAMESPACE_URI);
        rssElement.setAttribute("xmlns:dc", DUBLIN_CORE_METADATA_ELEMENTS_NAMESPACE_URI);
        rssElement.setAttribute("version", "2.0");

        Element channelElement = createChannelElement(xmlDocument, document, menuIndex, request);

        rssElement.appendChild(channelElement);

        return rssElement;
    }

    private Element createChannelElement(Document xmlDocument, TextDocument document,
                                         int menuIndex, HttpServletRequest request) {
        Element channelElement = xmlDocument.createElement("channel");

        channelElement.appendChild(createTextElement(xmlDocument, "title", document.getHeadline()));
        channelElement.appendChild(createTextElement(xmlDocument, "link", getUrlToDocument(request, document)));
        channelElement.appendChild(createTextElement(xmlDocument, "description", document.getMenuText()));

        appendMenuItems(xmlDocument, channelElement, document, menuIndex, request);

        return channelElement;
    }

    private String getUrlToDocument(HttpServletRequest request, com.imcode.imcms.api.Document document) {
        String requestUrl = request.getRequestURL().toString();
        requestUrl = StringUtils.substringBefore(requestUrl, "/servlet/") + Utility
                .getContextRelativePathToDocumentWithId(document.getId());
        return requestUrl;
    }

    private void appendMenuItems(Document xmlDocument, Element channelElement, TextDocument document,
                                 int menuIndex, HttpServletRequest request) {

        DateFormat rfc822DateFormat = new SimpleDateFormat(RFC822_DATE_PATTERN, Locale.ENGLISH);
        Format iso8601DateFormat = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT ;

        TextDocument.MenuItem[] visibleMenuItems = document.getMenu(menuIndex).getVisibleMenuItems();
        for ( int i = 0; i < visibleMenuItems.length; i++ ) {
            TextDocument.MenuItem menuItem = visibleMenuItems[i];
            Element itemElement = createItemElement(xmlDocument, menuItem, request, rfc822DateFormat, iso8601DateFormat);

            channelElement.appendChild(itemElement);
        }
    }

    private Element createItemElement(Document xmlDocument, TextDocument.MenuItem menuItem, HttpServletRequest request,
                                      DateFormat rfc822DateFormat,
                                      Format iso8601DateFormat) {
        Element itemElement = xmlDocument.createElement("item");
        com.imcode.imcms.api.Document itemDocument = menuItem.getDocument();

        Date modifiedDatetime = itemDocument.getModifiedDatetime();
        Date createdDatetime = itemDocument.getCreatedDatetime();
        Date publicationStartDatetime = itemDocument.getPublicationStartDatetime();
        User creator = itemDocument.getCreator();

        appendTextElement(itemElement, "title", itemDocument.getHeadline());
        appendTextElement(itemElement, "link", getUrlToDocument(request, itemDocument));
        appendTextElement(itemElement, "description", itemDocument.getMenuText()) ;
        appendTextElement(itemElement, "pubDate", rfc822DateFormat.format(publicationStartDatetime)) ;
        appendTextElement(itemElement, "author", creator.getEmailAddress()) ;
        appendTextElementNS(itemElement, DUBLIN_CORE_METADATA_ELEMENTS_NAMESPACE_URI, "dc:creator", creator.getFirstName() + " " + creator.getLastName());
        appendTextElementNS(itemElement, DUBLIN_CORE_METADATA_TERMS_NAMESPACE_URI, "dcterms:created", iso8601DateFormat.format(createdDatetime)) ;
        appendTextElementNS(itemElement, DUBLIN_CORE_METADATA_TERMS_NAMESPACE_URI, "dcterms:modified", iso8601DateFormat.format(modifiedDatetime)) ;
        appendTextElementNS(itemElement, IMCMS_MENU_NAMESPACE_URI, "imcms:target", itemDocument.getTarget()) ;
        appendTextElementNS(itemElement, IMCMS_MENU_NAMESPACE_URI, "imcms:treeKey", menuItem.getTreeKey().toString()) ;

        return itemElement;
    }

    private void appendTextElementNS(Element parentElement, String namespaceUri, String qualifiedName, String text) {
        parentElement.appendChild(createTextElementNS(parentElement.getOwnerDocument(), namespaceUri, qualifiedName, text)) ;
    }

    private void appendTextElement(Element parentElement, String tagName, String text) {
        parentElement.appendChild(createTextElement(parentElement.getOwnerDocument(), tagName, text)) ;
    }

    private Element createTextElementNS(Document xmlDocument, String namespaceUri, String qualifiedName, String text) {
        Element treeKeyElement = xmlDocument.createElementNS(namespaceUri, qualifiedName) ;
        treeKeyElement.appendChild(xmlDocument.createTextNode(text));
        return treeKeyElement;
    }

    private Element createTextElement(Document xmlDocument, String tagName, String text) {
        Element element = xmlDocument.createElement(tagName);
        element.appendChild(xmlDocument.createTextNode(text));
        return element;
    }
}
