package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.service.UserService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class UserControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataInitializer userDataInitializer;

    private List<User> users;

    private List<UserDTO> expectedUsers;

    @Override
    public String controllerPath() {
        return "/users";
    }

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
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @After
    public void clearUsers() {
        userDataInitializer.cleanRepositories(users);
    }

    @Test
    public void getAdminUsersTest() throws Exception {
        final String usersJson = asJson(expectedUsers);
        getAllExpectedOkAndJsonContentEquals(usersJson);
    }

}
