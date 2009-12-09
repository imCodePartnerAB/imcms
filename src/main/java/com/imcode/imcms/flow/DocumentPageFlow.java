package com.imcode.imcms.flow;

import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.user.UserDomainObject;
import imcode.util.HttpSessionUtils;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.Utility;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.List;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.NotImplementedException;

import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.mapping.NoPermissionInternalException;
import com.imcode.imcms.api.DocumentLabels;
import com.imcode.imcms.api.I18nLanguage;

public abstract class DocumentPageFlow extends PageFlow {

    protected final DocumentPageFlow.SaveDocumentCommand saveDocumentCommand;

    protected DocumentPageFlow( DispatchCommand returnCommand,
                                SaveDocumentCommand saveDocumentCommand ) {
        super( returnCommand );
        this.saveDocumentCommand = saveDocumentCommand;
    }

    public abstract DocumentDomainObject getDocument() ;

    protected synchronized void saveDocument( HttpServletRequest request ) {
        try {
            saveDocumentCommand.saveDocument( getDocument(), Utility.getLoggedOnUser( request ) );
        } catch ( NoPermissionToEditDocumentException e ) {
            throw new ShouldHaveCheckedPermissionsEarlierException(e);
        } catch ( NoPermissionToAddDocumentToMenuException e ) {
            throw new ConcurrentDocumentModificationException(e);
        } catch (DocumentSaveException e) {
            throw new UnhandledException(e);
        }
    }

    protected void saveDocumentAndReturn( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        saveDocument( request );
        dispatchReturn( request, response );
    }

    public static DocumentPageFlow fromRequest( HttpServletRequest request ) {
        return (DocumentPageFlow)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW );
    }

    public static abstract class SaveDocumentCommand implements Serializable {
        
       /**
        * Default implementation ignores labels and languages. 
        */
       public void saveDocument(DocumentDomainObject document, Collection<DocumentLabels> labels, UserDomainObject user)
               throws NoPermissionInternalException, DocumentSaveException {
           saveDocument(document, user);
       }

       public abstract void saveDocument(DocumentDomainObject document, UserDomainObject user) throws NoPermissionInternalException, DocumentSaveException;
    }
}