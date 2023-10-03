package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.model.RolePermissions;
import com.imcode.imcms.security.AccessContentType;
import imcode.server.user.UserDomainObject;

/**
 * To know do the user have access to do something with some document or not.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.02.18.
 */
public interface AccessService {

    boolean hasUserViewAccess(UserDomainObject user, Integer documentId);

    boolean hasUserEditAccess(UserDomainObject user, Integer documentId, AccessContentType accessContentType);

    boolean hasUserPublishAccess(UserDomainObject user, int docId);

    RestrictedPermission getPermission(UserDomainObject user, int documentId);

    RolePermissions getTotalRolePermissionsByUser(UserDomainObject user);

	boolean hasUserFileAdminAccess(int userId);
}
