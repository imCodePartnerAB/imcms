package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentRoles;
import com.imcode.imcms.domain.dto.RestrictedPermissionDTO;
import com.imcode.imcms.domain.dto.RolePermissionsDTO;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.model.RolePermissions;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.RestrictedPermissionJPA;
import com.imcode.imcms.security.AccessContentType;
import com.imcode.imcms.util.Value;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final DocumentService documentService;

    private final List<Integer> adminFilesAllowedUsers;

    DefaultAccessService(DocumentRolesService documentRolesService, RoleService roleService,
                         @Qualifier("defaultDocumentService") DocumentService documentService,
                         @org.springframework.beans.factory.annotation.Value("#{'${admin.files.allowed-users}'.split(',')}") List<Integer> adminFilesAllowedUsers) {
        this.documentRolesService = documentRolesService;
        this.roleService = roleService;
        this.documentService = documentService;
        this.adminFilesAllowedUsers = adminFilesAllowedUsers;
    }

    @Override
    public boolean hasUserViewAccess(UserDomainObject user, Integer documentId) {
        return documentService.get(documentId).isVisible() ||
                documentRolesService.getDocumentRoles(documentId, user).getMostPermission().isAtLeastAsPrivilegedAs(Permission.VIEW);
    }

    @Override
    public boolean hasUserEditAccess(UserDomainObject user, Integer documentId, AccessContentType accessContentType) {
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
                return hasRestrictedEditAccess(accessContentType, documentRoles.getDocument(), mostPermission);
        }

        return true;
    }

    @Override
    public boolean hasUserPublishAccess(UserDomainObject user, int docId) {
        if(user.isSuperAdmin()) return true;

        final RolePermissions rolePermissions = getTotalRolePermissionsByUser(user);
        return (rolePermissions.isPublishOwnDocuments() && user.getLogin().equals(documentService.get(docId).getCreated().getBy())) ||
                (rolePermissions.isPublishAllDocuments() && getPermission(user, docId).getPermission() == Permission.EDIT);
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
    public RolePermissions getTotalRolePermissionsByUser(UserDomainObject user) {
        final RolePermissions totalPermissions = new RolePermissionsDTO();

        user.getRoleIds().stream()
                .map(roleService::getById)
                .filter(Objects::nonNull)
                .forEach(role -> {
                    RolePermissions permissions = role.getPermissions();
                    if(permissions.isGetPasswordByEmail()) totalPermissions.setGetPasswordByEmail(true);
                    if(permissions.isAccessToAdminPages()) totalPermissions.setAccessToAdminPages(true);
                    if(permissions.isAccessToDocumentEditor()) totalPermissions.setAccessToDocumentEditor(true);
                    if(permissions.isPublishOwnDocuments()) totalPermissions.setPublishOwnDocuments(true);
                    if(permissions.isPublishAllDocuments()) totalPermissions.setPublishAllDocuments(true);
                });

        return totalPermissions;
    }

	@Override
	public boolean hasUserFileAdminAccess(int userId) {
		return adminFilesAllowedUsers.contains(userId);
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

    private boolean hasRestrictedEditAccess(AccessContentType accessContentType, Meta meta, Permission permission) {
        return meta.getRestrictedPermissions()
                .stream()
                .filter(restrictedPermission -> restrictedPermission.getPermission().equals(permission))
                .findFirst() // should be one or zero!
                .filter(restrictedPermissionJPA -> hasRestrictedEditAccess(restrictedPermissionJPA, accessContentType))
                .isPresent();
    }

    private boolean hasRestrictedEditAccess(RestrictedPermission restrictedPermission, AccessContentType accessContentType) {
        switch (accessContentType) {
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
