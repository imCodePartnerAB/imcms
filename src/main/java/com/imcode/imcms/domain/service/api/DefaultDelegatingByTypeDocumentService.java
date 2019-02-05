package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.exception.UnsupportedDocumentTypeException;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.repository.MetaRepository;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.12.17.
 */
@Service
class DefaultDelegatingByTypeDocumentService implements DelegatingByTypeDocumentService {

    private final DocumentService<TextDocumentDTO> textDocumentService;
    private final DocumentService<FileDocumentDTO> fileDocumentService;
    private final DocumentService<UrlDocumentDTO> urlDocumentService;
    private final MetaRepository metaRepository;

    DefaultDelegatingByTypeDocumentService(DocumentService<TextDocumentDTO> textDocumentService,
                                           DocumentService<FileDocumentDTO> fileDocumentService,
                                           DocumentService<UrlDocumentDTO> urlDocumentService,
                                           MetaRepository metaRepository) {

        this.textDocumentService = textDocumentService;
        this.fileDocumentService = fileDocumentService;
        this.urlDocumentService = urlDocumentService;
        this.metaRepository = metaRepository;
    }

    @Override
    public Document createNewDocument(DocumentType type, Integer parentDocId) {
        return getCorrespondingDocumentService(type).createFromParent(parentDocId);
    }

    @Override
    public Document get(int docId) {
        return getCorrespondingDocumentService(docId).get(docId);
    }

    @Override
    public Document save(Document saveMe) {
        return getCorrespondingDocumentService(saveMe.getType()).save(UberDocumentDTO.of(saveMe).toTypedDocument());
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return getCorrespondingDocumentService(docId).publishDocument(docId, userId);
    }

    @Override
    public SolrInputDocument index(int docId) {
        return getCorrespondingDocumentService(docId).index(docId);
    }

    @Override
    public Document copy(int docId) {
        return getCorrespondingDocumentService(docId).copy(docId);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        getCorrespondingDocumentService(docIdToDelete).deleteByDocId(docIdToDelete);
    }

    @Transactional
    DocumentService<? extends Document> getCorrespondingDocumentService(int docId) {
        return Optional.ofNullable(metaRepository.findType(docId))
                .map(this::getCorrespondingDocumentService)
                .orElseThrow(DocumentNotExistException::new);
    }

    private DocumentService<? extends Document> getCorrespondingDocumentService(DocumentType type) {
        switch (type) {
            case TEXT:
                return textDocumentService;

            case FILE:
                return fileDocumentService;

            case URL:
                return urlDocumentService;

            default:
                throw new UnsupportedDocumentTypeException(type);
        }
    }
}
