package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.persistence.entity.Meta;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for work with File Documents only.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.12.17.
 */
@Service
@Transactional
public class FileDocumentService implements DocumentService<FileDocumentDTO> {

    private final DocumentService<DocumentDTO> defaultDocumentService;

    FileDocumentService(@Qualifier("defaultDocumentService") DocumentService<DocumentDTO> documentService) {

        defaultDocumentService = documentService;
    }

    @Override
    public FileDocumentDTO createEmpty(Meta.DocumentType type) {
        return new FileDocumentDTO(defaultDocumentService.createEmpty(type));
    }

    @Override
    public FileDocumentDTO get(int docId) {
        return new FileDocumentDTO(defaultDocumentService.get(docId));
    }

    public int save(FileDocumentDTO saveMe) {
        return defaultDocumentService.save(saveMe);
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return defaultDocumentService.publishDocument(docId, userId);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        defaultDocumentService.deleteByDocId(docIdToDelete);
    }
}
