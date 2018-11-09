package com.imcode.imcms.controller.api;

import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.service.SystemPropertyService;
import com.imcode.imcms.mapping.jpa.SystemProperty;
import com.imcode.imcms.mapping.jpa.SystemPropertyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Transactional
public class SystemPropertyControllerTest extends AbstractControllerTest {

    @Autowired
    private SystemPropertyService systemPropertyService;

    @Autowired
    private SystemPropertyRepository systemPropertyRepository;

    @Override
    protected String controllerPath() {
        return "/properties";
    }


    @Test
    public void findByName_When_PropertyExist_Expect_OK() throws Exception {
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
    public void update_When_PropertyExist_Expect_OK() throws Exception {
        List<SystemProperty> properties = systemPropertyService.findAll();
        int firstProperty = 0;
        SystemProperty systemProperty = systemPropertyService.update(properties.get(firstProperty));

        performPostWithContentExpectOk(systemProperty);
    }

    @Test
    public void update_When_PropertyExist_Expect_OKAndCorrectEntity() throws Exception {
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
        // performDeleteWithContentExpectException(requestBuilder, Exception.class);
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

    // TODO fix test
//    @Test
    public void findAll_When_PropertiesNotExist_Expect_EmptyResultAndCorrectException() throws Exception {
        assertFalse(systemPropertyService.findAll().isEmpty());
        systemPropertyRepository.deleteAll();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());

        // assertTrue(systemPropertyService.findAll().isEmpty());
        performRequestBuilderExpectException(InvalidDataAccessResourceUsageException.class, requestBuilder);

    }
}