package com.imcode.imcms.domain.service;

import imcode.server.user.UserDomainObject;

public interface UserLockValidatorService {

    boolean isAmountAttemptsMorePropValue(Integer userAmountAttempts);
    boolean isUserBlocked(UserDomainObject user);
    void unlockingUserForLogin(UserDomainObject user);
    void lockUserForLogin(Integer userId);
    Integer increaseAttempts(UserDomainObject user);
    void resetAttempts(Integer userId);
    void unLockDateTimeBlocked(Integer userId);


}
