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
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private Resource filesPath;

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
    public void clearTestFiles() throws IOException {
        File[] testFiles = filesPath.getFile().listFiles();
        for(File file: testFiles){
            file.delete();
        }
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
    public void publishDocumentFiles() {
        final DocumentFile futurePublicFile = documentFiles.get(0);
        futurePublicFile.setDefaultFile(true); // one has to be default
        futurePublicFile.setFilename("test" + System.currentTimeMillis());

        documentFileService.saveAll(documentFiles, docId);
        versionDataInitializer.createData(Version.WORKING_VERSION_INDEX + 1, docId);

        documentFileService.publishDocumentFiles(docId);

        final DocumentFile publicFile = documentFileService.getPublicByDocId(docId);

        assertNotNull(publicFile);
        assertNotNull(publicFile.getId());
        assertTrue(publicFile.isDefaultFile());
        assertEquals(publicFile.getFilename(), futurePublicFile.getFilename());
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

        File testFile = new File(filesPath.getFile(), filename);
        assertTrue(testFile.exists());
        assertArrayEquals(content, FileUtils.readFileToByteArray(testFile));
    }

    @Test
    public void saveAll_When_DocumentFileReplaced_And_MultipartFileSet_Expect_OldFileDeleted_And_NewFileSaved() throws IOException {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();
        final File testFile = new File(filesPath.getFile(), filename);

        final String filename2 = "test_name2.txt";
        final byte[] content2 = "test text2".getBytes();
        final File testFile2 = new File(filesPath.getFile(), filename2);

        final DocumentFile documentFile = documentFiles.get(0);

        documentFile.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFile), docId);

        assertTrue(testFile.exists());

        final DocumentFile documentFile2 = documentFiles.get(1);
        documentFile2.setMultipartFile(new MockMultipartFile("files", filename2, null, content2));
        documentFileService.saveAll(Collections.singletonList(documentFile2), docId);

        final List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(1, savedDocumentFiles.size());
        savedDocumentFiles.forEach(docFile -> docFile.setId(null));
        assertEquals(documentFile2, savedDocumentFiles.get(0));

        assertFalse(testFile.exists());
        assertTrue(testFile2.exists());
        assertArrayEquals(content2, FileUtils.readFileToByteArray(testFile2));
    }

    @Test
    public void saveAll_When_DocumentFileReplaced_And_DocumentVersionHasPreviosDocumentFile_And_NewMultipartFileSet_Expect_PreviosFileLeft_And_NewFileSaved() throws IOException {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();
        final File testFile = new File(filesPath.getFile(), filename);

        final String filename2 = "test_name2.txt";
        final byte[] content2 = "test text2".getBytes();
        final File testFile2 = new File(filesPath.getFile(), filename2);

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

        assertTrue(testFile.exists());
        assertArrayEquals(content, FileUtils.readFileToByteArray(testFile));
        assertTrue(testFile2.exists());
        assertArrayEquals(content2, FileUtils.readFileToByteArray(testFile2));
    }

    @Test
    public void saveAll_When_ListContaintsOldAndNewDocumentFiles_And_NewMultipartFileSet_Expect_OldFileLeft_And_NewFileSaved() throws IOException {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();
        final File testFile = new File(filesPath.getFile(), filename);

        final String filename2 = "test_name2.txt";
        final byte[] content2 = "test text2".getBytes();
        final File testFile2 = new File(filesPath.getFile(), filename2);

        final DocumentFile documentFile = documentFiles.get(0);

        documentFile.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFile), docId);

        assertTrue(testFile.exists());

        final DocumentFile documentFile2 = documentFiles.get(1);
        documentFile2.setMultipartFile(new MockMultipartFile("files", filename2, null, content2));

        List<DocumentFile> documentFiles = List.of(documentFile, documentFile2);
        documentFileService.saveAll(documentFiles, docId);

        final List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(2, savedDocumentFiles.size());
        savedDocumentFiles.forEach(docFile -> docFile.setId(null));
        assertTrue(savedDocumentFiles.containsAll(documentFiles));

        assertTrue(testFile.exists());
        assertTrue(testFile2.exists());
        assertArrayEquals(content, FileUtils.readFileToByteArray(testFile));
        assertArrayEquals(content2, FileUtils.readFileToByteArray(testFile2));
    }

    @Test
    public void deleteByDocId_Expect_DocumentFilesDeleted() throws IOException {
        final String filename = "test_name.txt";
        final byte[] content = "test text".getBytes();
        final File testFile = new File(filesPath.getFile(), filename);

        final DocumentFile documentFile = documentFiles.get(0);
        documentFile.setMultipartFile(new MockMultipartFile("files", filename, null, content));
        documentFileService.saveAll(Collections.singletonList(documentFile), docId);

        final List<DocumentFileJPA> savedDocumentFiles = documentFileRepository.findAll();
        assertEquals(1, savedDocumentFiles.size());

        documentFileService.deleteByDocId(docId);

        assertTrue(documentFileRepository.findAll().isEmpty());
        assertFalse(testFile.exists());
    }
}