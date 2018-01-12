package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.model.TextDocumentTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class TextDocumentTemplateServiceTest {

    private static final int DOC_ID = 1001;
    @Autowired
    private TextDocumentTemplateService textDocumentTemplateService;

    private TextDocumentTemplate saved;

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @Before
    public void setUp() throws Exception {
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
