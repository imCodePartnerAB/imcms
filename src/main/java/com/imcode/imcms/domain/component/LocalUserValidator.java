package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.UserData;
import org.springframework.stereotype.Component;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.06.18.
 */
@Component
public class LocalUserValidator implements UserValidator {

    @Override
    public UserValidationResult validate(UserData userData) {
        return new UserValidationResult();
    }

}
