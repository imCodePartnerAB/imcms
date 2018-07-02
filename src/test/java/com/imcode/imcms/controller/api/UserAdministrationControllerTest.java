package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.service.UserCreationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class UserAdministrationControllerTest extends MockingControllerTest {

    @Mock
    private UserCreationService userCreationService;

    @InjectMocks
    private UserAdministrationController controller;

    @Test
    void createUser() {
    }

    @Override
    protected String controllerPath() {
        return "/user";
    }

    @Override
    protected Object controllerToMock() {
        return controller;
    }
}