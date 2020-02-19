package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.UserService;
import imcode.server.ImcmsConstants;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

@Data
public abstract class UserValidationResult {

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
    private boolean emailValid = true;
    private boolean emailAlreadyTaken;
    private boolean emptyUserRoles;

    private boolean validUserData;

    protected UserValidationResult(UserFormData userData, UserService userService) {
        validateLoginName(userData, userService);
        validatePasswords(userData);
        validateEmail(userData, userService);
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
                && emailValid
                && !emailAlreadyTaken
                && !emptyUserRoles
                && !passwordTooWeak
        ;
    }

    private void validateUserRoles(int[] roleIds) {
        this.emptyUserRoles = ArrayUtils.isEmpty(roleIds);
    }

    protected abstract void validateEmail(UserFormData userData, UserService userService);

    protected abstract void validatePasswords(UserFormData userData);

    void validatePassword1(String password) {
        validatePassword(password, this::setEmptyPassword1, this::setPassword1TooLong, this::setPassword1TooShort);
    }

    void validatePassword2(String password) {
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

    protected abstract void validateLoginName(UserFormData userData, UserService userService);

}
