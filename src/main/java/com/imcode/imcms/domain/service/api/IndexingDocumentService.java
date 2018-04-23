package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.service.impl.DocumentIndexer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("documentService")
public class IndexingDocumentService implements DelegatingByTypeDocumentService {

    private final DelegatingByTypeDocumentService defaultDelegatingByTypeDocumentService;
    private final DocumentIndexer documentIndexer;
    private final DocumentIndex documentIndex;

    @Autowired
    public IndexingDocumentService(DelegatingByTypeDocumentService defaultDelegatingByTypeDocumentService,
                                   DocumentIndexer documentIndexer,
                                   DocumentIndex documentIndex) {

        this.defaultDelegatingByTypeDocumentService = defaultDelegatingByTypeDocumentService;
        this.documentIndexer = documentIndexer;
        this.documentIndex = documentIndex;
    }

    @Override
    public Document get(int docId) throws DocumentNotExistException {
        return defaultDelegatingByTypeDocumentService.get(docId);
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        final boolean isPublished = defaultDelegatingByTypeDocumentService.publishDocument(docId, userId);

        if (isPublished) {
            indexDocument(docId);
        }

        return isPublished;
    }

    @Override
    public SolrInputDocument index(int docId) {
        return defaultDelegatingByTypeDocumentService.index(docId);
    }

    @Override
    public Document copy(int docId) {
        final Document copiedDocument = defaultDelegatingByTypeDocumentService.copy(docId);

        indexDocument(copiedDocument.getId());

        return copiedDocument;
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        defaultDelegatingByTypeDocumentService.deleteByDocId(docIdToDelete);
    }

    @Override
    public Document save(Document saveMe) {
        final Document savedDocument = defaultDelegatingByTypeDocumentService.save(saveMe);

        indexDocument(savedDocument.getId());

        return savedDocument;
    }

    @Override
    public Document createNewDocument(Meta.DocumentType type, Integer parentDocId) {
        return defaultDelegatingByTypeDocumentService.createNewDocument(type, parentDocId);
    }

    @PostConstruct
    private void init() {
        documentIndexer.setDocumentService(this);
    }

    private void indexDocument(int docId) {
        documentIndex.indexDocument(docId);
    }
}
