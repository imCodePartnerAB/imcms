package com.imcode.imcms.api;

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
    DocumentService documentService;
    DocumentDomainObject internalDocument;
    DocumentMapper documentMapper;
    DocumentPermissionSetMapper documentPermissionMapper;

    public Document( SecurityChecker securityChecker, DocumentService documentService, DocumentDomainObject document, DocumentMapper documentMapper, DocumentPermissionSetMapper permissionSetMapper ) {
        this.securityChecker = securityChecker;
        this.documentService = documentService;
        this.internalDocument = document;
        this.documentMapper = documentMapper;
        this.documentPermissionMapper = permissionSetMapper;
    }

    /**
     * @return map of rolename String -> DocumentPermissionSet instances.
     */
    public Map getAllRolesMappedToPermissions() throws NoPermissionException {
        securityChecker.hasEditPermission( internalDocument );
        Map rolesMappedToPermissionsIds = documentPermissionMapper.getAllRolesMappedToPermissions( internalDocument );
        Map result = wrapDomainObjectsInMap( rolesMappedToPermissionsIds );
        return result;
    }

    private static Map wrapDomainObjectsInMap( Map rolesMappedToPermissionsIds ) {
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

    public DocumentPermissionSet getPermissionSetRestrictedOne() {
        DocumentPermissionSetDomainObject restrictedOne = documentPermissionMapper.getPermissionSetRestrictedOne( internalDocument );
        DocumentPermissionSet result = new DocumentPermissionSet( restrictedOne );
        return result;
    }

    public DocumentPermissionSet getPermissionSetRestrictedTwo() {
        DocumentPermissionSetDomainObject restrictedTwo = documentPermissionMapper.getPermissionSetRestrictedTwo( internalDocument );
        DocumentPermissionSet result = new DocumentPermissionSet( restrictedTwo );
        return result;
    }

    public String getHeadline() {
        return internalDocument.getHeadline();
    }

    public String getMenuText() {
        return internalDocument.getText();
    }

    public String getMenuImageURL() {
        return internalDocument.getImage();
    }

    public void setHeadline( String headline ) {
        internalDocument.setHeadline( headline );
    }

    public void setMenuText( String menuText ) {
        internalDocument.setText( menuText );
    }

    public void setMenuImageURL( String imageUrl ) {
        internalDocument.setImage( imageUrl );
    }

    public int getId() {
        return internalDocument.getMetaId();
    }

    DocumentDomainObject getInternal() {
        return internalDocument;
    }
}
