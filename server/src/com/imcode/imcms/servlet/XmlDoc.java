package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.XmlDocumentBuilder;
import imcode.util.Utility;
import org.apache.commons.lang.UnhandledException;
import org.w3c.dom.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;

public class XmlDoc extends HttpServlet {

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        int documentId = Integer.parseInt( request.getParameter( "id" ) );

        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( documentId );

        if ( null == document ) {
            response.sendError( HttpServletResponse.SC_NOT_FOUND ) ;
        } else if (!Utility.getLoggedOnUser( request ).canAccess(document)) {
            response.sendError( HttpServletResponse.SC_FORBIDDEN ) ;
        } else {
            XmlDocumentBuilder xmlDocumentBuilder = new XmlDocumentBuilder();
            xmlDocumentBuilder.addDocument( document );
            xmlDocumentBuilder.addDocument( document );
            Document xmlDocument = xmlDocumentBuilder.getXmlDocument() ;
            outputXmlDocument( response, xmlDocument );
        }
    }

    private void outputXmlDocument( HttpServletResponse response, Document xmlDocument ) throws IOException {
        response.setContentType( "text/xml; charset=UTF-8" );
        writeXmlDocumentToStream( xmlDocument, response.getOutputStream() );
    }

    private void writeXmlDocumentToStream( Document xmlDocument, OutputStream outputStream ) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
            StreamResult outputTarget = new StreamResult( outputStream );
            DOMSource xmlSource = new DOMSource( xmlDocument );
            transformer.transform( xmlSource, outputTarget );
        } catch ( TransformerConfigurationException e ) {
            throw new UnhandledException( e );
        } catch ( TransformerException e ) {
            throw new UnhandledException( e );
        }
    }

}
