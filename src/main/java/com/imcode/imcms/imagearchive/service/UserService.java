package com.imcode.imcms.imagearchive.service;

import com.imcode.imcms.api.Role;
import com.imcode.imcms.api.User;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RolePermissionDomainObject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Service
@Transactional
public class UserService {

    @PersistenceContext(unitName = "com.imcode.imcms")
//    @Autowired
    private EntityManager entityManager;

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

    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    /*
    Returns a set of role ids that have the given permissions(use/change/any)
    categoryIds restrist the search to the given category ids. CategoryIds = null means permissions for any category
    */
    public Set<Integer> getRoleIdsWithPermission(User user, List<Integer> categoryIds, RolePermissionDomainObject... permissions) {
        Set<Integer> roleIds = new HashSet<Integer>();
        Session session = getCurrentSession();

        for (Role role : user.getRoles()) {
            for (RolePermissionDomainObject permission : permissions) {
                String queryStr = "SELECT cr.roleId FROM CategoryRoles cr WHERE cr.roleId = :userRoleId";
                if (categoryIds != null && categoryIds.size() > 0) {
                    queryStr += " and cr.categoryId IN (:categoryIds)";
                }

                if (RoleDomainObject.USE_IMAGES_IN_ARCHIVE_PERMISSION.equals(permission)) {
                    queryStr += " and cr.canUse = 1";
                    Query query = session.createQuery(queryStr);
                    query.setInteger("userRoleId", role.getId());
                    if (categoryIds != null && categoryIds.size() > 0) {
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
                    if (categoryIds != null && categoryIds.size() > 0) {
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
}
