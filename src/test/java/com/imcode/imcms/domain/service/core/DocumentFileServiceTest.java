package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.FileDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.model.DocumentFile;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentFileRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class DocumentFileServiceTest {

    private Integer docId;
    private List<DocumentFile> documentFiles;

    @Autowired
    private DocumentFileService documentFileService;

    @Autowired
    private DocumentFileRepository documentFileRepository;

    @Autowired
    private FileDocumentDataInitializer documentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Before
    public void setUp() throws Exception {
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
    public void deleteByDocId() {
        // todo: cover when there will be implementation
    }
}