package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.entity.UserAdminRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserAdminRolesRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private UserAdminRolesRepository userAdminRolesRepository;

    @Test
    public void crud() {
        final User user = userDataInitializer.createData("test-login");
        RoleJPA role = new RoleJPA();
        role.setName("test-role");

        role = roleRepository.save(role);

        final UserAdminRole userAdminRole = userAdminRolesRepository.save(new UserAdminRole(user, role));

        assertEquals(userAdminRole.getRole(), role);
        assertEquals(userAdminRole.getUser(), user);

        final List<UserAdminRole> userAdminRoles1 = userAdminRolesRepository.findUserAdminRoleByUserId(user.getId());

        assertEquals(userAdminRoles1.size(), 1);
        assertEquals(userAdminRoles1.get(0), userAdminRole);

        final List<UserAdminRole> userAdminRoles2 = userAdminRolesRepository.findUserAdminRoleByRoleId(role.getId());

        assertEquals(userAdminRoles2.size(), 1);
        assertEquals(userAdminRoles2.get(0), userAdminRole);

        userAdminRolesRepository.deleteUserAdminRoleByUserId(user.getId());

        final List<UserAdminRole> shouldBeEmpty = userAdminRolesRepository.findUserAdminRoleByUserId(user.getId());

        assertTrue(shouldBeEmpty.isEmpty());
    }
}