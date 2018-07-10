package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserDTO;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserNotExistsException;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.persistence.entity.User;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.07.18.
 */
class UserEditValidationResult extends UserValidationResult {

    UserEditValidationResult(UserFormData userData, UserService userService) {
        super(userData, userService);
    }

    @Override
    protected void validateEmail(UserFormData userData, UserService userService) {
        final String email = StringUtils.defaultString(userData.getEmail());

        setEmptyEmail(StringUtils.isBlank(email));

        if (isEmptyEmail()) return;

        setEmailValid(Utility.isValidEmail(email));

        final List<UserDTO> usersByEmail = userService.getUsersByEmail(email);

        if (usersByEmail.isEmpty()) return;
        if ((usersByEmail.size() == 1) && usersByEmail.get(0).getId().equals(userData.getId())) return;

        setEmailAlreadyTaken(true);
    }

    @Override
    protected void validatePasswords(UserFormData userData) {
        setPasswordsEqual(true);
    }

    @Override
    protected void validateLoginName(UserFormData userData, UserService userService) {
        final String login = StringUtils.defaultString(userData.getLogin());

        setEmptyLoginName(StringUtils.isBlank(login));

        try {
            final User user = userService.getUser(login);
            setLoginAlreadyTaken(!user.getId().equals(userData.getId()));

        } catch (UserNotExistsException e) {
            setLoginAlreadyTaken(false);
        }
    }
}
