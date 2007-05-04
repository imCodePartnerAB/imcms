package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.DocumentService;
import com.imcode.imcms.api.NoPermissionException;
import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.util.rss.Rss20DocumentFactory;
import com.imcode.imcms.util.rss.RssDocumentFactory;
import com.imcode.imcms.util.rss.imcms.DocumentMenuChannel;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MenuRss extends HttpServlet {

    private final static Logger LOG = Logger.getLogger(MenuRss.class);
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        try {
            ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
            int documentId = Integer.parseInt(request.getParameter("meta_id"));
            int menuIndex = Integer.parseInt(request.getParameter("menu_index"));

            DocumentService documentService = cms.getDocumentService();
            TextDocument document = documentService.getTextDocument(documentId);
            if ( null == document ) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                final RssDocumentFactory rssDocumentFactory = new Rss20DocumentFactory();
                DocumentMenuChannel documentMenuChannel = new DocumentMenuChannel(document, getUrlRoot(request), menuIndex);
                Document xmlDocument = rssDocumentFactory.createRssDocument(documentMenuChannel);
                Utility.outputXmlDocument(response, xmlDocument);
            }
        } catch ( NoPermissionException e ) {
            LOG.debug("Forbidden.",e);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch ( ClassCastException nfe ) {
            LOG.debug("Forbidden.",nfe);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch ( NumberFormatException nfe ) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String getUrlRoot(HttpServletRequest request) {
        return StringUtils.substringBefore(request.getRequestURL().toString(), "/servlet/");
    }

}
