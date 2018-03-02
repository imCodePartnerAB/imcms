package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.AccessService;
import com.imcode.imcms.domain.service.UserRolesService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.security.AccessType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.02.18.
 */
@Service
@Transactional
public class DefaultAccessService implements AccessService {

    private final MetaRepository metaRepository;
    private final UserRolesService userRolesService;
    private final UserService userService;

    DefaultAccessService(MetaRepository metaRepository,
                         UserRolesService userRolesService,
                         UserService userService) {

        this.metaRepository = metaRepository;
        this.userRolesService = userRolesService;
        this.userService = userService;
    }

    @Override
    public boolean hasUserEditAccess(int userId, Integer documentId, AccessType accessType) {
        final Meta meta = metaRepository.findOne(documentId);

        if (meta == null) {
            return false;
        }

        final User user;
        try {
            user = userService.getUser(userId);
        } catch (UserNotExistsException e) {
            return false;
        }

        final Map<Integer, Permission> roleIdToPermission = meta.getRoleIdToPermission();

        final Optional<Permission> oPermission = userRolesService.getRolesByUser(user)
                .stream()
                .map(Role::getId)
                .map(roleIdToPermission::get)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder());

        if (!oPermission.isPresent()) {
            return false;
        }

        final Permission mostPermission = oPermission.get();

        switch (mostPermission) {
            case EDIT:
                return true;
            case NONE:
            case VIEW:
                return false;
            case RESTRICTED_1:
            case RESTRICTED_2:
                return hasRestrictedEditAccess(accessType, meta, mostPermission);
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
