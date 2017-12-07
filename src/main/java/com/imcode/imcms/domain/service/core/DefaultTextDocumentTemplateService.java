package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
import com.imcode.imcms.persistence.repository.TextDocumentTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class DefaultTextDocumentTemplateService implements TextDocumentTemplateService {

    private final TextDocumentTemplateRepository textDocumentTemplateRepository;

    DefaultTextDocumentTemplateService(TextDocumentTemplateRepository textDocumentTemplateRepository) {
        this.textDocumentTemplateRepository = textDocumentTemplateRepository;
    }

    @Override
    public TextDocumentTemplateDTO save(TextDocumentTemplateDTO saveMe) {
        TextDocumentTemplateJPA documentTemplate = new TextDocumentTemplateJPA(saveMe);
        documentTemplate = textDocumentTemplateRepository.save(documentTemplate);
        return new TextDocumentTemplateDTO(documentTemplate);
    }

    @Override
    public Optional<TextDocumentTemplateDTO> get(int docId) {
        return Optional.ofNullable(textDocumentTemplateRepository.findOne(docId)).map(TextDocumentTemplateDTO::new);
    }
}
