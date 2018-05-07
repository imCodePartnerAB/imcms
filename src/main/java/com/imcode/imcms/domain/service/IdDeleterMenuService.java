package com.imcode.imcms.domain.service;

import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;

public interface IdDeleterMenuService extends MenuService {

    Menu removeId(Menu dto, Version version);
}
