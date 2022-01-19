package com.imcode.imcms.aspects;

import com.imcode.imcms.api.exception.NoPermissionException;
import com.imcode.imcms.domain.dto.Documentable;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.RolePermissions;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.security.AccessRoleType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Aspect that controls access in ImCMS.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 12.02.18.
 */
@Aspect
@Component
public class AccessControlAspect {

    private final AccessService accessService;

    public AccessControlAspect(AccessService accessService) {
        this.accessService = accessService;
    }

    @Before("@annotation(access)")
    public void checkAccess(JoinPoint jp, CheckAccess access) {
        final UserDomainObject user = Imcms.getUser();

        if (user.isSuperAdmin()) return; // super admin can do everything

        boolean hasAccess = false;

        final AccessRoleType[] roles = access.role();
        if(roles.length > 0) hasAccess = checkRole(roles, user);

        final AccessContentType[] permissions = access.docPermission();
        if(!hasAccess && permissions.length > 0) hasAccess = checkPermission(jp.getArgs(), permissions, user);

        if (!hasAccess) {
            throw new NoPermissionException("User do not has the necessary permission");
        }
    }

    /**
     * Checks current user for a specific role permission.
     */
    private boolean checkRole(AccessRoleType[] accessRoleTypes, UserDomainObject user){
        boolean hasAccess = false;

        final RolePermissions rolePermissionsByUser = accessService.getTotalRolePermissionsByUser(user);
        for (AccessRoleType accessRoleType : accessRoleTypes) {
            switch (accessRoleType) {
                case DOCUMENT_EDITOR:
                    hasAccess = rolePermissionsByUser.isAccessToDocumentEditor();
                    break;
                case ADMIN_PAGES:
                    hasAccess = rolePermissionsByUser.isAccessToAdminPages();
                    break;
            }

            if (hasAccess) break;
        }

        return hasAccess;
    }

    /**
     * Checks current user for a permission of a specific document.
     */
    private boolean checkPermission(Object[] args, AccessContentType[] accessContentTypes, UserDomainObject user){
        boolean hasAccess = false;

        if(args.length > 0){
            Integer docId = null;

            Object value = args[0];
            if (value instanceof Document) {
                docId = ((Document) value).getId();
            } else if (value instanceof Documentable) {
                docId = ((Documentable) value).getDocId();
            } else if (value instanceof Integer) {
                docId = (Integer) value;
            }

            if (docId != null){
                for(AccessContentType accessContentType : accessContentTypes){
                    hasAccess = accessService.hasUserEditAccess(user, docId, accessContentType);
                    if (hasAccess) break;
                }
            }
        }

        return hasAccess;
    }

}
