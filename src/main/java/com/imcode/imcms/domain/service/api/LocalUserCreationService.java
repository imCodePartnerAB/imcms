package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.UserValidationResult;
import com.imcode.imcms.domain.component.UserValidator;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserCreationService;
import com.imcode.imcms.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.06.18.
 */
@Service
class LocalUserCreationService implements UserCreationService {

    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    LocalUserCreationService(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @Override
    public void createUser(UserFormData userData) throws UserValidationException {
        final UserValidationResult validationResult = userValidator.validate(userData);

        if (validationResult.isValidUserData()) {
            userService.createUser(userData);
            return;
        }

        throw new UserValidationException(validationResult);
    }
}
