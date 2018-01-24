package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.exception.UnsupportedDocumentTypeException;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.TypedDocumentService;
import com.imcode.imcms.domain.service.core.WrappingDocumentService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.repository.MetaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Central {@link com.imcode.imcms.domain.service.DocumentService} instance
 * that delegates calls to corresponding service by document's type.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.12.17.
 */
@Service
public class DelegatingByTypeDocumentService implements TypedDocumentService<Document> {

    private final WrappingDocumentService<TextDocumentDTO> wrappedTextDocumentService;
    private final WrappingDocumentService<FileDocumentDTO> wrappedFileDocumentService;
    private final MetaRepository metaRepository;

    DelegatingByTypeDocumentService(DocumentService<TextDocumentDTO> textDocumentService,
                                    DocumentService<FileDocumentDTO> fileDocumentService,
                                    MetaRepository metaRepository) {

        this.wrappedFileDocumentService = new WrappingDocumentService<>(fileDocumentService);
        this.wrappedTextDocumentService = new WrappingDocumentService<>(textDocumentService);
        this.metaRepository = metaRepository;
    }

    @Override
    public Document createEmpty(DocumentType type) {
        return getCorrespondingDocumentService(type).createEmpty();
    }

    @Override
    public Document get(int docId) {
        return getCorrespondingDocumentService(docId).get(docId);
    }

    @Override
    public int save(UberDocumentDTO saveMe) {
        return getCorrespondingDocumentService(saveMe.getType()).save(saveMe);
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return getCorrespondingDocumentService(docId).publishDocument(docId, userId);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        getCorrespondingDocumentService(docIdToDelete).deleteByDocId(docIdToDelete);
    }

    private WrappingDocumentService getCorrespondingDocumentService(int docId) {
        return Optional.ofNullable(metaRepository.findType(docId))
                .map(this::getCorrespondingDocumentService)
                .orElseThrow(DocumentNotExistException::new);
    }

    private WrappingDocumentService getCorrespondingDocumentService(DocumentType type) {
        switch (type) {
            case TEXT:
                return wrappedTextDocumentService;

            case FILE:
                return wrappedFileDocumentService;

            default:
                throw new UnsupportedDocumentTypeException(type);
        }
    }
}
