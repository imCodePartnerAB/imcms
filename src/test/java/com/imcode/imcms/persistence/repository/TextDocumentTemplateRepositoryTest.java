package com.imcode.imcms.persistence.repository;


import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.model.TextDocumentTemplate;
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
public class TextDocumentTemplateRepositoryTest extends WebAppSpringTestConfig {

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
    public void findDocIdByTemplateName() {
        String templateName = "demo";

        List<Integer> docIds = saved.stream()
                .map(TextDocumentTemplate::getDocId)
                .collect(Collectors.toList());

        assertEquals(docIds, repository.findDocIdByTemplateName(templateName));
    }

    @Test
    public void findDocIdByTemplateName_When_NameIsUnknown_Expected_EmptyList() {
        final String templateName = "testttt123";
        assertTrue(repository.findDocIdByTemplateName(templateName).isEmpty());
    }

}
