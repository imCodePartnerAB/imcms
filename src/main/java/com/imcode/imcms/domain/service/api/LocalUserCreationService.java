package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.LocalUserCreationPostValidationActionConsumer;
import com.imcode.imcms.domain.component.LocalUserValidationAndSaving;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserCreationService;
import com.imcode.imcms.domain.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 27.06.18.
 */
@Service
class LocalUserCreationService extends LocalUserValidationAndSaving implements UserCreationService {

    LocalUserCreationService(UserService userService, LocalUserCreationPostValidationActionConsumer userPostValidation) {
        super(userService, userPostValidation);
    }

    @Override
    public void createUser(UserFormData userData) throws UserValidationException {
        super.saveIfValid(userData);
    }
}
