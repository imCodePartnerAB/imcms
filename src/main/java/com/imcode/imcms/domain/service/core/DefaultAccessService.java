package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.DocumentRoles;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.repository.DocumentRolesRepository;
import com.imcode.imcms.security.AccessType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.02.18.
 */
@Service
@Transactional
public class DefaultAccessService implements AccessService {

    private final UserService userService;
    private final DocumentRolesRepository documentRolesRepository;

    DefaultAccessService(UserService userService, DocumentRolesRepository documentRolesRepository) {
        this.userService = userService;
        this.documentRolesRepository = documentRolesRepository;
    }

    @Override
    public boolean hasUserEditAccess(int userId, Integer documentId, AccessType accessType) {
        try {
            userService.getUser(userId);
        } catch (UserNotExistsException e) {
            return false;
        }

        final List<DocumentRoles> documentRolesList = documentRolesRepository.getDocumentRolesByDocIdAndUserId(
                userId, documentId
        );

        if (documentRolesList.isEmpty()) {
            return false;
        }

        final Permission mostPermission = documentRolesList.stream()
                .map(DocumentRoles::getPermission)
                .min(Comparator.naturalOrder())
                .get();

        switch (mostPermission) {
            case EDIT:
                return true;
            case NONE:
            case VIEW:
                return false;
            case RESTRICTED_1:
            case RESTRICTED_2:
                return hasRestrictedEditAccess(accessType, documentRolesList.get(0).getDocument(), mostPermission);
        }

        return true;
    }

    @Override
    public RestrictedPermission getEditPermission(int userId, int documentId) {
        return null;
    }

    private boolean hasRestrictedEditAccess(AccessType accessType, Meta meta, Permission permission) {
        return meta.getRestrictedPermissions()
                .stream()
                .filter(restrictedPermission -> restrictedPermission.getPermission().equals(permission))
                .findFirst() // should be one or zero!
                .filter(restrictedPermissionJPA -> hasRestrictedEditAccess(restrictedPermissionJPA, accessType))
                .isPresent();
    }

    private boolean hasRestrictedEditAccess(RestrictedPermission restrictedPermission, AccessType accessType) {
        switch (accessType) {
            case ALL:
                return true;
            case DOC_INFO:
                return restrictedPermission.isEditDocInfo();
            case LOOP:
                return restrictedPermission.isEditLoop();
            case MENU:
                return restrictedPermission.isEditMenu();
            case TEXT:
                return restrictedPermission.isEditText();
            case IMAGE:
                return restrictedPermission.isEditImage();
            default:
                return false;
        }
    }
}
