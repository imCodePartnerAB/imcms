package com.imcode.imcms;

import imcode.server.IMCText;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;

import java.util.Map;

public class Document {
    protected SecurityChecker securityChecker;
    protected DocumentDomainObject internalDocument;
    protected DocumentMapper internalDocumentMapper;

    public Document( SecurityChecker securityChecker, DocumentDomainObject document, DocumentMapper mapper ) {
        this.securityChecker = securityChecker;
        this.internalDocument = document;
        this.internalDocumentMapper = mapper;
    }

    /**
     * @return map of rolename String -> {@link com.imcode.imcms.DocumentPermissionSet} constants.
     */
    public Map getAllRolesMappedToPermissions() throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        return internalDocumentMapper.getAllRolesMappedToPermissions( internalDocument );
    }
}
