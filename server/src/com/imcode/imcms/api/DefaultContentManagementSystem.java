package com.imcode.imcms.api;

import imcode.server.IMCService;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;

public class DefaultContentManagementSystem implements ContentManagementSystem {

    private UserService userService;
    private DocumentService documentService;
    private TemplateService templateService;
    private DatabaseService databaseService;
    private User currentUser;

    public DefaultContentManagementSystem( IMCService service, UserDomainObject accessor ) {
        currentUser = new User( accessor );
        DocumentPermissionSetMapper documentPermissionSetMapper = new DocumentPermissionSetMapper( service );

        ImcmsAuthenticatorAndUserMapper imcmsAAUM = new ImcmsAuthenticatorAndUserMapper( service );
        String[] roleNames = imcmsAAUM.getRoleNames( accessor );

        DocumentMapper documentMapper = new DocumentMapper( service, imcmsAAUM );

        SecurityChecker securityChecker = new SecurityChecker( documentMapper, accessor, roleNames );

        userService = new UserService( securityChecker, imcmsAAUM );
        documentService = new DocumentService( securityChecker, documentMapper, documentPermissionSetMapper, imcmsAAUM );
        templateService = new TemplateService( service, securityChecker );
        databaseService = new DatabaseService( service ) ;
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

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public TemplateService getTemplateService() {
        return templateService;
    }

}
