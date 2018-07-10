package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserValidationException;

/**
 * Provides possibility to edit existing users
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 10.07.18.
 */
public interface UserEditorService {

    void editUser(UserFormData userData) throws UserValidationException;

}
