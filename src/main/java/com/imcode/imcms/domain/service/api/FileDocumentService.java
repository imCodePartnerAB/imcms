package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.DocumentFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for work with File Documents only.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.12.17.
 */
@Service
@Transactional
class FileDocumentService implements DocumentService<FileDocumentDTO> {

    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final DocumentFileService documentFileService;

    FileDocumentService(DocumentService<DocumentDTO> documentService2,
                        DocumentFileService documentFileService) {

        this.defaultDocumentService = documentService2;
        this.documentFileService = documentFileService;
    }

    @Override
    public FileDocumentDTO createEmpty() {
        return FileDocumentDTO.createEmpty(defaultDocumentService.createEmpty());
    }

    @Override
    public FileDocumentDTO get(int docId) {
        final FileDocumentDTO fileDocument = new FileDocumentDTO(defaultDocumentService.get(docId));

        final List<DocumentFileDTO> documentFiles = documentFileService.getByDocId(docId)
                .stream()
                .map(DocumentFileDTO::new)
                .collect(Collectors.toList());

        fileDocument.setFiles(documentFiles);

        return fileDocument;
    }

    public int save(FileDocumentDTO saveMe) {
        final int savedDocId = defaultDocumentService.save(new DocumentDTO(saveMe));

        final List<DocumentFile> saveMeFiles = saveMe.getFiles()
                .stream()
                .map(documentFileDTO -> {
                    documentFileDTO.setDocId(savedDocId);
                    return ((DocumentFile) documentFileDTO);
                })
                .collect(Collectors.toList());

        documentFileService.saveAll(saveMeFiles);

        return savedDocId;
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
