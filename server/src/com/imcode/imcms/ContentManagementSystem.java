package com.imcode.imcms;

import imcode.server.IMCService;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;

public class ContentManagementSystem  {

    private UserService userService;
    private DocumentService documentService;
    private User currentUser;

    public ContentManagementSystem( IMCService service, imcode.server.user.UserDomainObject accessor ) {
        currentUser = new User( accessor );

        DocumentPermissionSetMapper documentPermissionSetMapper = new DocumentPermissionSetMapper( service );

        ImcmsAuthenticatorAndUserMapper imcmsAAUM = new ImcmsAuthenticatorAndUserMapper( service );
        String[] roleNames = imcmsAAUM.getRoleNames( accessor );

        DocumentMapper documentMapper = new DocumentMapper( service, imcmsAAUM );

        SecurityChecker securityChecker = new SecurityChecker( documentMapper, accessor, roleNames );

        userService = new UserService( securityChecker, imcmsAAUM );
        documentService = new DocumentService( securityChecker, documentMapper, documentPermissionSetMapper );
    }

    public UserService getUserService(){
        return userService;
    }

    public DocumentService getDocumentService(){
        return documentService;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
