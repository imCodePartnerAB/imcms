package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
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

    public TextDocumentTemplateJPA save(TextDocumentTemplateDTO saveMe) {
        final TextDocumentTemplateJPA documentTemplate = new TextDocumentTemplateJPA(saveMe);
        return textDocumentTemplateRepository.save(documentTemplate);
    }

    public TextDocumentTemplateDTO get(TextDocumentTemplateDTO getMe) {
        final Integer docId = getMe.getDocId();
        final TextDocumentTemplateJPA templateJPA = textDocumentTemplateRepository.findOne(docId);
        return new TextDocumentTemplateDTO(templateJPA);
    }
}
