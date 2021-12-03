package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.exception.AloneTemplateException;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.components.datainitializer.TextDocumentDataInitializer;
import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.service.TemplateGroupService;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.model.Template;
import com.imcode.imcms.model.TemplateGroup;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class TemplateServiceTest extends WebAppSpringTestConfig {

    @Value("WEB-INF/templates/text")
    private File templateDirectory;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateService templateService;
    @Autowired
    private TemplateGroupService templateGroupService;
    @Autowired
    private TextDocumentTemplateService textDocumentTemplateService;

    @Autowired
    private TemplateDataInitializer templateDataInitializer;
    @Autowired
    private TextDocumentDataInitializer textDocumentDataInitializer;

    private Template defaultTemplate;
    private File defaultTemplateFile;
    private List<Template> templatesExpected;

    @BeforeEach
    public void setUp() {
        defaultTemplate = new TemplateDTO(null, "testttt123", false);
        defaultTemplateFile = new File(templateDirectory, defaultTemplate.getName() + ".jsp");

        defaultTemplateFile.delete();

        templateDataInitializer.cleanRepositories();
        textDocumentDataInitializer.cleanRepositories();
        templatesExpected = templateDataInitializer.createData(5);
    }

    @Test
    public void getAll_When_TemplatesExist_Expected_AllTemplates() {
        assertEquals(templatesExpected, templateService.getAll());
    }

    @Test
    public void get_When_TemplateDoesNotExist_Expected_Null() {
        assertNull(templateService.get("nonexistentName"));
    }

    @Test
    public void isValidName_When_ExtensionIsAllowable_Expected_True() {
        assertTrue(templateService.isValidName("name.jsp"));
    }

    @Test
    public void isValidName_When_ExtensionIsNotAllowable_Expected_False() {
        assertFalse(templateService.isValidName("name.txt"));
    }

    @Test
    public void save_When_TemplateFileExist_Expected_SaveEntity() throws IOException{
        assertTrue(defaultTemplateFile.createNewFile());
        templateService.save(defaultTemplate);
        assertEquals(templatesExpected.size() + 1, templateRepository.findAll().size());
    }

    @Test
    public void save_When_TemplateFileDoesNotExist_Expected_EntityNotSaved() {
        templateService.save(defaultTemplate);
        assertEquals(templatesExpected.size(), templateRepository.findAll().size());
    }

    @Test
    public void replaceTemplateFile_When_TemplateIsUsedByDocuments_Expected_ReplacedTemplateInTextDocuments() {
        final String replacedTemplateName = "replacedTemplateName";
        final String templateName = "templateName";
        final Template replacedTemplate = templateDataInitializer.createData(replacedTemplateName);
        final Template template = templateDataInitializer.createData(templateName);

        final TextDocumentDTO textDocument = textDocumentDataInitializer.createTextDocument(replacedTemplate.getName());
        final TextDocumentDTO textDocument2 = textDocumentDataInitializer.createTextDocument(template.getName());
        assertEquals(replacedTemplateName, textDocument.getTemplate().getTemplateName());
        assertEquals(templateName, textDocument2.getTemplate().getTemplateName());

        templateService.replaceTemplateFile(replacedTemplateName, templateName);

        assertNotNull(templateRepository.findByName(replacedTemplateName));
        assertNotNull(templateRepository.findByName(templateName));

        assertTrue(textDocumentTemplateService.getByTemplateName(replacedTemplateName).isEmpty());
        assertEquals(2, textDocumentTemplateService.getByTemplateName(templateName).size());
    }

    @Test
    public void replaceTemplateFile_When_TemplateToReplaceDoesNotExist_Expected_ReplacedTemplateInTextDocuments() {
        final String nonexistentTemplateName = "nonexistentTemplateName";
        final String existingTemplateName = "existingTemplateName";
        templateDataInitializer.createData(existingTemplateName);

        assertNull(templateRepository.findByName(nonexistentTemplateName));
        assertNotNull(templateRepository.findByName(existingTemplateName));
        assertThrows(EmptyResultDataAccessException.class, () -> templateService.replaceTemplateFile(existingTemplateName, nonexistentTemplateName));
    }

    @Test
    public void renameTemplate_When_TemplateIsUsedByDocuments_Expected_RenamedTemplateAndTextDocumentsTemplate(){
        final String oldTemplateName = "oldTemplateName";
        final String newTemplateName = "newTemplateName";
        final Template template = templateDataInitializer.createData(oldTemplateName);

        final TextDocumentDTO textDocument = textDocumentDataInitializer.createTextDocument(template.getName());
        final TextDocumentDTO textDocument2 = textDocumentDataInitializer.createTextDocument(template.getName());
        assertEquals(oldTemplateName, textDocument.getTemplate().getTemplateName());
        assertEquals(oldTemplateName, textDocument2.getTemplate().getTemplateName());

        templateService.renameTemplate(oldTemplateName, newTemplateName);

        assertNull(templateRepository.findByName(oldTemplateName));
        assertNotNull(templateRepository.findByName(newTemplateName));

        assertTrue(textDocumentTemplateService.getByTemplateName(oldTemplateName).isEmpty());
        assertEquals(2, textDocumentTemplateService.getByTemplateName(newTemplateName).size());
    }

    @Test
    public void deleteTemplate_When_TemplateExist_Expected_DeleteEntity() {
        final Template testTemplate = templatesExpected.get(0);
        assertEquals(5, templateRepository.findAll().size());
        templateService.delete(testTemplate.getId());
        assertEquals(4, templateRepository.findAll().size());
    }

    @Test
    public void deleteTemplate_When_OnlyOneTemplateExist_Expected_CorrectException() {
        templateDataInitializer.cleanRepositories();
        final List<Template> template = templateDataInitializer.createData(1);
        assertEquals(1, templateRepository.findAll().size());
        assertThrows(AloneTemplateException.class, () -> templateService.delete(template.get(0).getId()));
        assertEquals(1, templateRepository.findAll().size());
    }

    @Test
    public void deleteTemplate_When_TemplateNotExist_Expected_CorrectException() {
        final Integer fakeId = -10;
        assertEquals(5, templateRepository.findAll().size());
        assertThrows(EmptyResultDataAccessException.class, () -> templateService.delete(fakeId));
        assertEquals(5, templateRepository.findAll().size());
    }

    @Test
    public void deleteTemplate_When_TemplateHasGroup_Expected_DeleteEntity(){
        final TemplateGroup templateGroup = templateDataInitializer.createData("groupName", 1, false);
        final Template template = templateGroup.getTemplates().iterator().next();

        templateService.delete(template.getId());
        assertNull(templateRepository.findOne(template.getId()));
        assertTrue(templateGroupService.get(templateGroup.getId()).getTemplates().isEmpty());
    }

    @Test
    public void checkTemplates_When_EntityExists_But_TemplateFileDoesNotExist_Expected_DeleteEntity() {
        templateDataInitializer.cleanRepositories();

        final String templateName = "nonexistentTemplateFileName";
        templateDataInitializer.createData(templateName);
        assertNotNull(templateRepository.findByName(templateName));

        templateService.checkTemplates();

        assertNull(templateRepository.findByName(templateName));
    }

    @Test
    public void checkTemplates_When_EntityDoesNotExist_But_TemplateFileExists_Expected_AddEntity() throws IOException {
        templateDataInitializer.cleanRepositories();
        assertTrue(templateRepository.findAll().isEmpty());

        assertTrue(defaultTemplateFile.createNewFile());

        templateService.checkTemplates();

        assertNotNull(templateRepository.findByName(FilenameUtils.getBaseName(defaultTemplateFile.getName())));
    }
}
