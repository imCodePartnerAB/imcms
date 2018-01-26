package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.DocumentUrlService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public class UrlDocumentService implements DocumentService<UrlDocumentDTO> {

    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final DocumentUrlService documentUrlService;

    public UrlDocumentService(DocumentService<DocumentDTO> documentService, DocumentUrlService documentUrlService) {
        this.defaultDocumentService = documentService;
        this.documentUrlService = documentUrlService;
    }

    @Override
    public UrlDocumentDTO createEmpty() {
        return UrlDocumentDTO.createEmpty(defaultDocumentService.createEmpty());
    }

    @Override
    public UrlDocumentDTO get(int docId) {
        final UrlDocumentDTO urlDocumentDTO = new UrlDocumentDTO(defaultDocumentService.get(docId));
        final DocumentUrlDTO documentUrlDTO = new DocumentUrlDTO(documentUrlService.getByDocId(docId));

        urlDocumentDTO.setDocumentUrlDTO(documentUrlDTO);

        return urlDocumentDTO;
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return defaultDocumentService.publishDocument(docId, userId);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        defaultDocumentService.deleteByDocId(docIdToDelete);
    }

    @Override
    public int save(UrlDocumentDTO saveMe) {
        final boolean isNew = (saveMe.getId() == null);
        final Optional<DocumentUrlDTO> documentUrlDTO = Optional.ofNullable(saveMe.getDocumentUrlDTO());

        final int savedDocId = defaultDocumentService.save(new DocumentDTO(saveMe));

        if (isNew) {
            documentUrlDTO.ifPresent(urlDTO -> urlDTO.setDocId(savedDocId));
        }

        documentUrlDTO.ifPresent(documentUrlService::save);

        return savedDocId;
    }
}
