package com.imcode.imcms.api;

import imcode.server.document.DocumentPermissionSetTypeDomainObject;

/**
 * Describes a type of permission set.
 * Some types have a defined permisssion set, while other can have a permission set allowing or not certain actions.
 */
public class DocumentPermissionSetType {

    /**
     * Grants a permission set allowing all actions.
     */
    public final static DocumentPermissionSetType FULL = new DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject.FULL);

    /**
     * A permission set type which permission set can be defined.
     */
    public final static DocumentPermissionSetType RESTRICTED_1 = new DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject.RESTRICTED_1);

    /**
     * A permission set type which permission set can be defined.
     */
    public final static DocumentPermissionSetType RESTRICTED_2 = new DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject.RESTRICTED_2);

    /**
     * Grants read only permission set, not editing of anything is allowed.
     */
    public final static DocumentPermissionSetType READ = new DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject.READ);

    /**
     * The permission set of this type doesn't allow nor editing for seeing.
     */
    public final static DocumentPermissionSetType NONE = new DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject.NONE);

    private final DocumentPermissionSetTypeDomainObject internal ;

    DocumentPermissionSetType(DocumentPermissionSetTypeDomainObject internal) {
        this.internal = internal;
    }

    DocumentPermissionSetTypeDomainObject getInternal() {
        return internal;
    }
}
