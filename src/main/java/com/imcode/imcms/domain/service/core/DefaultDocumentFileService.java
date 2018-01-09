package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.model.DocumentFile;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.repository.DocumentFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultDocumentFileService implements DocumentFileService {

    private final DocumentFileRepository documentFileRepository;

    public DefaultDocumentFileService(DocumentFileRepository documentFileRepository) {
        this.documentFileRepository = documentFileRepository;
    }

    @Override
    public List<DocumentFile> saveAll(List<DocumentFile> saveUs) {
        return saveUs.stream()
                .map(documentFile -> new DocumentFileDTO(
                        documentFileRepository.save(new DocumentFileJPA(documentFile))
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentFile> getByDocId(int docId) {
        return documentFileRepository.findByDocId(docId)
                .stream()
                .map(DocumentFileDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {

    }
}
