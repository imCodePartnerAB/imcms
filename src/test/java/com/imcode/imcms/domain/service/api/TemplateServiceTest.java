package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.exception.AloneTemplateInDbException;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
public class TemplateServiceTest extends WebAppSpringTestConfig {

    @Value("WEB-INF/templates/text")
    private File templateDirectory;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateDataInitializer dataInitializer;

    private List<Template> templatesExpected;

    private Template defaultTemplate;
    private File defaultTemplateFile;

    @BeforeEach
    public void setUp() {
        defaultTemplate = new TemplateDTO(null, "testttt123", false, null);
        defaultTemplateFile = new File(templateDirectory, defaultTemplate.getName() + ".jsp");

        dataInitializer.cleanRepositories();
        templatesExpected = dataInitializer.createData(5);
    }

    @Test
    public void getAll_When_TemplatesExist_Expected_AllTemplates() {
        assertEquals(templatesExpected, templateService.getAll());
    }

    @Test
    public void save_When_NoTemplate_Expected_Null() {
        templateService.save(defaultTemplate);

        assertFalse(templateService.get(defaultTemplate.getName()).isPresent());
    }

    @Test
    public void getByName_When_NameExist_Expected_CorrectResult() throws IOException {
        final String templateName = "testttt123";
        final File templateFile = new File(templateDirectory, templateName + ".jsp");
        assertTrue(templateFile.createNewFile());

        try {
            templateService.save(defaultTemplate);
            final Optional<Template> templateOptional = templateService.get(defaultTemplate.getName());
            assertTrue(templateOptional.isPresent());
            final Template templateResult = templateOptional.get();
            templateResult.setId(null);
            assertEquals(defaultTemplate, templateResult);

        } finally {
            assertTrue(templateFile.delete());
        }
    }

    @Test
    public void save_When_TemplateDoesntExistAndFileExist_Expected_SuccessfulSaving() throws Exception {
        final String templateName = "testttt123";
        final File templateFile = new File(templateDirectory, templateName + ".jsp");

        try {
            assertTrue(templateFile.createNewFile());

            Template templateDTO = new TemplateDTO(null, templateName, false, null);
            templateService.save(templateDTO);
            final Optional<Template> oTemplate = templateService.get(templateName);
            assertTrue(oTemplate.isPresent());

            templateDTO = oTemplate.get();
            final Optional<Template> templateOptional = templateService.get(templateName);
            assertTrue(templateOptional.isPresent());
            final Template templateResult = templateOptional.get();
            assertEquals(templateDTO.getName(), templateResult.getName());
            assertEquals(templateDTO.isHidden(), templateResult.isHidden());

        } finally {
            assertTrue(templateFile.delete());
        }
    }

    @Test
    public void saveTemplateFile_When_OptionIsCreateNewAndFileDoesntExist_Expected_SuccessfulCreating() throws IOException {
        final OpenOption writeMode = StandardOpenOption.CREATE_NEW;
        saveAndAssertTemplateFile(writeMode);
    }

    @Test
    public void saveTemplateFile_When_OptionIsWriteAndFileExist_Expected_SuccessfulRewriting() throws IOException {
        assertTrue(defaultTemplateFile.createNewFile());

        final OpenOption writeMode = StandardOpenOption.WRITE;

        saveAndAssertTemplateFile(writeMode);
    }

    @Test
    public void deleteTemplate_When_TemplateExist_Expected_DeleteEntity() {
        final Template testTemplate = templatesExpected.get(0);
        assertEquals(5, templateService.getAll().size());
        templateService.delete(testTemplate.getId());
        assertFalse(templateService.get(testTemplate.getName()).isPresent());
        assertEquals(4, templateService.getAll().size());
    }

    @Test
    public void deleteTemplate_When_TemplateNotExist_Expected_CorrectException() {
        final Integer fakeId = -10;
        assertEquals(5, templateService.getAll().size());
        assertThrows(EmptyResultDataAccessException.class, () -> templateService.delete(fakeId));
        assertEquals(5, templateService.getAll().size());
    }

    @Test
    public void deleteTemplate_When_InGroupOneTemplate_Expected_CorrectException() {
        dataInitializer.cleanRepositories();
        final List<Template> template = dataInitializer.createData(1);
        assertEquals(1, templateService.getAll().size());
        assertThrows(AloneTemplateInDbException.class, () -> templateService.delete(template.get(0).getId()));
        assertEquals(1, templateService.getAll().size());
    }

    private void saveAndAssertTemplateFile(OpenOption writeMode) throws IOException {
        final byte[] exceptedContent = "Some content".getBytes();

        try {
            templateService.saveTemplateFile(defaultTemplate, exceptedContent, writeMode);

            final byte[] actualContent = Files.readAllBytes(defaultTemplateFile.toPath());
            assertArrayEquals(exceptedContent, actualContent);

        } finally {
            assertTrue(defaultTemplateFile.delete());
        }
    }

}
