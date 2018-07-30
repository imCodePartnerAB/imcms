package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Role;

import java.util.List;

public interface RoleService {

    Role getById(int id);

    List<Role> getAll();

    Role save(Role saveMe);

    Role saveNewRole(Role role);

    void delete(int roleID);
}
