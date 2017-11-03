package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.mapping.jpa.User;
import imcode.server.user.RoleId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private Function<User, UserDTO> userToUserDTO;

    private List<User> users;

    private List<UserDTO> expectedUsers;

    @Before
    public void createUsers() {
        users = new ArrayList<>(12);
        List<User> adminUsers = new ArrayList<>(9);

        final List<User> superAdmins = userDataInitializer.createData(5, RoleId.SUPERADMIN_ID);
        final List<User> admins = userDataInitializer.createData(4, RoleId.USERADMIN_ID);
        final List<User> defaultUsers = userDataInitializer.createData(3, RoleId.USERS_ID);

        users.addAll(superAdmins);
        users.addAll(admins);
        users.addAll(defaultUsers);

        adminUsers.add(userService.getUser("admin"));
        adminUsers.addAll(superAdmins);
        adminUsers.addAll(admins);

        expectedUsers = adminUsers.stream()
                .map(userToUserDTO)
                .collect(Collectors.toList());
    }

    @After
    public void clearUsers() {
        userDataInitializer.cleanRepositories(users);
    }

    @Test
    public void getAdminUsersTest() {
        assertEquals(expectedUsers, userService.getAdminUsers());
    }

    @Test
    public void testFindAll() throws Exception {
        assertNotNull(userService.findAll(true, true));
        assertNotNull(userService.findAll(true, false));
        assertNotNull(userService.findAll(false, false));
        assertNotNull(userService.findAll(false, true));
    }

    @Test
    public void findByNamePrefix() throws Exception {
        assertNotNull(userService.findByNamePrefix("prefix", true));
        assertNotNull(userService.findByNamePrefix("prefix", false));
    }

}
