package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.07.18.
 */
@Component
public class LocalUserEditValidator implements UserValidator {

    private final UserService userService;

    @Autowired
    public LocalUserEditValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserValidationResult validate(UserFormData userData) {
        return new UserEditValidationResult(userData, userService);
    }

}

