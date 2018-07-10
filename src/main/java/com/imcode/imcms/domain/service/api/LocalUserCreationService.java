package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.LocalUserCreationPostValidationActionConsumer;
import com.imcode.imcms.domain.component.UserPostValidationActionConsumer;
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
    private final UserPostValidationActionConsumer userPostValidation;

    @Autowired
    LocalUserCreationService(UserService userService, LocalUserCreationPostValidationActionConsumer userPostValidation) {
        this.userService = userService;
        this.userPostValidation = userPostValidation;
    }

    @Override
    public void createUser(UserFormData userData) throws UserValidationException {
        userPostValidation.doIfValid(userData, userService::saveUser);
    }
}
