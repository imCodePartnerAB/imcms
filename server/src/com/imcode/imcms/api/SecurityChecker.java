package com.imcode.imcms.api;

import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;

class SecurityChecker {

    private final static String SUPERADMIN_ROLE = "Superadmin";

    private DocumentMapper docMapper;
    private UserDomainObject accessingUser;

    SecurityChecker( DocumentMapper docMapper, UserDomainObject accessor ) {
        this.docMapper = docMapper;
        this.accessingUser = accessor;
    }

    void isSuperAdmin() throws NoPermissionException {
        if( !accessingUser.isSuperAdmin() ) {
            throw new NoPermissionException( "User is not " + SUPERADMIN_ROLE );
        }
    }

    void hasEditPermission( int documentId ) throws NoPermissionException {
        if( !docMapper.userHasMoreThanReadPermissionOnDocument( accessingUser, docMapper.getDocument( documentId ) ) ) {
            throw new NoPermissionException("The logged in user does not have permission to edit document " + documentId );
        };
    }

    void hasEditPermission( Document document ) throws NoPermissionException  {
        hasEditPermission(document.getInternal().getId());
    }

    UserDomainObject getCurrentLoggedInUser() {
        return accessingUser;
    }

    void hasAtLeastDocumentReadPermission( Document document ) throws NoPermissionException {
        if (!docMapper.userHasAtLeastDocumentReadPermission( accessingUser, document.getInternal() )) {
            throw new NoPermissionException("The logged in user does not have permission to access document "+document.getId()) ;
        }
    }

    void userHasPermissionToAddDocumentToAnyMenu( Document document ) throws NoPermissionException {
        if (!docMapper.userHasPermissionToAddDocumentToAnyMenu(accessingUser, document.getInternal())) {
            throw new NoPermissionException("The logged in user does not have permission to add this document to any menu "+document.getId()) ;
        }
    }

    void isSuperAdminOrSameUser(User user) throws NoPermissionException {
        if (!accessingUser.isSuperAdmin() && !user.getInternalUser().equals( accessingUser )) {
            throw new NoPermissionException( "Must be the same user or " + SUPERADMIN_ROLE );
        }
    }

}
