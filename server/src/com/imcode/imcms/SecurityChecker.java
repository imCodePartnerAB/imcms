package com.imcode.imcms;

import imcode.server.user.User;
import imcode.server.document.Document;
import imcode.server.document.DocumentMapper;

import java.util.HashSet;
import java.util.Arrays;

public class SecurityChecker {

    private final static String SUPERADMIN_ROLE = "Superadmin";
    private final static String USER_ADMIN = "Useradmin";

    private DocumentMapper docMapper;
    private User accessor;
    private HashSet accessorRoles;

    private boolean isSuperAdmin;
    private boolean isUserAdmin;

    public SecurityChecker( DocumentMapper docMapper, User accessor, String[] accessorRoles ) {
        this.docMapper = docMapper;
        this.accessor = accessor;
        this.accessorRoles = new HashSet( Arrays.asList( accessorRoles ) );

        isSuperAdmin = this.accessorRoles.contains( SUPERADMIN_ROLE );
        isUserAdmin = this.accessorRoles.contains( USER_ADMIN );
    }

    public void loggedIn() throws NoPermissionException {
        if( null == accessor ) {
            throw new NoPermissionException( "User not logged in" );
        }
    }

    public void isSuperAdmin() throws NoPermissionException {
        if( !isSuperAdmin ) {
            throw new NoPermissionException( "User is not " + SUPERADMIN_ROLE );
        }
    }

    public void isUserAdmin() throws NoPermissionException {
        if( !isUserAdmin ) {
            throw new NoPermissionException( "User is not " + USER_ADMIN );
        }
    }

    void isSuperAdminOrIsUserAdminOrIsSameUser( UserBean userBean ) throws NoPermissionException {
        boolean isSameUser = userBean.getLoginName().equalsIgnoreCase( accessor.getLoginName() );
        if( !isSuperAdmin && !isUserAdmin && !isSameUser ) {
            throw new NoPermissionException( "User is not superadmin, useradmin nor the same user." );
        }
    }

    void hasEditPermission( UserBean user ) throws NoPermissionException {
        throw new NoPermissionException( "Not implemented yet. Didn't feel like it." );
    }

    public void hasEditPermission( Document document ) throws NoPermissionException  {
        if( !docMapper.hasAdminPermissions( document, accessor ) ) {
            throw new NoPermissionException("The logged in user does not have permission to edit document: " + document.getMetaId() );
        };
    }

}
