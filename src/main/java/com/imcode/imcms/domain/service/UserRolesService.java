package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.User;

import java.util.List;

public interface UserRolesService {

    List<User> getUsersByRole(Role role);

    List<Role> getRolesByUser(User user);

    void updateUserRoles(List<? extends Role> roles, User user);
}
