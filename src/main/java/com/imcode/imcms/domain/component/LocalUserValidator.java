package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserData;
import com.imcode.imcms.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.06.18.
 */
@Component
public class LocalUserValidator implements UserValidator {

    private final UserService userService;

    @Autowired
    public LocalUserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserValidationResult validate(UserData userData) {
        return new UserValidationResult(userData, userService);
    }

}
