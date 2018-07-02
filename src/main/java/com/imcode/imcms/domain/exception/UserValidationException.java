package com.imcode.imcms.domain.exception;

import com.imcode.imcms.domain.component.UserValidationResult;

/**
 * When something wrong with user fields while create/edit user
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.06.18.
 */
public class UserValidationException extends RuntimeException {
    private static final long serialVersionUID = 7799856979816003123L;

    public UserValidationException(UserValidationResult validationResult) {

    }
}
