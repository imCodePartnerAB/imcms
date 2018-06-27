package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.dto.UserData;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.persistence.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalUserValidatorTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private LocalUserValidator userValidator;

    @Test
    public void validate_With_NullLoginName_Expect_EmptyLoginNameIsTrue() {
        final UserData userData = new UserData();
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyLoginName());
    }

    @Test
    public void validate_With_EmptyLoginName_Expect_EmptyLoginNameIsTrue() {
        final UserData userData = new UserData();
        userData.setLoginName("");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyLoginName());
    }

    @Test
    public void validate_With_NotEmptyLoginName_Expect_EmptyLoginNameIsFalse() {
        final UserData userData = new UserData();
        userData.setLoginName("loginName");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmptyLoginName());
    }

    @Test
    public void validate_With_ExistingLoginName_Expect_LoginNameTakenIsTrue() {
        final UserData userData = new UserData();
        final String existingLoginName = "existingLoginName";
        userData.setLoginName(existingLoginName);

        when(userService.getUser(eq(existingLoginName))).thenReturn(new User());

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isLoginAlreadyTaken());
    }

    @Test
    public void validate_With_UniqueLoginName_Expect_LoginNameTakenIsFalse() {
        final UserData userData = new UserData();
        final String existingLoginName = "existingLoginName";
        userData.setLoginName(existingLoginName);

        when(userService.getUser(eq(existingLoginName))).thenThrow(new UserNotExistsException(existingLoginName));

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isLoginAlreadyTaken());
    }

    @Test
    public void validate_With_NullPassword1_Expect_EmptyPassword1IsTrue() {
        final UserData userData = new UserData();
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyPassword1());
    }

    @Test
    public void validate_With_EmptyPassword1_Expect_EmptyPassword1IsTrue() {
        final UserData userData = new UserData();
        userData.setPassword1("");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyPassword1());
    }

    @Test
    public void validate_With_NotEmptyPassword1_Expect_EmptyPassword1IsFalse() {
        final UserData userData = new UserData();
        userData.setPassword1("password");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmptyPassword1());
    }

    @Test
    public void validate_With_TooShortPassword1_Expect_Password1IsTooShortTrue() {
        final UserData userData = new UserData();
        userData.setPassword1("pas"); // minimum is 4, don't know why
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isPassword1TooShort());
    }

    @Test
    public void validate_With_TooLongPassword1_Expect_Password1IsTooLongTrue() {
        final UserData userData = new UserData();
        userData.setPassword1(
                "--------10--------20--------30--------40--------50--------60--------70--------80--------90-------100"
                        + "-------110-------120-----128" + "1"// extra one
        ); // max is 128, don't know why
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isPassword1TooLong());
    }

    @Test
    public void validate_With_ExactSizePassword1_Expect_Password1IsTooLongFalse() {
        final UserData userData = new UserData();
        userData.setPassword1(
                "--------10--------20--------30--------40--------50--------60--------70--------80--------90-------100"
                        + "-------110-------120-----128"// exact size
        ); // max is 128, don't know why
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isPassword1TooLong());
    }

    @Test
    public void validate_With_NullPassword2_Expect_EmptyPassword2IsTrue() {
        final UserData userData = new UserData();
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyPassword2());
    }

    @Test
    public void validate_With_EmptyPassword2_Expect_EmptyPassword2IsTrue() {
        final UserData userData = new UserData();
        userData.setPassword2("");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyPassword2());
    }

    @Test
    public void validate_With_NotEmptyPassword2_Expect_EmptyPassword2IsFalse() {
        final UserData userData = new UserData();
        userData.setPassword2("password");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmptyPassword2());
    }

    @Test
    public void validate_With_TooShortPassword2_Expect_Password2IsTooShortTrue() {
        final UserData userData = new UserData();
        userData.setPassword2("pas"); // minimum is 4, don't know why
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isPassword2TooShort());
    }

    @Test
    public void validate_With_TooLongPassword2_Expect_Password2IsTooLongTrue() {
        final UserData userData = new UserData();
        userData.setPassword2(
                "--------10--------20--------30--------40--------50--------60--------70--------80--------90-------100"
                        + "-------110-------120-----128" + "1"// extra one
        ); // max is 128, don't know why
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isPassword2TooLong());
    }

    @Test
    public void validate_With_ExactSizePassword2_Expect_Password2IsTooLongFalse() {
        final UserData userData = new UserData();
        userData.setPassword2(
                "--------10--------20--------30--------40--------50--------60--------70--------80--------90-------100"
                        + "-------110-------120-----128"// exact size
        ); // max is 128, don't know why
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isPassword2TooLong());
    }

    @Test
    public void validate_With_SameValidSizePasswords_Expect_PasswordsAreEqualIsTrue() {
        final UserData userData = new UserData();
        userData.setPassword1("password");
        userData.setPassword2("password");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isPasswordsEqual());
    }

    @Test
    public void validate_With_NotSameValidSizePasswords_Expect_PasswordsAreEqualIsFalse() {
        final UserData userData = new UserData();
        userData.setPassword1("password");
        userData.setPassword2("pass");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isPasswordsEqual());
    }

    @Test
    public void validate_With_NullEmail_Expect_EmptyEmailIsTrue() {
        final UserData userData = new UserData();
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyEmail());
    }

    @Test
    public void validate_With_EmptyEmail_Expect_EmptyEmailIsTrue() {
        final UserData userData = new UserData();
        userData.setEmail("");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyEmail());
    }

    @Test
    public void validate_With_NotEmptyEmail_Expect_EmptyEmailIsFalse() {
        final UserData userData = new UserData();
        userData.setEmail("test@test.com");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmptyEmail());
    }

    @Test
    public void validate_With_ValidEmail_Expect_EmailValidIsTrue() {
        final UserData userData = new UserData();
        userData.setEmail("test@test.com");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmailValid());
    }

    @Test
    public void validate_With_InvalidEmail_Expect_EmailValidIsFalse() {
        final UserData userData = new UserData();
        userData.setEmail("test-at-test.com");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmailValid());
    }

    @Test
    public void validate_With_ValidEmailThatIsAlreadyTaken_Expect_EmailAlreadyTakenIsTrue() {
        final String email = "test1@test.com";
        when(userService.getUsersByEmail(eq(email))).thenReturn(Collections.singletonList(new UserDTO()));

        final UserData userData = new UserData();
        userData.setEmail(email);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmailAlreadyTaken());
    }

    @Test
    public void validate_With_ValidEmailThatIsNotAlreadyTaken_Expect_EmailAlreadyTakenIsFalse() {
        final String email = "test2@test.com";
        when(userService.getUsersByEmail(eq(email))).thenReturn(Collections.emptyList());

        final UserData userData = new UserData();
        userData.setEmail(email);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmailAlreadyTaken());
    }

    @Test
    public void validate_With_NullUserRoles_Expect_EmptyUserRolesIsTrue() {
        final UserData userData = new UserData();
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyUserRoles());
    }

    @Test
    public void validate_With_EmptyUserRoles_Expect_EmptyUserRolesIsTrue() {
        final UserData userData = new UserData();
        userData.setRoleIds(new int[0]);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyUserRoles());
    }

    @Test
    public void validate_With_NotEmptyUserRoles_Expect_EmptyUserRolesIsFalse() {
        final UserData userData = new UserData();
        userData.setRoleIds(new int[]{1});
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmptyUserRoles());
    }

}
