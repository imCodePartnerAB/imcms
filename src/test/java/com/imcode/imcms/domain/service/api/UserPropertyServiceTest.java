package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.exception.DataIsNotValidException;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.components.datainitializer.UserPropertyDataInitializer;
import com.imcode.imcms.domain.dto.UserPropertyDTO;
import com.imcode.imcms.domain.service.UserPropertyService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.UserProperty;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.UserPropertyRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class UserPropertyServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private UserPropertyService userPropertyService;

    @Autowired
    UserPropertyRepository userPropertyRepository;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private UserPropertyDataInitializer userPropertyDataInitializer;

    private User user;
    private int userId;
    private final String keyName = "keyName";
    private final String value = "value";
    private UserPropertyDTO userProperty;

    @BeforeEach
    public void setUp() {
        userDataInitializer.cleanRepositories();
        userPropertyDataInitializer.cleanRepositories();

        user = userDataInitializer.createData("test");
        userId = user.getId();
        userProperty = new UserPropertyDTO(userPropertyDataInitializer.createData(userId, keyName, value));

        final UserDomainObject userSuperAdmin = new UserDomainObject(1);
        userSuperAdmin.setLanguageIso639_2("eng");
        userSuperAdmin.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(userSuperAdmin);
    }

    @Test
    public void getAll_When_UserPropertyExist_Expected_CorrectResult() {
        assertNotNull(userProperty);
        final UserProperty userProperty2 = userPropertyDataInitializer.createData(userId, "keyName2", "value");

        List<UserProperty> expectedUserPropertyList = userPropertyService.getAll();

        assertFalse(expectedUserPropertyList.isEmpty());
        assertEquals(userProperty, expectedUserPropertyList.get(0));
        assertEquals(userProperty2, expectedUserPropertyList.get(1));
    }

    @Test
    public void getAll_When_UserPropertyNotExist_Expected_EmptyResult() {
        userPropertyService.deleteById(userProperty.getId());
        List<UserProperty> expectedUserPropertyList = userPropertyService.getAll();
        assertTrue(expectedUserPropertyList.isEmpty());
    }

    @Test
    public void getByUserId_When_UserPropertyExist_Expected_CorrectResult() {
        assertNotNull(userProperty);

        List<UserProperty> expectedUserPropertyList = userPropertyService.getByUserId(userId);

        assertFalse(expectedUserPropertyList.isEmpty());
        assertEquals(1, expectedUserPropertyList.size());

        UserProperty actualUserProperty = expectedUserPropertyList.get(0);
        assertEquals(userProperty.getId(), actualUserProperty.getId());
        assertEquals(userProperty.getUserId(), actualUserProperty.getUserId());
        assertEquals(userProperty.getKeyName(), actualUserProperty.getKeyName());
        assertEquals(userProperty.getValue(), actualUserProperty.getValue());

    }

    @Test
    public void getByUserId_When_UserPropertyNotExist_Expected_EmptyResult() {
        userPropertyService.deleteById(userProperty.getId());
        List<UserProperty> expectedUserPropertyList = userPropertyService.getByUserId(userId);
        assertTrue(expectedUserPropertyList.isEmpty());
    }

    @Test
    public void getByUserIdAndKeyName_When_UserPropertyExist_Expected_CorrectData() {
        assertNotNull(userProperty);

        UserProperty expectedUserProperty = userPropertyService.getByUserIdAndKeyName(userId, keyName);
        assertNotNull(expectedUserProperty);
        assertEquals(userProperty, expectedUserProperty);
    }

    @Test
    public void getByUserIdAndKeyName_When_UserPropertyNotExist_Expected_EmptyData() {
        int nonExistentId = 1000;
        assertThrows(EmptyResultDataAccessException.class, () -> userPropertyService.getByUserIdAndKeyName(nonExistentId, keyName));
    }

    @Test
    public void getByUserIdAndValue_When_userPropertyNotExist_Expected_CorrectResult() {
        int nonExistentId = 1000;
        assertThrows(EmptyResultDataAccessException.class, () -> userPropertyService.getByUserIdAndKeyName(nonExistentId, value));
    }

    @Test
    public void create_When_UserPropertyIsCorrect_Expected_CorrectUserProperty() {
        userPropertyDataInitializer.cleanRepositories();

        final UserPropertyDTO userProperty = new UserPropertyDTO(null, userId, keyName, value);

        userPropertyService.create(userProperty);

        final List<UserProperty> expectedUserProperties = userPropertyService.getAll();

        assertNotNull(expectedUserProperties);
        assertFalse(expectedUserProperties.isEmpty());

        UserProperty expectedUserProperty = expectedUserProperties.get(0);

        assertEquals(userProperty.getUserId(), expectedUserProperty.getUserId());
        assertEquals(userProperty.getKeyName(), expectedUserProperty.getKeyName());
        assertEquals(userProperty.getValue(), expectedUserProperty.getValue());
    }

    @Test
    public void create_When_KeyNameOrValueIsEmpty_Expected_CorrectException() {
        UserPropertyDTO nonExistentUserProperty = new UserPropertyDTO(1000, 1000, "", "");
        assertThrows(DataIsNotValidException.class, () -> userPropertyService.create(nonExistentUserProperty));
    }

    @Test
    public void update_When_UserPropertyExist_Expected_CorrectData() {
        assertNotNull(userProperty);

        userProperty.setKeyName("keyName2");
        UserProperty expectedUserProperty = userPropertyService.update(userProperty);
        assertNotNull(expectedUserProperty);
        assertEquals(userProperty, expectedUserProperty);

        userProperty.setValue("value2");
        expectedUserProperty = userPropertyService.update(userProperty);
        assertNotNull(expectedUserProperty);
        assertEquals(userProperty, expectedUserProperty);

        userProperty.setKeyName("keyName3");
        userProperty.setValue("value3");
        expectedUserProperty = userPropertyService.update(userProperty);
        assertNotNull(expectedUserProperty);
        assertEquals(userProperty, expectedUserProperty);
    }

    @Test
    public void update_When_UserPropertyNotExist_Expected_CorrectException() {
        assertNotNull(userProperty);

        UserProperty nonExistentUserProperty = new UserPropertyDTO(1000, 1000, keyName,  value);
        assertThrows(EntityNotFoundException.class, () -> userPropertyService.update(nonExistentUserProperty));
    }

    @Test
    public void update_When_KeyNameOrValueIsEmpty_Expected_CorrectException() {
        assertNotNull(userProperty);

        UserProperty userPropertyWithEmptyValue = userProperty;
        userPropertyWithEmptyValue.setValue("");
        assertThrows(DataIsNotValidException.class, () -> userPropertyService.update(userPropertyWithEmptyValue));
    }

    @Test
    public void updateAll_When_UserPropertyExist_Expected_CorrectData(){
        final String editedKeyName = "keyName3";

        final UserPropertyDTO deleteUserPropertyDTO = userProperty;
        final UserPropertyDTO editUserPropertyDTO = new UserPropertyDTO(userPropertyDataInitializer.createData(userId, "keyName2", value));
        editUserPropertyDTO.setKeyName(editedKeyName);
        editUserPropertyDTO.setValue("value2");
        final UserPropertyDTO createUserPropertyDTO = new UserPropertyDTO(null, userId, keyName, value);

        userPropertyService.update(Collections.singletonList(deleteUserPropertyDTO), Collections.singletonList(editUserPropertyDTO), Collections.singletonList(createUserPropertyDTO));

        final List<UserProperty> userProperties = userPropertyService.getByUserId(userId);
        assertEquals(2, userProperties.size());
        final UserPropertyDTO receivedEditUserPropertyDTO = new UserPropertyDTO(userProperties.stream()
                .filter(userProperty -> editedKeyName.equals(userProperty.getKeyName()))
                .findAny().get());
        final UserPropertyDTO receivedCreateUserPropertyDTO = new UserPropertyDTO(userProperties.stream()
                .filter(userProperty -> keyName.equals(userProperty.getKeyName()))
                .peek(userProperty -> userProperty.setId(null))
                .findAny().get());
        assertEquals(editUserPropertyDTO, receivedEditUserPropertyDTO);
        assertEquals(createUserPropertyDTO, receivedCreateUserPropertyDTO);
    }

    @Test
    public void updateAll_When_KeyNameOrValueIsEmpty_Expected_CorrectException(){
        final UserPropertyDTO deleteUserPropertyDTO = userProperty;
        final UserPropertyDTO editUserPropertyDTO = new UserPropertyDTO(userPropertyDataInitializer.createData(userId, "keyName2", value));
        editUserPropertyDTO.setKeyName("");
        editUserPropertyDTO.setValue("");
        final UserPropertyDTO createUserPropertyDTO = new UserPropertyDTO(null, userId, keyName, value);

        assertThrows(DataIsNotValidException.class, () ->
            userPropertyService.update(Collections.singletonList(deleteUserPropertyDTO), Collections.singletonList(editUserPropertyDTO), Collections.singletonList(createUserPropertyDTO)));
    }

    @Test
    public void deleteById_When_UserPropertyExist_Expected_EmptyResult() {
        userPropertyService.deleteById(userProperty.getId());
        assertTrue(userPropertyService.getAll().isEmpty());
    }

    @Test
    public void delete_When_UserPropertyExist_Expected_EmptyResult() {
        userPropertyService.delete(userProperty);
        assertTrue(userPropertyService.getAll().isEmpty());
    }

    @Test
    public void delete_When_UserPropertyNotExist_Expected_CorrectException() {
        final UserPropertyDTO nonExistenceUserProperty = new UserPropertyDTO();
        nonExistenceUserProperty.setId(10000);
        nonExistenceUserProperty.setUserId(user.getId());
        nonExistenceUserProperty.setKeyName("key");
        nonExistenceUserProperty.setValue("value");

        assertThrows(EmptyResultDataAccessException.class, ()-> userPropertyService.delete(nonExistenceUserProperty));
    }
}