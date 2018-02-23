package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.factory.DocumentDtoFactory;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.DocumentUrlService;
import imcode.server.document.index.DocumentIndex;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public class UrlDocumentService implements DocumentService<UrlDocumentDTO> {

    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final DocumentDtoFactory documentDtoFactory;
    private final DocumentUrlService documentUrlService;

    public UrlDocumentService(DocumentService<DocumentDTO> documentService,
                              DocumentDtoFactory documentDtoFactory,
                              DocumentUrlService documentUrlService) {

        this.defaultDocumentService = documentService;
        this.documentDtoFactory = documentDtoFactory;
        this.documentUrlService = documentUrlService;
    }

    @Override
    public UrlDocumentDTO createFromParent(Integer parentDocId) { // todo: use copying to create new doc based on parent
        return documentDtoFactory.createEmptyUrlDocument();
    }

    @Override
    public UrlDocumentDTO get(int docId) {
        final UrlDocumentDTO urlDocumentDTO = new UrlDocumentDTO(defaultDocumentService.get(docId));
        final DocumentUrlDTO documentUrlDTO = new DocumentUrlDTO(documentUrlService.getByDocId(docId));

        urlDocumentDTO.setDocumentURL(documentUrlDTO);

        return urlDocumentDTO;
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return defaultDocumentService.publishDocument(docId, userId);
    }

    @Override
    public SolrInputDocument index(int docId) {
        final SolrInputDocument solrInputDocument = defaultDocumentService.index(docId);
        final String url = get(docId).getDocumentURL().getUrl();

        solrInputDocument.addField(DocumentIndex.FIELD_URL, url);

        return solrInputDocument;
    }

    @Override
    public UrlDocumentDTO copy(int docId) {
        final UrlDocumentDTO clonedUrlDocumentDTO = get(docId).clone();

        clonedUrlDocumentDTO.getCommonContents()
                .forEach(commonContentDTO ->
                        commonContentDTO.setHeadline("(Copy/Kopia) " + commonContentDTO.getHeadline()));

        save(clonedUrlDocumentDTO);

        return get(clonedUrlDocumentDTO.getId());
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

        documentUrlDTO.ifPresent(documentUrlService::save);

        return saveMe;
    }
}
