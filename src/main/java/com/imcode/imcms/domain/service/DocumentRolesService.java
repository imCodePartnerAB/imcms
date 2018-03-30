package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentRoles;

public interface DocumentRolesService {

    DocumentRoles getDocumentRoles(int documentId, int userId);

}
