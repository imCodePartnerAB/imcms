package com.imcode.imcms.api;

import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.log4j.Logger;

public class SecurityChecker {

    private final static String SUPERADMIN_ROLE = "Superadmin";
    private final static String USER_ADMIN = "Useradmin";

    private DocumentMapper docMapper;
    private UserDomainObject accessingUser;
    private HashSet accessorRoles;

    private boolean isSuperAdmin;
    private boolean isUserAdmin;

    private static Logger log = Logger.getLogger( SecurityChecker.class );

    SecurityChecker( DocumentMapper docMapper, UserDomainObject accessor, String[] accessorRoles ) {
        this.docMapper = docMapper;
        this.accessingUser = accessor;
        this.accessorRoles = new HashSet( Arrays.asList( accessorRoles ) );

        isSuperAdmin = this.accessorRoles.contains( SUPERADMIN_ROLE );
        isUserAdmin = this.accessorRoles.contains( USER_ADMIN );
    }

    void isUserAdmin() throws NoPermissionException {
        if( !isUserAdmin ) {
            throw new NoPermissionException( "User is not " + SUPERADMIN_ROLE );
        }
    }

    void isSuperAdmin() throws NoPermissionException {
        if( !isSuperAdmin ) {
            throw new NoPermissionException( "User is not " + SUPERADMIN_ROLE );
        }
    }

    void hasEditPermission( int documentId ) throws NoPermissionException {
        if( !docMapper.hasEditPermission( accessingUser, docMapper.getDocument( documentId ) ) ) {
            throw new NoPermissionException("The logged in user does not have permission to edit document " + documentId );
        };
    }

    void hasEditPermission( Document document ) throws NoPermissionException  {
        hasEditPermission(document.internalDocument.getId());
    }

    UserDomainObject getCurrentLoggedInUser() {
        return accessingUser;
    }

    void hasAtLeastDocumentReadPermission( Document document ) throws NoPermissionException {
        if (!docMapper.hasAtLeastDocumentReadPermission( accessingUser, document.getInternal() )) {
            throw new NoPermissionException("The logged in user does not have permission to access document "+document.getId()) ;
        }
    }

    void hasTemplateGroupPermission( TemplateGroup templateGroup ) {
        // todo
    }

    void hasSharePermission( Document document ) throws NoPermissionException {
        if (!docMapper.hasSharePermission(accessingUser, document.getId())) {
            throw new NoPermissionException("The logged in user does not have permission to share document "+document.getId()) ;
        }
    }

    void isSuperAdminOrSameUser(User user) throws NoPermissionException {
        if (!isSuperAdmin && !user.getInternalUser().equals( accessingUser )) {
            throw new NoPermissionException( "Must be the same user or " + SUPERADMIN_ROLE );
        }
    }

}
