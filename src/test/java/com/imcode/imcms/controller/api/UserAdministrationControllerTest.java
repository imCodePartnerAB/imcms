package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.component.UserValidationResult;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserCreationService;
import com.imcode.imcms.domain.service.UserEditorService;
import com.imcode.imcms.domain.service.UserService;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserAdministrationControllerTest extends MockingControllerTest {

    private static final String CONTROLLER_PATH = "/user";

    @Mock
    private UserCreationService userCreationService;

    @Mock
    private UserEditorService userEditorService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserAdministrationController controller;

    @Override
    protected Object controllerToMock() {
        return controller;
    }

    @Test
    void createUser_When_UserDataIsInvalid_Expect_ReturnedMavWithValidationResult() throws Exception {

        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        final UserValidationResult result = mock(UserValidationResult.class);

        @SuppressWarnings("ThrowableNotThrown") final UserValidationException exception = new UserValidationException(result);

        doThrow(exception).when(userCreationService).createUser(any());

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/create");

        perform(requestBuilder).andExpect(model().attribute("errorMessages", Matchers.hasSize(Matchers.greaterThan(0))));
    }

    @Test
    void createUser_When_UserDataIsValid_Expect_CreateUserCalledAndStatusOk() throws Exception {
        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/create");

        perform(requestBuilder).andExpect(status().is3xxRedirection());

        then(userCreationService).should().createUser(any());
    }

    @Test
    void editUser_When_GoingToEditPageWithExistingUserId_Expect_ThatUserIsInModel() throws Exception {
        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        final int userId = 42;
        final UserFormData mockUser = mock(UserFormData.class);
        mockUser.setId(userId);

        given(userService.getUserData(userId)).willReturn(mockUser);

        final RequestBuilder requestBuilder = get(CONTROLLER_PATH + "/edition/" + userId);
        perform(requestBuilder).andExpect(model().attribute("editedUser", Matchers.is(mockUser)));
    }

    @Test
    void editUser_When_UserDataIsInvalid_Expect_ReturnedMavWithValidationResult() throws Exception {

        final UserDomainObject user = mock(UserDomainObject.class);
        Imcms.setUser(user);

        final UserValidationResult result = mock(UserValidationResult.class);

        @SuppressWarnings("ThrowableNotThrown") final UserValidationException exception = new UserValidationException(result);

        doThrow(exception).when(userEditorService).editUser(any());

        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/edit");

        perform(requestBuilder).andExpect(model().attribute("errorMessages", Matchers.hasSize(Matchers.greaterThan(0))));
    }

    @Test
    void editUser_When_UserDataIsValid_Expect_CreateUserCalledAndStatusOk() throws Exception {
        final RequestBuilder requestBuilder = post(CONTROLLER_PATH + "/edit");

        perform(requestBuilder).andExpect(status().is3xxRedirection());

        then(userEditorService).should().editUser(any());
    }
}
