package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class DocumentFileRepositoryTest extends WebAppSpringTestConfig {

    @Autowired
    private DocumentFileRepository documentFileRepository;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    private DocumentFileJPA createdDocumentFile;

    @BeforeEach
    public void setUp() {
        final DocumentDTO documentDTO = documentDataInitializer.createData();

        final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
        documentFileJPA.setDocId(documentDTO.getId());
        documentFileJPA.setVersionIndex(Version.WORKING_VERSION_INDEX);
        documentFileJPA.setFileId("test_id");
        documentFileJPA.setFilename("test_name");
        documentFileJPA.setOriginalFilename("test_name");
        documentFileJPA.setMimeType("test");

        createdDocumentFile = documentFileRepository.save(documentFileJPA);
    }

    @Test
    public void findByDocId() {
        final List<DocumentFileJPA> foundFiles = documentFileRepository.findByDocId(createdDocumentFile.getDocId());
        assertEquals(foundFiles.size(), 1);
        assertTrue(foundFiles.contains(createdDocumentFile));
    }

    @Test
    public void findByDocIdAndVersionIndex() {
        final int firstVersionIndex = 0;
        final int lastVersionIndex = 3;
        final int maxItems = 10;
        final Integer docId = documentDataInitializer.createData().getId();
        versionDataInitializer.createData(firstVersionIndex, docId);
        versionDataInitializer.createData(lastVersionIndex, docId);

        IntStream.rangeClosed(1, maxItems).forEach(value -> {
            final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
            documentFileJPA.setDocId(docId);
            documentFileJPA.setVersionIndex((value % 2 == 0) ? firstVersionIndex : lastVersionIndex);
            documentFileJPA.setFileId("test_id" + value);
            documentFileJPA.setFilename("test_name" + value);
            documentFileJPA.setOriginalFilename("test_name" + value);
            documentFileJPA.setMimeType("test" + value);

            documentFileRepository.save(documentFileJPA);
        });

        final List<DocumentFileJPA> firstVersionFiles = documentFileRepository.findByDocIdAndVersionIndex(
                docId, firstVersionIndex
        );

        assertNotNull(firstVersionFiles);
        assertFalse(firstVersionFiles.isEmpty());
        assertEquals(firstVersionFiles.size(), maxItems / 2);
        assertEquals(firstVersionFiles.get(0).getVersionIndex(), firstVersionIndex);

        final List<DocumentFileJPA> lastVersionFiles = documentFileRepository.findByDocIdAndVersionIndex(
                docId, lastVersionIndex
        );

        assertNotNull(lastVersionFiles);
        assertFalse(lastVersionFiles.isEmpty());
        assertEquals(lastVersionFiles.size(), maxItems / 2);
        assertEquals(lastVersionFiles.get(0).getVersionIndex(), lastVersionIndex);
    }

    @Test
    public void findByDocIdAndVersionIndex_When_NothingInDB_Expect___() {
        documentFileRepository.deleteAll();
        final List<DocumentFileJPA> emptyList = documentFileRepository.findByDocIdAndVersionIndex(
                createdDocumentFile.getDocId(), 0
        );

        assertNotNull(emptyList);
        assertTrue(emptyList.isEmpty());
    }

    @Test
    public void findDefaultByDocIdAndVersionIndex() {
        final int firstVersionIndex = 0;
        final int lastVersionIndex = 3;
        final int maxItems = 10;
        final Integer docId = documentDataInitializer.createData().getId();
        versionDataInitializer.createData(firstVersionIndex, docId);
        versionDataInitializer.createData(lastVersionIndex, docId);

        IntStream.range(1, maxItems).forEach(value -> {
            final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
            documentFileJPA.setDocId(docId);
            documentFileJPA.setVersionIndex((value % 2 == 0) ? firstVersionIndex : lastVersionIndex);
            documentFileJPA.setFileId("test_id" + value);
            documentFileJPA.setFilename("test_name" + value);
            documentFileJPA.setOriginalFilename("test_name" + value);
            documentFileJPA.setMimeType("test" + value);

            documentFileRepository.save(documentFileJPA);
        });

        final String specialFileName = "SPECIAL";

        for (int versionIndex : new int[]{firstVersionIndex, lastVersionIndex}) {
            final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
            documentFileJPA.setDocId(docId);
            documentFileJPA.setVersionIndex(versionIndex);
            documentFileJPA.setFileId("test_id");
            documentFileJPA.setFilename(specialFileName);
            documentFileJPA.setOriginalFilename(specialFileName);
            documentFileJPA.setMimeType("test");
            documentFileJPA.setDefaultFile(true); // main thing for this test

            documentFileRepository.save(documentFileJPA);

            final DocumentFileJPA defaultFile = documentFileRepository.findDefaultByDocIdAndVersionIndex(
                    docId, firstVersionIndex
            );

            assertNotNull(defaultFile);
            assertTrue(defaultFile.isDefaultFile());
            assertEquals(defaultFile.getFilename(), documentFileJPA.getFilename());
        }
    }

    @Test
    public void findByVersion() {
        documentFileRepository.deleteAll();
        assertTrue(documentFileRepository.findAll().isEmpty());

        final int firstVersionIndex = 0;
        final int secondVersionIndex = 13;
        final int maxItems = 10;

        final Integer firstDocId = documentDataInitializer.createData().getId();
        final Integer secondDocId = documentDataInitializer.createData().getId();

        final Version[] versions = {
                versionDataInitializer.createData(firstVersionIndex, firstDocId),
                versionDataInitializer.createData(secondVersionIndex, firstDocId),
                versionDataInitializer.createData(firstVersionIndex, secondDocId),
                versionDataInitializer.createData(secondVersionIndex, secondDocId)
        };

        int totalAmount = 0;

        for (Version version : versions) {
            IntStream.range(1, maxItems).forEach(value -> {
                final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
                documentFileJPA.setDocId(version.getDocId());
                documentFileJPA.setVersionIndex(version.getNo());
                documentFileJPA.setFileId("test_id" + value);
                documentFileJPA.setFilename("test_name" + value);
                documentFileJPA.setOriginalFilename("test_name" + value);
                documentFileJPA.setMimeType("test" + value);

                documentFileRepository.save(documentFileJPA);
            });

            totalAmount += documentFileRepository.findByVersion(version).size();
        }

        final List<DocumentFileJPA> all = documentFileRepository.findAll();

        assertNotEquals(totalAmount, 0);
        assertFalse(all.isEmpty());
        assertEquals(totalAmount, all.size());
    }
}
