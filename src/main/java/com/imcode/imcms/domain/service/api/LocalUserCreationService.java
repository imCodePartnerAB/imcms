package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.UserValidationResult;
import com.imcode.imcms.domain.component.UserValidator;
import com.imcode.imcms.domain.dto.UserData;
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
public class LocalUserCreationService implements UserCreationService {

    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    public LocalUserCreationService(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @Override
    public void createUser(UserData userData) throws UserValidationException {
        final UserValidationResult validate = userValidator.validate(userData);
//        userService.
    }
}
