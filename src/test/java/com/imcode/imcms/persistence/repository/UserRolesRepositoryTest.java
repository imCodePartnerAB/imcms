package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.entity.UserRoleId;
import com.imcode.imcms.persistence.entity.UserRoles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserRolesRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void findAll_When_RecordsExist_Expect_RecordsReturned() {
        final List<UserRoles> userRoles = userRolesRepository.findAll();
        assertFalse(userRoles.isEmpty());
    }

    @Test
    public void get_When_SpecifiedRecordExist_Expect_Returned() {
        final UserRoles expected = userRolesRepository.findAll().get(0);
        final UserRoleId existingId = expected.getId();

        final UserRoles actual = userRolesRepository.getOne(existingId);

        assertNotNull(actual);
        assertEquals(expected, actual);

        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getRole(), actual.getRole());
    }

    @Test
    public void saveNewRecord_Expect_Saved() {
        User user = new User("test", "test", "test@imcode.com");
        userRepository.saveAndFlush(user);

        assertNotNull(user);

        RoleJPA role = Collections.max(roleRepository.findAll(), Comparator.comparing(RoleJPA::getId));

        assertNotNull(role);

        final int expectedSize = userRolesRepository.findAll().size() + 1;
        userRolesRepository.save(new UserRoles(user, role));

        final List<UserRoles> afterSaved = userRolesRepository.findAll();

        final UserRoles actualUserRole = Collections.max(afterSaved,
                Comparator.comparing(userRole -> userRole.getUser().getId()));

        assertEquals(expectedSize, afterSaved.size());
        assertEquals(user, actualUserRole.getUser());
        assertEquals(role, actualUserRole.getRole());
    }

    @Test
    public void delete_WhenSpecifiedRecordExists_Expect_Deleted() {
        User user = new User("test", "test", "test@imcode.com");
        userRepository.saveAndFlush(user);

        final RoleJPA newRole = new RoleJPA();
        newRole.setName("test_role");
        roleRepository.save(newRole);

        final UserRoles userRoles = new UserRoles(user, newRole);
        userRolesRepository.save(userRoles);

        final List<UserRoles> beforeDeleting = userRolesRepository.findAll();

        userRolesRepository.delete(userRoles);

        final List<UserRoles> afterDeleting = userRolesRepository.findAll();

        assertEquals(beforeDeleting.size() - 1, afterDeleting.size());
    }

    @Test
    public void getRoles_When_UserIdExists_Expect_Returned() {
        final User user = new User("test", "test", "test@imcode.com");
        userRepository.saveAndFlush(user);

        final List<RoleJPA> roles = roleRepository.findAll();

        roles.forEach(roleJPA -> userRolesRepository.save(new UserRoles(user, roleJPA)));

        final List<UserRoles> userRolesByUserId = userRolesRepository.findUserRolesByUserId(user.getId());

        final List<RoleJPA> actualRoles = userRolesByUserId.stream()
                .map(UserRoles::getRole)
                .collect(Collectors.toList());

        assertEquals(roles.size(), userRolesByUserId.size());
        assertTrue(roles.containsAll(actualRoles));
        assertTrue(actualRoles.containsAll(roles));
    }

    @Test
    public void getUsers_When_RoleIdExists_Expect_Returned() {

        final int userListSize = 3;

        final RoleJPA newRole = new RoleJPA();
        newRole.setName("test_role");

        roleRepository.save(newRole);

        final List<User> users = userDataInitializer.createData(userListSize, newRole.getId());

        final List<UserRoles> userRolesByRoleId = userRolesRepository.findUserRolesByRoleId(newRole.getId());

        final List<User> actualUsers = userRolesByRoleId.stream()
                .map(UserRoles::getUser)
                .collect(Collectors.toList());

        assertEquals(userListSize, actualUsers.size());
        assertEquals(users, actualUsers);
    }

    @Test
    public void deleteUserRolesByUserId_When_SomeRolesExist_Expect_Deleted() {
        final User user = userDataInitializer.createData("test-user");

        RoleJPA role1 = new RoleJPA();
        role1.setName("test-role-1");

        role1 = roleRepository.save(role1);

        final UserRoles userRole1 = new UserRoles(user, role1);

        RoleJPA role2 = new RoleJPA();
        role2.setName("test-role-2");

        role2 = roleRepository.save(role2);

        final UserRoles userRole2 = new UserRoles(user, role2);

        final List<UserRoles> userRoles = Arrays.asList(userRole1, userRole2);
        userRolesRepository.saveAll(userRoles);

        final List<UserRoles> userRolesByUserId = userRolesRepository.findUserRolesByUserId(user.getId());

        assertTrue(userRolesByUserId.containsAll(userRoles));
        assertTrue(userRoles.containsAll(userRolesByUserId));

        userRolesRepository.deleteUserRolesByUserId(user.getId());

        final List<UserRoles> shouldBeEmpty = userRolesRepository.findUserRolesByUserId(user.getId());

        assertTrue(shouldBeEmpty.isEmpty());
    }
}