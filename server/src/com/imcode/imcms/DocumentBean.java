package com.imcode.imcms;

import imcode.server.user.ImcmsAuthenticatorAndUserMapper;

import java.util.Map;
import java.util.HashMap;
import imcode.server.document.Document ;
import imcode.server.document.DocumentMapper;

public class DocumentBean {
    private SecurityChecker securityChecker;
    private Document document;
    private DocumentMapper mapper;

    DocumentBean( SecurityChecker securityChecker,  Document document, DocumentMapper mapper ) {
        this.securityChecker = securityChecker;
        this.document = document;
        this.mapper = mapper;
    }

    /**
     * @return map of rolename String -> {@link DocumentPermissionSet} constants.
     */
    public Map getAllRolesMappedToPermissions() throws NoPermissionException {
        securityChecker.hasEditPermission( document );
        return mapper.getAllRolesMappedToPermissions( document );
    }

}