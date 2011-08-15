package com.imcode.imcms.addon.imagearchive.service;

import com.imcode.imcms.addon.imagearchive.entity.Categories;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RolePermissionDomainObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.api.Role;
import com.imcode.imcms.api.User;

@Transactional
public class UserService {
    @Autowired
    private SessionFactory factory;


    /* categoryIds restrist the search to the given ids. CategoryIds = null means permissions for any category */
    public List<Integer> getRoleIdsWithPermission(User user, List<Integer> categoryIds, RolePermissionDomainObject... permissions) {
        List<Integer> roleIds = new ArrayList<Integer>();
        Session session = factory.getCurrentSession();

        for (Role role : user.getRoles()) {
            for (RolePermissionDomainObject permission : permissions) {
                String queryStr = "select cr.roleId from CategoryRoles cr WHERE cr.roleId = :userRoleId";
                if(categoryIds != null && categoryIds.size() > 0) {
                    queryStr += " and cr.categoryId IN (:categoryIds)";
                }
                
                if (RoleDomainObject.USE_IMAGES_IN_ARCHIVE_PERMISSION.equals(permission)) {
                    queryStr += " and cr.canUse = 1";
                    Query query = session.createQuery(queryStr);
                    query.setInteger("userRoleId", role.getId());
                    if(categoryIds != null && categoryIds.size() > 0) {
                        query.setParameterList("categoryIds", categoryIds);
                    }
                    long count = query.list().size();

                    if (count > 0) {
                        roleIds.add(role.getId());
                    }
                } else if (RoleDomainObject.CHANGE_IMAGES_IN_ARCHIVE_PERMISSION.equals(permission)) {
                    queryStr += " and cr.canChange = 1";
                    Query query = session.createQuery(queryStr);
                    query.setInteger("userRoleId", role.getId());
                    if(categoryIds != null && categoryIds.size() > 0) {
                        query.setParameterList("categoryIds", categoryIds);
                    }
                    long count = query.list().size();

                    if (count > 0) {
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
