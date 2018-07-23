package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.persistence.entity.User;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDataService {

    User getUser(int id) throws UserNotExistsException;

    UserDTO getUser(String login) throws UserNotExistsException;

    void updateUser(UserDTO updateMe);

    List<UserDTO> getAdminUsers();

    List<UserDTO> getAllActiveUsers();

    List<User> findAll(boolean includeExternal, boolean includeInactive);

    List<User> findByNamePrefix(String prefix, boolean includeInactive);

    List<UserDTO> getUsersByEmail(String email);

    void saveUser(UserFormData userData);

    List<UserDTO> searchUsers(String searchTerm, Set<Integer> withRoles, boolean includeInactive);

}
