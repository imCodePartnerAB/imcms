package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.UserRoleId;
import com.imcode.imcms.persistence.entity.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles, UserRoleId> {

    List<UserRoles> findUserRolesByUserId(int userId);

    List<UserRoles> findUserRolesByRoleId(int roleId);

    void deleteUserRolesByUserId(int userId);
}
