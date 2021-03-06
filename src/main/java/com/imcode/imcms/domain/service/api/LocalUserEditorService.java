package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.LocalUserEditPostValidationActionConsumer;
import com.imcode.imcms.domain.component.LocalUserValidationAndSaving;
import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;
import com.imcode.imcms.domain.service.UserEditorService;
import com.imcode.imcms.domain.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.07.18.
 */
//TODO need check why we get errors with annotation transactional here !!
@Service
class LocalUserEditorService extends LocalUserValidationAndSaving implements UserEditorService {

    LocalUserEditorService(UserService userService, LocalUserEditPostValidationActionConsumer userPostValidation) {
        super(userService, userPostValidation);
    }

    @Override
    public void editUser(UserFormData userData) throws UserValidationException {
        if (StringUtils.isBlank(userData.getEmail())) {
            userData.setEmail(null);
        }

        super.saveIfValid(userData);
    }
}
