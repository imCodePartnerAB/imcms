package com.imcode.imcms.servlet;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.XmlDocumentBuilder;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.w3c.dom.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class XmlDoc extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int documentId = Integer.parseInt(request.getParameter("meta_id"));

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument(documentId);
        UserDomainObject currentUser = Utility.getLoggedOnUser(request);

        if (null == document) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else if (!currentUser.canAccess(document) || !document.isPublished() && !currentUser.canEdit(document)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
            XmlDocumentBuilder xmlDocumentBuilder = new XmlDocumentBuilder(currentUser);
            xmlDocumentBuilder.addDocument(document);
            Document xmlDocument = xmlDocumentBuilder.getXmlDocument();
            Utility.outputXmlDocument(response, xmlDocument);
        }
    }

}
