package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.exception.DataIsNotValidException;
import com.imcode.imcms.components.datainitializer.UserDataInitializer;
import com.imcode.imcms.components.datainitializer.UserPropertyDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.UserPropertyDTO;
import com.imcode.imcms.domain.service.UserPropertyService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.UserProperty;
import com.imcode.imcms.persistence.entity.User;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class UserPropertyControllerTest extends AbstractControllerTest {

    @Autowired
    private UserPropertyService userPropertyService;

    @Autowired
    private UserDataInitializer userDataInitializer;

    @Autowired
    private UserPropertyDataInitializer userPropertyDataInitializer;

    private User user;
    private int userId;
    private final String keyName = "keyName";
    private final String value = "value";

    @BeforeEach
    public void setUp() {
        userDataInitializer.cleanRepositories();
        userPropertyDataInitializer.cleanRepositories();

        user = userDataInitializer.createData("test");
        userId = user.getId();

        final UserDomainObject userSuperAdmin = new UserDomainObject(1);
        userSuperAdmin.setLanguageIso639_2("eng");
        userSuperAdmin.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(userSuperAdmin);
    }

    @Override
    protected String controllerPath() {
        return "/user/properties";
    }

    @Test
    public void getAll_When_UserPropertyExists_Expected_OkAndCorrectResult() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());
        userPropertyDataInitializer.createData(userId, keyName, value);
        assertFalse(userPropertyService.getAll().isEmpty());

        final List<UserProperty> userPropertyList = userPropertyService.getAll();

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(userPropertyList.toArray()));

    }

    @Test
    public void getAll_When_UserPropertyNotExists_Expected_OkAndEmptyResult() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }

    @Test
    public void getById_When_UserPropertyExist_Expected_OkAndCorrectResult() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());
        userPropertyDataInitializer.createData(userId, keyName, value);
        assertFalse(userPropertyService.getAll().isEmpty());

        final List<UserProperty> userPropertyList = userPropertyService.getByUserId(userId);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/" + userId);
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(userPropertyList));
    }

    @Test
    public void getById_When_UserPropertyNotExist_Expected_OkAndEmptyResult() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());
        final List<UserProperty> userPropertyList = userPropertyService.getByUserId(userId);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/" + userId);
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(userPropertyList));
    }

    @Test
    public void getByUserIdAndKeyName_When_UserPropertyExist_Expected_OkAndCorrectResult() throws Exception {
        userPropertyDataInitializer.createData(userId, keyName, value);
        assertFalse(userPropertyService.getAll().isEmpty());

        final UserProperty userProperty = userPropertyService.getByUserIdAndKeyName(userId, keyName);

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/name").param("id", userId+"").param("keyName", keyName);
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(userProperty));
    }

    @Test
    public void getByIdAndKeyName_When_UserPropertyNotExist_Expected_OkAndEmptyResult() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/name").param("id", userId+"").param("keyName", keyName);
        performRequestBuilderExpectException(EmptyResultDataAccessException.class, requestBuilder);
    }

    @Test
    public void createEntity_When_UserPropertyCorrect_Expected_OkAndCreatedCorrectData() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());

        final UserPropertyDTO userProperty = new UserPropertyDTO(userPropertyDataInitializer.createData(null, userId, keyName, value));
        performPostWithContentExpectOk(userProperty);

        final List<UserProperty> userPropertyList = userPropertyService.getAll();
        assertEquals(1, userPropertyList.size());

        final UserProperty expectedUserProperty = userPropertyList.get(0);
        assertEquals(userProperty.getUserId(), expectedUserProperty.getUserId());
        assertEquals(userProperty.getKeyName(), expectedUserProperty.getKeyName());
        assertEquals(userProperty.getValue(), expectedUserProperty.getValue());
    }

    @Test
    public void createEntity_When_KeyNameOrValueIsEmpty_Expected_CorrectException() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());

        final UserPropertyDTO userPropertyDTO = new UserPropertyDTO(userPropertyDataInitializer.createData(null, userId, "", ""));
        performPostWithContentExpectException(userPropertyDTO, DataIsNotValidException.class);
    }

    @Test
    public void update_When_UserPropertyExist_Expected_CorrectData() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());

        UserPropertyDTO userPropertyDTO = new UserPropertyDTO(userPropertyDataInitializer.createData(userId, keyName, value));
        userPropertyDTO.setKeyName("keyName2");
        userPropertyDTO.setValue("value2");

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(userPropertyDTO);
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(userPropertyDTO));

        final UserProperty actualUserProperty = userPropertyService.getByUserId(userId).get(0);
        assertEquals(userPropertyDTO.getId(), actualUserProperty.getId());
        assertEquals(userPropertyDTO.getUserId(), actualUserProperty.getUserId());
        assertEquals(userPropertyDTO.getKeyName(), actualUserProperty.getKeyName());
        assertEquals(userPropertyDTO.getValue(), actualUserProperty.getValue());
    }

    @Test
    public void update_When_KeyNameOrValueIsEmpty_Expected_CorrectException() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());
        UserPropertyDTO userPropertyDTO = (UserPropertyDTO) userPropertyDataInitializer.createData(userId, keyName, value);
        assertFalse(userPropertyService.getAll().isEmpty());

        userPropertyDTO.setKeyName("keyName2");
        userPropertyDTO.setValue("");

        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(userPropertyDTO);
        performRequestBuilderExpectException(DataIsNotValidException.class, requestBuilder);
    }

    @Test
    public void updateAll_When_UserPropertyExist_Expected_CorrectData() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());

        final String editedKeyName = "keyName3";

        final UserPropertyDTO deleteUserPropertyDTO = new UserPropertyDTO(userPropertyDataInitializer.createData(userId, keyName, value));
        final UserPropertyDTO editUserPropertyDTO = new UserPropertyDTO(userPropertyDataInitializer.createData(userId, "keyName2", value));
        editUserPropertyDTO.setKeyName(editedKeyName);
        editUserPropertyDTO.setValue("value2");
        final UserPropertyDTO createUserPropertyDTO = new UserPropertyDTO(null, userId, keyName, value);

        final HashMap<String, List<UserPropertyDTO>> data = new HashMap<>();
        data.put("deletedProperties", Collections.singletonList(deleteUserPropertyDTO));
        data.put("editedProperties", Collections.singletonList(editUserPropertyDTO));
        data.put("createdProperties", Collections.singletonList(createUserPropertyDTO));

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(data, "/update");
        performRequestBuilderExpectedOk(requestBuilder);

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
    public void updateAll_When_KeyNameOrValueIsEmpty_Expected_CorrectException() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());

        final UserPropertyDTO deleteUserPropertyDTO = new UserPropertyDTO(userPropertyDataInitializer.createData(userId, keyName, value));
        final UserPropertyDTO editUserPropertyDTO = new UserPropertyDTO(userPropertyDataInitializer.createData(userId, "keyName2", value));
        editUserPropertyDTO.setKeyName("");
        editUserPropertyDTO.setValue("value2");
        final UserPropertyDTO createUserPropertyDTO = new UserPropertyDTO(null, userId, "", value);

        final HashMap<String, List<UserPropertyDTO>> data = new HashMap<>();
        data.put("deletedProperties", Collections.singletonList(deleteUserPropertyDTO));
        data.put("editedProperties", Collections.singletonList(editUserPropertyDTO));
        data.put("createdProperties", Collections.singletonList(createUserPropertyDTO));

        final MockHttpServletRequestBuilder requestBuilder = getPostRequestBuilderWithContent(data, "/update");
        performRequestBuilderExpectException(DataIsNotValidException.class, requestBuilder);
    }

    @Test
    public void delete_When_UserPropertyExist_Expected_EmptyResult() throws Exception {
        assertTrue(userPropertyService.getAll().isEmpty());
        final UserPropertyDTO userProperty = (UserPropertyDTO) userPropertyDataInitializer.createData(userId, keyName, value);
        assertFalse(userPropertyService.getAll().isEmpty());

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + userProperty.getId());
        performRequestBuilderExpectedOk(requestBuilder);

        assertEquals(0, userPropertyService.getAll().size());
    }

    @Test
    public void delete_When_UserPropertyNotExist_Expected_CorrectException() throws Exception {
        int nonExistenceId = 1000;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + nonExistenceId);
        performRequestBuilderExpectException(EmptyResultDataAccessException.class, requestBuilder);
    }
}
