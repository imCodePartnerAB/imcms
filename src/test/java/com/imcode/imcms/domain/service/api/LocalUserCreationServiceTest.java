package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.UserValidationResult;
import com.imcode.imcms.domain.component.UserValidator;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LocalUserCreationServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private LocalUserCreationService localUserCreationService;

    @Test
    void createUser_When_ValidationResultIsFine_Expect_UserServiceCreateUserCalled() {
        final UserFormData userData = new UserFormData();
        final UserValidationResult validationResult = mock(UserValidationResult.class);

        given(validationResult.isValidUserData()).willReturn(true);
        given(userValidator.validate(userData)).willReturn(validationResult);

        localUserCreationService.createUser(userData);

        then(userService).should().createUser(userData);
    }

    @Test
    void createUser_When_ValidationResultIsNotFine_Expect_UserValidationExceptionThrown() {
        final UserFormData userData = new UserFormData();
        final UserValidationResult validationResult = mock(UserValidationResult.class);

        given(validationResult.isValidUserData()).willReturn(false);
        given(userValidator.validate(userData)).willReturn(validationResult);

        assertThrows(UserValidationException.class, () -> localUserCreationService.createUser(userData));

        then(userService).shouldHaveZeroInteractions();
    }

}