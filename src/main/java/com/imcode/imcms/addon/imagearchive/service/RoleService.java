package com.imcode.imcms.addon.imagearchive.service;

import imcode.server.user.RolePermissionDomainObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.addon.imagearchive.entity.Categories;
import com.imcode.imcms.addon.imagearchive.entity.CategoryRoles;
import com.imcode.imcms.addon.imagearchive.entity.Exif;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import com.imcode.imcms.api.User;

@Transactional
public class RoleService {
    @Autowired
    @Qualifier("hibernateTemplate")
    private HibernateTemplate template;
    
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public Roles findRoleById(int id) {
        return (Roles) template.get(Roles.class, id);
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Roles> findRoles() {
        return template.find("FROM Roles r ORDER BY r.roleName");
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Categories> findRoleCategories(int roleId) {
        return template.find(
                "SELECT c FROM CategoryRoles cr JOIN cr.category c WHERE cr.roleId = ? AND c.type.imageArchive IS TRUE ORDER BY c.name", roleId);
    }
    
    @SuppressWarnings("unchecked")
    public List<Categories> findFreeCategories(int roleId) {
        return template.find(
                "SELECT c FROM Categories c WHERE " +
                "NOT EXISTS (FROM CategoryRoles cr WHERE cr.roleId = ? AND cr.categoryId = c.id) " +
                "AND c.type.imageArchive IS TRUE ORDER BY c.name", roleId);
    }
    
    @SuppressWarnings("unchecked")
    public void assignCategoryRoles(final Roles role, final List<Integer> categoryIds, final boolean canUse, final boolean canChange) {
        template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
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
                
                return null;
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Categories> findCategories(final User user, final RolePermissionDomainObject... permissions) {
        return template.executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
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
        });
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Integer> findCategoryIds(final User user, final RolePermissionDomainObject... permissions) {
        return template.executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                List<Integer> roleIds = UserService.getRoleIdsWithPermission(user, permissions);
                if (roleIds.isEmpty()) {
                    return Collections.EMPTY_LIST;
                }
                
                return session.createQuery(
                        "SELECT cr.categoryId FROM CategoryRoles cr WHERE cr.roleId IN (:roleIds) AND cr.category.type.imageArchive IS TRUE ")
                        .setParameterList("roleIds", roleIds)
                        .list();
            }
        });
    }
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public boolean hasAccessToCategory(final User user, final int categoryId, final RolePermissionDomainObject... permissions) {
        return (Boolean) template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
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
                
                long count = (Long) session.createQuery(
                        "SELECT count(cr.categoryId) FROM CategoryRoles cr " +
                        "WHERE cr.categoryId = :categoryId AND cr.roleId IN (:roleIds) AND cr.category.type.imageArchive IS TRUE")
                        .setInteger("categoryId", categoryId)
                        .setParameterList("roleIds", roleIds)
                        .uniqueResult();
                
                return count != 0L;
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<String> findArtists(final User user) {
        return template.executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
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
                
                return session.getNamedQuery("artistsByRoleIds")
                        .setParameterList("roleIds", roleIds)
                        .setShort("changedType", Exif.TYPE_CHANGED)
                        .setInteger("userId", user.getId())
                        .list();
            }
        });
    }
}
