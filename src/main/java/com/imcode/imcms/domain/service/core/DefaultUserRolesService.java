package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.UserRolesService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.RoleJPA;
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
        return userRolesRepository.findUserRolesByRoleId(role.getId())
                .stream()
                .map(UserRoles::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> getRolesByUser(int userId) {
        return userRolesRepository.findUserRolesByUserId(userId)
                .stream()
                .map(userRoles -> new RoleDTO(userRoles.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserRoles(List<? extends Role> roles, User user) {
        userRolesRepository.deleteUserRolesByUserId(user.getId());

        final List<UserRoles> saveUs = roles.stream()
                .map(role -> new UserRoles(user, new RoleJPA(role)))
                .collect(Collectors.toList());

        saveUs.add(new UserRoles(user, new RoleJPA(Roles.USER))); // should always add "user" role to user, dunno why...

        userRolesRepository.save(saveUs);
    }
}
