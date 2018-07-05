package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.UserAdminRole;
import com.imcode.imcms.persistence.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAdminRolesRepository extends JpaRepository<UserAdminRole, UserRoleId> {

    List<UserAdminRole> findUserAdminRoleByUserId(int userId);

    List<UserAdminRole> findUserAdminRoleByRoleId(int roleId);

    void deleteUserAdminRoleByUserId(int userId);

}
