package com.imcode.imcms;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;

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
        Map rolesMappedToPermissionsIds = internalDocumentMapper.getAllRolesMappedToPermissions( internalDocument );
        Set keys = rolesMappedToPermissionsIds.keySet();
        Iterator keyIter = keys.iterator();
        Map result = new HashMap();
        while( keyIter.hasNext() ) {
            String roleName = (String)keyIter.next();
            String roleId = (String)rolesMappedToPermissionsIds.get( roleName );
            result.put( roleName, DocumentPermissionSet.get( Integer.parseInt(roleId) ));
        }
        return result;
    }

    public void setRestrictedPermissionOne( DocumentPermissionSet restriction ) {
        setRestrictedPermission( 1, restriction );
    }

    public void setRestrictedPermissionTwo( DocumentPermissionSet restriction ) {
        setRestrictedPermission( 2, restriction );
    }

    public void setRestrictedPermission( int restrictionNumber,  DocumentPermissionSet restriction ) {

    }

    public DocumentPermissionSet getRestrictedPermissionOne() {
        return getRestrictedPermission( 1 );
    }

    public DocumentPermissionSet getRestrictedPermissionTwo() {
        return getRestrictedPermission( 2 );
    }

    private DocumentPermissionSet getRestrictedPermission( int i ) {
        DocumentPermissionSet result = null;
        return result;
    }
}
