package com.imcode.imcms.api;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.DocumentPermissionSetMapper;
import imcode.server.IMCConstants;
import imcode.server.user.UserAndRoleMapper;
import imcode.server.user.RoleDomainObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class Document {
    SecurityChecker securityChecker;
    DocumentService documentService;
    DocumentDomainObject internalDocument;
    DocumentMapper documentMapper;
    DocumentPermissionSetMapper documentPermissionSetMapper;
    UserAndRoleMapper userAndRoleMapper;

    private final static Logger log = Logger.getLogger( "com.imcode.imcms.api.Document" );

    public Document( DocumentDomainObject document, SecurityChecker securityChecker, DocumentService documentService, DocumentMapper documentMapper, DocumentPermissionSetMapper permissionSetMapper, UserAndRoleMapper userAndRoleMapper ) {
        this.securityChecker = securityChecker;
        this.documentService = documentService;
        this.internalDocument = document;
        this.documentMapper = documentMapper;
        this.documentPermissionSetMapper = permissionSetMapper;
        this.userAndRoleMapper = userAndRoleMapper;
    }

    /**
     * @return map of rolename String -> DocumentPermissionSet instances.
     */
    public Map getAllRolesMappedToPermissions() throws NoPermissionException {
        securityChecker.hasEditPermission( this );

        Map rolesMappedToPermissionSetIds = internalDocument.getRolesMappedToPermissionSetIds() ;

        Map result = new HashMap();
        for( Iterator it = rolesMappedToPermissionSetIds.entrySet().iterator(); it.hasNext() ; ) {
            Map.Entry rolePermissionTuple = (Map.Entry) it.next() ;
            RoleDomainObject role = (RoleDomainObject) rolePermissionTuple.getKey() ;
            int permissionType = ((Integer)rolePermissionTuple.getValue()).intValue() ;
            switch( permissionType ) {
                case IMCConstants.DOC_PERM_SET_FULL:
                    result.put( role.getName(), documentPermissionSetMapper.createFullPermissionSet() );
                    break;
                case IMCConstants.DOC_PERM_SET_RESTRICTED_1:
                case IMCConstants.DOC_PERM_SET_RESTRICTED_2:
                    result.put( role.getName(), documentPermissionSetMapper.createRestrictedPermissionSet( internalDocument, permissionType, securityChecker.getCurrentLoggedInUser().getLangPrefix() )  );
                    break;
                case IMCConstants.DOC_PERM_SET_READ:
                    result.put( role.getName(), documentPermissionSetMapper.createReadPermissionSet()  );
                    break;
                case IMCConstants.DOC_PERM_SET_NONE:
                    break;
                default:
                    log.warn( "A missing mapping in DocumentPermissionSetMapper" );
                    break;
            }
        }

        return wrapDomainObjectsInMap( result );

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

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Document ) ) {
            return false;
        }

        final Document document = (Document) o;

        if ( !internalDocument.equals( document.internalDocument ) ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return internalDocument.hashCode();
    }

    public DocumentPermissionSet getPermissionSetRestrictedOne() throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        DocumentPermissionSetDomainObject restrictedOne = documentPermissionSetMapper.getPermissionSetRestrictedOne( internalDocument );
        DocumentPermissionSet result = new DocumentPermissionSet( restrictedOne );
        return result;
    }

    public DocumentPermissionSet getPermissionSetRestrictedTwo() throws NoPermissionException {
        securityChecker.hasEditPermission( this );
        DocumentPermissionSetDomainObject restrictedTwo = documentPermissionSetMapper.getPermissionSetRestrictedTwo( internalDocument );
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

    public void setPermissionSetForRole( String roleName, int permissionSet ) throws NoSuchRoleException {
        RoleDomainObject role = userAndRoleMapper.getRole(roleName);
        if (null == role) {
            throw new NoSuchRoleException("No role by the name '"+roleName+"'.") ;
        }
        internalDocument.setPermissionSetForRole(role, permissionSet) ;
    }
}
