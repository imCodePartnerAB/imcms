package com.imcode.imcms.servlet;

import imcode.util.HttpSessionUtils;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.ApplicationServer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import org.apache.log4j.Logger;

public class ImageArchiveServlet extends HttpServlet {
    private Logger log = Logger.getLogger( ImageArchiveServlet.class );

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        log.debug("Jorå");
        DocumentFinder documentFinder = DocumentFinder.getInstance( request );

        setSelectedDocument( request, documentFinder );

        if( !documentFinder.isDocumentSelected() ) {
            String forwardReturnUrl = "ImageArchiveServlet?" + ImageArchive.REQUEST_ATTRIBUTE_PARAMETER__IMAGE_ARHCIVE + "=" + HttpSessionUtils.getSessionAttributeNameFromRequest( request, ImageArchive.REQUEST_ATTRIBUTE_PARAMETER__IMAGE_ARHCIVE );
            documentFinder.setForwardReturnUrl( forwardReturnUrl );
            documentFinder.forward( request, response );
        } else {
            ImageArchive imageArchive = ImageArchive.getInstance( request );
            DocumentDomainObject documentDomainObject = documentFinder.getSelectedDocument();
            imageArchive.setFileDocumentDomainObject( (FileDocumentDomainObject) documentDomainObject );
            request.getRequestDispatcher( imageArchive.getForwardReturnUrl() ).forward( request, response );
        }
    }

    private void setSelectedDocument( HttpServletRequest request, DocumentFinder documentFinder ) {
        String selectedDocument = request.getParameter( SearchDocuments.REQUEST_PARAM_SELECTED_DOCUMENT );
        if( null != selectedDocument ) {
            DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
            documentFinder.setSelectedDocument( documentMapper.getDocument( Integer.parseInt( selectedDocument ) ) );
        }
    }
}
