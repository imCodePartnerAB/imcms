package com.imcode.imcms.api;

import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;

class SecurityChecker {

    private ContentManagementSystem contentManagementSystem;

    SecurityChecker( ContentManagementSystem contentManagementSystem ) {
        this.contentManagementSystem = contentManagementSystem;
    }

    private User getCurrentUser() {
        return contentManagementSystem.getCurrentUser();
    }

    private DocumentMapper getDocumentMapper() {
        return contentManagementSystem.getInternal().getDocumentMapper();
    }

    void isSuperAdmin() throws NoPermissionException {
        if( !getCurrentUser().isSuperAdmin() ) {
            throw new NoPermissionException( "User is not " + RoleDomainObject.SUPERADMIN.getName() );
        }
    }

    void hasEditPermission( Document document ) throws NoPermissionException  {
        if( !getCurrentUser().canEdit( document ) ) {
            throw new NoPermissionException("The logged in user does not have permission to edit document " + document.getId() );
        }
    }

    void hasAtLeastDocumentReadPermission( Document document ) throws NoPermissionException {
        if (!getCurrentUser().getInternal().canAccess( document.getInternal() )) {
            throw new NoPermissionException("The logged in user does not have permission to access document "+document.getId()) ;
        }
    }

    void userHasPermissionToAddDocumentToAnyMenu( Document document ) throws NoPermissionException {
        if (!getCurrentUser().getInternal().canAddDocumentToAnyMenu( document.getInternal() )) {
            throw new NoPermissionException("The logged in user does not have permission to add this document to any menu "+document.getId()) ;
        }
    }

    void isSuperAdminOrSameUser(User user) throws NoPermissionException {
        if (!getCurrentUser().isSuperAdmin() && !user.equals( getCurrentUser() )) {
            throw new NoPermissionException( "Must be the same user or " + RoleDomainObject.SUPERADMIN.getName() );
        }
    }

    void isNotDefaultUser() throws NoPermissionException {
        if (getCurrentUser().getInternal().isDefaultUser()) {
            throw new NoPermissionException( "Can't be the default user." );
        }
    }
}
