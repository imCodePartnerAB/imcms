package com.imcode.imcms.servlet.admin;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import imcode.server.document.FileDocumentDomainObject;
import imcode.server.ApplicationServer;
import imcode.util.HttpSessionUtils;
import com.imcode.imcms.servlet.ImageArchiveFacade;

public class ImageArchive extends HttpServlet {
    public static final String REQUEST_ATTRIBUTE_PARAMETER__IMAGE_ARHCIVE = "imageArchive";

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doGet( request, response );
    }

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        ImageArchiveFacade imageArchiveFacade = (ImageArchiveFacade) HttpSessionUtils.getObjectFromSessionWithKeyInRequest( request, REQUEST_ATTRIBUTE_PARAMETER__IMAGE_ARHCIVE );

        FileDocumentDomainObject imageFileDoc = (FileDocumentDomainObject) ApplicationServer.getIMCServiceInterface().getDocumentMapper().getDocument( 1002 );
        imageArchiveFacade.setFileDocumentDomainObject( imageFileDoc );

        String forwardReturnUrl = imageArchiveFacade.getForwardReturnUrl();
        request.getRequestDispatcher( forwardReturnUrl ).forward( request, response );

        // todo, use the search instead.
/*
        String SearchTargetString = "SearchDocuments?"+
                        SearchDocuments.PARAM_DOCUMENT_TYPE + "=" + DocumentDomainObject.DOCTYPE_FILE;
        RequestDispatcher rd = request.getRequestDispatcher(SearchTargetString );
        rd.forward(request,response);*/
    }
}
