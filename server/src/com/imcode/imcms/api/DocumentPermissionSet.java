package com.imcode.imcms.api;

import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.TemplateGroupDomainObject;

public class DocumentPermissionSet {

    public final static int FULL = DocumentPermissionSetDomainObject.TYPE_ID__FULL;
    public final static int RESTRICTED_1 = DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1;
    public final static int RESTRICTED_2 = DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2;
    public final static int READ = DocumentPermissionSetDomainObject.TYPE_ID__READ;
    public final static int NONE = DocumentPermissionSetDomainObject.TYPE_ID__NONE;

    private DocumentPermissionSetDomainObject internalDocPermSet;

    public DocumentPermissionSet( DocumentPermissionSetDomainObject internalDocPermSet ) {
        this.internalDocPermSet = internalDocPermSet;
    }

    public String getType() {
        return internalDocPermSet.getType();
    }

    public String toString() {
        return internalDocPermSet.toString();
    }

    /**
     * @deprecated Use {@link #getEditDocumentInformationPermission()}
     */
    public boolean getEditHeadlinePermission() {
        return internalDocPermSet.getEditDocumentInformation();
    }

    public boolean getEditDocumentInformationPermission() {
        return internalDocPermSet.getEditDocumentInformation();
    }

    public boolean getEditRolePermissionsPermission() {
        return internalDocPermSet.getEditPermissions();
    }

    public boolean getEditTextsPermission() {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            return ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).getEditTexts();
        }

        return false;
    }

    public boolean getEditIncludesPermission() {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            return ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).getEditIncludes();
        }
        return false;
    }

    public boolean getEditPicturesPermission() {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            return ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).getEditImages();
        }
        return false;
    }

    public String[] getEditableTemplateGroupNames() {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            TemplateGroupDomainObject[] internalTemplateGroups = ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).getAllowedTemplateGroups();
            String[] templateGroupNames = new String[internalTemplateGroups.length] ;
            for ( int i = 0; i < internalTemplateGroups.length; i++ ) {
                TemplateGroupDomainObject internalTemplateGroup = internalTemplateGroups[i];
                templateGroupNames[i] = internalTemplateGroup.getName() ;
            }
            return templateGroupNames ;
        }
        return new String[]{};
    }

    /**
     * @return An empty String array.
     * @deprecated This method will be removed. Returning document type names is broken.
     */
    public String[] getEditableMenuDocumentTypeNames() {
        return new String[]{};
    }

    public void setEditDocumentInformationPermission( boolean b ) {
        internalDocPermSet.setEditDocumentInformation( b );
    }

    /**
     * @deprecated Use {@link #setEditDocumentInformationPermission(boolean)} *
     */
    public void setEditHeadlinePermission( boolean b ) {
        internalDocPermSet.setEditDocumentInformation( b );
    }

    public void setEditIncludesPermission( boolean b ) {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).setEditIncludes( b );
        }
    }

    public void setEditPermissionsPermission( boolean b ) {
        internalDocPermSet.setEditPermissions( b );
    }

    public void setEditPicturesPermission( boolean b ) {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).setEditImages( b );
        }
    }

    public void setEditTextsPermission( boolean b ) {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).setEditTexts( b );
        }
    }

    public void setEditRolePermissionsPermission( boolean b ) {
        internalDocPermSet.setEditPermissions( b );
    }
}