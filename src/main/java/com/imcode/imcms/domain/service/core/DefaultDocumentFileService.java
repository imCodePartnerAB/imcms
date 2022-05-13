package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.DocumentFile;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentFileRepository;
import imcode.util.Utility;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
class DefaultDocumentFileService
        extends AbstractVersionedContentService<DocumentFileJPA, DocumentFileRepository>
        implements DocumentFileService {

    public static final Logger LOG = Logger.getLogger(DefaultDocumentFileService.class);
    private final DocumentFileRepository documentFileRepository;
    private final VersionService versionService;
    private final File filesPath;

    @SneakyThrows
    DefaultDocumentFileService(DocumentFileRepository documentFileRepository,
                               VersionService versionService,
                               @Value("${FilePath}") Resource filesPath) {

        super(documentFileRepository);
        this.documentFileRepository = documentFileRepository;
        this.versionService = versionService;
        this.filesPath = filesPath.getFile();
    }

    @PostConstruct
    private void createFilesPathDirectories() {
        filesPath.mkdirs();
    }

    @Override
    public <T extends DocumentFile> List<DocumentFile> saveAll(List<T> saveUs, int docId) {
        setDocAndFileIds(saveUs, docId);
        resolveDuplicatedIds(saveUs);
        deleteNoMoreUsedFiles(saveUs, docId);
        saveNewFiles(saveUs);

        return saveDocumentFiles(saveUs);
    }

    @Override
    public <T extends DocumentFile> DocumentFile save(T saveMe) {
        return new DocumentFileDTO(documentFileRepository.save(new DocumentFileJPA(saveMe)));
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
    public void copy(int fromDocId, int toDocId) {

        getByDocId(fromDocId).forEach(documentFile -> {
            final DocumentFileDTO clonedDocumentFileDTO = new DocumentFileDTO(documentFile).clone();
            clonedDocumentFileDTO.setDocId(toDocId);

            save(clonedDocumentFileDTO);
        });
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        // todo: implement, or not =)
    }

    @Override
    protected DocumentFileJPA removeId(DocumentFileJPA documentFile, Version version) {
        final DocumentFileJPA documentFileJPA = new DocumentFileJPA(documentFile);
        documentFileJPA.setVersionIndex(version.getNo());
        documentFileJPA.setId(null);

        return documentFileJPA;
    }

    private List<DocumentFileJPA> findWorkingVersionFiles(int docId) {
        return documentFileRepository.findByDocIdAndVersionIndex(docId, Version.WORKING_VERSION_INDEX);
    }

    private <T extends DocumentFile> void resolveDuplicatedIds(List<T> documentFiles) {
        final AtomicInteger counter = new AtomicInteger();

        documentFiles.stream()
                .collect(Collectors.toMap(
                        DocumentFile::getFileId,
                        documentFile -> new ArrayList<>(Collections.singletonList(documentFile)),
                        (documentFiles1, documentFiles2) -> {
                            documentFiles1.addAll(documentFiles2);
                            return documentFiles1;
                        }
                ))
                .entrySet()
                .stream()
                .filter(fileIdToDocFilesEntry -> fileIdToDocFilesEntry.getValue().size() > 1)
                .flatMap(fileIdToDocFilesEntry -> fileIdToDocFilesEntry.getValue().stream())
                .forEach(documentFile -> documentFile.setFileId(documentFile.getFilename() + counter.addAndGet(1)));
    }

    private <T extends DocumentFile> void setDocAndFileIds(List<T> saveUs, int docId) {
        saveUs.forEach(documentFile -> {
            documentFile.setDocId(docId);
            final String fileId = documentFile.getFileId();

            if (StringUtils.isBlank(fileId)) {
                documentFile.setFileId(documentFile.getFilename());
            }
        });
    }

    private <T extends DocumentFile> void deleteNoMoreUsedFiles(List<T> saveUs, int docId) {
        final Set<Integer> existingFileIds = saveUs.stream()
                .map(DocumentFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final List<DocumentFileJPA> noMoreNeededFiles = getByDocId(docId).stream()
                .filter(documentFile -> !existingFileIds.contains(documentFile.getId()))
                .map(DocumentFileJPA::new)
                .collect(Collectors.toList());

	    documentFileRepository.deleteAll(noMoreNeededFiles);
    }

    private <T extends DocumentFile> void saveNewFiles(List<T> saveUs) {
        // do not rewrite using Java Stream API, file transfer can be long operation. in cycle.
        for (DocumentFile documentFile : saveUs) {
            final MultipartFile file = documentFile.getMultipartFile();

            if (file == null) {
                continue;
            }

            int copiesCount = 1;
            String originalFilename = Utility.normalizeString(file.getOriginalFilename());
            originalFilename = originalFilename.replace("(", "").replace(")", "");
            File destination = new File(filesPath, originalFilename);

            while (destination.exists()) {
                final String baseName = FilenameUtils.getBaseName(originalFilename);
                final String newName = baseName + copiesCount + "." + FilenameUtils.getExtension(originalFilename);
                destination = new File(filesPath, newName);
                copiesCount++;
            }

            documentFile.setFilename(destination.getName());

            try {
                file.transferTo(destination);
            } catch (IOException e) {
                LOG.error("Error while saving Document File.", e);
            }
        }
    }

    private <T extends DocumentFile> List<DocumentFile> saveDocumentFiles(List<T> saveUs) {
        return saveUs.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }
}
