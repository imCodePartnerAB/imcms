package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentRoles;
import com.imcode.imcms.domain.dto.RestrictedPermissionDTO;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.DocumentRolesService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.RolePermissions;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.RestrictedPermissionJPA;
import com.imcode.imcms.security.AccessType;
import com.imcode.imcms.util.Value;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.02.18.
 */
@Service
@Transactional
public class DefaultAccessService implements AccessService {

    private final RestrictedPermissionDTO fullEditPermission = Value.with(new RestrictedPermissionDTO(), permission -> {
        permission.setPermission(Permission.EDIT);
        permission.setEditDocInfo(true);
        permission.setEditImage(true);
        permission.setEditLoop(true);
        permission.setEditText(true);
        permission.setEditMenu(true);
    });

    private final RestrictedPermissionDTO viewPermission = Value.with(
            new RestrictedPermissionDTO(),
            permission -> permission.setPermission(Permission.VIEW)
    );

    private final RestrictedPermissionDTO nonePermission = Value.with(
            new RestrictedPermissionDTO(),
            permission -> permission.setPermission(Permission.NONE)
    );

    private final DocumentRolesService documentRolesService;
    private final RoleService roleService;

    DefaultAccessService(DocumentRolesService documentRolesService, RoleService roleService) {
        this.documentRolesService = documentRolesService;
        this.roleService = roleService;
    }

    @Override
    public boolean hasUserEditAccess(UserDomainObject user, Integer documentId, AccessType accessType) {
        final DocumentRoles documentRoles = documentRolesService.getDocumentRoles(documentId, user);

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
    public RestrictedPermission getPermission(UserDomainObject user, int documentId) {
        if (user.isSuperAdmin()) {
            return fullEditPermission;
        }

        final DocumentRoles documentRoles = documentRolesService.getDocumentRoles(documentId, user);

        // if no common roles for document and user then return VIEW permission
        if (documentRoles.hasNoRoles()) {
            return nonePermission;
        }

        final Set<Permission> userPermissions = documentRoles.getPermissions();

        // if EDIT permission is present then return
        if (userPermissions.contains(Permission.EDIT)) {
            return fullEditPermission;
        }

        return getRestrictedPermissionForUser(userPermissions, documentRoles.getDocument().getRestrictedPermissions());
    }

    @Override
    public boolean hasUserAccessToDocumentEditor(UserDomainObject user) {
        if (user.isDefaultUser()) return false;

        return getRolePermissionsByUser(user).stream()
                .anyMatch(RolePermissions::isAccessToDocumentEditor);
    }

    private List<RolePermissions> getRolePermissionsByUser(UserDomainObject user) {
        return user.getRoleIds().stream()
                .map(roleService::getById)
                .filter(Objects::nonNull)
                .map(Role::getPermissions)
                .collect(Collectors.toList());
    }

    private RestrictedPermission getRestrictedPermissionForUser(Set<Permission> userPermissions,
                                                                Set<RestrictedPermissionJPA> restrictedPermissions) {

        final Set<RestrictedPermission> documentRestrictedPermissions = restrictedPermissions.stream()
                .map(RestrictedPermissionDTO::new)
                .collect(Collectors.toSet());

        final Set<Permission> documentPermissions = documentRestrictedPermissions.stream()
                .map(RestrictedPermission::getPermission)
                .collect(Collectors.toSet());

        // intersection of restricted and user permissions
        documentPermissions.retainAll(userPermissions);

        if (documentPermissions.size() == 2) { // if both exist then return union of them
            return mergePermissions(Permission.RESTRICTED_1, documentRestrictedPermissions);
        }

        if (documentPermissions.size() == 1) { // if one of them then return existing
            final Permission restrictedPermission = documentPermissions.iterator().next();

            return documentRestrictedPermissions.stream()
                    .filter(permission -> permission.getPermission().equals(restrictedPermission))
                    .findFirst()
                    .map(RestrictedPermissionDTO::new)
                    .orElse(userPermissions.contains(Permission.VIEW) ? viewPermission : nonePermission);
        }

        if (userPermissions.contains(Permission.VIEW)) {
            return viewPermission;
        }

        return nonePermission;
    }

    private RestrictedPermission mergePermissions(Permission resultPermission,
                                                  Collection<RestrictedPermission> permissions) {

        final RestrictedPermissionDTO restrictedPermission = new RestrictedPermissionDTO();
        restrictedPermission.setPermission(resultPermission);

        for (RestrictedPermission permission : permissions) {
            restrictedPermission.setEditText(restrictedPermission.isEditText() || permission.isEditText());
            restrictedPermission.setEditMenu(restrictedPermission.isEditMenu() || permission.isEditMenu());
            restrictedPermission.setEditImage(restrictedPermission.isEditImage() || permission.isEditImage());
            restrictedPermission.setEditLoop(restrictedPermission.isEditLoop() || permission.isEditLoop());
            restrictedPermission.setEditDocInfo(restrictedPermission.isEditDocInfo() || permission.isEditDocInfo());
        }

        return restrictedPermission;
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
