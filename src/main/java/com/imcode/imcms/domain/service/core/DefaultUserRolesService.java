package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.dto.UserDTO;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultUserRolesService implements UserRolesService {

    private final UserRolesRepository userRolesRepository;

    public DefaultUserRolesService(UserRolesRepository userRolesRepository) {
        this.userRolesRepository = userRolesRepository;
    }

    @Override
    public List<UserDTO> getUsersByRole(int roleId) {
        return userRolesRepository.findUserRolesByRoleId(roleId)
                .stream()
                .map(userRoles -> new UserDTO(userRoles.getUser()))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Integer> getUserIdsByRole(int roleId){
        return userRolesRepository.findUserRolesByRoleId(roleId)
                .stream()
                .map(userRoles -> userRoles.getUser().getId())
                .collect(Collectors.toSet());
    }

    @Override
    public List<Role> getRolesByUser(int userId) {
        return userRolesRepository.findUserRolesByUserId(userId)
                .stream()
                .map(userRoles -> new RoleDTO(userRoles.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Integer> getRoleIdsByUser(int userId){
        return userRolesRepository.findUserRolesByUserId(userId)
                .stream()
                .map(userRoles -> userRoles.getRole().getId())
                .collect(Collectors.toSet());
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
