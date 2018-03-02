package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.security.AccessType;

/**
 * To know do the user have access to do something with some document or not.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.02.18.
 */
public interface AccessService {
    boolean hasUserEditAccess(int userId, Integer documentId, AccessType accessType);

    RestrictedPermission getEditPermission(int userId, int documentId);
}
