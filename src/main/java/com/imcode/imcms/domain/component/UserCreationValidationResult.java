package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.UserService;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.07.18.
 */
class UserCreationValidationResult extends UserValidationResult {

    UserCreationValidationResult(UserFormData userData, UserService userService) {
        super(userData, userService);
    }

    @Override
    protected void validateEmail(UserFormData userData, UserService userService) {
        final String email = userData.getEmail();

        if (email == null) {
            return;
        }

        setEmailValid(Utility.isValidEmail(email));
        setEmailAlreadyTaken(!userService.getUsersByEmail(email).isEmpty());
    }

    @Override
    protected void validatePasswords(UserFormData userData) {
        final String password1 = StringUtils.defaultString(userData.getPassword());
        final String password2 = StringUtils.defaultString(userData.getPassword2());

        validatePassword1(password1);
        validatePassword2(password2);

        setPasswordsEqual(Objects.equals(password1, password2));
        setPasswordTooWeak(password1.equalsIgnoreCase(userData.getLogin()));
    }

    @Override
    protected void validateLoginName(UserFormData userData, UserService userService) {
        final String login = StringUtils.defaultString(userData.getLogin());

        setEmptyLoginName(StringUtils.isBlank(login));

        try {
            userService.getUser(login);
            setLoginAlreadyTaken(true);

        } catch (UserNotExistsException e) {
            setLoginAlreadyTaken(false);
        }
    }

}
