package com.imcode.imcms;

import imcode.server.IMCService;
import imcode.server.document.DocumentMapper;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;

public class ContentManagementSystem  {

    private UserService userMapper;
    private DocumentService docMapper;
    private User accessingUser;

    public ContentManagementSystem( IMCService service, imcode.server.user.UserDomainObject accessor ) {
        accessingUser = new User( accessor );

        ImcmsAuthenticatorAndUserMapper imcmsAAUM = new ImcmsAuthenticatorAndUserMapper( service );
        String[] roleNames = imcmsAAUM.getRoleNames( accessor );
        DocumentMapper documentMapper = new DocumentMapper( service, imcmsAAUM );
        SecurityChecker securityChecker = new SecurityChecker( documentMapper, accessor, roleNames );

        userMapper = new UserService( securityChecker, imcmsAAUM );
        docMapper = new DocumentService( securityChecker, documentMapper );
    }

    public UserService getUserMapperBean(){
        return userMapper;
    }

    public DocumentService getDocumentMapper(){
        return docMapper;
    }

    public User getAccessionUser() {
        return accessingUser;
    }
}
