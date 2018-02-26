package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.model.TextDocumentTemplate;
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
    public TextDocumentTemplate save(TextDocumentTemplate saveMe) {
        TextDocumentTemplateJPA documentTemplate = new TextDocumentTemplateJPA(saveMe);
        documentTemplate = textDocumentTemplateRepository.save(documentTemplate);
        return new TextDocumentTemplateDTO(documentTemplate);
    }

    @Override
    public Optional<TextDocumentTemplate> get(int docId) {
        return Optional.ofNullable(textDocumentTemplateRepository.findOne(docId)).map(TextDocumentTemplateDTO::new);
    }

    @Override
    public void copy(int fromDocId, int toDocId) {
        get(fromDocId).ifPresent(textDocumentTemplate -> {
            final TextDocumentTemplateDTO clonedTextDocumentTemplateDTO =
                    new TextDocumentTemplateDTO(textDocumentTemplate).clone();

            clonedTextDocumentTemplateDTO.setDocId(toDocId);

            save(clonedTextDocumentTemplateDTO);
        });
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        textDocumentTemplateRepository.deleteByDocId(docIdToDelete);
    }
}
