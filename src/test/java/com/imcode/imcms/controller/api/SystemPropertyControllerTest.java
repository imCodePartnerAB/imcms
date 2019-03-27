package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.service.SystemPropertyService;
import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.mapping.jpa.SystemProperty;
import com.imcode.imcms.model.Roles;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Transactional
public class SystemPropertyControllerTest extends AbstractControllerTest {

    @Autowired
    private SystemPropertyService systemPropertyService;

    @Override
    protected String controllerPath() {
        return "/properties";
    }

    @BeforeEach
    public void setUp() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        DocGetterCallback docGetterCallback = user.getDocGetterCallback();
        DocumentLanguage language = DocumentLanguage.builder()
                .code("en")
                .build();

        docGetterCallback.setLanguage(language);
        Imcms.setUser(user);
    }

    @Test
    public void findByName_When_PropertyExist_Expect_Ok() throws Exception {
        String name = "StartDocument";
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/" + name);

        performRequestBuilderExpectedOk(requestBuilder);
    }

    @Test
    void findByName_When_PropertyExist_Expect_OkAndCorrectEntity() throws Exception {
        List<SystemProperty> properties = systemPropertyService.findAll();
        String name = "StartDocument";
        int firstProperty = 0;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/" + name);
        assertEquals("StartDocument", properties.get(firstProperty).getName());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(properties.get(firstProperty)));
    }

    @Test
    public void findByName_When_PropertyNotExist_Expect_ResultEmpty() throws Exception {
        String notExistingName = "DocumentStart";
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/" + notExistingName);
        performRequestBuilderExpectedOk(requestBuilder);

    }

    @Test
    public void update_When_PropertyExist_Expect_Ok() throws Exception {
        List<SystemProperty> properties = systemPropertyService.findAll();
        int firstProperty = 0;
        SystemProperty systemProperty = systemPropertyService.update(properties.get(firstProperty));

        performPostWithContentExpectOk(systemProperty);
    }

    @Test
    public void update_When_PropertyExist_Expect_OkAndCorrectEntity() throws Exception {
        List<SystemProperty> properties = systemPropertyService.findAll();
        int firstProperty = 0;
        SystemProperty systemProperty = systemPropertyService.update(properties.get(firstProperty));
        performPostWithContentExpectOk(systemProperty);

        performPostWithContentExpectOkAndJsonContentEquals(systemProperty, systemProperty);


    }

    @Test
    public void deleteById_When_PropertyExist_Expect_Ok() {
        int id = 2;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + id);
        getDeleteRequestBuilderWithContent(requestBuilder);

    }

    @Test
    public void deleteById_When_PropertyExist_Expect_OkAndCorrectDeleteById() throws Exception {
        int id = 2;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + id);
        performRequestBuilderExpectedOk(requestBuilder);


    }

    @Test
    public void deleteById_When_PropertyNotExist_Expect_CorrectException() throws Exception {
        int fakeId = 40;
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + fakeId);
        performRequestBuilderExpectException(EmptyResultDataAccessException.class, requestBuilder);

    }

    @Test
    public void findAll_When_PropertiesExist_Expect_Ok() throws Exception {
        List<SystemProperty> properties = systemPropertyService.findAll();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOk(requestBuilder);

    }

    @Test
    public void findAll_When_PropertiesExist_Expect_OkAndCorrectEntities() throws Exception {
        List<SystemProperty> properties = systemPropertyService.findAll();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(properties.toArray()));

    }

}