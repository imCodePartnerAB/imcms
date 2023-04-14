package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.FileDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.model.DocumentFile;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentFileRepository;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StorageFile;
import com.imcode.imcms.storage.StoragePath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class DocumentFileServiceTest extends WebAppSpringTestConfig {

    private Integer docId;
    private List<DocumentFile> documentFiles;

    @Autowired
    private DocumentFileService documentFileService;

    @Autowired
    private DelegatingByTypeDocumentService documentService;

    @Autowired
    private VersionService versionService;

    @Autowired
    private DocumentFileRepository documentFileRepository;

    @Autowired
    private FileDocumentDataInitializer documentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Value("${FilePath}")
    private String filesPath;

    @Autowired
    @Qualifier("fileDocumentStorageClient")
    private StorageClient storageClient;

    @BeforeEach
    public void setUp() {
        documentFileRepository.deleteAll();
        docId = documentDataInitializer.createFileDocument().getId();
        documentFiles = IntStream.rangeClosed(0, 10)
                .mapToObj(value -> {
                    final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
                    documentFileJPA.setDocId(docId);
                    documentFileJPA.setVersionIndex(Version.WORKING_VERSION_INDEX);
                    documentFileJPA.setFileId("test_id_" + value);
                    documentFileJPA.setFilename("test_name" + value);
                    documentFileJPA.setMimeType("test" + value);

                    return (DocumentFile) documentFileJPA;
                })
                .collect(Collectors.toList());
    }

    @AfterEach
    public void clearTestFiles() {
        storageClient.listPaths(StoragePath.get(DIRECTORY, filesPath))
                .forEach(path -> storageClient.delete(path, true));
    }

    @Test
    public void saveAll() {
        documentFileService.saveAll(documentFiles, docId);

        final List<DocumentFileJPA> saved = documentFileRepository.findAll();

        assertNotNull(saved);
        assertEquals(documentFiles.size(), saved.size());
    }

    @Test
    public void getByDocId() {
        final List<DocumentFile> saved = documentFileService.saveAll(documentFiles, docId);
        final List<DocumentFile> found = documentFileService.getByDocId(docId);

        assertTrue(saved.containsAll(found));
    }

    @Test
    public void getByDocIdAndVersion_Expected_DocumentFilesOfSpecificVersion() {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();

        final String filenameVersion1 = "test_name1.txt";
        final byte[] contentVersion1 = "test text1".getBytes();

        final DocumentFile documentFileVersion1 = documentFiles.get(0);
        documentFileVersion1.setMultipartFile(new MockMultipartFile("files", filenameVersion1, null, contentVersion1));
        documentFileService.saveAll(Collections.singletonList(documentFileVersion1), docId);

        List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(1, savedDocumentFiles.size());

        //Create new version and save document file with version
        DocumentFileJPA versionedDocumentFile = savedDocumentFiles.get(0);
        int publishVersionNo = versionService.create(docId, 1).getNo();
        versionedDocumentFile.setVersionIndex(publishVersionNo);
        documentFileRepository.save(versionedDocumentFile);

        final DocumentFile documentFileWorkingVersion = documentFiles.get(1);
        documentFileWorkingVersion.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFileWorkingVersion), docId);

        final List<DocumentFile> byDocIdAndVersion = documentFileService.getByDocIdAndVersion(docId, publishVersionNo);

        assertEquals(byDocIdAndVersion.size(), 1);
        assertEquals(new DocumentFileDTO(versionedDocumentFile), byDocIdAndVersion.get(0));
    }

    @Test
    public void getByDocIdAndVersion_When_NoDocumentFilesWithSpecificVersion_Expected_EmptyList() {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();

        final DocumentFile documentFileVersion1 = documentFiles.get(0);
        documentFileVersion1.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFileVersion1), docId);

        List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(1, savedDocumentFiles.size());

        int publishVersionNo = versionService.create(docId, 1).getNo();

        final List<DocumentFile> byDocIdAndVersion = documentFileService.getByDocIdAndVersion(docId, publishVersionNo);
        assertTrue(byDocIdAndVersion.isEmpty());
    }

    @Test
    public void publishDocumentFiles() {
        final DocumentFile futurePublicFile = documentFiles.get(0);
        futurePublicFile.setDefaultFile(true); // one has to be default
        futurePublicFile.setFilename("test" + System.currentTimeMillis());

        documentFileService.saveAll(documentFiles, docId);
        versionDataInitializer.createData(Version.WORKING_VERSION_INDEX + 1, docId);

        documentFileService.publishDocumentFiles(docId);

        final List<DocumentFile> publicByDocIdList = documentFileService.getPublicByDocId(docId);
        assertNotNull(publicByDocIdList);
        assertEquals(publicByDocIdList.size(), documentFiles.size());
    }

    @Test
    public void save_When_DocumentAlreadyHaveFile_Expect_OldRemovedAndNewSaved() {
        final FileDocumentDTO document = documentDataInitializer.createFileDocument();

        final List<DocumentFile> oldFiles = new ArrayList<>(document.getFiles());
        assertNotNull(oldFiles);
        assertFalse(oldFiles.isEmpty());

        final Integer documentId = document.getId();
        final List<DocumentFile> receivedFiles = documentFileService.getByDocId(documentId);

        assertEquals(receivedFiles.size(), oldFiles.size());
        assertTrue(receivedFiles.containsAll(oldFiles));

        // new
        final String fileName = "new_file";

        final DocumentFileDTO newFile = new DocumentFileDTO();
        newFile.setDocId(documentId);
        newFile.setFileId(fileName);
        newFile.setFilename(fileName);
        newFile.setMimeType("test");
        newFile.setDefaultFile(true);

        List<DocumentFile> newFiles = new ArrayList<>();
        newFiles.add(newFile);

        newFiles = documentFileService.saveAll(newFiles, documentId);

        final List<DocumentFile> receivedNewFiles = documentFileService.getByDocId(documentId);

        assertEquals(receivedNewFiles.size(), newFiles.size());
        assertTrue(receivedNewFiles.containsAll(newFiles));
    }

    @Test
    public void save_When_NullFileIdSet_Expect_IdCopiedFromNameWithIndex() {
        final FileDocumentDTO document = documentDataInitializer.createFileDocument();
        final Integer documentId = document.getId();
        final String fileName = "new_file";

        List<DocumentFile> newFiles = IntStream.range(0, 5)
                .mapToObj(value -> {
                    final DocumentFileDTO newFile = new DocumentFileDTO();
                    newFile.setDocId(documentId);
                    newFile.setFileId(null); // the main point
                    newFile.setFilename(fileName);
                    newFile.setMimeType("test");
                    newFile.setDefaultFile(true);

                    return newFile;
                })
                .collect(Collectors.toList());

        newFiles = documentFileService.saveAll(newFiles, documentId);

        final List<DocumentFile> receivedNewFiles = documentFileService.getByDocId(documentId);

        assertEquals(receivedNewFiles.size(), newFiles.size());
        assertTrue(receivedNewFiles.containsAll(newFiles));

        receivedNewFiles.forEach(documentFile -> assertTrue(
                documentFile.getFileId().contains(documentFile.getFilename())
        ));
    }

    @Test
    public void saveAll_When_MultipartFileSet_Expect_FileSaved() throws IOException {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();

        final DocumentFile documentFile = documentFiles.get(0);
        documentFile.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFile), docId);

        final List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(1, savedDocumentFiles.size());
        savedDocumentFiles.forEach(docFile -> docFile.setId(null));
        assertEquals(documentFile, savedDocumentFiles.get(0));

        StoragePath testFilePath = StoragePath.get(FILE, filesPath, filename);
        assertTrue(storageClient.exists(testFilePath));
        try(final StorageFile testFile = storageClient.getFile(testFilePath)){
            assertArrayEquals(content, testFile.getContent().readAllBytes());
        }
    }

    @Test
    public void saveAll_When_DocumentFileReplaced_And_MultipartFileSet_Expect_OldFileDeleted_And_NewFileSaved() throws IOException {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();
        StoragePath testFilePath = StoragePath.get(FILE, filesPath, filename);

        final String filename2 = "test_name2.txt";
        final byte[] content2 = "test text2".getBytes();
        StoragePath testFilePath2 = StoragePath.get(FILE, filesPath, filename2);

        final DocumentFile documentFile = documentFiles.get(0);

        documentFile.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFile), docId);

        assertTrue(storageClient.exists(testFilePath));

        final DocumentFile documentFile2 = documentFiles.get(1);
        documentFile2.setMultipartFile(new MockMultipartFile("files", filename2, null, content2));
        documentFileService.saveAll(Collections.singletonList(documentFile2), docId);

        final List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(1, savedDocumentFiles.size());
        savedDocumentFiles.forEach(docFile -> docFile.setId(null));
        assertEquals(documentFile2, savedDocumentFiles.get(0));

        assertFalse(storageClient.exists(testFilePath));
        assertTrue(storageClient.exists(testFilePath2));
        try(final StorageFile testFile2 = storageClient.getFile(testFilePath2)){
            assertArrayEquals(content2, testFile2.getContent().readAllBytes());
        }
    }

    @Test
    public void saveAll_When_DocumentFileReplaced_And_DocumentVersionHasPreviousDocumentFile_And_NewMultipartFileSet_Expect_PreviousFileLeft_And_NewFileSaved() throws IOException {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();
        StoragePath testFilePath = StoragePath.get(FILE, filesPath, filename);

        final String filename2 = "test_name2.txt";
        final byte[] content2 = "test text2".getBytes();
        StoragePath testFilePath2 = StoragePath.get(FILE, filesPath, filename2);

        versionService.create(docId, 1);

        final DocumentFile documentFile = documentFiles.get(0);
        documentFile.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFile), docId);

        List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(1, savedDocumentFiles.size());

        //Create new version and save document file with version
        DocumentFileJPA versionedDocumentFile = savedDocumentFiles.get(0);
        int publishVersionNo = versionService.create(docId, 1).getNo();
        versionedDocumentFile.setVersionIndex(publishVersionNo);
        documentFileRepository.save(versionedDocumentFile);

        final DocumentFile documentFile2 = documentFiles.get(1);
        documentFile2.setMultipartFile(new MockMultipartFile("files", filename2, null, content2));
        documentFileService.saveAll(Collections.singletonList(documentFile2), docId);

        savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(2, savedDocumentFiles.size());
        savedDocumentFiles.forEach(docFile -> docFile.setId(null));
        assertTrue(savedDocumentFiles.containsAll(List.of(versionedDocumentFile, documentFile2)));

        assertTrue(storageClient.exists(testFilePath));
        assertTrue(storageClient.exists(testFilePath2));
        try(final StorageFile testFile = storageClient.getFile(testFilePath);
            final StorageFile testFile2 = storageClient.getFile(testFilePath2)){
            assertArrayEquals(content, testFile.getContent().readAllBytes());
            assertArrayEquals(content2, testFile2.getContent().readAllBytes());
        }
    }

    @Test
    public void saveAll_When_ListContainsOldAndNewDocumentFiles_And_NewMultipartFileSet_Expect_OldFileLeft_And_NewFileSaved() throws IOException {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();
        StoragePath testFilePath = StoragePath.get(FILE, filesPath, filename);

        final String filename2 = "test_name2.txt";
        final byte[] content2 = "test text2".getBytes();
        StoragePath testFilePath2 = StoragePath.get(FILE, filesPath, filename2);

        final DocumentFile documentFile = documentFiles.get(0);

        documentFile.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFile), docId);

        assertTrue(storageClient.exists(testFilePath));

        final DocumentFile documentFile2 = documentFiles.get(1);
        documentFile2.setMultipartFile(new MockMultipartFile("files", filename2, null, content2));

        List<DocumentFile> documentFiles = List.of(documentFile, documentFile2);
        documentFileService.saveAll(documentFiles, docId);

        final List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(2, savedDocumentFiles.size());
        savedDocumentFiles.forEach(docFile -> docFile.setId(null));
        assertTrue(savedDocumentFiles.containsAll(documentFiles));

        assertTrue(storageClient.exists(testFilePath));
        assertTrue(storageClient.exists(testFilePath2));
        try(final StorageFile testFile = storageClient.getFile(testFilePath);
            final StorageFile testFile2 = storageClient.getFile(testFilePath2)){
            assertArrayEquals(content, testFile.getContent().readAllBytes());
            assertArrayEquals(content2, testFile2.getContent().readAllBytes());
        }
    }

    @Test
    public void setAsWorkingVersion_Expected_CopyDocumentFilesFromSpecificVersionToWorkingVersion() throws IOException {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();
        StoragePath testFilePath = StoragePath.get(FILE, filesPath, filename);

        final String filenameVersion1 = "test_name1.txt";
        final byte[] contentVersion1 = "test text1".getBytes();
        StoragePath testFileVersion1Path = StoragePath.get(FILE, filesPath, filenameVersion1);

        final DocumentFile documentFileVersion1 = documentFiles.get(0);
        documentFileVersion1.setMultipartFile(new MockMultipartFile("files", filenameVersion1, null, contentVersion1));
        documentFileService.saveAll(Collections.singletonList(documentFileVersion1), docId);

        List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(1, savedDocumentFiles.size());

        //Create new version and save document file with version
        DocumentFileJPA versionedDocumentFile = savedDocumentFiles.get(0);
        Version publishVersion = versionService.create(docId, 1);
        versionedDocumentFile.setVersionIndex(publishVersion.getNo());
        documentFileRepository.save(versionedDocumentFile);

        final DocumentFile documentFileWorkingVersion = documentFiles.get(1);
        documentFileWorkingVersion.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFileWorkingVersion), docId);

        final List<DocumentFileJPA> docFileBeforeReset = documentFileRepository.findByDocIdAndVersionIndex(docId, Version.WORKING_VERSION_INDEX);

        documentFileService.setAsWorkingVersion(publishVersion);

        final List<DocumentFileJPA> docFileAfterReset = documentFileRepository.findByDocIdAndVersionIndex(docId, Version.WORKING_VERSION_INDEX);

        //check the previous file has been deleted (because working and previous versions don't have it)
        assertNotEquals(docFileBeforeReset, docFileAfterReset);
        //check the file from version exists
        assertFalse(storageClient.exists(testFilePath));

        assertTrue(storageClient.exists(testFileVersion1Path));
        try(final StorageFile testFile = storageClient.getFile(testFileVersion1Path)){
            assertArrayEquals(contentVersion1, testFile.getContent().readAllBytes());
        }
    }

    @Test
    public void setAsWorkingVersion_When_noDocumentFilesWithSpecificVersion_Expected_WorkingVersionHasNoFiles() {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();
        StoragePath testFilePath = StoragePath.get(FILE, filesPath, filename);

        final DocumentFile documentFileVersion1 = documentFiles.get(0);
        documentFileVersion1.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFileVersion1), docId);

        List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(1, savedDocumentFiles.size());

        Version publishVersion = versionService.create(docId, 1);
        documentFileService.setAsWorkingVersion(publishVersion);

        assertTrue(documentFileRepository.findAll().isEmpty());
        assertFalse(storageClient.exists(testFilePath));
    }

    @Test
    public void deleteByDocId_Expect_DocumentFilesDeleted() {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();
        StoragePath testFilePath = StoragePath.get(FILE, filesPath, filename);

        final DocumentFile documentFile = documentFiles.get(0);
        documentFile.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFile), docId);

        final List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(1, savedDocumentFiles.size());

        documentFileService.deleteByDocId(docId);

        assertTrue(documentFileRepository.findAll().isEmpty());
        assertFalse(storageClient.exists(testFilePath));
    }
}