package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentRoles;
import imcode.server.user.UserDomainObject;

public interface DocumentRolesService {

    DocumentRoles getDocumentRoles(int documentId, UserDomainObject user);

    DocumentRoles getDocumentRoles(int documentId);

}
