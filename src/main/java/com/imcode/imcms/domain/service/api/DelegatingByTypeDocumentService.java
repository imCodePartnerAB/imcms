package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.exception.UnsupportedDocumentTypeException;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.repository.MetaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Central {@link com.imcode.imcms.domain.service.DocumentService} instance
 * that delegates calls to corresponding service by type.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.12.17.
 */
@Service("documentService")
public class DelegatingByTypeDocumentService<T extends DocumentDTO> implements DocumentService<T> {

    private final DocumentService<TextDocumentDTO> textDocumentService;
    private final DocumentService<FileDocumentDTO> fileDocumentService;
    private final MetaRepository metaRepository;

    DelegatingByTypeDocumentService(DocumentService<TextDocumentDTO> textDocumentService,
                                    DocumentService<FileDocumentDTO> fileDocumentService,
                                    MetaRepository metaRepository) {

        this.textDocumentService = textDocumentService;
        this.fileDocumentService = fileDocumentService;
        this.metaRepository = metaRepository;
    }

    @Override
    public T createEmpty(DocumentType type) {
        return getCorrespondingDocumentService(type).createEmpty(type);
    }

    @Override
    public T get(int docId) {
        return getCorrespondingDocumentService(docId).get(docId);
    }

    @Override
    public int save(T saveMe) {
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

    private DocumentService<T> getCorrespondingDocumentService(int docId) {
        return Optional.ofNullable(metaRepository.findType(docId))
                .map(this::getCorrespondingDocumentService)
                .orElseThrow(DocumentNotExistException::new);
    }

    @SuppressWarnings("unchecked")
    private DocumentService<T> getCorrespondingDocumentService(DocumentType type) {
        switch (type) {
            case TEXT:
                return (DocumentService<T>) textDocumentService;

            case FILE:
                return (DocumentService<T>) fileDocumentService;

            default:
                throw new UnsupportedDocumentTypeException(type);
        }
    }
}
