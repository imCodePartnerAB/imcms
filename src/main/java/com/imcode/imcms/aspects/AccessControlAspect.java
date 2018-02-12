package com.imcode.imcms.aspects;

import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
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

    /**
     * Checks current user for access to specified ImCMS' content.
     */
    @Before("@annotation(access)")
    public void checkAccess(CheckAccess access) {

        final AccessType accessType = access.value();
        // todo: change to check specified access like images, menu, etc.

        if (!Imcms.getUser().isSuperAdmin()) {
            throw new NoPermissionToEditDocumentException("User do not have access to change image structure.");
        }
    }

}
