package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class UserControllerTest extends AbstractControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataInitializer userDataInitializer;

    private List<UserDTO> expectedUsers;

    @Override
    public String controllerPath() {
        return "/users/admins";
    }

    @Before
    public void createUsers() {
        List<User> adminUsers = new ArrayList<>(9);

        final List<User> superAdmins = userDataInitializer.createData(5, Roles.SUPER_ADMIN.getId());
        final List<User> admins = userDataInitializer.createData(4, Roles.USER_ADMIN.getId());
        userDataInitializer.createData(3, Roles.USER.getId()); // some other users

        adminUsers.add(userService.getUser("admin"));
        adminUsers.addAll(superAdmins);
        adminUsers.addAll(admins);

        expectedUsers = adminUsers.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Test
    public void getAdminUsersTest() throws Exception {
        final String usersJson = asJson(expectedUsers);
        getAllExpectedOkAndJsonContentEquals(usersJson);
    }

}
