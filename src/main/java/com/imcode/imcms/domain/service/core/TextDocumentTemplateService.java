package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.persistence.entity.TextDocumentTemplate;
import com.imcode.imcms.persistence.repository.TextDocumentTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for work with templates in relation to text documents.
 */
@Service
@Transactional
public class TextDocumentTemplateService {

    private final TextDocumentTemplateRepository textDocumentTemplateRepository;

    public TextDocumentTemplateService(TextDocumentTemplateRepository textDocumentTemplateRepository) {
        this.textDocumentTemplateRepository = textDocumentTemplateRepository;
    }

    public void save(int docId, String templateName) {
        final TextDocumentTemplate documentTemplate = textDocumentTemplateRepository.findOne(docId);
        documentTemplate.setTemplateName(templateName);
        textDocumentTemplateRepository.save(documentTemplate);
    }

    public String getTemplateName(int docId) {
        return textDocumentTemplateRepository.findOne(docId).getTemplateName();
    }
}
