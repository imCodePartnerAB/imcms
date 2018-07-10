package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.UserService;

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

    }

    @Override
    protected void validatePasswords(UserFormData userData) {

    }

    @Override
    protected void validateLoginName(UserFormData userData, UserService userService) {

    }
}
