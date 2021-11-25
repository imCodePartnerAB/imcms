package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class TemplateRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateGroupRepository templateGroupRepository;

    @BeforeEach
    public void setUp() {
        templateDataInitializer.cleanRepositories();
    }

    @Test
    public void updateTemplateName_When_TemplateNameExist_Expected_UpdateName() {
        final String templateName = "test";
        templateDataInitializer.createData(templateName);
        final String newName = "newTest";

        templateRepository.updateTemplateName(templateName, newName);

        assertEquals(newName, templateRepository.findByName(newName).getName());
        assertNull(templateRepository.findByName(templateName));
    }

    @Test
    public void updateTemplateName_When_TemplateNameNotExist_Expected_Empty() {
        final String templateName = "test";
        final String newName = "newTest";
        final String fakeName = "fakeTest";
        templateDataInitializer.createData(templateName);

        templateRepository.updateTemplateName(fakeName, newName);

        assertNull(templateRepository.findByName(fakeName));
    }

    @Test
    public void deleteTemplateGroup_Expected_DeletedTemplateGroupField(){
        final TemplateGroup templateGroup = templateDataInitializer.createData("groupName", 1, false);
        final Template template = templateGroup.getTemplates().iterator().next();

        templateRepository.deleteTemplateGroupByTemplateId(template.getId());
        assertTrue(templateGroupRepository.findOne(templateGroup.getId()).getTemplates().isEmpty());
    }
}
