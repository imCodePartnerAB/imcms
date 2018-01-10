package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.DocumentFile;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultDocumentFileService extends AbstractVersionedContentService<DocumentFileJPA, DocumentFile, DocumentFileRepository> implements DocumentFileService {

    private final DocumentFileRepository documentFileRepository;
    private final VersionService versionService;

    public DefaultDocumentFileService(DocumentFileRepository documentFileRepository,
                                      VersionService versionService) {

        super(documentFileRepository);
        this.documentFileRepository = documentFileRepository;
        this.versionService = versionService;
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
        return findWorkingVersionFiles(docId).stream()
                .map(DocumentFileDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void publishDocumentFiles(int docId) {
        final Version latestVersion = versionService.getLatestVersion(docId);
        final Version workingVersion = versionService.getDocumentWorkingVersion(docId);

        createVersionedContent(workingVersion, latestVersion);
    }

    @Override
    public DocumentFile getPublicByDocId(int docId) {
        final int latestVersionIndex = versionService.getLatestVersion(docId).getNo();
        return documentFileRepository.findDefaultByDocIdAndVersionIndex(docId, latestVersionIndex);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        // todo: implement, or not =)
    }

    private List<DocumentFileJPA> findWorkingVersionFiles(int docId) {
        return documentFileRepository.findByDocIdAndVersionIndex(docId, Version.WORKING_VERSION_INDEX);
    }

    @Override
    protected DocumentFile mapToDTO(DocumentFileJPA documentFileJPA, Version version) {
        return new DocumentFileDTO(documentFileJPA);
    }

    @Override
    protected DocumentFileJPA mapToJpaWithoutId(DocumentFile documentFile, Version version) {
        final DocumentFileJPA documentFileJPA = new DocumentFileJPA(documentFile);
        documentFileJPA.setVersionIndex(version.getNo());
        documentFileJPA.setId(null);

        return documentFileJPA;
    }
}
