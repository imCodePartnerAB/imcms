package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.factory.DocumentDtoFactory;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.DocumentFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for work with File Documents only.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.12.17.
 */
@Transactional
public class FileDocumentService implements DocumentService<FileDocumentDTO> {

    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final DocumentFileService documentFileService;
    private final DocumentDtoFactory documentDtoFactory;

    public FileDocumentService(DocumentService<DocumentDTO> documentService,
                               DocumentDtoFactory documentDtoFactory,
                               DocumentFileService documentFileService) {

        this.defaultDocumentService = documentService;
        this.documentFileService = documentFileService;
        this.documentDtoFactory = documentDtoFactory;
    }

    @Override
    public FileDocumentDTO createEmpty() {
        return documentDtoFactory.createEmptyFileDocument();
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
        final List<DocumentFile> saveMeFiles = new ArrayList<>(saveMe.getFiles());

        documentFileService.saveAll(saveMeFiles, savedDocId);

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
