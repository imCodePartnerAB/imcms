package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.LocalUserCreationPostValidationActionConsumer;
import com.imcode.imcms.domain.component.LocalUserCreationValidator;
import com.imcode.imcms.domain.component.UserValidationResult;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LocalUserCreationServiceTest {

    private final UserService userService = mock(UserService.class);
    private final LocalUserCreationValidator userValidator = spy(new LocalUserCreationValidator(userService));
    private final LocalUserCreationPostValidationActionConsumer validationActionConsumer = spy(
            new LocalUserCreationPostValidationActionConsumer(userValidator)
    );
    private final LocalUserCreationService localUserCreationService = spy(new LocalUserCreationService(
            userService, validationActionConsumer
    ));

    @Test
    void createUser_When_ValidationResultIsFine_Expect_UserServiceCreateUserCalled() {
        final UserFormData userData = new UserFormData();
        final UserValidationResult validationResult = mock(UserValidationResult.class);

        doReturn(true).when(validationResult).isValidUserData();
        doReturn(validationResult).when(userValidator).validate(userData);

        localUserCreationService.createUser(userData);

        then(userService).should().saveUser(userData);
    }

    @Test
    void createUser_When_ValidationResultIsNotFine_Expect_UserValidationExceptionThrown() {
        final UserFormData userData = new UserFormData();
        final UserValidationResult validationResult = mock(UserValidationResult.class);

        doReturn(false).when(validationResult).isValidUserData();
        doReturn(validationResult).when(userValidator).validate(userData);

        assertThrows(UserValidationException.class, () -> localUserCreationService.createUser(userData));

        then(userService).shouldHaveZeroInteractions();
    }

}
