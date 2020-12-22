package com.imcode.imcms.aspects;

import com.imcode.imcms.domain.dto.Documentable;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.security.CheckAccess;
import imcode.server.Imcms;
import imcode.server.document.NoPermissionToEditDocumentException;
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

    /**
     * Checks current user for access to specified ImCMS' content.
     */
    @Before("@annotation(access)")
    public void checkAccess(JoinPoint jp, CheckAccess access) {
        final UserDomainObject user = Imcms.getUser();

        if (user.isSuperAdmin()) {
            return; // super admin can do everything
        }

        final Object[] args = jp.getArgs();

        if (args.length != 0) {
            final AccessType accessType = access.value();
            boolean hasAccess = true;
            Integer docId = null;

            switch (accessType) {
                case ALL:
                    hasAccess = false; // only super admin allowed here, and checking was already done
                    break;

                case DOC_INFO:
                    if (args[0] instanceof Document) {
                        docId = ((Document) args[0]).getId();

                    } else if (args[0] instanceof Integer) {
                        docId = (Integer) args[0];
                    }
                    break;

                case IMAGE:
                case TEXT:
                case MENU:
                case LOOP:
                    if (args[0] instanceof Documentable) {
                        docId = ((Documentable) args[0]).getDocId();

                    } else if (args[0] instanceof Integer) {
                        docId = (Integer) args[0];
                    }
                    break;
                case DOCUMENT_EDITOR:
                    hasAccess = accessService.hasUserAccessToDocumentEditor(user);
                    break;
            }

            if (docId != null) {
                hasAccess = accessService.hasUserEditAccess(user, docId, accessType);
            }

            if (!hasAccess) {
                throw new NoPermissionToEditDocumentException("User do not have access to change document structure.");
            }
        }
    }

}
