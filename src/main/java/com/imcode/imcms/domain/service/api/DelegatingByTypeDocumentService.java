package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
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
class DelegatingByTypeDocumentService implements TypedDocumentService<Document> {

    private final DocumentService<UberDocumentDTO> wrappedTextDocumentService;
    private final DocumentService<UberDocumentDTO> wrappedFileDocumentService;
    private final DocumentService<UberDocumentDTO> wrappedUrlDocumentService;
    private final MetaRepository metaRepository;

    DelegatingByTypeDocumentService(DocumentService<TextDocumentDTO> textDocumentService,
                                    DocumentService<FileDocumentDTO> fileDocumentService,
                                    DocumentService<UrlDocumentDTO> urlDocumentService,
                                    MetaRepository metaRepository) {

        this.wrappedTextDocumentService = new WrappingDocumentService<>(textDocumentService);
        this.wrappedFileDocumentService = new WrappingDocumentService<>(fileDocumentService);
        this.wrappedUrlDocumentService = new WrappingDocumentService<>(urlDocumentService);
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
    public UberDocumentDTO save(UberDocumentDTO saveMe) {
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

    private DocumentService<UberDocumentDTO> getCorrespondingDocumentService(int docId) {
        return Optional.ofNullable(metaRepository.findType(docId))
                .map(this::getCorrespondingDocumentService)
                .orElseThrow(DocumentNotExistException::new);
    }

    private DocumentService<UberDocumentDTO> getCorrespondingDocumentService(DocumentType type) {
        switch (type) {
            case TEXT:
                return wrappedTextDocumentService;

            case FILE:
                return wrappedFileDocumentService;

            case URL:
                return wrappedUrlDocumentService;

            default:
                throw new UnsupportedDocumentTypeException(type);
        }
    }
}
