package com.imcode.imcms;

import imcode.server.document.DocumentPermissionSetDomainObject;

public class DocumentPermissionSet {
    public final static String FULL = DocumentPermissionSetDomainObject.FULL;
    public final static String READ = DocumentPermissionSetDomainObject.READ;
    public final static String NONE = DocumentPermissionSetDomainObject.NONE;
    public final static String RESTRICTED_1 = DocumentPermissionSetDomainObject.RESTRICTED_1;
    public final static String RESTRICTED_2 = DocumentPermissionSetDomainObject.RESTRICTED_2;

    private DocumentPermissionSetDomainObject internalDocPermSet;

    public DocumentPermissionSet( DocumentPermissionSetDomainObject internalDocPermSet ) {
        this.internalDocPermSet = internalDocPermSet;
    }

    public String getName() {
        return internalDocPermSet.getName();
    }

    public String toString() {
        return internalDocPermSet.toString();
    }
}
