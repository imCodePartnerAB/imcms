package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.User;

import java.util.List;

public interface UserAdminRolesService {

    List<User> getUsersByAdminRole(Role role);

    List<Role> getAdminRolesByUser(User user);

    void updateUserAdminRoles(List<? extends Role> roles, User user);

}
