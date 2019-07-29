package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.TemplateGroupDTO;
import com.imcode.imcms.domain.service.TemplateGroupService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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
    public void getAll_When_TemplateGroupExists_Expected_OkAndCorrectEntities() throws Exception {
        final List<TemplateGroup> tests = dataInitializer.createTemplateGroups(4);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(tests.toArray()));
    }

    @Test
    public void getAll_When_TemplateGroupNotExists_Expected_EmptyResult() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath());
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, "[]");
    }

    @Test
    public void getByName_When_TemplateGroupNotExists_Expected_CorrectException() throws Exception {
        dataInitializer.createTemplateGroups(4);
        final String fakeName = "/fake";
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + fakeName);
        performRequestBuilderExpectException(NullPointerException.class, requestBuilder);
    }

    @Test
    public void getByName_When_TemplateGroupExists_Expected_CorrectEntity() throws Exception {
        final TemplateGroup test = dataInitializer.createData("test", 2, true);
        final TemplateGroup saved = templateGroupService.save(test);
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(controllerPath() + "/" + saved.getName());

        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(saved));
    }

    @Test
    public void create_Expected_CreateEntity() throws Exception {
        final List<Template> templates = dataInitializer.createData(2);
        final TemplateGroupDTO templateGroup = new TemplateGroupDTO();
        templateGroup.setName("test");
        templateGroup.setTemplates(new HashSet<>(templates));

        assertTrue(templateGroupService.getAll().isEmpty());
        performPostWithContentExpectOk(templateGroup);

        List<TemplateGroup> groups = templateGroupService.getAll();
        assertFalse(groups.isEmpty());
        assertEquals(1, groups.size());

    }

    @Test
    public void create_When_TemplateGroupHasEmptyName_Expected_Ok() throws Exception {
        final List<Template> templates = dataInitializer.createData(2);
        final TemplateGroupDTO templateGroup = new TemplateGroupDTO();
        templateGroup.setName("");
        templateGroup.setTemplates(new HashSet<>(templates));

        assertTrue(templateGroupService.getAll().isEmpty());
        performPostWithContentExpectOk(templateGroup);

        List<TemplateGroup> groups = templateGroupService.getAll();
        assertFalse(groups.isEmpty());
        assertEquals(1, groups.size());
    }

    @Test
    public void edit_When_TemplateGroupExistWithName_Expected_Ok() throws Exception {
        final List<Template> templates = dataInitializer.createData(2);
        final TemplateGroupDTO templateGroup = new TemplateGroupDTO();
        templateGroup.setName("test");
        templateGroup.setTemplates(new HashSet<>(templates));

        assertTrue(templateGroupService.getAll().isEmpty());
        final TemplateGroup saved = templateGroupService.save(templateGroup);

        saved.setName("test");
        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(saved);
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(saved));

        assertEquals(1, templateGroupService.getAll().size());
    }

    @Test
    public void edit_Expected_EditEntity() throws Exception {
        final List<Template> templates = dataInitializer.createData(2);
        final TemplateGroupDTO templateGroup = new TemplateGroupDTO();
        templateGroup.setName("test");
        templateGroup.setTemplates(new HashSet<>(templates));

        assertTrue(templateGroupService.getAll().isEmpty());
        final TemplateGroup saved = templateGroupService.save(templateGroup);
        final String secondName = "test2";
        saved.setName(secondName);
        final MockHttpServletRequestBuilder requestBuilder = getPutRequestBuilderWithContent(saved);
        performRequestBuilderExpectedOkAndJsonContentEquals(requestBuilder, asJson(saved));

        assertEquals(1, templateGroupService.getAll().size());
        final TemplateGroup editedTemplateGroup = templateGroupService.get(secondName);
        assertNotNull(editedTemplateGroup);
        assertNotEquals(templateGroup.getName(), editedTemplateGroup.getName());
    }

    @Test
    public void delete_When_TemplateGroupExist_Expected_Deleted() throws Exception {
        final List<TemplateGroup> templateGroups = dataInitializer.createTemplateGroups(2);
        final TemplateGroup templateGroup = templateGroups.get(0);
        assertEquals(2, templateGroupService.getAll().size());
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + templateGroup.getName());
        performRequestBuilderExpectedOk(requestBuilder);
        assertEquals(1, templateGroupService.getAll().size());
    }

    @Test
    public void delete_When_TemplateGroupNotExist_Expected_NothingAndCorrectSize() throws Exception {
        dataInitializer.createTemplateGroups(2);
        final String fakeName = "fake";
        assertEquals(2, templateGroupService.getAll().size());
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(controllerPath() + "/" + fakeName);
        performRequestBuilderExpectedOk(requestBuilder);
        assertEquals(2, templateGroupService.getAll().size());
    }
}
