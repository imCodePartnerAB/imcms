package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.06.18.
 */
@Component
public class LocalUserCreationValidator implements UserValidator {

    private final UserService userService;

    @Autowired
    public LocalUserCreationValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserValidationResult validate(UserFormData userData) {
        return new UserCreationValidationResult(userData, userService);
    }

}
