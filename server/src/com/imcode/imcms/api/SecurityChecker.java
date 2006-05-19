package com.imcode.imcms.api;

class SecurityChecker {

    private ContentManagementSystem contentManagementSystem;

    SecurityChecker( ContentManagementSystem contentManagementSystem ) {
        this.contentManagementSystem = contentManagementSystem;
    }

    private User getCurrentUser() {
        return contentManagementSystem.getCurrentUser();
    }

    void isSuperAdmin() throws NoPermissionException {
        if( !getCurrentUser().isSuperAdmin() ) {
            throw new NoPermissionException( "User is not superadmin." );
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

    void canEditUser(User user) throws NoPermissionException {
        if (!getCurrentUser().isUserAdmin() && !getCurrentUser().isSuperAdmin() && !user.equals( getCurrentUser() )) {
            throw new NoPermissionException( "Must be the same user or superadmin." );
        }
    }

    void isNotDefaultUser() throws NoPermissionException {
        if (getCurrentUser().getInternal().isDefaultUser()) {
            throw new NoPermissionException( "Can't be the default user." );
        }
    }
}
