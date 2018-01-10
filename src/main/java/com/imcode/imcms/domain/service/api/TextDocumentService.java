package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.TextDocumentTemplateService;
import com.imcode.imcms.persistence.entity.Meta;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for work with Text Documents only.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.12.17.
 */
@Service
@Transactional
class TextDocumentService implements DocumentService<TextDocumentDTO> {

    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final TextDocumentTemplateService textDocumentTemplateService;

    TextDocumentService(@Qualifier("defaultDocumentService") DocumentService<DocumentDTO> documentService,
                        TextDocumentTemplateService textDocumentTemplateService) {

        defaultDocumentService = documentService;
        this.textDocumentTemplateService = textDocumentTemplateService;
    }

    @Override
    public TextDocumentDTO createEmpty(Meta.DocumentType type) {
        final DocumentDTO emptyCommonDoc = defaultDocumentService.createEmpty(type);
        return TextDocumentDTO.createEmpty(emptyCommonDoc);
    }

    @Override
    public TextDocumentDTO get(int docId) {
        final TextDocumentDTO textDocDTO = new TextDocumentDTO(defaultDocumentService.get(docId));
        textDocumentTemplateService.get(docId).map(TextDocumentTemplateDTO::new).ifPresent(textDocDTO::setTemplate);
        return textDocDTO;
    }

    @Override
    public int save(TextDocumentDTO saveMe) {
        final boolean isNew = (saveMe.getId() == null);
        final Optional<TextDocumentTemplateDTO> oTemplate = Optional.ofNullable(saveMe.getTemplate());

        final int savedDocId = defaultDocumentService.save(saveMe);

        if (isNew) {
            oTemplate.ifPresent(textDocumentTemplateDTO -> textDocumentTemplateDTO.setDocId(savedDocId));
        }

        oTemplate.ifPresent(textDocumentTemplateService::save);

        return savedDocId;
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return defaultDocumentService.publishDocument(docId, userId);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        textDocumentTemplateService.deleteByDocId(docIdToDelete);
        defaultDocumentService.deleteByDocId(docIdToDelete);
    }

}
