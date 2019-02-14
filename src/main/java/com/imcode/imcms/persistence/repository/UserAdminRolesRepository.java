package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.UserAdminRole;
import com.imcode.imcms.persistence.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAdminRolesRepository extends JpaRepository<UserAdminRole, UserRoleId> {

    List<UserAdminRole> findUserAdminRoleByUserId(int userId);

    List<UserAdminRole> findUserAdminRoleByRoleId(int roleId);

    void deleteUserAdminRoleByUserId(int userId);

}
