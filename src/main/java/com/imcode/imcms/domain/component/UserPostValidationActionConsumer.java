package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;

import java.util.function.Consumer;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.07.18.
 */
public abstract class UserPostValidationActionConsumer {

    private final UserValidator userValidator;

    protected UserPostValidationActionConsumer(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    void doIfValid(UserFormData userData, Consumer<UserFormData> doIfValid) throws UserValidationException {
        final UserValidationResult validationResult = userValidator.validate(userData);

        if (validationResult.isValidUserData()) {
            doIfValid.accept(userData);
            return;
        }

        throw new UserValidationException(validationResult);
    }

}
