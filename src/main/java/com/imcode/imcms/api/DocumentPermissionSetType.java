package com.imcode.imcms.api;

import com.imcode.imcms.domain.dto.PermissionDTO;

public class DocumentPermissionSetType {

    public final static DocumentPermissionSetType FULL = new DocumentPermissionSetType(PermissionDTO.EDIT);
    public final static DocumentPermissionSetType RESTRICTED_1 = new DocumentPermissionSetType(PermissionDTO.RESTRICTED_1);
    public final static DocumentPermissionSetType RESTRICTED_2 = new DocumentPermissionSetType(PermissionDTO.RESTRICTED_2);
    public final static DocumentPermissionSetType READ = new DocumentPermissionSetType(PermissionDTO.VIEW);
    public final static DocumentPermissionSetType NONE = new DocumentPermissionSetType(PermissionDTO.NONE);

    private final PermissionDTO internal;

    DocumentPermissionSetType(PermissionDTO internal) {
        this.internal = internal;
    }

    PermissionDTO getInternal() {
        return internal;
    }
}
