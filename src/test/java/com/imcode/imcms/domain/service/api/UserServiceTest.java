package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class UserServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataInitializer userDataInitializer;

    private List<UserDTO> expectedUsers;

    @BeforeEach
    public void createUsers() {
        expectedUsers = new ArrayList<>(9);

        final List<UserDTO> superAdmins = toDTO(userDataInitializer.createData(5, Roles.SUPER_ADMIN.getId()));
        final List<UserDTO> admins = toDTO(userDataInitializer.createData(4, Roles.USER_ADMIN.getId()));
        userDataInitializer.createData(3, Roles.USER.getId());

        expectedUsers.add(userService.getUser("admin"));
        expectedUsers.addAll(superAdmins);
        expectedUsers.addAll(admins);
    }

    private List<UserDTO> toDTO(Collection<User> usersToTransform) {
        return usersToTransform.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Test
    public void getAdminUsersTest() {
        assertEquals(expectedUsers, userService.getAdminUsers());
    }

    @Test
    public void testFindAll() {
        assertNotNull(userService.findAll(true, true));
        assertNotNull(userService.findAll(true, false));
        assertNotNull(userService.findAll(false, false));
        assertNotNull(userService.findAll(false, true));
    }

    @Test
    public void findByNamePrefix() {
        assertNotNull(userService.findByNamePrefix("prefix", true));
        assertNotNull(userService.findByNamePrefix("prefix", false));
    }

}
