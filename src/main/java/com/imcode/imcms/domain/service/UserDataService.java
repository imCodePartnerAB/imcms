package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.exception.UserNotExistsException;

public interface UserDataService {
    UserFormData getUserData(int id) throws UserNotExistsException;
}
