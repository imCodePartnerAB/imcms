package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.UserService;
import imcode.server.ImcmsConstants;
import imcode.util.Utility;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Consumer;

@Data
public class UserValidationResult {

    private boolean emptyLoginName;
    private boolean loginAlreadyTaken;
    private boolean emptyPassword1;
    private boolean password1TooShort;
    private boolean password1TooLong;
    private boolean emptyPassword2;
    private boolean password2TooShort;
    private boolean password2TooLong;
    private boolean passwordsEqual;
    private boolean passwordTooWeak;
    private boolean emptyEmail;
    private boolean emailValid;
    private boolean emailAlreadyTaken;
    private boolean emptyUserRoles;

    private boolean validUserData;

    UserValidationResult(UserFormData userData, UserService userService) {
        validateLoginName(userData.getLogin(), userService);
        validatePasswords(userData);
        validateEmail(userData.getEmail(), userService);
        validateUserRoles(userData.getRoleIds());
        sumUpValidation();
    }

    private void sumUpValidation() {
        this.validUserData = !emptyLoginName
                && !loginAlreadyTaken
                && !emptyPassword1
                && !password1TooShort
                && !password1TooLong
                && !emptyPassword2
                && !password2TooShort
                && !password2TooLong
                && passwordsEqual
                && !emptyEmail
                && emailValid
                && !emailAlreadyTaken
                && !emptyUserRoles
                && !passwordTooWeak
        ;
    }

    private void validateUserRoles(int[] roleIds) {
        this.emptyUserRoles = ArrayUtils.isEmpty(roleIds);
    }

    private void validateEmail(String email, UserService userService) {
        email = StringUtils.defaultString(email);

        this.emptyEmail = StringUtils.isBlank(email);
        this.emailValid = Utility.isValidEmail(email);
        this.emailAlreadyTaken = !userService.getUsersByEmail(email).isEmpty();
    }

    private void validatePasswords(UserFormData userData) {
        final String password1 = StringUtils.defaultString(userData.getPassword());
        final String password2 = StringUtils.defaultString(userData.getPassword2());

        validatePassword1(password1);
        validatePassword2(password2);

        this.passwordsEqual = Objects.equals(password1, password2);
        this.passwordTooWeak = password1.equalsIgnoreCase(userData.getLogin());
    }

    private void validatePassword1(String password) {
        validatePassword(password, this::setEmptyPassword1, this::setPassword1TooLong, this::setPassword1TooShort);
    }

    private void validatePassword2(String password) {
        validatePassword(password, this::setEmptyPassword2, this::setPassword2TooLong, this::setPassword2TooShort);
    }

    private void validatePassword(String password,
                                  Consumer<Boolean> emptyPass,
                                  Consumer<Boolean> passTooLong,
                                  Consumer<Boolean> passTooShort) {

        emptyPass.accept(StringUtils.isBlank(password));
        passTooLong.accept(password.length() > ImcmsConstants.MAXIMUM_PASSWORD_LENGTH);
        passTooShort.accept(password.length() < ImcmsConstants.MINIMUM_PASSWORD_LENGTH);
    }

    private void validateLoginName(String loginName, UserService userService) {
        loginName = StringUtils.defaultString(loginName);

        this.emptyLoginName = StringUtils.isBlank(loginName);

        try {
            userService.getUser(loginName);
            this.loginAlreadyTaken = true;

        } catch (UserNotExistsException e) {
            this.loginAlreadyTaken = false;
        }
    }

}
