package com.imcode.imcms.addon.imagearchive.service;

import imcode.server.user.RolePermissionDomainObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.CategoryRoles;
import com.imcode.imcms.addon.imagearchive.entity.Exif;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import com.imcode.imcms.api.User;
import org.hibernate.SessionFactory;

@Transactional
public class RoleService {
    @Autowired
    private SessionFactory factory;
    
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public Roles findRoleById(int id) {

        return (Roles) factory.getCurrentSession().get(Roles.class, id);
    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Roles> findRoles() {

        return factory.getCurrentSession()
                .createQuery("FROM Roles r ORDER BY r.roleName")
                .list();
    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Categories> findRoleCategories(int roleId) {

        return factory.getCurrentSession()
                .createQuery("SELECT c FROM CategoryRoles cr JOIN cr.category c WHERE " +
                "cr.roleId = :roleId AND c.type.imageArchive IS TRUE ORDER BY c.name")
                .setInteger("roleId", roleId)
                .list();
    }

    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Categories> findFreeCategories(int roleId) {

        return factory.getCurrentSession()
                .createQuery("SELECT c FROM Categories c WHERE " +
                "NOT EXISTS (FROM CategoryRoles cr WHERE cr.roleId = :roleId AND cr.categoryId = c.id) " +
                "AND c.type.imageArchive IS TRUE ORDER BY c.name")
                .setInteger("roleId", roleId)
                .list();
    }
    
    public void assignCategoryRoles(Roles role, List<Integer> categoryIds, boolean canUse, boolean canChange) {

        Session session = factory.getCurrentSession();

        StringBuilder deleteBuilder = new StringBuilder(
                "DELETE FROM CategoryRoles cr WHERE cr.roleId = :roleId ");

        if (categoryIds != null) {
            deleteBuilder.append(" AND cr.categoryId NOT IN (:categoryIds) ");
        }

        Query deleteQuery = session.createQuery(deleteBuilder.toString())
                .setInteger("roleId", role.getId());
        if (categoryIds != null) {
            deleteQuery.setParameterList("categoryIds", categoryIds);
        }
        deleteQuery.executeUpdate();

        if (categoryIds != null) {
            List<Integer> existingCategoryIds = session.createQuery(
                    "SELECT cr.categoryId FROM CategoryRoles cr WHERE cr.roleId = :roleId AND cr.categoryId IN (:categoryIds)")
                    .setInteger("roleId", role.getId())
                    .setParameterList("categoryIds", categoryIds)
                    .list();
            Set<Integer> existingSet = new HashSet<Integer>(existingCategoryIds);

            for (int id : categoryIds) {
                if (!existingSet.contains(id)) {
                    CategoryRoles cr = new CategoryRoles(id, role.getId());
                    session.persist(cr);
                }
            }
        }
        session.flush();

        int permissions = (Integer) session.createQuery("SELECT r.permissions FROM Roles r WHERE r.id = :roleId")
                .setInteger("roleId", role.getId())
                .uniqueResult();

        if (canUse) {
            permissions |= Roles.PERMISSION_USE_IMAGE;
        } else {
            permissions &= ~Roles.PERMISSION_USE_IMAGE;
        }

        if (canChange) {
            permissions |= Roles.PERMISSION_CHANGE_IMAGE;
        } else {
            permissions &= ~Roles.PERMISSION_CHANGE_IMAGE;
        }

        session.createQuery("UPDATE Roles r SET r.permissions = :permissions WHERE r.id = :roleId")
                .setInteger("permissions", permissions)
                .setInteger("roleId", role.getId())
                .executeUpdate();
        role.setPermissions(permissions);

    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Categories> findCategories(User user, RolePermissionDomainObject... permissions) {

        Session session = factory.getCurrentSession();
        List<Integer> roleIds = null;

        if (user.isSuperAdmin()) {
            return session.createQuery(
                    "SELECT DISTINCT c.id AS id, c.name AS name FROM Categories c " +
                    "WHERE c.type.imageArchive IS TRUE ORDER BY c.name")
                    .setResultTransformer(Transformers.aliasToBean(Categories.class))
                    .list();
            
        } else if (user.isDefaultUser()) {
            roleIds = new ArrayList<Integer>();
            roleIds.add(Roles.USERS_ID);

        } else {
            roleIds = UserService.getRoleIdsWithPermission(user, permissions);
            if (roleIds.isEmpty()) {
                return Collections.EMPTY_LIST;
            }

        }

        return session.createQuery(
                "SELECT DISTINCT c.id AS id, c.name AS name FROM CategoryRoles cr INNER JOIN cr.category c " +
                "WHERE cr.roleId IN (:roleIds) AND c.type.imageArchive IS TRUE ORDER BY c.name")
                .setParameterList("roleIds", roleIds)
                .setResultTransformer(Transformers.aliasToBean(Categories.class))
                .list();

    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Integer> findCategoryIds(User user, RolePermissionDomainObject... permissions) {

        List<Integer> roleIds = UserService.getRoleIdsWithPermission(user, permissions);
        if (roleIds.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        return factory.getCurrentSession()
                .createQuery(
                "SELECT cr.categoryId FROM CategoryRoles cr WHERE cr.roleId IN (:roleIds) AND cr.category.type.imageArchive IS TRUE ")
                .setParameterList("roleIds", roleIds)
                .list();
    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public boolean hasAccessToCategory(User user, int categoryId, RolePermissionDomainObject... permissions) {

        List<Integer> roleIds = null;

        if (user.isSuperAdmin()) {
            return true;

        } else if (user.isDefaultUser()) {
            roleIds = new ArrayList<Integer>(1);
            roleIds.add(Roles.USERS_ID);

        } else {
            roleIds = UserService.getRoleIdsWithPermission(user, permissions);
            if (roleIds.isEmpty()) {
                return false;
            }
            
        }

        long count = (Long) factory.getCurrentSession()
                .createQuery(
                "SELECT count(cr.categoryId) FROM CategoryRoles cr " +
                "WHERE cr.categoryId = :categoryId AND cr.roleId IN (:roleIds) AND cr.category.type.imageArchive IS TRUE")
                .setInteger("categoryId", categoryId)
                .setParameterList("roleIds", roleIds)
                .uniqueResult();

        return count != 0L;
    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<String> findArtists(User user) {

        List<Integer> roleIds = null;
        if (user.isDefaultUser()) {
            roleIds = new ArrayList<Integer>(1);
            roleIds.add(Roles.USERS_ID);

        } else {
            roleIds = UserService.getRoleIdsWithPermission(user, Roles.ALL_PERMISSIONS);
            if (roleIds.isEmpty()) {
                return Collections.EMPTY_LIST;
            }

        }

        return factory.getCurrentSession()
                .getNamedQuery("artistsByRoleIds")
                .setParameterList("roleIds", roleIds)
                .setShort("changedType", Exif.TYPE_CHANGED)
                .setInteger("userId", user.getId())
                .list();
    }
}
