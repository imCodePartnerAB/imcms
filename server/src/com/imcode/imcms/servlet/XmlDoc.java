package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.XmlDocumentBuilder;
import imcode.util.Utility;
import org.w3c.dom.Document;
import org.apache.commons.lang.math.IntRange;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

import com.imcode.imcms.mapping.DocumentMapper;

public class XmlDoc extends HttpServlet {

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        String metaId = request.getParameter( "meta_id" );
        int index = metaId.indexOf('-');
        IntRange idRange;
        if (index > 0) {
            String startMetaId = metaId.substring(0, index);
            String endMetaId = metaId.substring(index + 1);
            idRange = new IntRange(Integer.parseInt(startMetaId), Integer.parseInt(endMetaId));
        }
        else {
            int documentId = Integer.parseInt( metaId );
            idRange = new IntRange(documentId, documentId);     
        }

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        Iterator documentsIterator = documentMapper.getDocumentsIterator(idRange);
        UserDomainObject currentUser = Utility.getLoggedOnUser(request);
        XmlDocumentBuilder xmlDocumentBuilder = new XmlDocumentBuilder(currentUser);
        
        while (documentsIterator.hasNext()) {
            DocumentDomainObject document = (DocumentDomainObject)documentsIterator.next();
            if ( null == document ) {
                continue;
            } else if (!currentUser.canAccess(document) || !document.isPublished() && !currentUser.canEdit(document) ) {
                continue;
            }
            else {
                xmlDocumentBuilder.addDocument( document);
            }
        }

        Document xmlDocument = xmlDocumentBuilder.getXmlDocument() ;
        Utility.outputXmlDocument( response, xmlDocument );
    }

}
