package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.domain.service.TemplateGroupService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import com.imcode.imcms.persistence.entity.TemplateGroupJPA;
import com.imcode.imcms.persistence.repository.TemplateGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class TemplateGroupServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private TemplateDataInitializer dataInitializer;

    @Autowired
    private TemplateGroupService templateGroupService;

    @Autowired
    private TemplateGroupRepository templateGroupRepository;

    @BeforeEach
    public void setUp() {
        dataInitializer.cleanRepositories();
    }

    @Test
    public void getAll_When_templateGroupsWithTemplates_Expect_theyAllPersisted() {
        assertTrue(templateGroupService.getAll().isEmpty());

        final List<TemplateGroup> expectedTemplateGroupList = dataInitializer.createTemplateGroups(6);
        final List<TemplateGroup> returnedTemplateGroupList = templateGroupService.getAll();
        assertEquals(expectedTemplateGroupList.size(), returnedTemplateGroupList.size());

        final TemplateGroup returnedTemplateGroup = returnedTemplateGroupList.get(0);
        final TemplateGroup expectedTemplateGroup = expectedTemplateGroupList.stream()
                .filter(templateGroup -> templateGroup.getId().equals(returnedTemplateGroup.getId()))
                .findAny().get();
        assertEquals(expectedTemplateGroup.getName(), returnedTemplateGroup.getName());
        final Set<Integer> returnedTemplateIds = returnedTemplateGroup.getTemplates().stream().map(Template::getId).collect(Collectors.toSet());
        final Set<Integer> expectedTemplateIds = expectedTemplateGroup.getTemplates().stream().map(Template::getId).collect(Collectors.toSet());
        assertTrue(returnedTemplateIds.containsAll(expectedTemplateIds));
    }

    @Test
    public void save() {
        final TemplateGroup test = dataInitializer.createData("test", 5, true);

        templateGroupService.save(test);

        final TemplateGroup persisted = templateGroupService.get("test");
        persisted.setId(null);
        assertEquals(test, persisted);
    }

    @Test
    public void edit_WhenTemplateGroupExist_Expected_EditEntity() {
        final TemplateGroup test = dataInitializer.createData("test", 5, true);
        final TemplateGroup saved = templateGroupService.save(test);
        final String anotherName = "Another";
        saved.setName(anotherName);
        templateGroupService.edit(saved);

        assertNotEquals(test.getName(), saved.getName());
    }

    @Test
    public void getByName_When_TemplateGroupNameExist_Expected_CorrectTemplateGroup() {
        final String name = "TEST";
        final TemplateGroup test = dataInitializer.createData(name, 5, false);
        final TemplateGroup persistedByName = templateGroupService.get(name);

        assertNotNull(persistedByName);
        assertEquals(test, persistedByName);
    }

    @Test
    public void getById_When_TemplateGroupIdExist_Expected_CorrectTemplateGroup() {
        final TemplateGroup expectedTemplateGroup = dataInitializer.createData("TEST", 5, false);
        final TemplateGroup actualTemplateGroup = templateGroupService.get(expectedTemplateGroup.getId());

        assertNotNull(actualTemplateGroup);
        assertEquals(expectedTemplateGroup, actualTemplateGroup);
    }

    @Test
    public void addTemplate_When_TemplateAndGroupExist_Expected_AddedToGroup(){
        final String templateName = "templateName";

        final TemplateGroup templateGroup = dataInitializer.createData("groupName", 0, false);
        assertTrue(templateGroup.getTemplates().isEmpty());

        final Template template = dataInitializer.createData(templateName);
        templateGroupService.addTemplate(templateName, templateGroup.getId());

        final Set<Template> returnedTemplates = templateGroupRepository.getOne(templateGroup.getId()).getTemplates();
        assertEquals(1, returnedTemplates.size());
        assertEquals(template.getId(), returnedTemplates.iterator().next().getId());
    }

    @Test
    public void addTemplate_When_TemplateDoesNotExist_Expected_NothingChanged(){
        final String nonexistentTemplateName = "nonexistentTemplateName";

        final TemplateGroup templateGroup = dataInitializer.createData("groupName", 0, false);
        final Integer templateId = templateGroup.getId();
        assertTrue(templateGroup.getTemplates().isEmpty());

        templateGroupService.addTemplate(nonexistentTemplateName, templateId);

	    final Set<Template> returnedTemplates = templateGroupRepository.getOne(templateId).getTemplates();
        assertTrue(returnedTemplates.isEmpty());
    }

    @Test
    public void addTemplate_When_TemplateExistsInGroup_Expected_NothingChanged(){
        final String templateName = "templateName";

        final TemplateGroup templateGroup = dataInitializer.createData("groupName", 0, false);
        final Integer templateId = templateGroup.getId();
        assertTrue(templateGroup.getTemplates().isEmpty());

        final Template template = dataInitializer.createData(templateName);
	    templateGroupService.addTemplate(templateName, templateId);
	    assertEquals(1, templateGroupRepository.getOne(templateId).getTemplates().size());

	    templateGroupService.addTemplate(template.getName(), templateId);
	    assertEquals(1, templateGroupRepository.getOne(templateId).getTemplates().size());
    }

    @Test
    public void deleteTemplate_When_TemplateAndGroupExist_Expected_DeletedFromGroup(){
        final TemplateGroup createdTemplateGroup = dataInitializer.createData("groupName", 1, false);
        final Template template = createdTemplateGroup.getTemplates().iterator().next();
        final Integer createdTemplateGroupId = createdTemplateGroup.getId();
        final Integer templateId = template.getId();

	    final TemplateGroupJPA templateGroup = templateGroupRepository.getOne(createdTemplateGroupId);
        assertEquals(1, templateGroup.getTemplates().size());
        assertEquals(templateId, templateGroup.getTemplates().iterator().next().getId());

        templateGroupService.deleteTemplate(template.getName(), createdTemplateGroupId);

	    assertTrue(templateGroupRepository.getOne(createdTemplateGroupId).getTemplates().isEmpty());
    }

    @Test
    public void deleteTemplate_When_TemplateDoesNotExist_Expected_NothingChanged(){
        final String nonexistentTemplateName = "nonexistentTemplateName";

        final TemplateGroup createdTemplateGroup = dataInitializer.createData("groupName", 1, false);
        final Template template = createdTemplateGroup.getTemplates().iterator().next();
        final Integer createdTemplateGroupId = createdTemplateGroup.getId();
        final Integer templateId = template.getId();

	    TemplateGroupJPA templateGroup = templateGroupRepository.getOne(createdTemplateGroupId);
        assertEquals(1, templateGroup.getTemplates().size());
        assertEquals(templateId, templateGroup.getTemplates().iterator().next().getId());

        templateGroupService.deleteTemplate(nonexistentTemplateName, createdTemplateGroupId);

	    templateGroup = templateGroupRepository.getOne(createdTemplateGroupId);
	    assertEquals(1, templateGroup.getTemplates().size());
        assertEquals(templateId, templateGroup.getTemplates().iterator().next().getId());
    }

    @Test
    public void deleteTemplate_When_GroupDoesNotExist_Expected_NothingChanged(){
        dataInitializer.createData("groupName", 1, false);
        final int nonexistentId = 1000;

	    assertEquals(1, templateGroupRepository.findAll().size());
	    assertTrue(templateGroupRepository.findById(nonexistentId).isEmpty());

        templateGroupService.deleteTemplate("templateName", nonexistentId);

        assertEquals(1, templateGroupRepository.findAll().size());
    }

    @Test
    public void remove_When_GroupHaveTemplates_Expected_GroupDeleted(){
        final String templateName = "templateName";

        final TemplateGroup templateGroup = dataInitializer.createData("groupName", 0, false);
        assertTrue(templateGroup.getTemplates().isEmpty());

        final Template template = dataInitializer.createData(templateName);

        templateGroup.setTemplates(Collections.singleton(template));
	    templateGroupRepository.save(new TemplateGroupJPA(templateGroup));
	    assertFalse(templateGroupRepository.getOne(templateGroup.getId()).getTemplates().isEmpty());

	    templateGroupService.remove(templateGroup.getId());
	    assertTrue(templateGroupRepository.findById(templateGroup.getId()).isEmpty());
    }
}