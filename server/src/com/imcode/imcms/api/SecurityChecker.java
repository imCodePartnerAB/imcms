package com.imcode.imcms.api;

import imcode.server.document.DocumentMapper;
import imcode.server.user.RoleDomainObject;

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
        if (!getDocumentMapper().userHasAtLeastDocumentReadPermission( getCurrentUser().getInternal(), document.getInternal() )) {
            throw new NoPermissionException("The logged in user does not have permission to access document "+document.getId()) ;
        }
    }

    void userHasPermissionToAddDocumentToAnyMenu( Document document ) throws NoPermissionException {
        if (!getDocumentMapper().userHasPermissionToAddDocumentToAnyMenu(getCurrentUser().getInternal(), document.getInternal())) {
            throw new NoPermissionException("The logged in user does not have permission to add this document to any menu "+document.getId()) ;
        }
    }

    void isSuperAdminOrSameUser(User user) throws NoPermissionException {
        if (!getCurrentUser().isSuperAdmin() && !user.equals( getCurrentUser() )) {
            throw new NoPermissionException( "Must be the same user or " + RoleDomainObject.SUPERADMIN.getName() );
        }
    }

}
