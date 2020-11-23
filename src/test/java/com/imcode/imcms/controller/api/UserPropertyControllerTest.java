package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.service.UserPropertyService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserPropertyControllerTest extends MockingControllerTest {

    @Mock
    private UserPropertyService userPropertyService;

    @InjectMocks
    private UserPropertyController userPropertyController;

    @Test
    void getAll(){
//        final UserDTO user1 = new UserDTO();
//        final UserDTO user2 = new UserDTO();
//        final List<UserDTO> users = Arrays.asList(user1, user2);
//
//        given(user.getAdminUsers()).willReturn(users);
//        perform(get(CONTROLLER_PATH + "/admins")).andExpectAsJson(users);
    }

    @Override
    protected Object controllerToMock() {
        return userPropertyController;
    }
}
