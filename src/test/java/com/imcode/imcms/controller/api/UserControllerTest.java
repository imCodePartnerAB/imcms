package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.service.UserRolesService;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.model.Roles;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest extends MockingControllerTest {

    static final String CONTROLLER_PATH = "/users";

    @Mock
    private UserService userService;
    @Mock
    private UserRolesService userRolesService;

    @InjectMocks
    private UserController userController;

    @Test
    public void getAdminUsersTest() {
        final UserDTO user1 = new UserDTO();
        final UserDTO user2 = new UserDTO();
        final List<UserDTO> users = Arrays.asList(user1, user2);

        given(userService.getAdminUsers()).willReturn(users);
        perform(get(CONTROLLER_PATH + "/admins")).andExpectAsJson(users);
    }

    @Test
    public void getAll_When_UserIsSuperAdmin_Expected_ListUsers() {
        UserDomainObject user = new UserDomainObject();
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        final UserDTO user1 = new UserDTO();
        final UserDTO user2 = new UserDTO();
        final List<UserDTO> users = Arrays.asList(user1, user2);

        given(userService.getAllActiveUsers()).willReturn(users);
        perform(get(CONTROLLER_PATH)).andExpectAsJson(users);
    }

    @Test
    public void getAll_When_UserIsNotSuperAdmin_Expected_FilteredListUsers() {
        Imcms.setUser(new UserDomainObject());

        final UserDTO user1 = new UserDTO();
        user1.setId(1);
        final UserDTO superAdmin = new UserDTO();
        superAdmin.setId(2);
        final List<UserDTO> usersWithSuperAdmin = Arrays.asList(user1, superAdmin);
        final List<UserDTO> usersWithoutSuperAdmin = Collections.singletonList(user1);

        given(userRolesService.getUserIdsByRole(Roles.SUPER_ADMIN.getId())).willReturn(Collections.singleton(superAdmin.getId()));

        given(userService.getAllActiveUsers()).willReturn(usersWithSuperAdmin);

        perform(get(CONTROLLER_PATH)).andExpectAsJson(usersWithoutSuperAdmin);
    }

    @Test
    void updateUser_When_UpdatedUserIsNotSuperAdmin_Expected_UpdateUser() {
        Imcms.setUser(new UserDomainObject());

        final int userId = 13;
        final UserDTO user = new UserDTO();
        user.setId(userId);
        user.setActive(false);

        perform(patch(CONTROLLER_PATH), user);

        final ArgumentCaptor<UserDTO> captor = ArgumentCaptor.forClass(UserDTO.class);
        then(userService).should().updateUser(captor.capture());

        assertEquals(user, captor.getValue());
    }

    @Test
    void updateUser_When_CurrentUserIsNotSuperAdmin_And_UpdatedUserIsSuperAdmin_Expected_Forbidden() {
        Imcms.setUser(new UserDomainObject());

        final int userId = 13;
        final UserDTO user = new UserDTO();
        user.setId(userId);
        user.setActive(false);

        given(userRolesService.getRoleIdsByUser(user.getId())).willReturn(Collections.singleton(Roles.SUPER_ADMIN.getId()));

        perform(patch(CONTROLLER_PATH), user).andExpect(status().is4xxClientError());
    }

    @Test
    void searchUsers_When_UserIsSuperAdmin_Expected_ListUsers() {
        final UserDomainObject currentUser = new UserDomainObject();
        currentUser.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(currentUser);

        final String term = "term";
        final boolean includeInactive = true;
        final Set<Integer> roleIds = new HashSet<>();
        roleIds.add(2);
        roleIds.add(42);

        final String[] stringRoleIds = roleIds.stream().map(String::valueOf).toArray(String[]::new);

        final UserDTO user1 = new UserDTO();
        final UserDTO user2 = new UserDTO();
        final List<UserDTO> users = Arrays.asList(user1, user2);

        given(userService.searchUsers(term, roleIds, includeInactive)).willReturn(users);

        final MockHttpServletRequestBuilder requestBuilder = get(CONTROLLER_PATH + "/search")
                .param("term", term)
                .param("includeInactive", "" + includeInactive)
                .param("roleIds[]", stringRoleIds);

        perform(requestBuilder).andExpectAsJson(users);
    }

    @Test
    void searchUsers_When_UserIsNotSuperAdmin_Expected_FilteredListUsers() {
        Imcms.setUser(new UserDomainObject());

        final String term = "term";
        final boolean includeInactive = true;
        final Set<Integer> roleIds = new HashSet<>();
        roleIds.add(2);
        roleIds.add(42);

        final String[] stringRoleIds = roleIds.stream().map(String::valueOf).toArray(String[]::new);

        final UserDTO user1 = new UserDTO();
        user1.setId(1);
        final UserDTO superAdmin = new UserDTO();
        superAdmin.setId(2);
        final List<UserDTO> users = Arrays.asList(user1, superAdmin);
        final List<UserDTO> superAdmins = Collections.singletonList(superAdmin);
        final List<UserDTO> withoutSuperAdmin = Collections.singletonList(user1);

        given(userService.searchUsers(term, roleIds, includeInactive)).willReturn(users);
        given(userRolesService.getUserIdsByRole(Roles.SUPER_ADMIN.getId())).willReturn(superAdmins.stream().map(UserDTO::getId).collect(Collectors.toSet()));

        final MockHttpServletRequestBuilder requestBuilder = get(CONTROLLER_PATH + "/search")
                .param("term", term)
                .param("includeInactive", "" + includeInactive)
                .param("roleIds[]", stringRoleIds);

        perform(requestBuilder).andExpectAsJson(withoutSuperAdmin);
    }

    @Override
    protected Object controllerToMock() {
        return userController;
    }
}
