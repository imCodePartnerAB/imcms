package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
import com.imcode.imcms.persistence.repository.TextDocumentTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for work with templates in relation to text documents.
 */
@Service
@Transactional
public class TextDocumentTemplateService {

    private final TextDocumentTemplateRepository textDocumentTemplateRepository;

    TextDocumentTemplateService(TextDocumentTemplateRepository textDocumentTemplateRepository) {
        this.textDocumentTemplateRepository = textDocumentTemplateRepository;
    }

    public TextDocumentTemplateDTO save(TextDocumentTemplateDTO saveMe) {
        TextDocumentTemplateJPA documentTemplate = new TextDocumentTemplateJPA(saveMe);
        documentTemplate = textDocumentTemplateRepository.save(documentTemplate);
        return new TextDocumentTemplateDTO(documentTemplate);
    }

    public Optional<TextDocumentTemplateDTO> get(int docId) {
        return Optional.ofNullable(textDocumentTemplateRepository.findOne(docId)).map(TextDocumentTemplateDTO::new);
    }
}
