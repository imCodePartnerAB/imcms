package com.imcode.imcms.api;

import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;

import java.util.Arrays;
import java.util.HashSet;

import com.imcode.imcms.api.NoPermissionException;

class SecurityChecker {

    private final static String SUPERADMIN_ROLE = "Superadmin";
    private final static String USER_ADMIN = "Useradmin";

    private DocumentMapper docMapper;
    private imcode.server.user.UserDomainObject accessingUser;
    private HashSet accessorRoles;

    private boolean isSuperAdmin;
    private boolean isUserAdmin;

    SecurityChecker( DocumentMapper docMapper, UserDomainObject accessor, String[] accessorRoles ) {
        this.docMapper = docMapper;
        this.accessingUser = accessor;
        this.accessorRoles = new HashSet( Arrays.asList( accessorRoles ) );

        isSuperAdmin = this.accessorRoles.contains( SUPERADMIN_ROLE );
        isUserAdmin = this.accessorRoles.contains( USER_ADMIN );
    }

    void loggedIn() throws NoPermissionException {
        if( null == accessingUser ) {
            throw new NoPermissionException( "User not logged in" );
        }
    }

    void isSuperAdmin() throws NoPermissionException {
        if( !isSuperAdmin ) {
            throw new NoPermissionException( "User is not " + SUPERADMIN_ROLE );
        }
    }

    void isSuperAdminOrIsUserAdminOrIsSameUser( User userBean ) throws NoPermissionException {
        boolean isSameUser = userBean.getLoginName().equalsIgnoreCase( accessingUser.getLoginName() );
        if( !isSuperAdmin && !isUserAdmin && !isSameUser ) {
            throw new NoPermissionException( "User is not superadmin, useradmin nor the same user." );
        }
    }

    public void hasEditPermission( int documentId ) throws NoPermissionException {
        if( !docMapper.hasEditPermission( docMapper.getDocument( documentId ), accessingUser ) ) {
            throw new NoPermissionException("The logged in user does not have permission to edit document " + documentId );
        };
    }

    void hasEditPermission( Document document ) throws NoPermissionException  {
        hasEditPermission(document.internalDocument.getMetaId());
    }

    UserDomainObject getCurrentLoggedInUser() {
        return accessingUser;
    }

    void hasAtLeastDocumentReadPermission( Document document ) throws NoPermissionException {
        if (!docMapper.hasAtLeastDocumentReadPermission( accessingUser, document.getInternal() )) {
            throw new NoPermissionException("The logged in user does not have permission to access document "+document.getId()) ;
        }
    }

    public void hasTemplateGroupPermission( TemplateGroup templateGroup ) {
        // todo
    }

    public void hasSharePermission( Document document ) throws NoPermissionException {
        if (!docMapper.hasSharePermission(accessingUser, document.getId())) {
            throw new NoPermissionException("The logged in user does not have permission to share document "+document.getId()) ;
        }
    }

}
