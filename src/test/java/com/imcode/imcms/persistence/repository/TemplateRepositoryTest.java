package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Transactional
public class TemplateRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @Autowired
    private TemplateRepository templateRepository;

    @BeforeEach
    public void setUp() {
        templateDataInitializer.cleanRepositories();
    }

    @Test
    @Rollback(value = false)
    public void updateTemplateName_When_TemplateNameExist_Expected_UpdateName() {
        final String templateName = "test";
        templateDataInitializer.createData(templateName);
        final String newName = "newTest";

        templateRepository.updateTemplateName(newName, templateName);

//        assertNotEquals(templateName, template.getName());
        assertEquals(newName, templateRepository.findOne(newName).getName());
        assertNull(templateRepository.findOne(templateName));
    }

    @Test
    public void updateTemplateName_When_TemplateNameNotExist_Expected_Empty() {

    }
}
