package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.service.TemplateGroupService;
import com.imcode.imcms.model.TemplateGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class TemplateGroupControllerTest extends AbstractControllerTest {

    @Autowired
    private TemplateDataInitializer dataInitializer;

    @Autowired
    private TemplateGroupService templateGroupService;

    @BeforeEach
    public void setUp() {
        dataInitializer.cleanRepositories();
    }

    @Override
    protected String controllerPath() {
        return "/template-group";
    }

    @Test
    public void getAll_When_TemplateGroupExists_Expected_OkAndCorrectSize() throws Exception {
        final List<TemplateGroup> test = dataInitializer.createTemplateGroups(4);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(test.toArray()));
    }

    @Test
    public void getAll_When_TemplateGroupNotExists_Expected_EmptyResult() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson("[]"));
    }

    @Test
    public void getByName_When_TemplateGroupNotExists_Expected_CorrectException() {

    }

    @Test
    public void getByName_When_TemplateGroupExists_Expected_CorrectEntity() {

    }

    @Test
    public void create_Expected_CreateEntity() {

    }

    @Test
    public void create_When_TemplateGroupHasEmptyName_Expected_CorrectException() {

    }

    @Test
    public void create_When_TemplateGroupExistWithName_Expected_CorrectException() {

    }

    @Test
    public void edit_When_TemplateGroupHasEmptyName_Expected_CorrectException() {

    }

    @Test
    public void edit_When_TemplateGroupExistWithName_Expected_CorrectException() {

    }

    @Test
    public void edit_Expected_EditEntity() {

    }

    @Test
    public void delete_When_TemplateGroupExist_Expected_Deleted() {

    }

    @Test
    public void delete_When_TemplateGroupNotExist_Expected_CorrectException() {

    }
}
