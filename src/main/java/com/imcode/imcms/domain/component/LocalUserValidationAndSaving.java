package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserService;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.07.18.
 */
public abstract class LocalUserValidationAndSaving {

    private final UserService userService;
    private final UserPostValidationActionConsumer userPostValidation;

    protected LocalUserValidationAndSaving(UserService userService, UserPostValidationActionConsumer userPostValidation) {
        this.userService = userService;
        this.userPostValidation = userPostValidation;
    }

    protected void saveIfValid(UserFormData userData) throws UserValidationException {
        userPostValidation.doIfValid(userData, userService::saveUser);
    }

}
