package com.imcode.imcms.servlet;

import imcode.server.document.DocumentDomainObject;
import imcode.util.HttpSessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import java.io.IOException;

public class DocumentFinder extends WebComponent {
    private boolean documentSelected;
    private DocumentDomainObject selectedDocument;
    public static final String REQUEST_ATTRIBUTE_PARAMETER__SEARCH_DOCUMENTS = "searchDocuments";

    public static DocumentFinder getInstance( HttpServletRequest request ) {
        DocumentFinder documentFinder = (DocumentFinder)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, DocumentFinder.REQUEST_ATTRIBUTE_PARAMETER__SEARCH_DOCUMENTS );
        if ( null == documentFinder ) {
            documentFinder = new DocumentFinder();
        }
        return documentFinder;
    }

    public boolean isDocumentSelected() {
        return documentSelected;
    }

    public void setSelectedDocument( DocumentDomainObject selectedDocument ) {
        documentSelected = true;
        this.selectedDocument = selectedDocument;
    }

    public DocumentDomainObject getSelectedDocument() {
        return selectedDocument;
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        HttpSessionUtils.setSessionAttributeAndSetNameInRequestAttribute( this, request, DocumentFinder.REQUEST_ATTRIBUTE_PARAMETER__SEARCH_DOCUMENTS );

        String SearchTargetString = "SearchDocuments?" +
                SearchDocuments.PARAM__DOCUMENT_TYPE + "=" + DocumentDomainObject.DOCTYPE_FILE + "&" +
                SearchDocuments.PARAM__SHOW_SELECT_LINK + "=" + "true" + "&" +
                SearchDocuments.PARAM__CHOSEN_URL + "=" + this.getForwardReturnUrl() ;
        RequestDispatcher rd = request.getRequestDispatcher( SearchTargetString );
        rd.forward( request, response );
    }
}
