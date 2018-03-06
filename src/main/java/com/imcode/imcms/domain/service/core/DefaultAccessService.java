package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentRoles;
import com.imcode.imcms.domain.dto.RestrictedPermissionDTO;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.DocumentRolesService;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.RestrictedPermissionJPA;
import com.imcode.imcms.security.AccessType;
import imcode.server.Imcms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.02.18.
 */
@Service
@Transactional
public class DefaultAccessService implements AccessService {

    private final DocumentRolesService documentRolesService;

    DefaultAccessService(DocumentRolesService documentRolesService) {
        this.documentRolesService = documentRolesService;
    }

    @Override
    public boolean hasUserEditAccess(int userId, Integer documentId, AccessType accessType) {
        final DocumentRoles documentRoles = documentRolesService.getDocumentRoles(documentId, userId);

        if (documentRoles.hasNoRoles()) {
            return false;
        }

        final Permission mostPermission = documentRoles.getMostPermission();

        switch (mostPermission) {
            case EDIT:
                return true;
            case NONE:
            case VIEW:
                return false;
            case RESTRICTED_1:
            case RESTRICTED_2:
                return hasRestrictedEditAccess(accessType, documentRoles.getDocument(), mostPermission);
        }

        return true;
    }

    @Override
    public RestrictedPermission getEditPermission(int userId, int documentId) {
        final RestrictedPermissionDTO restrictedPermissionDTO = new RestrictedPermissionDTO();
        restrictedPermissionDTO.setPermission(Permission.VIEW); // by default

        if (Imcms.getUser().isSuperAdmin()) {
            setRestrictedPermissionDTO(restrictedPermissionDTO, Permission.EDIT,
                    true, true, true, true, true);
            return restrictedPermissionDTO;
        }

        final DocumentRoles documentRoles = documentRolesService.getDocumentRoles(documentId, userId);

        // if no common roles for document and user then return VIEW permission
        if (documentRoles.hasNoRoles()) {
            return restrictedPermissionDTO;
        }

        final Set<Permission> userPermissions = documentRoles.getPermissions();

        // if EDIT permission is present then return
        if (userPermissions.contains(Permission.EDIT)) {
            setRestrictedPermissionDTO(restrictedPermissionDTO, Permission.EDIT,
                    true, true, true, true, true);

            return restrictedPermissionDTO;
        }

        final Set<RestrictedPermissionJPA> documentRestrictedPermissions = documentRoles.getDocument()
                .getRestrictedPermissions();

        final Set<Permission> documentPermissions = documentRestrictedPermissions.stream()
                .map(RestrictedPermissionJPA::getPermission)
                .collect(Collectors.toSet());

        // intersection of permissions (RESTRICTED_1 AND RESTRICTED_2)
        documentPermissions.retainAll(userPermissions);

        if (documentPermissions.size() == 2) { // if both exist then return union of them

            final Iterator<RestrictedPermissionJPA> restrictedPermissionIterator =
                    documentRestrictedPermissions.iterator();

            final RestrictedPermissionJPA firstRestrictedPermission = restrictedPermissionIterator.next();
            final RestrictedPermissionJPA secondRestrictedPermission = restrictedPermissionIterator.next();

            setRestrictedPermissionDTO(restrictedPermissionDTO, Permission.RESTRICTED_1,
                    firstRestrictedPermission.isEditText() || secondRestrictedPermission.isEditText(),
                    firstRestrictedPermission.isEditMenu() || secondRestrictedPermission.isEditMenu(),
                    firstRestrictedPermission.isEditImage() || secondRestrictedPermission.isEditImage(),
                    firstRestrictedPermission.isEditLoop() || secondRestrictedPermission.isEditLoop(),
                    firstRestrictedPermission.isEditDocInfo() || secondRestrictedPermission.isEditDocInfo()
            );

        } else if (documentPermissions.size() == 1) { // if one of them then return existing

            final Permission restrictedPermission = documentPermissions.iterator().next();

            documentRestrictedPermissions.stream()
                    .filter(restrictedPermissionJPA -> restrictedPermissionJPA.getPermission()
                            .equals(restrictedPermission))
                    .findFirst()
                    .ifPresent(restrictedPermissionJPA -> setRestrictedPermissionDTO(
                            restrictedPermissionDTO,
                            restrictedPermissionJPA.getPermission(),
                            restrictedPermissionJPA.isEditText(),
                            restrictedPermissionJPA.isEditMenu(),
                            restrictedPermissionJPA.isEditImage(),
                            restrictedPermissionJPA.isEditLoop(),
                            restrictedPermissionJPA.isEditDocInfo())
                    );
        }

        return restrictedPermissionDTO;
    }

    private void setRestrictedPermissionDTO(RestrictedPermissionDTO restrictedPermission, Permission permission,
                                            boolean isEditText, boolean isEditMenu, boolean isEditImage,
                                            boolean isEditLoop, boolean isEditDocInfo) {

        restrictedPermission.setPermission(permission);
        restrictedPermission.setEditText(isEditText);
        restrictedPermission.setEditMenu(isEditMenu);
        restrictedPermission.setEditImage(isEditImage);
        restrictedPermission.setEditLoop(isEditLoop);
        restrictedPermission.setEditDocInfo(isEditDocInfo);
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
