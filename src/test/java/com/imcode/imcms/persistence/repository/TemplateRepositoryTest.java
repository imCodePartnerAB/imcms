package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TemplateRepositoryTest extends WebAppSpringTestConfig { //test

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @Autowired
    private TemplateRepository templateRepository;

    @BeforeEach
    public void setUp() {
        templateDataInitializer.cleanRepositories();
    }

    @Test
    public void updateTemplateName_When_TemplateNameExist_Expected_UpdateName() {
        final String templateName = "test";
        templateDataInitializer.createData(templateName);
        final String newName = "newTest";

        templateRepository.updateTemplateName(newName, templateName);

        assertEquals(newName, templateRepository.findByName(newName).getName());
        assertNull(templateRepository.findByName(templateName));
    }

    @Test
    public void updateTemplateName_When_TemplateNameNotExist_Expected_Empty() {
        final String templateName = "test";
        final String newName = "newTest";
        final String fakeName = "fakeTest";
        templateDataInitializer.createData(templateName);

        templateRepository.updateTemplateName(newName, fakeName);

        assertNull(templateRepository.findByName(fakeName));
    }
}
