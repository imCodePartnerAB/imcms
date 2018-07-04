package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserFormData;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.06.18.
 */
public interface UserValidator {

    UserValidationResult validate(UserFormData userData);

}
