package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.service.UserRolesService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.entity.UserRoles;
import com.imcode.imcms.persistence.repository.UserRolesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultUserRolesService implements UserRolesService {

    private final UserRolesRepository userRolesRepository;

    public DefaultUserRolesService(UserRolesRepository userRolesRepository) {
        this.userRolesRepository = userRolesRepository;
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRolesRepository.getUserRolesByRoleId(role.getId())
                .stream()
                .map(UserRoles::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> getRolesByUser(User user) {
        return userRolesRepository.getUserRolesByUserId(user.getId())
                .stream()
                .map(UserRoles::getRole)
                .collect(Collectors.toList());
    }
}
