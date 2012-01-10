package com.imcode.imcms.api;

import com.imcode.util.CountingIterator;
import imcode.server.Imcms;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;

import java.util.List;
import java.util.Set;

/**
 * Class describing which actions are allowed to be performed on a document.
 */
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

    /**
     * Constructs document permission set with the given DocumentPermissionSetDomainObject or it's sub-class backing it.
     * Depending on the class of internalDocPermSet backing this set some action permissions are ignored.
     * Such as, TextDocumentPermissionSetDomainObject supports editing of texts, images, etc
     * while plain DocumentPermissionSetDomainObject (used for url documents for example) doesn't,
     * since URLDocuments don't have text, images, etc.
     * @param internalDocPermSet DocumentPermissionSetDomainObject to be used internally
     */
    public DocumentPermissionSet( DocumentPermissionSetDomainObject internalDocPermSet ) {
        this.internalDocPermSet = internalDocPermSet;
    }

    /**
     * @deprecated No replacement, don't use. Will be removed in 4.0 or later. 
     */
    public String getType() {
        return internalDocPermSet.getTypeName();
    }

    /**
     * Returns a string representation of this permission set, consisting of DocumentPermissionSetType name and if the
     * type is either {@link DocumentPermissionSetType#RESTRICTED_1} or {@link DocumentPermissionSetType#RESTRICTED_2} values of
     * {@link DocumentPermissionSet#getEditDocumentInformationPermission} and {@link DocumentPermissionSet#getEditRolePermissionsPermission}
     * @return a String representation of this DocumentPermissionSet
     */
    public String toString() {
        return internalDocPermSet.toString();
    }

    /**
     * Returns if the permission set allows editing of document's meta data(info)
     * @return boolean value if meta data is allowed to be edited
     */
    public boolean getEditDocumentInformationPermission() {
        return internalDocPermSet.getEditDocumentInformation();
    }

    /**
     * Returns if the permission set allows editing of document's role privileges.
     * @return boolean value if role privileges are allowed to be edited
     */
    public boolean getEditRolePermissionsPermission() {
        return internalDocPermSet.getEditPermissions();
    }

    /**
     * Returns if the permission set allows editing of text fields
     * @return boolean value if text fields are allowed to be edited
     */
    public boolean getEditTextsPermission() {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            return ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).getEditTexts();
        }

        return false;
    }

    /**
     * Returns if the permission set allows editing of includes
     * @return boolean value if includes are allowed to be edited
     */
    public boolean getEditIncludesPermission() {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            return ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).getEditIncludes();
        }
        return false;
    }

    /**
     * Returns if the permission set allows editing of images
     * @return boolean value if images are allowed to be edited
     */
    public boolean getEditPicturesPermission() {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            return ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).getEditImages();
        }
        return false;
    }

    /**
     * Returns if the permission set allows editing of menus
     * @return boolean value if menus are allowed to be edited
     */
    public boolean getEditMenusPermission() {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            return ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).getEditMenus();
        }

        return false;
    }

    /**
     * Returns the names of template groups, templates of which are allowed to be assigned to the document
     * possessing this permission set.
     * @return array of template group names.
     */
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

    /**
     * Sets if the pemission set allows editing of document's meta data.
     * @param b true to allow meta data editing, false to not to
     */
    public void setEditDocumentInformationPermission( boolean b ) {
        internalDocPermSet.setEditDocumentInformation( b );
    }

    /**
     * Sets if the pemission set allows editing of document's includes.
     * @param b true to allow editing of includes, false to not to
     */
    public void setEditIncludesPermission( boolean b ) {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).setEditIncludes( b );
        }
    }

    /**
     * Sets if the pemission set allows editing of document's role permissions.
     * @param b true to allow editing of role permissions, false to not to
     */
    public void setEditPermissionsPermission( boolean b ) {
        internalDocPermSet.setEditPermissions( b );
    }

    /**
     * Sets if the pemission set allows editing of document's includes.
     * @param b true to allow editing of includes, false to not to
     */
    public void setEditPicturesPermission( boolean b ) {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).setEditImages( b );
        }
    }

    /**
     * Sets if the pemission set allows editing of document's menus.
     * @param b true to allow editing of menus, false to not to
     */
    public void setEditMenusPermission( boolean b ) {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).setEditMenus( b );
        }
    }

    /**
     * Sets if the pemission set allows editing of document's texts.
     * @param b true to allow editing of texts, false to not to
     */
    public void setEditTextsPermission( boolean b ) {
        if ( internalDocPermSet instanceof TextDocumentPermissionSetDomainObject ) {
            ( (TextDocumentPermissionSetDomainObject)internalDocPermSet ).setEditTexts( b );
        }
    }

    /**
     * Sets if the pemission set allows editing of document's role permission.
     * Same as {@link DocumentPermissionSet#setEditPermissionsPermission(boolean)}
     * @param b true to allow editing of includes, false to not to
     */
    public void setEditRolePermissionsPermission( boolean b ) {
        internalDocPermSet.setEditPermissions( b );
    }
}