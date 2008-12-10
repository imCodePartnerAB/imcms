package com.imcode.imcms.api;

import com.imcode.util.CountingIterator;
import imcode.server.Imcms;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;

import java.util.List;
import java.util.Set;

public class DocumentPermissionSet {

    /** @deprecated Use {@link DocumentPermissionSetType#FULL} */
    public final static int FULL = DocumentPermissionSetTypeDomainObject.FULL.getId();
    /** @deprecated Use {@link DocumentPermissionSetType#RESTRICTED_1} */
    public final static int RESTRICTED_1 = DocumentPermissionSetTypeDomainObject.RESTRICTED_1.getId();
    /** @deprecated Use {@link DocumentPermissionSetType#RESTRICTED_2} */
    public final static int RESTRICTED_2 = DocumentPermissionSetTypeDomainObject.RESTRICTED_2.getId();
    /** @deprecated Use {@link DocumentPermissionSetType#READ} */
    public final static int READ = DocumentPermissionSetTypeDomainObject.READ.getId();
    /** @deprecated Use {@link DocumentPermissionSetType#NONE} */
    public final static int NONE = DocumentPermissionSetTypeDomainObject.NONE.getId();

    private DocumentPermissionSetDomainObject internalDocPermSet;

    public DocumentPermissionSet( DocumentPermissionSetDomainObject internalDocPermSet ) {
        this.internalDocPermSet = internalDocPermSet;
    }

    /**
     * @deprecated No replacement, don't use. Will be removed in 4.0 or later. 
     */
    public String getType() {
        return internalDocPermSet.getTypeName();
    }

    public String toString() {
        return internalDocPermSet.toString();
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

    public boolean getEditMenusPermission() {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            return ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).getEditMenus();
        }

        return false;
    }

    public String[] getEditableTemplateGroupNames() {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            Set allowedTemplateGroupIds = ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).getAllowedTemplateGroupIds();
            List allowedTemplateGroups = Imcms.getServices().getTemplateMapper().getTemplateGroups(allowedTemplateGroupIds);
            String[] templateGroupNames = new String[allowedTemplateGroupIds.size()] ;
            for ( CountingIterator iterator = new CountingIterator(allowedTemplateGroups.iterator()); iterator.hasNext(); ) {
                TemplateGroupDomainObject templateGroup = (TemplateGroupDomainObject) iterator.next();
                templateGroupNames[iterator.getCount()-1] = templateGroup.getName() ;
            }
            return templateGroupNames ;
        }
        return new String[]{};
    }

    public void setEditDocumentInformationPermission( boolean b ) {
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

    public void setEditMenusPermission( boolean b ) {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).setEditMenus( b );
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