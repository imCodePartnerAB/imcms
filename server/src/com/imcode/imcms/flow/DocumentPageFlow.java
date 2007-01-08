package com.imcode.imcms.flow;

import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.mapping.NoPermissionInternalException;
import imcode.server.document.ConcurrentDocumentModificationException;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.NoPermissionToEditDocumentException;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.user.UserDomainObject;
import imcode.util.HttpSessionUtils;
import imcode.util.ShouldHaveCheckedPermissionsEarlierException;
import imcode.util.Utility;
import org.apache.commons.lang.UnhandledException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

public abstract class DocumentPageFlow extends PageFlow {

    protected final DocumentPageFlow.SaveDocumentCommand saveDocumentCommand;

    protected DocumentPageFlow( DispatchCommand returnCommand,
                                SaveDocumentCommand saveDocumentCommand ) {
        super( returnCommand );
        this.saveDocumentCommand = saveDocumentCommand;
    }

    public abstract DocumentDomainObject getDocument() ;

    private synchronized void saveDocument( HttpServletRequest request ) {
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

    public static interface SaveDocumentCommand extends Serializable {
        void saveDocument( DocumentDomainObject document, UserDomainObject user ) throws NoPermissionInternalException, DocumentSaveException;
    }
}