package com.imcode.imcms;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Document {
    SecurityChecker securityChecker;
    DocumentDomainObject internalDocument;
    DocumentMapper internalDocumentMapper;
    DocumentPermissionSetMapper internalDocumentPermissionSetMapper;

    public Document( SecurityChecker securityChecker, DocumentDomainObject document, DocumentMapper documentMapper, DocumentPermissionSetMapper permissionSetMapper ) {
        this.securityChecker = securityChecker;
        this.internalDocument = document;
        this.internalDocumentMapper = documentMapper;
        this.internalDocumentPermissionSetMapper = permissionSetMapper;
    }

    /**
     * @return map of rolename String -> DocumentPermissionSet instances.
     */
    public Map getAllRolesMappedToPermissions() throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        Map rolesMappedToPermissionsIds = internalDocumentPermissionSetMapper.getAllRolesMappedToPermissions( internalDocument );
        Map result = wrapDomainObjectsInMap( rolesMappedToPermissionsIds );
        return result;
    }

    private Map wrapDomainObjectsInMap( Map rolesMappedToPermissionsIds ) {
        Map result = new HashMap();
        Set keys = rolesMappedToPermissionsIds.keySet();
        Iterator keyIterator = keys.iterator();
        while( keyIterator.hasNext() ) {
            String roleName = (String)keyIterator.next();
            DocumentPermissionSetDomainObject documentPermissionSetDO = (DocumentPermissionSetDomainObject)rolesMappedToPermissionsIds.get( roleName );
            DocumentPermissionSet documentPermissionSet = new DocumentPermissionSet( documentPermissionSetDO );
            result.put( roleName, documentPermissionSet );
        }
        return result;
    }

}
