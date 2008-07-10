package com.imcode.imcms.api;

import imcode.server.document.DocumentPermissionSetTypeDomainObject;

public class DocumentPermissionSetType {

    public final static DocumentPermissionSetType FULL = new DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject.FULL);
    public final static DocumentPermissionSetType RESTRICTED_1 = new DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject.RESTRICTED_1);
    public final static DocumentPermissionSetType RESTRICTED_2 = new DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject.RESTRICTED_2);
    public final static DocumentPermissionSetType READ = new DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject.READ);
    public final static DocumentPermissionSetType NONE = new DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject.NONE);

    private final DocumentPermissionSetTypeDomainObject internal ;

    DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject internal) {
        this.internal = internal;
    }

    DocumentPermissionSetTypeDomainObject getInternal() {
        return internal;
    }
}
