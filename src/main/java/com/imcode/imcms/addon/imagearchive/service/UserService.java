package com.imcode.imcms.addon.imagearchive.service;

import imcode.server.user.RoleDomainObject;
import imcode.server.user.RolePermissionDomainObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.Role;
import com.imcode.imcms.api.User;

@Transactional
public class UserService {
    public static List<Integer> getRoleIdsWithPermission(User user, RolePermissionDomainObject... permissions) {
        List<Integer> roleIds = new ArrayList<Integer>();
        
        for (Role role : user.getRoles()) {
            for (RolePermissionDomainObject permission : permissions) {
                if (RoleDomainObject.USE_IMAGES_IN_ARCHIVE_PERMISSION.equals(permission)) {
                    if (role.hasUseImagesInArchivePermission()) {
                        roleIds.add(role.getId());
                    }
                } else if (RoleDomainObject.CHANGE_IMAGES_IN_ARCHIVE_PERMISSION.equals(permission)) {
                    if (role.hasChangeImagesInArchivePermission()) {
                        roleIds.add(role.getId());
                    }
                }
            }
        }
        
        return roleIds;
    }

    public static List<Integer> getRoleIds(User user) {
        Role[] roles = user.getRoles();
        
        if (roles.length == 0) {
            return Collections.emptyList();
        }
        
        List<Integer> roleIds = new ArrayList<Integer>(roles.length);
        
        for (Role role : roles) {
            roleIds.add(role.getId());
        }
        
        return roleIds;
    }
}
