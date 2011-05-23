package com.imcode.imcms.addon.imagearchive.service;

import com.imcode.imcms.addon.imagearchive.command.SaveRoleCategoriesCommand;
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

    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Categories> findAllCategories() {

        return factory.getCurrentSession()
                .createQuery("SELECT c FROM Categories c WHERE " +
                " c.type.name = :typeName ORDER BY c.name")
                .setString("typeName", "Images")
                .list();
    }
    
    public void assignCategoryRoles(Roles role, List<SaveRoleCategoriesCommand.CategoryRight> categoryRights) {

        Session session = factory.getCurrentSession();

        StringBuilder deleteBuilder = new StringBuilder(
                "DELETE FROM CategoryRoles cr WHERE cr.roleId = :roleId ");


        Query deleteQuery = session.createQuery(deleteBuilder.toString())
                .setInteger("roleId", role.getId());
        deleteQuery.executeUpdate();

        if(categoryRights != null) {
            for(SaveRoleCategoriesCommand.CategoryRight categoryRight: categoryRights) {
                CategoryRoles cr = new CategoryRoles(categoryRight.getCategoryId(), role.getId(), categoryRight.isCanUse(), categoryRight.isCanEditOrAdd());
                    session.persist(cr);
            }
        }
        session.flush();
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
