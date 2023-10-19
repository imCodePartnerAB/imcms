package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.util.Value;
import imcode.server.document.index.DocumentIndex;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UrlDocumentService implements DocumentService<UrlDocumentDTO> {

    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final DocumentUrlService documentUrlService;
    private final VersionService versionService;

    public UrlDocumentService(DocumentService<DocumentDTO> documentService,
                              DocumentUrlService documentUrlService,
                              VersionService versionService) {

        this.defaultDocumentService = documentService;
        this.documentUrlService = documentUrlService;
        this.versionService = versionService;
    }

    @Override
    public long countDocuments() {
        return defaultDocumentService.countDocuments();
    }

    @Override
    public UrlDocumentDTO createFromParent(Integer parentDocId) {
        return Value.with(
                new UrlDocumentDTO(defaultDocumentService.createFromParent(parentDocId)),
                urlDocumentDTO -> urlDocumentDTO.setDocumentURL(DocumentUrlDTO.createDefault())
        );
    }

    @Override
    public UrlDocumentDTO get(int docId) {
        final UrlDocumentDTO urlDocumentDTO = new UrlDocumentDTO(defaultDocumentService.get(docId));
        final DocumentUrlDTO documentUrlDTO = Optional.ofNullable(documentUrlService.getByDocId(docId))
                .map(DocumentUrlDTO::new)
                .orElse(null);

        urlDocumentDTO.setDocumentURL(documentUrlDTO);

        return urlDocumentDTO;
    }

    @Override
    public List<DocumentDTO> get(Collection<Integer> docIds){
        return defaultDocumentService.get(docIds);
    }

    @Override
    public UrlDocumentDTO get(int docId, int versionNo) {
        final UrlDocumentDTO urlDocumentDTO = new UrlDocumentDTO(defaultDocumentService.get(docId, versionNo));
        final DocumentUrlDTO documentUrlDTO = Optional.ofNullable(documentUrlService.getByDocIdAndVersionNo(docId, versionNo))
                .map(DocumentUrlDTO::new)
                .orElse(null);

        urlDocumentDTO.setDocumentURL(documentUrlDTO);

        return urlDocumentDTO;
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return defaultDocumentService.publishDocument(docId, userId);
    }

    @Override
    public void setAsWorkingVersion(int docId, int versionNo){
        defaultDocumentService.setAsWorkingVersion(docId, versionNo);
        documentUrlService.setAsWorkingVersion(versionService.findByDocIdAndNo(docId, versionNo));
    }

    @Override
    public SolrInputDocument index(int docId) {
        final SolrInputDocument solrInputDocument = defaultDocumentService.index(docId);
        final String url = get(docId).getDocumentURL().getUrl();

        solrInputDocument.addField(DocumentIndex.FIELD__URL, url);

        return solrInputDocument;
    }

	@Override
	public SolrInputDocument updateDocumentVersion(int docId) {
		return defaultDocumentService.updateDocumentVersion(docId);
	}

	@Override
    public UrlDocumentDTO copy(int docId) {
        final int copiedDocId = defaultDocumentService.copy(docId).getId();

        documentUrlService.copy(docId, copiedDocId);

        return get(copiedDocId);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        defaultDocumentService.deleteByDocId(docIdToDelete);
    }

    @Override
    public UrlDocumentDTO save(UrlDocumentDTO saveMe) {
        final boolean isNew = (saveMe.getId() == null);
        final Optional<DocumentUrlDTO> documentUrlDTO = Optional.ofNullable(saveMe.getDocumentURL());

        final int savedDocId = defaultDocumentService.save(saveMe).getId();

        if (isNew) {
            documentUrlDTO.ifPresent(urlDTO -> urlDTO.setDocId(savedDocId));
        }

        documentUrlDTO.ifPresent(documentURL -> {     // FIXME: 29.08.2022 ifPresent is strange logic that is not considered in other places (e.g. copying a document)
            if(!documentURL.equals(documentUrlService.getByDocId(savedDocId))){
                documentUrlService.save(documentURL);
            }
        });

        return get(savedDocId);
    }

    @Override
    public List<UrlDocumentDTO> getDocumentsByTemplateName(String templateName) {
        return null;
    }

    @Override
    public int countDocumentsByTemplateName(String templateName) {
        return 0;
    }

    @Override
    public String getUniqueAlias(String alias) {
        return defaultDocumentService.getUniqueAlias(alias);
    }
}
