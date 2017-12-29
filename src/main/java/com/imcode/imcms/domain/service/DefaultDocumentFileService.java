package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.model.DocumentFile;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.repository.DocumentFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class DefaultDocumentFileService implements DocumentFileService {

    private final DocumentFileRepository documentFileRepository;

    public DefaultDocumentFileService(DocumentFileRepository documentFileRepository) {
        this.documentFileRepository = documentFileRepository;
    }

    @Override
    public DocumentFile save(DocumentFile saveMe) {
        return new DocumentFileDTO(documentFileRepository.save(new DocumentFileJPA(saveMe)));
    }

    @Override
    public Optional<DocumentFile> getByDocId(int docId) {
        return Optional.empty();//ofNullable(documentFileRepository.findByDocId(docId));
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {

    }
}
