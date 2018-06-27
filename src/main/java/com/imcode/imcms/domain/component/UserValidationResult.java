package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserData;
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
    private boolean emptyPassword1;
    private boolean password1TooShort;
    private boolean password1TooLong;
    private boolean emptyPassword2;
    private boolean password2TooShort;
    private boolean password2TooLong;
    private boolean passwordsEqual;
    private boolean emptyEmail;
    private boolean emailValid;
    private boolean emailAlreadyTaken;
    private boolean emptyUserRoles;

    private boolean validUserData;

    UserValidationResult(UserData userData, UserService userService) {
        validateLoginName(userData.getLoginName());
        validatePasswords(userData);
        validateEmail(userData.getEmail(), userService);
        validateUserRoles(userData.getRoleIds());
        sumUpValidation();
    }

    private void sumUpValidation() {
        this.validUserData = !emptyLoginName
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
        ;
    }

    private void validateUserRoles(int[] roleIds) {
        this.emptyUserRoles = ArrayUtils.isEmpty(roleIds);
    }

    private void validateEmail(String email, UserService userService) {
        this.emptyEmail = StringUtils.isBlank(email);
        this.emailValid = Utility.isValidEmail(email);
        this.emailAlreadyTaken = !userService.getUsersByEmail(email).isEmpty();
    }

    private void validatePasswords(UserData userData) {
        final String password1 = userData.getPassword1();
        final String password2 = userData.getPassword2();

        validatePassword1(password1);
        validatePassword2(password2);
        this.passwordsEqual = Objects.equals(password1, password2);
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

        password = StringUtils.defaultString(password);
        emptyPass.accept(StringUtils.isBlank(password));
        passTooLong.accept(password.length() > ImcmsConstants.MAXIMUM_PASSWORD_LENGTH);
        passTooShort.accept(password.length() < ImcmsConstants.MINIMUM_PASSWORD_LENGTH);
    }

    private void validateLoginName(String loginName) {
        this.emptyLoginName = StringUtils.isBlank(loginName);
    }

}
