package com.imcode.imcms.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import imcode.util.HttpSessionUtils;
import imcode.server.document.FileDocumentDomainObject;

import java.io.IOException;

public class ImageArchive extends WebComponent {
    private boolean imageSelected;
    private FileDocumentDomainObject fileDocumentDomainObject;

    public static final String REQUEST_ATTRIBUTE_PARAMETER__IMAGE_ARCHIVE = "imageArchive";

    public static ImageArchive getInstance( HttpServletRequest request ) {
        ImageArchive imageArhive = (ImageArchive)HttpSessionUtils.getObjectFromSessionWithKeyInRequest( request, REQUEST_ATTRIBUTE_PARAMETER__IMAGE_ARCHIVE );
        if ( null == imageArhive ) {
            imageArhive = new ImageArchive();
        }
        return imageArhive;
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
        HttpSessionUtils.addObjectToSessionAndSetSessionAttributeNameInRequest( this, request, ImageArchive.REQUEST_ATTRIBUTE_PARAMETER__IMAGE_ARCHIVE );
        request.getRequestDispatcher( "ImageArchiveServlet" ).forward( request, response );
    }
}
