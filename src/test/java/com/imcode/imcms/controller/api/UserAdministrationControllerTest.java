package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.component.UserLockValidator;
import com.imcode.imcms.domain.component.UserValidationResult;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.Roles;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Collections;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserAdministrationControllerTest extends MockingControllerTest {

    private static final String CONTROLLER_PATH = "/user";

    @Mock
    private UserCreationService userCreationService;
    @Mock
    private UserEditorService userEditorService;
    @Mock
    private UserService userService;
    @Mock
    private UserRolesService userRolesService;
    @Mock
    private UserLockValidator userLockValidator;
    @Mock
    private LanguageService languageService;

    @InjectMocks
    private UserAdministrationController controller;

    final int userId = 1;
    final Role role = new RoleDTO(10, "test", null);

    @Override
    protected Object controllerToMock() {
        return controller;
    }

    @Test
    void createUser_When_CurrentUserIsNotSuperAdmin_And_AddSuperAdminRole_Expect_Forbidden(){
        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/create")
                .param("roleIds", Roles.SUPER_ADMIN.getId()+"");
        perform(requestBuilder).andExpect(status().is4xxClientError());
    }

    @Test
    void createUser_When_UserDataIsInvalid_Expect_ReturnedMavWithValidationResult() {
        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        final UserValidationException exception = new UserValidationException(mock(UserValidationResult.class));

        doThrow(exception).when(userCreationService).createUser(any());

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/create")
                .param("id", userId+"")
                .param("password", "password")
                .param("password2", "password");

        perform(requestBuilder).andExpect(model().attribute("errorMessages", Matchers.hasSize(Matchers.greaterThan(0))));
    }

    @Test
    void createUser_When_UserDataIsValid_Expect_CreateUserCalledAndStatusOk() {
        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/create")
                .param("id", userId+"")
                .param("password", "password")
                .param("password2", "password");

        perform(requestBuilder).andExpect(status().is3xxRedirection());

        then(userCreationService).should().createUser(any());
    }

    @Test
    void editUser_When_GoingToEditPageWithExistingUserId_And_CurrentUserIsSuperAdmin_Expect_ThatUserIsInModel() {
        final UserDomainObject user = mock(UserDomainObject.class);
        when(user.isSuperAdmin()).thenReturn(true);
        Imcms.setUser(user);

        final UserFormData mockUser = mock(UserFormData.class);
        mockUser.setId(userId);

        given(userService.getUserData(userId)).willReturn(mockUser);

        final RequestBuilder requestBuilder = get(CONTROLLER_PATH + "/edition/" + userId);
        perform(requestBuilder).andExpect(model().attribute("editedUser", Matchers.is(mockUser)));
    }

    @Test
    void editUser_When_GoingToEditPageWithExistingSuperAdminUserId_And_CurrentUserIsNotSuperAdmin_Expect_Forbidden() {
        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        final UserFormData editedUser = new UserFormData();
        editedUser.setId(userId);
        editedUser.setRoleIds(new int[]{Roles.SUPER_ADMIN.getId()});

        given(userService.getUserData(userId)).willReturn(editedUser);

        final RequestBuilder requestBuilder = get(CONTROLLER_PATH + "/edition/" + userId);
        perform(requestBuilder).andExpect(status().is4xxClientError());
    }

    @Test
    void editUser_When_CurrentUserIsNotSuperAdmin_And_AddSuperAdminRole_Expect_Forbidden(){
        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        given(userRolesService.getRoleIdsByUser(userId)).willReturn(Collections.singleton(role.getId()));

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/edit")
                .param("id", userId+"")
                .param("roleIds", Roles.SUPER_ADMIN.getId()+"")
                .param("roleIds", role.getId()+"");
        perform(requestBuilder).andExpect(status().is4xxClientError());
    }

    @Test
    void editUser_When_CurrentUserIsNotSuperAdmin_And_RemoveSuperAdminRole_Expect_Forbidden(){
        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        final Role superAdminRole = new RoleDTO();
        superAdminRole.setId(Roles.SUPER_ADMIN.getId());

        given(userRolesService.getRoleIdsByUser(userId)).willReturn(Collections.singleton(superAdminRole.getId()));

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/edit")
                .param("id", userId+"")
                .param("roleIds", role.getId()+"");
        perform(requestBuilder).andExpect(status().is4xxClientError());
    }

    @Test
    void editUser_When_UserDataIsInvalid_Expect_ReturnedMavWithValidationResult() {
        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        final UserValidationResult result = mock(UserValidationResult.class);

        final UserValidationException exception = new UserValidationException(result);

        doThrow(exception).when(userEditorService).editUser(any());

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/edit")
                .param("id", userId+"")
                .param("password", "password")
                .param("password2", "password");

        perform(requestBuilder).andExpect(model().attribute("errorMessages", Matchers.hasSize(Matchers.greaterThan(0))));
    }

    @Test
    void editUser_When_UserDataIsValid_Expect_CreateUserCalledAndStatusOk() {
        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/edit")
                .param("id", userId+"")
                .param("password", "password")
                .param("password2", "password");

        perform(requestBuilder).andExpect(status().is3xxRedirection());

        then(userEditorService).should().editUser(any());
    }
}
