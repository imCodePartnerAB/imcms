package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.service.UserAdminRolesService;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.entity.UserAdminRole;
import com.imcode.imcms.persistence.repository.UserAdminRolesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocalUserAdminRolesService implements UserAdminRolesService {

    private final UserAdminRolesRepository userAdminRolesRepository;

    public LocalUserAdminRolesService(UserAdminRolesRepository userAdminRolesRepository) {
        this.userAdminRolesRepository = userAdminRolesRepository;
    }

    @Override
    public List<User> getUsersByAdminRole(Role role) {
        return userAdminRolesRepository.findUserAdminRoleByRoleId(role.getId())
                .stream()
                .map(UserAdminRole::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> getAdminRolesByUser(int userId) {
        return userAdminRolesRepository.findUserAdminRoleByUserId(userId)
                .stream()
                .map(userAdminRole -> new RoleDTO(userAdminRole.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserAdminRoles(List<? extends Role> roles, User user) {
        userAdminRolesRepository.deleteUserAdminRoleByUserId(user.getId());

        final List<UserAdminRole> saveUs = roles.stream()
                .map(role -> new UserAdminRole(user, new RoleJPA(role)))
                .collect(Collectors.toList());

        userAdminRolesRepository.save(saveUs);
    }
}
