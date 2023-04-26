package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.DocumentFile;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentFileRepository;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StoragePath;
import imcode.util.Utility;
import imcode.util.io.EmptyInputStreamSource;
import imcode.util.io.FileUtility;
import imcode.util.io.InputStreamSource;
import imcode.util.io.StorageInputStreamSource;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

@Service
@Transactional
class DefaultDocumentFileService
        extends AbstractVersionedContentService<DocumentFileJPA, DocumentFileRepository>
        implements DocumentFileService {

    public static final Logger LOG = LogManager.getLogger(DefaultDocumentFileService.class);
    private final DocumentFileRepository documentFileRepository;
    private final VersionService versionService;
    private final StorageClient fileDocumentStorageClient;
    private final StoragePath storageFileDocumentDirectoryPath;

    @SneakyThrows
    DefaultDocumentFileService(DocumentFileRepository documentFileRepository,
                               VersionService versionService,
                               @Value("${FilePath}") String filesPath,
                               @Qualifier("fileDocumentStorageClient") StorageClient storageClient) {

        super(documentFileRepository);
        this.documentFileRepository = documentFileRepository;
        this.versionService = versionService;
        this.storageFileDocumentDirectoryPath = StoragePath.get(DIRECTORY, filesPath);
        this.fileDocumentStorageClient = storageClient;
    }

    @PostConstruct
    private void createFilesPathDirectories() {
        if(!fileDocumentStorageClient.exists(storageFileDocumentDirectoryPath)) {
            fileDocumentStorageClient.create(storageFileDocumentDirectoryPath);
        }
    }

    @Override
    public <T extends DocumentFile> List<DocumentFile> saveAll(List<T> saveUs, int docId) {
        setDocAndFileIds(saveUs, docId);
        resolveDuplicatedIds(saveUs);
        deleteNoMoreUsedDocumentFiles(saveUs, docId);
        saveNewFiles(saveUs);

        if(saveUs.isEmpty() && !getByDocId(docId).isEmpty()) super.updateWorkingVersion(docId); // when user deletes all files
        return saveDocumentFiles(saveUs);
    }

    @Override
    public <T extends DocumentFile> DocumentFile save(T saveMe) {
        super.updateWorkingVersion(saveMe.getDocId());
        return new DocumentFileDTO(documentFileRepository.save(new DocumentFileJPA(saveMe)));
    }

    @Override
    public void setAsWorkingVersion(Version version) {
        final List<DocumentFileJPA> documentFilesByVersion = documentFileRepository.findByDocIdAndVersionIndex(version.getDocId(), version.getNo());

        final List<DocumentFileJPA> saveDocumentFiles = new ArrayList<>();
        documentFilesByVersion.forEach(documentFile -> {
            final DocumentFileJPA docFileOne = new DocumentFileJPA(documentFile);
            docFileOne.setId(null);
            docFileOne.setVersionIndex(DocumentVersion.WORKING_VERSION_NO);
            saveDocumentFiles.add(docFileOne);
        });

        deleteNoMoreUsedDocumentFiles(saveDocumentFiles, version.getDocId());

        documentFileRepository.deleteByVersion(versionService.getDocumentWorkingVersion(version.getDocId()));
        documentFileRepository.flush();
        documentFileRepository.saveAll(saveDocumentFiles);

        super.updateWorkingVersion(version.getDocId());
    }

    @Override
    public List<DocumentFile> getByDocId(int docId) {
        return findWorkingVersionFiles(docId).stream()
                .map(documentFileJPA -> {
                    documentFileJPA.setInputStreamSource(getFileDocumentInputStreamSource(documentFileJPA));
                    return new DocumentFileDTO(documentFileJPA);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentFile> getByDocIdAndVersion(int docId, int versionNo) {
        return documentFileRepository.findByDocIdAndVersionIndex(docId, versionNo).stream()
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
    public List<DocumentFile> getPublicByDocId(int docId) {
        final int latestVersionIndex = versionService.getLatestVersion(docId).getNo();
        return documentFileRepository.findByDocIdAndVersionIndex(docId, latestVersionIndex).stream()
                .map(documentFileJPA -> {
                    documentFileJPA.setInputStreamSource(getFileDocumentInputStreamSource(documentFileJPA));
                    return new DocumentFileDTO(documentFileJPA);
                })
                .collect(Collectors.toList());
    }

    @Override
    public InputStreamSource getFileDocumentInputStreamSource(DocumentFile documentFile){
        StoragePath resultPath;

        StoragePath storagePathByName = storageFileDocumentDirectoryPath.resolve(FILE, documentFile.getFilename());
        if (fileDocumentStorageClient.exists(storagePathByName)) {
            resultPath = storagePathByName;

        } else {
            StoragePath storagePathById = storageFileDocumentDirectoryPath.resolve(FILE, documentFile.getFileId());
            if (fileDocumentStorageClient.exists(storagePathById)) {
                resultPath = storagePathById;

            } else {
                final String escapedFileId = FileUtility.escapeFilename(documentFile.getFileId());
                final String oldWayName = documentFile.getDocId() + "." + escapedFileId;
                StoragePath storagePathByIdOldWay = storageFileDocumentDirectoryPath.resolve(FILE, oldWayName);
                if (fileDocumentStorageClient.exists(storagePathByIdOldWay)) {
                    resultPath = storagePathByIdOldWay;

                } else {
                    return new EmptyInputStreamSource();
                }
            }
        }

        return new StorageInputStreamSource(resultPath, fileDocumentStorageClient);
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
        final List<DocumentFile> documentFiles = documentFileRepository.findByDocId(docIdToDelete).stream()
                .map(DocumentFileDTO::new)
                .collect(Collectors.toList());

        documentFileRepository.deleteByDocId(docIdToDelete);

        LOG.info("Deleting unnecessary files from a file document " + docIdToDelete);
        deleteNoMoreUsedFiles(documentFiles);
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
            documentFile.setOriginalFilename(StringUtils.defaultIfBlank(documentFile.getOriginalFilename(), documentFile.getFilename()));
            final String fileId = documentFile.getFileId();

            if (StringUtils.isBlank(fileId)) {
                documentFile.setFileId(documentFile.getFilename());
            }
        });
    }

    private <T extends DocumentFile> void deleteNoMoreUsedDocumentFiles(List<T> saveUs, int docId) {
        final Set<Integer> existingFileIds = saveUs.stream()
                .map(DocumentFile::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final List<DocumentFileJPA> noMoreNeededFiles = getByDocId(docId).stream()
                .filter(documentFile -> !existingFileIds.contains(documentFile.getId()))
                .map(DocumentFileJPA::new)
                .collect(Collectors.toList());

	    documentFileRepository.deleteAll(noMoreNeededFiles);
        LOG.info("Deleting unnecessary files from a file document " + docId);
        deleteNoMoreUsedFiles(noMoreNeededFiles);
    }

    private <T extends DocumentFile> void deleteNoMoreUsedFiles(List<T> noMoreNeededFiles){
        for(DocumentFile documentFile: noMoreNeededFiles){
            String filename = documentFile.getFilename();

            if(!documentFileRepository.existsByFilename(filename)){
                StoragePath storageFileDocumentPath = storageFileDocumentDirectoryPath.resolve(FILE, filename);
                if(fileDocumentStorageClient.exists(storageFileDocumentPath)){
                    fileDocumentStorageClient.delete(storageFileDocumentPath, false);
                }

                LOG.info(String.format("Deleting a file %s", storageFileDocumentPath));
            }
        }
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
            StoragePath destination = storageFileDocumentDirectoryPath.resolve(FILE, originalFilename);

            while (fileDocumentStorageClient.exists(destination)) {
                final String baseName = FilenameUtils.getBaseName(originalFilename);
                final String newName = baseName + copiesCount + "." + FilenameUtils.getExtension(originalFilename);
                destination = storageFileDocumentDirectoryPath.resolve(FILE, newName);
                copiesCount++;
            }

            if (documentFile.getFileId().equals(originalFilename))
                documentFile.setFileId(destination.getName());

            documentFile.setFilename(destination.getName());
            documentFile.setInputStreamSource(new StorageInputStreamSource(destination, fileDocumentStorageClient));

            try (InputStream multipartInputStream = file.getInputStream()){
                fileDocumentStorageClient.put(destination, multipartInputStream);
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
