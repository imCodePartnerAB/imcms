package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.persistence.entity.MenuItem;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.user.UserDomainObject;

public interface DocumentMenuService {
    boolean hasUserAccessToDoc(int docId, UserDomainObject user);

    boolean isPublicMenuItem(int docId);

    Meta.DisabledLanguageShowMode getDisabledLanguageShowMode(int documentId);

    MenuItemDTO getMenuItemDTO(MenuItem menuItem);
}
