package com.imcode.imcms.api;

import imcode.server.document.DocumentPermissionSetDomainObject;

public class DocumentPermissionSet {

    public final static int FULL = DocumentPermissionSetDomainObject.FULL ;
    public final static int RESTRICTED_1 = DocumentPermissionSetDomainObject.RESTRICTED_1 ;
    public final static int RESTRICTED_2 = DocumentPermissionSetDomainObject.RESTRICTED_2 ;
    public final static int READ = DocumentPermissionSetDomainObject.READ ;
    public final static int NONE = DocumentPermissionSetDomainObject.NONE ;

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

    public boolean getEditHeadlinePermission() {
        return internalDocPermSet.getEditHeadline();
    }

    public boolean getEditDocumentInformationPermission() {
        return internalDocPermSet.getEditDocumentInformation();
    }

    public boolean getEditRolePermissionsPermission() {
        return internalDocPermSet.getEditPermissions();
    }

    public boolean getEditTextsPermission() {
        return internalDocPermSet.getEditTexts();
    }

    public boolean getEditIncludesPermission() {
        return internalDocPermSet.getEditIncludes();
    }

    public boolean getEditPicturesPermission() {
        return internalDocPermSet.getEditPictures();
    }

    public String[] getEditableTemplateGroupNames() {
        return internalDocPermSet.getEditableTemplateNames();
    }

    public String[] getEditableMenuDocumentTypeNames() {
        return internalDocPermSet.getEditableMenuNames();
    }

    // set

    // todo
    /*
    public void setEditableMenuNames( boolean b ) {
        internalDocPermSet.setEditableMenuNames( b );
    }
    public void setEditMenuesPermission( boolean b ) {
        internalDocPermSet.setEditMenus( b );
    }

    public void setEditableTemplateGroupNames( boolean b ) {
        internalDocPermSet.setEditableTemplateGroupNames( b );
    }
    public void setEditTemplatesPermission( boolean b ) {
        internalDocPermSet.setEditTemplates( b );
    }

    public void setPermissionTypePermission( boolean b ) {
        internalDocPermSet.setPermissionType( b );
    }
    */

    public void setEditDocumentInformationPermission( boolean b ) {
        internalDocPermSet.setEditDocumentInformation( b );
    }
    public void setEditHeadlinePermission( boolean b ) {
        internalDocPermSet.setEditHeadline( b );
    }
    public void setEditIncludesPermission( boolean b ) {
        internalDocPermSet.setEditIncludes( b );
    }
    public void setEditPermissionsPermission( boolean b ) {
        internalDocPermSet.setEditPermissions( b );
    }
    public void setEditPicturesPermission( boolean b ) {
        internalDocPermSet.setEditPictures( b );
    }
    public void setEditTextsPermission( boolean b ) {
        internalDocPermSet.setEditTexts( b );
    }

    public void setEditRolePermissionsPermission( boolean b ) {
        internalDocPermSet.setEditPermissions( b );
    }
}