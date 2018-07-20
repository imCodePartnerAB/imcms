package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.MockingControllerTest;
import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest extends MockingControllerTest {

    static final String CONTROLLER_PATH = "/users";

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void getAdminUsersTest() throws Exception {
        final UserDTO user1 = new UserDTO();
        final UserDTO user2 = new UserDTO();
        final List<UserDTO> users = Arrays.asList(user1, user2);

        given(userService.getAdminUsers()).willReturn(users);
        perform(get(CONTROLLER_PATH + "/admins")).andExpectAsJson(users);
    }

    @Test
    void getAll() throws Exception {
        final UserDTO user1 = new UserDTO();
        final UserDTO user2 = new UserDTO();
        final List<UserDTO> users = Arrays.asList(user1, user2);

        given(userService.getAllActiveUsers()).willReturn(users);
        perform(get(CONTROLLER_PATH)).andExpectAsJson(users);
    }

    @Test
    void updateUser() {
        final int userId = 13;
        final UserDTO user = new UserDTO();
        user.setId(userId);
        user.setActive(false);

        perform(patch(CONTROLLER_PATH), user);

        final ArgumentCaptor<UserDTO> captor = ArgumentCaptor.forClass(UserDTO.class);
        then(userService).should().updateUser(captor.capture());

        assertEquals(user, captor.getValue());
    }

    @Override
    protected Object controllerToMock() {
        return userController;
    }
}
