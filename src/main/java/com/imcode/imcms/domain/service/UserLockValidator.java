package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.UserFormData;
import imcode.server.user.UserDomainObject;

public interface UserLockValidator {

    boolean isAmountAttemptsMorePropValue(Integer userAmountAttempts);
    boolean isUserBlocked(UserDomainObject user);
    boolean isUserBlocked(UserFormData user);
    void unlockingUserForLogin(UserDomainObject user);
    void lockUserForLogin(Integer userId);
    Integer increaseAttempts(UserDomainObject user);
    void resetAttempts(Integer userId);
    void unLockDateTimeBlocked(Integer userId);
    String getRemainingWaitTime(UserDomainObject user);
    Integer getRemainAttemptsLoggedIn(UserDomainObject user);

}
