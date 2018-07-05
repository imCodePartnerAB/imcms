package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.UserAdminRole;
import com.imcode.imcms.persistence.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdminRolesRepository extends JpaRepository<UserAdminRole, UserRoleId> {
}
