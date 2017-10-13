package com.imcode.imcms.mapping.jpa;

import com.imcode.imcms.api.Role;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.jpa.User.PasswordReset;
import imcode.server.user.RoleId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserDataInitializer userDataInitializer;

    private List<User> users;

    @Before
    public void createUsers() {
        users = new ArrayList<>(12);
        final List<User> superAdmins = userDataInitializer.createData(5, RoleId.SUPERADMIN_ID);
        final List<User> admins = userDataInitializer.createData(4, RoleId.USERADMIN_ID);
        final List<User> defaultUsers = userDataInitializer.createData(3, RoleId.USERS_ID);

        users.addAll(superAdmins);
        users.addAll(admins);
        users.addAll(defaultUsers);
    }

    @After
    public void clearUsers() {
        userDataInitializer.cleanRepositories(users);
    }

    @Test
    public void testFindByLogin() throws Exception {
        assertNotNull(repository.findByLogin("admin"));
    }

    @Test
    public void testFindByLoginIgnoreCase() throws Exception {
        assertNotNull(repository.findByLoginIgnoreCase("admin"));
    }

    @Test
    public void testFindByEmail() throws Exception {
        assertNotNull(repository.findByEmail("admin"));
    }

    @Test
    public void testFindByEmailUnique() throws Exception {
        assertNotNull(repository.findByEmailUnique("admin@imcode.com"));
    }

    @Test
    public void testFindById() throws Exception {
        assertNotNull(repository.findById(1));
    }

    @Test
    public void testFindByPasswordResetId() throws Exception {
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
        final List<User> admins = repository.findUsersWithRoleIds(Role.SUPERADMIN_ID, RoleId.USERADMIN_ID);
        Assert.assertEquals(9 + 1, admins.size());
    }
}
