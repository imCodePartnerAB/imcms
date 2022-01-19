package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.User;

import java.util.List;
import java.util.Set;

public interface UserRolesService {

    List<UserDTO> getUsersByRole(int roleId);

    Set<Integer> getUserIdsByRole(int roleId);

    List<Role> getRolesByUser(int userId);

    Set<Integer> getRoleIdsByUser(int userId);

    void updateUserRoles(List<? extends Role> roles, User user);
}
