package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LocalUserEditValidatorTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private LocalUserEditValidator userValidator;

    @Test
    void validate_With_NullLoginName_Expect_EmptyLoginNameIsTrue() {
        final UserFormData userData = new UserFormData();

        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyLoginName());
    }

    @Test
    void validate_With_EmptyLoginName_Expect_EmptyLoginNameIsTrue() {
        final UserFormData userData = new UserFormData();
        userData.setLogin("");

        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyLoginName());
    }

    @Test
    void validate_With_NotEmptyLoginName_Expect_EmptyLoginNameIsFalse() {
        final UserFormData userData = new UserFormData();
        userData.setLogin("loginName");

        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmptyLoginName());
    }

    @Test
    void validate_With_ExistingLoginName_Expect_LoginNameTakenIsTrue() {
        final UserFormData userData = new UserFormData();
        final String existingLoginName = "existingLoginName";
        userData.setLogin(existingLoginName);

        final UserDTO user = new UserDTO();
        user.setId(42);

        when(userService.getUser(eq(existingLoginName))).thenReturn(user);

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isLoginAlreadyTaken());
    }

    @Test
    void validate_With_SameUserLoginName_Expect_LoginNameTakenIsFalse() {
        final UserFormData userData = new UserFormData();
        final String existingLoginName = "existingLoginName";
        final int userId = 42;
        userData.setId(userId);
        userData.setLogin(existingLoginName);

        final UserDTO user = new UserDTO();
        user.setId(userId);

        when(userService.getUser(eq(existingLoginName))).thenReturn(user);

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isLoginAlreadyTaken());
    }

    @Test
    void validate_With_UniqueLoginName_Expect_LoginNameTakenIsFalse() {
        final UserFormData userData = new UserFormData();
        final String existingLoginName = "existingLoginName";
        userData.setLogin(existingLoginName);

        when(userService.getUser(eq(existingLoginName))).thenThrow(new UserNotExistsException(existingLoginName));

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isLoginAlreadyTaken());
    }

    @Test
    void validate_With_NotEmptyPasswords_Expect_PasswordsValidationIsOk() {
        final UserFormData userData = new UserFormData();
        userData.setPassword("password");
	    userData.setPassword2("password");

        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);

        final UserValidationResult validationResult = userValidator.validate(userData);

	    assertAll(
			    () -> assertFalse(validationResult.isEmptyPassword1()),
			    () -> assertFalse(validationResult.isPassword1TooLong()),
			    () -> assertFalse(validationResult.isEmptyPassword2()),
			    () -> assertFalse(validationResult.isPassword2TooShort()),
			    () -> assertFalse(validationResult.isPassword2TooLong()),
			    () -> assertTrue(validationResult.isPasswordsEqual())
	    );
    }

	@Test
	void validate_With_EmptyPasswords_Expect_PasswordsValidationIsOk() {
		final UserFormData userData = new UserFormData();
		userData.setPassword("");
		userData.setPassword2("");

		given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);

		final UserValidationResult validationResult = userValidator.validate(userData);

		assertAll(
				() -> assertFalse(validationResult.isEmptyPassword1()),
				() -> assertFalse(validationResult.isEmptyPassword2()),
				() -> assertFalse(validationResult.isPassword1TooLong()),
				() -> assertFalse(validationResult.isPassword2TooLong()),
				() -> assertFalse(validationResult.isPassword1TooShort()),
				() -> assertFalse(validationResult.isPassword2TooShort()),
				() -> assertTrue(validationResult.isPasswordsEqual())
		);
	}

	@Test
	void validate_With_DifferentPasswords_Expect_PasswordsAreNotEqual() {
		final UserFormData userData = new UserFormData();
		userData.setPassword("password1");
		userData.setPassword2("password2");

		given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);

		final UserValidationResult validationResult = userValidator.validate(userData);

		assertFalse(validationResult.isPasswordsEqual());
	}

	@Test
	void validate_With_ValidEmail_Expect_EmailValidIsTrue() {
		final UserFormData userData = new UserFormData();
		userData.setEmail("test@test.com");
		given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);
		final UserValidationResult validationResult = userValidator.validate(userData);

		assertTrue(validationResult.isEmailValid());
	}

    @Test
    void validate_With_InvalidEmail_Expect_EmailValidIsFalse() {
        final UserFormData userData = new UserFormData();
        userData.setEmail("test-at-test.com");
        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmailValid());
    }

    @Test
    void validate_With_ValidEmailThatIsAlreadyTaken_Expect_EmailAlreadyTakenIsTrue() {
        final String email = "test1@test.com";
        final UserDTO user = new UserDTO();
        final int firstUserId = 42;
        final int secondUserId = 43;
        user.setId(firstUserId);

        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);
        given(userService.getUsersByEmail(eq(email))).willReturn(Collections.singletonList(user));

        final UserFormData userData = new UserFormData();
        userData.setEmail(email);
        userData.setId(secondUserId);

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmailAlreadyTaken());
    }

    @Test
    void validate_With_SameUserValidEmail_Expect_EmailAlreadyTakenIsFalse() {
        final String email = "test1@test.com";
        final UserDTO user = new UserDTO();
        final int userId = 42;
        user.setId(userId);

        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);
        given(userService.getUsersByEmail(eq(email))).willReturn(Collections.singletonList(user));

        final UserFormData userData = new UserFormData();
        userData.setEmail(email);
        userData.setId(userId);

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmailAlreadyTaken());
    }

    @Test
    void validate_With_ValidEmailThatIsNotAlreadyTaken_Expect_EmailAlreadyTakenIsFalse() {
        final String email = "test2@test.com";
        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);
        given(userService.getUsersByEmail(eq(email))).willReturn(Collections.emptyList());

        final UserFormData userData = new UserFormData();
        userData.setEmail(email);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmailAlreadyTaken());
    }

    @Test
    void validate_With_NullUserRoles_Expect_EmptyUserRolesIsTrue() {
        final UserFormData userData = new UserFormData();
        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyUserRoles());
    }

    @Test
    void validate_With_EmptyUserRoles_Expect_EmptyUserRolesIsTrue() {
        final UserFormData userData = new UserFormData();
        userData.setRoleIds(new int[0]);
        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyUserRoles());
    }

    @Test
    void validate_With_NotEmptyUserRoles_Expect_EmptyUserRolesIsFalse() {
        final UserFormData userData = new UserFormData();
        userData.setRoleIds(new int[]{1});
        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmptyUserRoles());
    }

	@Test
	void validate_With_SameLoginAndPasswordButDifferentCases_Expect_PasswordTooWeakIsTrue() {
		final UserFormData userData = new UserFormData();
		final String login = "TEst";
		final String pass = "teST";
		userData.setLogin(login);
		userData.setPassword(pass);

		given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);
		final UserValidationResult validationResult = userValidator.validate(userData);

		assertTrue(validationResult.isPasswordTooWeak());
    }

    @Test
    void validate_With_DifferentLoginAndPassword_Expect_PasswordTooWeakIsFalse() {
        final UserFormData userData = new UserFormData();
        final String login = "test-login";
        final String pass = "test-pass";
        userData.setLogin(login);
        userData.setPassword(pass);

        given(userService.getUser(anyString())).willThrow(UserNotExistsException.class);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isPasswordTooWeak());
    }
}
