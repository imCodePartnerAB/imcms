package com.imcode.imcms;

import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.User;
import imcode.server.document.DocumentMapper;
import imcode.server.IMCService;

public class ImcmsSystem  {

    UserMapperBean userMapper;
    DocumentMapperBean docMapper;
    UserBean accessingUser;

    public ImcmsSystem( IMCService service, User accessor ) {
        accessingUser = new UserBean( accessor );

        ImcmsAuthenticatorAndUserMapper imcmsAAUM = new ImcmsAuthenticatorAndUserMapper( service );
        String[] roleNames = imcmsAAUM.getRoleNames( accessor );
        DocumentMapper documentMapper = new DocumentMapper( service, imcmsAAUM );
        SecurityChecker securityChecker = new SecurityChecker( documentMapper, accessor, roleNames );

        userMapper = new UserMapperBean( securityChecker, imcmsAAUM );
        docMapper = new DocumentMapperBean( securityChecker, documentMapper );
    }

    public UserMapperBean getUserMapperBean(){
        return userMapper;
    }

    public DocumentMapperBean getDocumentMapper(){
        return docMapper;
    }

    public UserBean getAccessionUser() {
        return accessingUser;
    }
}
