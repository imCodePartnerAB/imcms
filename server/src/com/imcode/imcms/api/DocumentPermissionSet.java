package com.imcode.imcms.api;

import imcode.server.document.DocumentPermissionSetDomainObject;

public class DocumentPermissionSet {
    public final static String FULL = DocumentPermissionSetDomainObject.FULL;
    public final static String RESTRICTED_1 = DocumentPermissionSetDomainObject.RESTRICTED_1;
    public final static String RESTRICTED_2 = DocumentPermissionSetDomainObject.RESTRICTED_2;
    public final static String READ = DocumentPermissionSetDomainObject.READ;
    public final static String NONE = DocumentPermissionSetDomainObject.NONE;

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
        return internalDocPermSet.getEditableTamplateNames();
    }

    public String[] getEditableMenuDocumentTypeNames() {
        return internalDocPermSet.getEditableMenuNames();
    }
}
