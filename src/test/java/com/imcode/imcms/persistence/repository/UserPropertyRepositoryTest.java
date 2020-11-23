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


    @BeforeEach
    public void setUp() {
        userDataInitializer.cleanRepositories();
        userPropertyDataInitializer.cleanRepositories();

        user = userDataInitializer.createData("test");
    }


    @Test
    public void getByUserId_When_UserExist_Expected_CorrectResult() {
        userPropertyDataInitializer.createData(user.getId(), "keyName", "value");

        List<UserProperty> userPropertyList = userPropertyRepository.findByUserId(user.getId());

        assertFalse(userPropertyList.isEmpty());
        assertEquals(1, userPropertyList.size());
    }

    @Test
    public void getByUserIdAndKeyName_When_UserExist_Expected_CorrectResult() {
        String keyName = "keyName";

        final UserProperty userProperty = userPropertyDataInitializer.createData(user.getId(), keyName, "value");
        UserProperty expectedUserProperty = userPropertyRepository.findByUserIdAndKeyName(user.getId(), keyName);

        assertNotNull(userProperty);
        assertEquals(expectedUserProperty, userProperty);
    }

    @Test
    public void getByUserIdAndValue_When_UserExist_Expected_CorrectResult() {
        String value = "value";

        final UserProperty expectedUserProperty1 = userPropertyDataInitializer.createData(user.getId(), "keyName", value);
        final UserProperty expectedUserProperty2 = userPropertyDataInitializer.createData(user.getId(), "keyName2", value);

        List<UserProperty> userPropertyList = userPropertyRepository.findByUserIdAndValue(user.getId(), value);

        assertFalse(userPropertyList.isEmpty());
        assertEquals(2, userPropertyList.size());
        assertEquals(expectedUserProperty1,  userPropertyList.get(0));
        assertEquals(expectedUserProperty2,  userPropertyList.get(1));
    }
}