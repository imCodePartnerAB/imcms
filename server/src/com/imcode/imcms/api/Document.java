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

    public Document( DocumentDomainObject document, SecurityChecker securityChecker, DocumentService documentService, DocumentMapper documentMapper, DocumentPermissionSetMapper permissionSetMapper ) {
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
        securityChecker.hasEditPermission( this );
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

    public DocumentPermissionSet getPermissionSetRestrictedOne() throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        DocumentPermissionSetDomainObject restrictedOne = documentPermissionMapper.getPermissionSetRestrictedOne( internalDocument );
        DocumentPermissionSet result = new DocumentPermissionSet( restrictedOne );
        return result;
    }

    public DocumentPermissionSet getPermissionSetRestrictedTwo() throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        DocumentPermissionSetDomainObject restrictedTwo = documentPermissionMapper.getPermissionSetRestrictedTwo( internalDocument );
        DocumentPermissionSet result = new DocumentPermissionSet( restrictedTwo );
        return result;
    }

    public int getId() {
        return internalDocument.getMetaId();
    }

    public String getHeadline() throws NoPermissionException {
        securityChecker.hasDocumentPermission( this );
        return internalDocument.getHeadline();
    }

    public String getMenuText() throws NoPermissionException {
        securityChecker.hasDocumentPermission( this );
        return internalDocument.getText();
    }

    public String getMenuImageURL() throws NoPermissionException {
        securityChecker.hasDocumentPermission( this );
        return internalDocument.getImage();
    }

    public void setHeadline( String headline ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setHeadline( headline );
    }

    public void setMenuText( String menuText ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setText( menuText );
    }

    public void setMenuImageURL( String imageUrl ) throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        internalDocument.setImage( imageUrl );
    }

    public User getCreator() throws NoPermissionException {
        securityChecker.hasDocumentPermission( this );
        return new User(internalDocument.getCreator()) ;
    }

    DocumentDomainObject getInternal() {
        return internalDocument;
    }

    public Language getLanguage() {
        return Language.getLanguageByISO639_2(internalDocument.getLanguageIso639_2()) ;
    }
}
