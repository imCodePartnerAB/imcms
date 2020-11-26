package com.imcode.imcms.persistence.repository;


import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.components.datainitializer.UserPropertyDataInitializer;
import com.imcode.imcms.model.UserProperty;
import com.imcode.imcms.persistence.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class UserPropertyRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private UserPropertyRepository userPropertyRepository;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private UserPropertyDataInitializer userPropertyDataInitializer;

    private User user;
    private int userId;
    private final String keyName = "keyName";
    private final String value = "value";
    private UserProperty userProperty;


    @BeforeEach
    public void setUp() {
        userDataInitializer.cleanRepositories();
        userPropertyDataInitializer.cleanRepositories();

        user = userDataInitializer.createData("test");
        userId = user.getId();
        userProperty = userPropertyDataInitializer.createData(userId, keyName, value);

    }


    @Test
    public void getByUserId_When_UserExist_Expected_CorrectResult() {
        List<UserProperty> userPropertyList = userPropertyRepository.findByUserId(user.getId());

        assertFalse(userPropertyList.isEmpty());
        assertEquals(1, userPropertyList.size());
    }

    @Test
    public void getByUserIdAndKeyName_When_UserExist_Expected_CorrectResult() {
        UserProperty expectedUserProperty = userPropertyRepository.findByUserIdAndKeyName(userId, keyName);

        assertNotNull(expectedUserProperty);
        assertEquals(userProperty.getId(), expectedUserProperty.getId());
        assertEquals(userProperty.getUserId(), expectedUserProperty.getUserId());
        assertEquals(userProperty.getKeyName(), expectedUserProperty.getKeyName());
        assertEquals(userProperty.getValue(), expectedUserProperty.getValue());
    }

    @Test
    public void getByUserIdAndValue_When_UserExist_Expected_CorrectResult() {
        List<UserProperty> userPropertyList = userPropertyRepository.findByUserIdAndValue(userId, value);

        assertFalse(userPropertyList.isEmpty());
        assertEquals(1, userPropertyList.size());
        UserProperty actualUserProperty = userPropertyList.get(0);

        assertEquals(userProperty.getId(), actualUserProperty.getId());
        assertEquals(userProperty.getUserId(), actualUserProperty.getUserId());
        assertEquals(userProperty.getKeyName(), actualUserProperty.getKeyName());
        assertEquals(userProperty.getValue(), actualUserProperty.getValue());
    }
}
