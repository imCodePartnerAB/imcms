package com.imcode.imcms;

import imcode.server.IMCText;
import imcode.server.document.Document;
import imcode.server.document.DocumentMapper;

import java.util.Map;

public class DocumentBean {
    protected SecurityChecker securityChecker;
    protected Document internalDocument;
    protected DocumentMapper internalDocumentMapper;

    public DocumentBean( SecurityChecker securityChecker, Document document, DocumentMapper mapper ) {
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
