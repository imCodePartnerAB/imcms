package com.imcode.imcms;

import imcode.server.IMCService;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentPermissionSetMapper;
import imcode.server.document.TemplateMapper;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;

public class ContentManagementSystem  {

    private UserService userService;
    private DocumentService documentService;
    private User currentUser;
    private TemplateService templateService;

    public TemplateService getTemplateService() {
        return templateService;
    }

    public void setTemplateService( TemplateService templateService ) {
        this.templateService = templateService;
    }

    public ContentManagementSystem( IMCService service, UserDomainObject accessor ) {
        currentUser = new User( accessor );
        DocumentPermissionSetMapper documentPermissionSetMapper = new DocumentPermissionSetMapper( service );

        ImcmsAuthenticatorAndUserMapper imcmsAAUM = new ImcmsAuthenticatorAndUserMapper( service );
        String[] roleNames = imcmsAAUM.getRoleNames( accessor );

        DocumentMapper documentMapper = new DocumentMapper( service, imcmsAAUM );

        SecurityChecker securityChecker = new SecurityChecker( documentMapper, accessor, roleNames );

        userService = new UserService( securityChecker, imcmsAAUM );
        documentService = new DocumentService( securityChecker, documentMapper, documentPermissionSetMapper );
        templateService = new TemplateService( service, securityChecker );
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
