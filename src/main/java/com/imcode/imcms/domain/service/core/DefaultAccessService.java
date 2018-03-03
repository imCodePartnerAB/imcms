package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.RestrictedPermissionDTO;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.DocumentRoles;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.RestrictedPermissionJPA;
import com.imcode.imcms.persistence.repository.DocumentRolesRepository;
import com.imcode.imcms.security.AccessType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // TODO: need for refactoring
    @Override
    public RestrictedPermission getEditPermission(int userId, int documentId) {
        final RestrictedPermissionDTO restrictedPermissionDTO = new RestrictedPermissionDTO();

        final List<DocumentRoles> documentRolesList = documentRolesRepository.getDocumentRolesByDocIdAndUserId(
                userId, documentId
        );

        if (documentRolesList.isEmpty()) {
            restrictedPermissionDTO.setPermission(Permission.VIEW);

            return restrictedPermissionDTO;
        }

        final Set<Permission> permissions = documentRolesList.stream()
                .map(DocumentRoles::getPermission)
                .collect(Collectors.toSet());

        if (permissions.contains(Permission.EDIT)) {
            restrictedPermissionDTO.setPermission(Permission.EDIT);
            restrictedPermissionDTO.setEditText(true);
            restrictedPermissionDTO.setEditMenu(true);
            restrictedPermissionDTO.setEditImage(true);
            restrictedPermissionDTO.setEditLoop(true);
            restrictedPermissionDTO.setEditDocInfo(true);

            return restrictedPermissionDTO;
        }

        final boolean isExistRestricted1 = permissions.contains(Permission.RESTRICTED_1);
        final boolean isExistRestricted2 = permissions.contains(Permission.RESTRICTED_2);

        if (!isExistRestricted1 && !isExistRestricted2) {
            restrictedPermissionDTO.setPermission(Permission.VIEW);

            return restrictedPermissionDTO;
        }

        // know that restricted1 or/and restricted 2 exist

        final Set<RestrictedPermissionJPA> restrictedPermissions = documentRolesList.get(0)
                .getDocument()
                .getRestrictedPermissions();

        if (restrictedPermissions.isEmpty()) {
            restrictedPermissionDTO.setPermission(Permission.VIEW);

            return restrictedPermissionDTO;
        }

        final Set<Permission> documentPermissions = restrictedPermissions.stream()
                .map(RestrictedPermissionJPA::getPermission)
                .collect(Collectors.toSet());

        if (restrictedPermissions.size() == 2 && isExistRestricted1 && isExistRestricted2) {
            restrictedPermissionDTO.setPermission(Permission.RESTRICTED_1);

            restrictedPermissions.forEach(permission -> {
                restrictedPermissionDTO.setEditText(restrictedPermissionDTO.isEditText() || permission.isEditText());
                restrictedPermissionDTO.setEditMenu(restrictedPermissionDTO.isEditMenu() || permission.isEditMenu());
                restrictedPermissionDTO.setEditImage(restrictedPermissionDTO.isEditImage() || permission.isEditImage());
                restrictedPermissionDTO.setEditLoop(restrictedPermissionDTO.isEditLoop() || permission.isEditLoop());
                restrictedPermissionDTO.setEditDocInfo(
                        restrictedPermissionDTO.isEditDocInfo() || permission.isEditDocInfo()
                );
            });

            return restrictedPermissionDTO;
        }

        documentPermissions.retainAll(permissions);

        final Permission restrictedPermission = documentPermissions.iterator().next();
        final RestrictedPermissionJPA restrictedPermissionJPA1 = restrictedPermissions.stream()
                .filter(restrictedPermissionJPA -> restrictedPermissionJPA.getPermission().equals(restrictedPermission))
                .findFirst()
                .get();

        return new RestrictedPermissionDTO(restrictedPermissionJPA1);
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
