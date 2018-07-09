package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.component.UserValidationResult;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserCreationService;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserAdministrationControllerTest extends MockingControllerTest {

    @Mock
    private UserCreationService userCreationService;

    @InjectMocks
    private UserAdministrationController controller;

    @Override
    protected String controllerPath() {
        return "/user";
    }

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

        final RequestBuilder requestBuilder = post(controllerPath() + "/create");

        perform(requestBuilder).andExpect(model().attribute("errorMessages", Matchers.hasSize(Matchers.greaterThan(0))));
    }

    @Test
    void createUser_When_UserDataIsValid_Expect_CreateUserCalledAndStatusOk() throws Exception {
        final RequestBuilder requestBuilder = post(controllerPath() + "/create");

        perform(requestBuilder).andExpect(status().is3xxRedirection());

        then(userCreationService).should().createUser(any());
    }
}
