package com.imcode.imcms.servlet;

import com.imcode.imcms.servlet.admin.ImageArchive;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;

import imcode.util.HttpSessionUtils;
import imcode.server.document.FileDocumentDomainObject;

import java.io.IOException;

public class ImageArchiveFacade extends ServletFacade {
    private boolean imageSelected;
    private FileDocumentDomainObject fileDocumentDomainObject;

    public static ImageArchiveFacade getInstance( HttpServletRequest request ) {
        ImageArchiveFacade imageArhiveFacade = (ImageArchiveFacade)HttpSessionUtils.getObjectFromSessionWithKeyInRequest( request, ImageArchive.REQUEST_ATTRIBUTE_PARAMETER__IMAGE_ARHCIVE );
        if ( null == imageArhiveFacade ) {
            imageArhiveFacade = new ImageArchiveFacade();
        }
        return imageArhiveFacade;
    }

    public boolean isImageSelected() {
        return imageSelected;
    }

    public FileDocumentDomainObject getSelectedImage() {
        return fileDocumentDomainObject;
    }

    public void setFileDocumentDomainObject( FileDocumentDomainObject fileDocumentDomainObject ) {
        this.imageSelected = true;
        this.fileDocumentDomainObject = fileDocumentDomainObject;
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        HttpSessionUtils.addObjectToSessionAndSetSessionAttributeNameInRequest( this, request, ImageArchive.REQUEST_ATTRIBUTE_PARAMETER__IMAGE_ARHCIVE );
        RequestDispatcher rd = request.getRequestDispatcher( "ImageArchive" );
        rd.forward( request, response );
    }


}
