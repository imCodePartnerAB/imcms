package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.LocalUserEditPostValidationActionConsumer;
import com.imcode.imcms.domain.component.UserPostValidationActionConsumer;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserEditorService;
import com.imcode.imcms.domain.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.07.18.
 */
@Service
class LocalUserEditorService implements UserEditorService {

    private final UserService userService;
    private final UserPostValidationActionConsumer userPostValidation;

    LocalUserEditorService(UserService userService, LocalUserEditPostValidationActionConsumer userPostValidation) {
        this.userService = userService;
        this.userPostValidation = userPostValidation;
    }

    @Override
    public void editUser(UserFormData userData) throws UserValidationException {
        userPostValidation.doIfValid(userData, userService::saveUser);
    }
}
