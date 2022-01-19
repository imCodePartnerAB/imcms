package com.imcode.imcms.controller.api;

import com.imcode.imcms.api.exception.AloneTemplateException;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.model.Template;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Transactional
public class TemplateControllerTest extends AbstractControllerTest {

    @Autowired
    private TemplateDataInitializer dataInitializer;

    @Autowired
    private TemplateService templateService;

    private List<Template> templatesExpected;

    @Override
    protected String controllerPath() {
        return "/templates";
    }

    @BeforeEach
    public void setUp() {
        final UserDomainObject user = new UserDomainObject();
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);

        dataInitializer.cleanRepositories();
        templatesExpected = dataInitializer.createData(5);
    }

    @Test
    public void getAll_When_TemplatesExist_Expected_ReturnedTemplates() throws Exception {
        final String templatesJson = asJson(templatesExpected);
        getAllExpectedOkAndJsonContentEquals(templatesJson);
    }

    @Test
    public void deleteTemplate_When_TemplateExist_Expected_DeleteEntity() throws Exception {
        final Template template = templatesExpected.get(0);
        assertEquals(5, templateService.getAll().size());
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
                controllerPath() + "/" + template.getId());
        performRequestBuilderExpectedOk(requestBuilder);
        assertNull(templateService.get(template.getName()));
        assertEquals(4, templateService.getAll().size());
    }

    @Test
    public void deleteTemplate_When_TemplateNotExist_Expected_CorrectException() throws Exception {
        final Integer fakeId = -10;
        assertEquals(5, templateService.getAll().size());
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
                controllerPath() + "/" + fakeId);
        performRequestBuilderExpectException(EmptyResultDataAccessException.class, requestBuilder);
        assertEquals(5, templateService.getAll().size());
    }

    @Test
    public void deleteTemplate_When_OnlyOneTemplate_Expected_CorrectException() throws Exception {
        dataInitializer.cleanRepositories();
        final List<Template> template = dataInitializer.createData(1);
        assertEquals(1, templateService.getAll().size());
        final Integer templateId = template.get(0).getId();
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
                controllerPath() + "/" + templateId);
        performRequestBuilderExpectException(AloneTemplateException.class, requestBuilder);
        assertEquals(1, templateService.getAll().size());
    }
}
