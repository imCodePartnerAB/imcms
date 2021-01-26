package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.UserFormData;
import com.imcode.imcms.domain.service.UserLockValidatorService;
import com.imcode.imcms.domain.service.UserService;
import imcode.server.user.UserDomainObject;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
class DefaultUserLockValidatorServiceService implements UserLockValidatorService {

    private final UserService userService;

    private final Integer amountAttempts;
    private final long timeBlocking;

    DefaultUserLockValidatorServiceService(UserService userService,
                                           @Value("${amount_attempts_loggedIn}") Integer amountAttempts,
                                           @Value("${time_blocking}") long timeBlocking) {
        this.userService = userService;
        this.amountAttempts = Integer.parseInt(StringUtils.defaultIfBlank(amountAttempts + "", "3"));
        this.timeBlocking = Long.parseLong(StringUtils.defaultIfBlank(timeBlocking + "", "1"));
    }

    @Override
    public boolean isAmountAttemptsMorePropValue(Integer userAmountAttempts) {
        return userAmountAttempts >= amountAttempts;
    }

    @Override
    public boolean isUserBlocked(UserDomainObject user) {
        final Date currentTime = new Date(System.currentTimeMillis());
        final long millisTimeBlocked = timeBlocking * 60 * 1000;

        if (user == null || null == user.getBlockedDate()) return false;

        final Calendar dateTime = Calendar.getInstance();
        dateTime.setTimeInMillis(user.getBlockedDate().getTime() + millisTimeBlocked);

        final Date timeUnBlocked = dateTime.getTime();

        return currentTime.before(timeUnBlocked);
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

    @Override
    public String getRemainingWaitTime(UserDomainObject user) {
        final Calendar dateTime = Calendar.getInstance();
        final long millisTimeBlocked = timeBlocking * 60 * 1000;

        dateTime.setTimeInMillis(user.getBlockedDate().getTime() + millisTimeBlocked);

        long timeRemained = dateTime.getTimeInMillis() - System.currentTimeMillis();

        long minutes = (timeRemained / 1000) / 60;
        long seconds = (timeRemained / 1000) % 60;

        return String.format("%d min : %d sec", minutes, seconds);
    }

    @Override
    public Integer getRemainAttemptsLoggedIn(@NonNull final UserDomainObject user) {
        int remainAttempts = amountAttempts - user.getAttempts();

        if (remainAttempts > 0) {
            return user.getAttempts();
        } else {
            return 1;// if user had exhausted attempts logged in so he will be have only 1 after unblocking
        }
    }

}