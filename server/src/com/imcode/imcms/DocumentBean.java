package com.imcode.imcms;

import imcode.server.IMCText;
import imcode.server.document.Document;
import imcode.server.document.DocumentMapper;

import java.util.Map;

public class DocumentBean {
    protected SecurityChecker securityChecker;
    protected Document document;
    protected DocumentMapper mapper;

    /**
     * @return map of rolename String -> {@link com.imcode.imcms.DocumentPermissionSet} constants.
     */
    public Map getAllRolesMappedToPermissions() throws NoPermissionException {
        securityChecker.hasEditPermission( document );
        return mapper.getAllRolesMappedToPermissions( document );
    }
}
