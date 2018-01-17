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

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultDocumentFileService
        extends AbstractVersionedContentService<DocumentFileJPA, DocumentFile, DocumentFileRepository>
        implements DocumentFileService {

    private final DocumentFileRepository documentFileRepository;
    private final VersionService versionService;

    DefaultDocumentFileService(DocumentFileRepository documentFileRepository,
                               VersionService versionService) {

        super(documentFileRepository);
        this.documentFileRepository = documentFileRepository;
        this.versionService = versionService;
    }

    /**
     * This will save list of files for specified document by id.
     * Note that all other files that are connected to document but not
     * mentioned in list will be deleted.
     * All changes applied for working document version.
     *
     * @param saveUs list of files to save
     * @param docId  id of document
     * @return list of saved files
     */
    @Override
    public List<DocumentFile> saveAll(List<DocumentFile> saveUs, int docId) {
        saveUs.forEach(documentFile -> {
            documentFile.setDocId(docId);
            final String fileId = documentFile.getFileId();

            if (fileId == null) {
                documentFile.setFileId(documentFile.getFilename());
            }
        });

        final Set<Integer> existingFileIds = saveUs.stream()
                .map(DocumentFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final List<DocumentFileJPA> noMoreNeededFiles = getByDocId(docId).stream()
                .filter(documentFile -> !existingFileIds.contains(documentFile.getId()))
                .map(DocumentFileJPA::new)
                .collect(Collectors.toList());

        documentFileRepository.delete(noMoreNeededFiles);

        saveUs.stream()
                .collect(Collectors.toMap(
                        DocumentFile::getFilename,
                        documentFile -> new ArrayList<>(Collections.singletonList(documentFile)),
                        (documentFiles, documentFiles2) -> {
                            documentFiles.addAll(documentFiles2);
                            return documentFiles;
                        }
                ))
                .entrySet()
                .stream()
                .filter(fileNameToDocFilesEntry -> fileNameToDocFilesEntry.getValue().size() > 1)
                .forEach(fileNameToDocFilesEntry -> {
                    final List<DocumentFile> docFilesWithSameName = fileNameToDocFilesEntry.getValue();

                    for (int i = 0; i < docFilesWithSameName.size(); i++) {
                        if (i == 0) continue;
                        final DocumentFile documentFile = docFilesWithSameName.get(i);
                        documentFile.setFileId(documentFile.getFilename() + i);
                    }
                });

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
