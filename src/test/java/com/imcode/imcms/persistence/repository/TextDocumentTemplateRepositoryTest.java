package com.imcode.imcms.persistence.repository;


import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.model.TextDocumentTemplate;
import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class TextDocumentTemplateRepositoryTest extends WebAppSpringTestConfig { //test

    @Autowired
    private TemplateDataInitializer templateDataInitializer;

    @Autowired
    private TextDocumentTemplateRepository repository;

    private List<TextDocumentTemplate> saved;

    @BeforeEach
    public void setUp() {
        templateDataInitializer.cleanRepositories();

        saved = Arrays.asList(
                templateDataInitializer.createData(1001, "demo", "demo")
        );
    }

    @Test
    public void findDocIdByTemplateName_When_DocumentsExist_Expected_DocIdsList() {
        String templateName = "demo";

        List<Integer> docIds = saved.stream()
                .map(TextDocumentTemplate::getDocId)
                .collect(Collectors.toList());

        assertEquals(docIds, repository.findDocIdByTemplateName(templateName));
    }

    @Test
    public void findDocIdByTemplateName_When_NameUnknown_Expected_EmptyList() {
        final String templateName = "testttt123";
        assertTrue(repository.findDocIdByTemplateName(templateName).isEmpty());
    }

    @Test
    public void findTextDocumentTemplateByTemplateName_When_TemplateNameExist_Expected_CorrectSize() {
        final TextDocumentTemplate template = saved.get(0);
        final List<TextDocumentTemplateJPA> textDocumentsByName = repository.findTextDocumentTemplateByTemplateName(template.getTemplateName());

        assertEquals(1, textDocumentsByName.size());
    }

    @Test
    public void findTextDocumentTemplateByTemplateName_When_TemplateNameNotExist_Expected_EmptyResult() {
        final String fakeName = "fake";
        final List<TextDocumentTemplateJPA> textDocumentsByName = repository.findTextDocumentTemplateByTemplateName(fakeName);

        assertEquals(0, textDocumentsByName.size());
        assertTrue(textDocumentsByName.isEmpty());
    }


}
