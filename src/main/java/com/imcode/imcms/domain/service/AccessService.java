package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.security.AccessType;
import imcode.server.user.UserDomainObject;

/**
 * To know do the user have access to do something with some document or not.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.02.18.
 */
public interface AccessService {
    boolean hasUserEditAccess(UserDomainObject user, Integer documentId, AccessType accessType);

    RestrictedPermission getPermission(UserDomainObject user, int documentId);
}
