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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalUserCreationValidatorTest {

    @Mock
    private UserService userService;

    private final String MOBILE_PHONE_NUMBER_SWE_VALID = "+46160369766";
    private final String MOBILE_PHONE_NUMBER_SWE_INVALID = "46160369766";

    @InjectMocks
    private LocalUserCreationValidator userValidator;

    @Test
    void validate_With_NullLoginName_Expect_EmptyLoginNameIsTrue() {
        final UserFormData userData = new UserFormData();
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyLoginName());
    }

    @Test
    void validate_With_EmptyLoginName_Expect_EmptyLoginNameIsTrue() {
        final UserFormData userData = new UserFormData();
        userData.setLogin("");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyLoginName());
    }

    @Test
    void validate_With_NotEmptyLoginName_Expect_EmptyLoginNameIsFalse() {
        final UserFormData userData = new UserFormData();
        userData.setLogin("loginName");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmptyLoginName());
    }

    @Test
    void validate_With_ExistingLoginName_Expect_LoginNameTakenIsTrue() {
        final UserFormData userData = new UserFormData();
        final String existingLoginName = "existingLoginName";
        userData.setLogin(existingLoginName);

        when(userService.getUser(eq(existingLoginName))).thenReturn(new UserDTO());

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isLoginAlreadyTaken());
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
    void validate_With_NullPassword1_Expect_EmptyPassword1IsTrue() {
        final UserFormData userData = new UserFormData();
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyPassword1());
    }

    @Test
    void validate_With_EmptyPassword1_Expect_EmptyPassword1IsTrue() {
        final UserFormData userData = new UserFormData();
        userData.setPassword("");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyPassword1());
    }

    @Test
    void validate_With_NotEmptyPassword1_Expect_EmptyPassword1IsFalse() {
        final UserFormData userData = new UserFormData();
        userData.setPassword("password");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmptyPassword1());
    }

    @Test
    void validate_With_TooShortPassword1_Expect_Password1IsTooShortTrue() {
        final UserFormData userData = new UserFormData();
        userData.setPassword("pas"); // minimum is 4, don't know why
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isPassword1TooShort());
    }

    @Test
    void validate_With_TooLongPassword1_Expect_Password1IsTooLongTrue() {
        final UserFormData userData = new UserFormData();
        userData.setPassword(
                "--------10--------20--------30--------40--------50--------60--------70--------80--------90-------100"
                        + "-------110-------120-------130-------140-------150-------160-------170-------180-------190"
                + "-------200-------210-------220-------230-------240-------250" + "1"
        );
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isPassword1TooLong());
    }

    @Test
    void validate_With_ExactSizePassword1_Expect_Password1IsTooLongFalse() {
        final UserFormData userData = new UserFormData();
        userData.setPassword(
                "--------10--------20--------30--------40--------50--------60--------70--------80--------90-------100"
                        + "-------110-------120-----128"// exact size
        ); // max is 128, don't know why
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isPassword1TooLong());
    }

    @Test
    void validate_With_NullPassword2_Expect_EmptyPassword2IsTrue() {
        final UserFormData userData = new UserFormData();
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyPassword2());
    }

    @Test
    void validate_With_EmptyPassword2_Expect_EmptyPassword2IsTrue() {
        final UserFormData userData = new UserFormData();
        userData.setPassword2("");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyPassword2());
    }

    @Test
    void validate_With_NotEmptyPassword2_Expect_EmptyPassword2IsFalse() {
        final UserFormData userData = new UserFormData();
        userData.setPassword2("password");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmptyPassword2());
    }

    @Test
    void validate_With_TooShortPassword2_Expect_Password2IsTooShortTrue() {
        final UserFormData userData = new UserFormData();
        userData.setPassword2("pas"); // minimum is 4, don't know why
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isPassword2TooShort());
    }

    @Test
    void validate_With_TooLongPassword2_Expect_Password2IsTooLongTrue() {
        final UserFormData userData = new UserFormData();
        userData.setPassword2(
                "--------10--------20--------30--------40--------50--------60--------70--------80--------90-------100"
                        + "-------110-------120-------130-------140-------150-------160-------170-------180-------190"
                        + "-------200-------210-------220-------230-------240-------250" + "1"
        );
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isPassword2TooLong());
    }

    @Test
    void validate_With_ExactSizePassword2_Expect_Password2IsTooLongFalse() {
        final UserFormData userData = new UserFormData();
        userData.setPassword2(
                "--------10--------20--------30--------40--------50--------60--------70--------80--------90-------100"
                        + "-------110-------120-----128"// exact size
        ); // max is 128, don't know why
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isPassword2TooLong());
    }

    @Test
    void validate_With_SameValidSizePasswords_Expect_PasswordsAreEqualIsTrue() {
        final UserFormData userData = new UserFormData();
        userData.setPassword("password");
        userData.setPassword2("password");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isPasswordsEqual());
    }

    @Test
    void validate_With_NotSameValidSizePasswords_Expect_PasswordsAreEqualIsFalse() {
        final UserFormData userData = new UserFormData();
        userData.setPassword("password");
        userData.setPassword2("pass");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isPasswordsEqual());
    }

    @Test
    void validate_With_ValidEmail_Expect_EmailValidIsTrue() {
        final UserFormData userData = new UserFormData();
        userData.setEmail("test@test.com");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmailValid());
    }

    @Test
    void validate_With_InvalidEmail_Expect_EmailValidIsFalse() {
        final UserFormData userData = new UserFormData();
        userData.setEmail("test-at-test.com");
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmailValid());
    }

    @Test
    void validate_With_ValidEmailThatIsAlreadyTaken_Expect_EmailAlreadyTakenIsTrue() {
        final String email = "test1@test.com";
        when(userService.getUsersByEmail(eq(email))).thenReturn(Collections.singletonList(new UserDTO()));

        final UserFormData userData = new UserFormData();
        userData.setEmail(email);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmailAlreadyTaken());
    }

    @Test
    void validate_With_ValidEmailThatIsNotAlreadyTaken_Expect_EmailAlreadyTakenIsFalse() {
        final String email = "test2@test.com";
        when(userService.getUsersByEmail(eq(email))).thenReturn(Collections.emptyList());

        final UserFormData userData = new UserFormData();
        userData.setEmail(email);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isEmailAlreadyTaken());
    }

    @Test
    void validate_With_NullUserRoles_Expect_EmptyUserRolesIsTrue() {
        final UserFormData userData = new UserFormData();
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyUserRoles());
    }

    @Test
    void validate_With_EmptyUserRoles_Expect_EmptyUserRolesIsTrue() {
        final UserFormData userData = new UserFormData();
        userData.setRoleIds(new int[0]);
        final UserValidationResult validationResult = userValidator.validate(userData);

        assertTrue(validationResult.isEmptyUserRoles());
    }

    @Test
    void validate_With_NotEmptyUserRoles_Expect_EmptyUserRolesIsFalse() {
        final UserFormData userData = new UserFormData();
        userData.setRoleIds(new int[]{1});
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

        final UserValidationResult validationResult = userValidator.validate(userData);

        assertFalse(validationResult.isPasswordTooWeak());
    }

    @Test
    void validate_With_Enabled2FA_And_EmptyMobilePhoneNumber_Expect_MobilePhoneNumberIsMissingTrue(){
        final UserFormData userFormData = new UserFormData();
        userFormData.setTwoFactoryAuthenticationEnabled(true);
        userFormData.setUserPhoneNumber(new String[]{});
        userFormData.setUserPhoneNumberType(new Integer[]{});

        final UserValidationResult validationResult = userValidator.validate(userFormData);

        assertTrue(validationResult.isMobilePhoneNumberMissing());
    }

    @Test
    void validate_With_Enabled2FA_And_InvalidMobilePhoneNumber_Expect_MobilePhoneNumbersIsValidFalse(){
        final UserFormData userFormData = new UserFormData();
        userFormData.setTwoFactoryAuthenticationEnabled(true);
        userFormData.setUserPhoneNumber(new String[]{MOBILE_PHONE_NUMBER_SWE_INVALID});
        userFormData.setUserPhoneNumberType(new Integer[]{3});

        final UserValidationResult validationResult = userValidator.validate(userFormData);

        assertFalse(validationResult.isMobilePhoneNumbersValid());
    }

    @Test
    void validate_With_Enabled2FA_And_OneMobilePhoneNumberValid_And_OtherMobilePhoneNumberInvalid_Expect_MobilePhoneNumbersIsValidFalse(){
        final UserFormData userFormData = new UserFormData();
        userFormData.setTwoFactoryAuthenticationEnabled(true);
        userFormData.setUserPhoneNumber(new String[]{MOBILE_PHONE_NUMBER_SWE_VALID, MOBILE_PHONE_NUMBER_SWE_INVALID});
        userFormData.setUserPhoneNumberType(new Integer[]{3, 3});

        final UserValidationResult validationResult = userValidator.validate(userFormData);

        assertFalse(validationResult.isMobilePhoneNumbersValid());
    }
}
