package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
import com.imcode.imcms.persistence.repository.TextDocumentTemplateRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class TextDocumentTemplateServiceTest {

    private static final int DOC_ID = 1001;
    @Autowired
    private TextDocumentTemplateService textDocumentTemplateService;

    @Autowired
    private TextDocumentTemplateRepository textDocumentTemplateRepository;

    private TextDocumentTemplateDTO saved;

    @Before
    public void setUp() throws Exception {
        final TextDocumentTemplateJPA templateJPA = textDocumentTemplateRepository.save(
                new TextDocumentTemplateJPA(DOC_ID, "demo", 0, "demo")
        );

        saved = new TextDocumentTemplateDTO(templateJPA);
    }

    @Test
    public void get() throws Exception {
        final TextDocumentTemplateDTO receivedTextDocumentTemplateDTO = textDocumentTemplateService.get(DOC_ID);

        assertEquals(receivedTextDocumentTemplateDTO, saved);
    }

    @Test
    public void save() throws Exception {
        final String testTemplateName = "test_" + System.currentTimeMillis();
        final TextDocumentTemplateDTO templateDTO = textDocumentTemplateService.get(DOC_ID);
        templateDTO.setTemplateName(testTemplateName);
        templateDTO.setChildrenTemplateName(testTemplateName);

        textDocumentTemplateService.save(templateDTO);

        final TextDocumentTemplateDTO receivedTextDocumentTemplateDTO = textDocumentTemplateService.get(DOC_ID);

        assertEquals(receivedTextDocumentTemplateDTO.getChildrenTemplateName(), testTemplateName);
        assertEquals(receivedTextDocumentTemplateDTO.getTemplateName(), testTemplateName);
    }

}