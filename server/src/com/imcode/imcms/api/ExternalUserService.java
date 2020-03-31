package com.imcode.imcms.api;

import com.imcode.imcms.model.ExternalUser;

public interface ExternalUserService {

    ExternalUser saveExternalUser(ExternalUser user) throws SaveException;
}
