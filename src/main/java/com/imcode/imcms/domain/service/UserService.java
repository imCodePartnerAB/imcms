package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.persistence.entity.User;

import java.util.List;

public interface UserService {

    User getUser(int id);

    User getUser(String login);

    List<UserDTO> getAdminUsers();

    List<UserDTO> getAllActiveUsers();

    List<User> findAll(boolean includeExternal, boolean includeInactive);

    List<User> findByNamePrefix(String prefix, boolean includeInactive);

}
