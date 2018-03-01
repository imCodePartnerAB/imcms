package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.user.UserDomainObject;

public interface DocumentMenuService {
    boolean hasUserAccessToDoc(int docId, UserDomainObject user);

    Meta.DisabledLanguageShowMode getDisabledLanguageShowMode(int documentId);

    MenuItemDTO getMenuItemDTO(int docId, Language language);
}
