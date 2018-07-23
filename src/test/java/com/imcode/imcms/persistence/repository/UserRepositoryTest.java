package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.PasswordReset;
import com.imcode.imcms.persistence.entity.RoleJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.entity.UserRoles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDataInitializer userDataInitializer;

    private List<User> users;

    @Before
    public void createUsers() {
        users = new ArrayList<>(12);
        final List<User> superAdmins = userDataInitializer.createData(5, Roles.SUPER_ADMIN.getId());
        final List<User> admins = userDataInitializer.createData(4, Roles.USER_ADMIN.getId());
        final List<User> defaultUsers = userDataInitializer.createData(3, Roles.USER.getId());

        users.addAll(superAdmins);
        users.addAll(admins);
        users.addAll(defaultUsers);
    }

    @Test
    public void testFindByLogin() {
        assertNotNull(repository.findByLogin("admin"));
    }

    @Test
    public void testFindByLoginIgnoreCase() {
        assertNotNull(repository.findByLoginIgnoreCase("admin"));
    }

    @Test
    public void testFindByEmail() {
        assertNotNull(repository.findByEmail("admin"));
    }

    @Test
    public void testFindById() {
        assertNotNull(repository.findById(1));
    }

    @Test
    public void testFindByPasswordResetId() {
        final User user = users.get(0);
        final PasswordReset passwordReset = new PasswordReset();
        passwordReset.setId("test");
        passwordReset.setTimestamp(new Date().getTime());
        user.setPasswordReset(passwordReset);
        repository.saveAndFlush(user);
        assertNotNull(repository.findByPasswordResetId("test"));
    }

    /**
     * We have predefined super admin user in the database, so we add 1 to actual admin users size.
     */
    @Test
    public void findUsersWithRoleIdsTest() {
        final List<User> admins = repository.findUsersWithRoleIds(Roles.SUPER_ADMIN.getId(), Roles.USER_ADMIN.getId());
        assertEquals(9 + 1, admins.size());
    }

    @Test
    public void findByIdIn() {

        final List<User> users = this.users.subList(0, 4);

        final Set<Integer> usersIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        final List<User> usersByIds = repository.findByIdIn(usersIds);

        assertEquals(users.size(), usersByIds.size());

        for (int i = 0; i < usersByIds.size(); i++) {
            final User expected = users.get(i);
            final User actual = usersByIds.get(i);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getLogin(), actual.getLogin());
        }
    }

    @Test
    public void findByEmailUnique_When_NoUserWithSuchEmail_Expect_Null() {
        assertNull(repository.findByEmailUnique("uniquEmailololo"));
    }

    @Test
    public void findByEmailUnique_When_UserWithSuchEmailExists_Expect_ThatUser() {
        final String email = "email@test.com";
        final User user = repository.save(new User("login", "pass", email));

        final User byEmailUnique = repository.findByEmailUnique(email);

        assertEquals(user, byEmailUnique);
    }

    @Test
    public void findByEmail_When_NoUserWithSuchEmail_Expect_EmptyList() {
        assertTrue(repository.findByEmail("uniquEmailololo").isEmpty());
    }

    @Test
    public void findByEmail_When_UserWithSuchEmailExists_Expect_ThatUser() {
        final String email = "email@test.com";
        final User user = repository.save(new User("login", "pass", email));

        final List<User> users = repository.findByEmail(email);

        assertEquals(users.size(), 1);
        assertEquals(user, users.get(0));
    }

    @Test
    public void searchUsers_When_UsersWithSearchedDataExist_Expect_CorrectFound() {
        final String term = "term";
        final String password = "password";

        User user1 = new User(term, password, "1");
        user1 = repository.save(user1);

        User user2 = new User("2", password, term);
        user2 = repository.save(user2);

        User user3 = new User("3", password, "3");
        user3.setFirstName(term);
        user3 = repository.save(user3);

        User user4 = new User("4", password, "4");
        user4.setLastName(term);
        user4 = repository.save(user4);

        User user5 = new User("5", password, "5");
        user5.setTitle(term);
        user5 = repository.save(user5);

        User user6 = new User("6", password, "6");
        user6.setCompany(term);
        user6 = repository.save(user6);

        User user7 = new User("7", password, "7");
        user7.setCompany("not what you are searching for");
        repository.save(user7);

        User user8 = new User("8", password, "8");
        user8.setCompany(term);
        user8.setActive(false);
        user8 = repository.save(user8);

        final List<User> actual = repository.searchUsers(term);
        final List<User> expected = Arrays.asList(user1, user2, user3, user4, user5, user6, user8);

        assertTrue(actual.containsAll(expected));
        assertTrue(expected.containsAll(actual));
    }

    @Test
    public void searchActiveUsers_When_ActiveAndNotActiveUsersWithSearchedDataExist_Expect_CorrectFound() {
        final String term = "term";
        final String password = "password";

        User user1 = new User(term, password, "1");
        user1.setActive(false);
        repository.save(user1);

        User user2 = new User("2", password, term);
        user2 = repository.save(user2);

        User user3 = new User("3", password, "3");
        user3.setActive(false);
        user3.setFirstName(term);
        repository.save(user3);

        User user4 = new User("4", password, "4");
        user4.setLastName(term);
        user4 = repository.save(user4);

        User user5 = new User("5", password, "5");
        user5.setActive(false);
        user5.setTitle(term);
        repository.save(user5);

        User user6 = new User("6", password, "6");
        user6.setCompany(term);
        user6 = repository.save(user6);

        User user7 = new User("7", password, "7");
        user7.setActive(false);
        user7.setCompany("not what you are searching for");
        repository.save(user7);

        User user8 = new User("8", password, "8");
        user8.setCompany(term);
        user8 = repository.save(user8);

        final List<User> actual = repository.searchActiveUsers(term);
        final List<User> expected = Arrays.asList(user2, user4, user6, user8);

        assertTrue(actual.containsAll(expected));
        assertTrue(expected.containsAll(actual));
    }

    @Test
    public void searchUsers_When_UsersWithRolesAndSearchedDataExist_Expect_CorrectFound() {

        final RoleJPA role1 = roleRepository.save(new RoleJPA("role1"));
        final RoleJPA role2 = roleRepository.save(new RoleJPA("role2"));
        final RoleJPA roleSearch1 = roleRepository.save(new RoleJPA("roleSearch1"));
        final RoleJPA roleSearch2 = roleRepository.save(new RoleJPA("roleSearch2"));

        final Set<Integer> searchRoles = new HashSet<>();
        searchRoles.add(roleSearch1.getId());
        searchRoles.add(roleSearch2.getId());

        final String term = "term";
        final String password = "password";

        User user1 = new User(term, password, "1");
        user1 = repository.save(user1);

        User user2 = new User("2", password, term);
        user2 = repository.save(user2);

        User user3 = new User("3", password, "3");
        user3.setFirstName(term);
        user3 = repository.save(user3);

        User user4 = new User("4", password, "4");
        user4.setLastName(term);
        user4 = repository.save(user4);

        User user5 = new User("5", password, "5");
        user5.setTitle(term);
        user5 = repository.save(user5);

        User user6 = new User("6", password, "6");
        user6.setCompany(term);
        user6 = repository.save(user6);

        User user7 = new User("7", password, "7");
        user7.setCompany("not what you are searching for");
        user7 = repository.save(user7);

        User user8 = new User("8", password, "8");
        user8.setCompany(term);
        user8.setActive(false);
        user8 = repository.save(user8);

        userRolesRepository.save(new UserRoles(user1, role1));
        userRolesRepository.save(new UserRoles(user2, role2));
        userRolesRepository.save(new UserRoles(user3, roleSearch1));
        userRolesRepository.save(new UserRoles(user4, roleSearch2));
        userRolesRepository.save(new UserRoles(user5, role1));
        userRolesRepository.save(new UserRoles(user6, role2));
        userRolesRepository.save(new UserRoles(user7, roleSearch1));
        userRolesRepository.save(new UserRoles(user8, roleSearch2));

        final List<User> actual = repository.searchUsers(term, searchRoles);
        final List<User> expected = Arrays.asList(user3, user4, user8);

        assertTrue(actual.containsAll(expected));
        assertTrue(expected.containsAll(actual));
    }

    @Test
    public void searchActiveUsers_When_ActiveAndNotActiveUsersWithSearchedDataAndWithRolesExist_Expect_CorrectFound() {
        final RoleJPA role1 = roleRepository.save(new RoleJPA("role1"));
        final RoleJPA role2 = roleRepository.save(new RoleJPA("role2"));
        final RoleJPA roleSearch1 = roleRepository.save(new RoleJPA("roleSearch1"));
        final RoleJPA roleSearch2 = roleRepository.save(new RoleJPA("roleSearch2"));

        final Set<Integer> searchRoles = new HashSet<>();
        searchRoles.add(roleSearch1.getId());
        searchRoles.add(roleSearch2.getId());

        final String term = "term";
        final String password = "password";

        User user1 = new User(term, password, "1");
        user1.setActive(false);
        user1 = repository.save(user1);

        User user2 = new User("2", password, term);
        user2 = repository.save(user2);

        User user3 = new User("3", password, "3");
        user3.setActive(false);
        user3.setFirstName(term);
        user3 = repository.save(user3);

        User user4 = new User("4", password, "4");
        user4.setLastName(term);
        user4 = repository.save(user4);

        User user5 = new User("5", password, "5");
        user5.setActive(false);
        user5.setTitle(term);
        user5 = repository.save(user5);

        User user6 = new User("6", password, "6");
        user6.setCompany(term);
        user6 = repository.save(user6);

        User user7 = new User("7", password, "7");
        user7.setActive(false);
        user7.setCompany("not what you are searching for");
        user7 = repository.save(user7);

        User user8 = new User("8", password, "8");
        user8.setCompany(term);
        user8 = repository.save(user8);

        userRolesRepository.save(new UserRoles(user1, role1));
        userRolesRepository.save(new UserRoles(user2, role2));
        userRolesRepository.save(new UserRoles(user3, roleSearch1));
        userRolesRepository.save(new UserRoles(user4, roleSearch2));
        userRolesRepository.save(new UserRoles(user5, role1));
        userRolesRepository.save(new UserRoles(user6, role2));
        userRolesRepository.save(new UserRoles(user7, roleSearch1));
        userRolesRepository.save(new UserRoles(user8, roleSearch2));

        final List<User> actual = repository.searchActiveUsers(term, searchRoles);
        final List<User> expected = Arrays.asList(user4, user8);

        assertTrue(actual.containsAll(expected));
        assertTrue(expected.containsAll(actual));
    }
}
