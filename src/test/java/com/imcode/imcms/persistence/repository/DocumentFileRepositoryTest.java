package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class DocumentFileRepositoryTest {

    @Autowired
    private DocumentFileRepository documentFileRepository;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    private DocumentFileJPA createdDocumentFile;

    @Before
    public void setUp() throws Exception {
        final DocumentDTO documentDTO = documentDataInitializer.createData();

        final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
        documentFileJPA.setDocId(documentDTO.getId());
        documentFileJPA.setVersionIndex(Version.WORKING_VERSION_INDEX);
        documentFileJPA.setFileId("test_id");
        documentFileJPA.setFilename("test_name");
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
}
