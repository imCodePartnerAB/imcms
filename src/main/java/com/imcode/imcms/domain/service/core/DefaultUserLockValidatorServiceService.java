package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.UserLockValidatorService;
import com.imcode.imcms.domain.service.UserService;
import imcode.server.user.UserDomainObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
class DefaultUserLockValidatorServiceService implements UserLockValidatorService {

    private final UserService userService;

    private final Integer amountAttempts;
    private final long timeBlocking;

    DefaultUserLockValidatorServiceService(UserService userService,
                                           @Value("${amount_attempts}") Integer amountAttempts,
                                           @Value("${time_blocking}") long timeBlocking) {
        this.userService = userService;
        this.amountAttempts = amountAttempts;
        this.timeBlocking = timeBlocking * 60 * 1000;
    }

    @Override
    public boolean isAmountAttemptsMorePropValue(Integer userAmountAttempts) {
        return userAmountAttempts >= amountAttempts;
    }

    @Override
    public boolean isUserBlocked(UserDomainObject user) {
        final long currentTimeMillis = System.currentTimeMillis();

        if (null == user.getBlockedDate()) return false;

        return currentTimeMillis - user.getBlockedDate().getTime() < timeBlocking;
    }

    @Override
    public void unlockingUserForLogin(UserDomainObject user) {
        if (user.getAttempts() == 0 && null == user.getBlockedDate()) return;

        resetAttempts(user.getId());
        unLockDateTimeBlocked(user.getId());
    }

    @Override
    public void lockUserForLogin(Integer userId) {
        final UserFormData receivedUserData = userService.getUserData(userId);

        receivedUserData.setBlockedDate(new Date(System.currentTimeMillis()));

        userService.saveUser(receivedUserData);
    }


    @Override
    public Integer increaseAttempts(UserDomainObject user) {
        final UserFormData receivedUserData = userService.getUserData(user.getId());
        receivedUserData.setAttempts(user.getAttempts() + 1);

        userService.saveUser(receivedUserData);

        return receivedUserData.getAttempts();
    }

    @Override
    public void resetAttempts(Integer userId) {
        final UserFormData receivedUserData = userService.getUserData(userId);

        receivedUserData.setAttempts(0);

        userService.saveUser(receivedUserData);
    }

    @Override
    public void unLockDateTimeBlocked(Integer userId) {
        final UserFormData receivedUserData = userService.getUserData(userId);

        receivedUserData.setBlockedDate(null);

        userService.saveUser(receivedUserData);
    }

}
