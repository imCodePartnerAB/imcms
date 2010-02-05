package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.XmlDocumentBuilder;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.SimpleDocumentQuery;
import imcode.util.Utility;
import org.w3c.dom.Document;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Query;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.api.LuceneParsedQuery;

public class XmlDoc extends HttpServlet {
    private final static Logger LOG = Logger.getLogger( XmlDoc.class.getName() );

    public void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        UserDomainObject currentUser = Utility.getLoggedOnUser(request);
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        String metaId = request.getParameter( "meta_id" );
        Iterator documentsIterator;
        if (metaId != null) {
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

            documentsIterator = documentMapper.getDocumentsIterator(idRange);
        }
        else {
            String q = StringUtils.defaultString(request.getParameter( "q" ));
            try {
                Query query = LuceneParsedQuery.parse(q);
                DocumentIndex documentIndex = documentMapper.getDocumentIndex();
                List documents = documentIndex.search(new SimpleDocumentQuery(query), currentUser);
                documentsIterator = documents.iterator();
            }
            catch ( org.apache.lucene.queryParser.ParseException pe ) {
                LOG.debug( "Bad query: " + q, pe );
                documentsIterator = new ArrayList().iterator();
            }
        }

        XmlDocumentBuilder xmlDocumentBuilder = new XmlDocumentBuilder(currentUser);
        
        while (documentsIterator.hasNext()) {
            DocumentDomainObject document = (DocumentDomainObject)documentsIterator.next();
            
            if (document != null) {
                xmlDocumentBuilder.addDocument( document);
            }
        }

        Document xmlDocument = xmlDocumentBuilder.getXmlDocument() ;
        Utility.outputXmlDocument( response, xmlDocument );
    }

}
