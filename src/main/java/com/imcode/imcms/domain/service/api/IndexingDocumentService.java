package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.service.impl.DocumentIndexer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("documentService")
public class IndexingDocumentService implements DelegatingByTypeDocumentService {

    private final DelegatingByTypeDocumentService defaultDelegatingByTypeDocumentService;
    private final DocumentIndexer documentIndexer;
    private final DocumentMapper documentMapper;
    private final DocumentIndex documentIndex;
    private final DocumentsCache documentsCache;

    public IndexingDocumentService(DelegatingByTypeDocumentService defaultDelegatingByTypeDocumentService,
                                   DocumentIndexer documentIndexer,
                                   DocumentMapper documentMapper, DocumentIndex documentIndex,
                                   DocumentsCache documentsCache) {

        this.defaultDelegatingByTypeDocumentService = defaultDelegatingByTypeDocumentService;
        this.documentIndexer = documentIndexer;
        this.documentMapper = documentMapper;
        this.documentIndex = documentIndex;
        this.documentsCache = documentsCache;
    }

    @Override
    public long countDocuments() {
        return defaultDelegatingByTypeDocumentService.countDocuments();
    }

    @Override
    public Document get(int docId) throws DocumentNotExistException {
        return defaultDelegatingByTypeDocumentService.get(docId);
    }

    @Override
    public Document get(int docId, int versionNo) throws DocumentNotExistException {
        return defaultDelegatingByTypeDocumentService.get(docId, versionNo);
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        final boolean isPublished = defaultDelegatingByTypeDocumentService.publishDocument(docId, userId);

        if (isPublished) {
            invalidateDocument(docId);
            documentsCache.invalidateCache();
        }

        return isPublished;
    }

    @Override
    public void setAsWorkingVersion(int docId, int versionNo){
        defaultDelegatingByTypeDocumentService.setAsWorkingVersion(docId, versionNo);
        defaultDelegatingByTypeDocumentService.index(docId);
    }

    @Override
    public SolrInputDocument index(int docId) {
        return defaultDelegatingByTypeDocumentService.index(docId);
    }

	@Override
	public SolrInputDocument updateDocumentVersion(int docId) {
		return defaultDelegatingByTypeDocumentService.updateDocumentVersion(docId);
	}

	@Override
    public Document copy(int docId) {
        final Document copiedDocument = defaultDelegatingByTypeDocumentService.copy(docId);

        invalidateDocument(copiedDocument.getId());

        return copiedDocument;
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        defaultDelegatingByTypeDocumentService.deleteByDocId(docIdToDelete);

        documentMapper.invalidateDocument(docIdToDelete);
        documentIndex.removeDocument(docIdToDelete);
    }

    @Override
    public Document save(Document saveMe) {
        final Document savedDocument = defaultDelegatingByTypeDocumentService.save(saveMe);

        invalidateDocument(savedDocument.getId());

        return savedDocument;
    }

    @Override
    public Document createNewDocument(Meta.DocumentType type, Integer parentDocId) {
        return defaultDelegatingByTypeDocumentService.createNewDocument(type, parentDocId);
    }

    @Override
    public String getUniqueAlias(String alias) {
        return defaultDelegatingByTypeDocumentService.getUniqueAlias(alias);
    }

    @PostConstruct
    private void init() {
        documentIndexer.setDocumentService(this);
    }

    private void invalidateDocument(int docId) {
        documentMapper.invalidateDocument(docId);
    }
}
