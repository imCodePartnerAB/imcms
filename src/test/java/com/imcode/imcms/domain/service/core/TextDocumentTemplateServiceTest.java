package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.model.TextDocumentTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class TextDocumentTemplateServiceTest extends WebAppSpringTestConfig {

    private static final int DOC_ID = 1001;
    @Autowired
    private TextDocumentTemplateService textDocumentTemplateService;

    private TextDocumentTemplate saved;

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @BeforeEach
    public void setUp() {
        templateDataInitializer.cleanRepositories();
        saved = new TextDocumentTemplateDTO(
                templateDataInitializer.createData(DOC_ID, "demo", "demo")
        );
    }

    @Test
    public void get() {
        final Optional<TextDocumentTemplate> oTemplate = textDocumentTemplateService.get(DOC_ID);
        assertTrue(oTemplate.isPresent());

        final TextDocumentTemplate receivedTextDocumentTemplateDTO = oTemplate.get();
        assertEquals(receivedTextDocumentTemplateDTO, saved);
    }

    @Test
    public void getByTemplateName_When_TemplateNameExist_Expected_CorrectSize() {
        final Optional<TextDocumentTemplate> oTemplate = textDocumentTemplateService.get(DOC_ID);
        assertTrue(oTemplate.isPresent());

        final TextDocumentTemplate receivedTextDocumentTemplate = oTemplate.get();

        List<TextDocumentTemplateDTO> receivTextDocumentTemplateDTOs = textDocumentTemplateService.getByTemplateName(
                receivedTextDocumentTemplate.getTemplateName());

        assertEquals(1, receivTextDocumentTemplateDTOs.size());
    }

    @Test
    public void getByTemplateName_When_TemplateNameNotExist_Expected_EmptyResult() {
        final String fakeName = "fake";

        List<TextDocumentTemplateDTO> receiveTextDocumentTemplateDTOs = textDocumentTemplateService.getByTemplateName(
                fakeName);

        assertEquals(0, receiveTextDocumentTemplateDTOs.size());
        assertTrue(receiveTextDocumentTemplateDTOs.isEmpty());
    }

    @Test
    public void save() {
        final String testTemplateName = "test_" + System.currentTimeMillis();
        Optional<TextDocumentTemplate> oTemplate = textDocumentTemplateService.get(DOC_ID);
        assertTrue(oTemplate.isPresent());
        final TextDocumentTemplate templateDTO = oTemplate.get();
        templateDTO.setTemplateName(testTemplateName);
        templateDTO.setChildrenTemplateName(testTemplateName);

        textDocumentTemplateService.save(templateDTO);

        oTemplate = textDocumentTemplateService.get(DOC_ID);
        assertTrue(oTemplate.isPresent());
        final TextDocumentTemplate receivedTextDocumentTemplateDTO = oTemplate.get();

        assertEquals(receivedTextDocumentTemplateDTO.getChildrenTemplateName(), testTemplateName);
        assertEquals(receivedTextDocumentTemplateDTO.getTemplateName(), testTemplateName);
    }

    @Test
    public void deleteByDocId() {
        Optional<TextDocumentTemplate> oTemplate = textDocumentTemplateService.get(DOC_ID);
        assertTrue(oTemplate.isPresent());

        textDocumentTemplateService.deleteByDocId(DOC_ID);

        oTemplate = textDocumentTemplateService.get(DOC_ID);
        assertFalse(oTemplate.isPresent());
    }
}
