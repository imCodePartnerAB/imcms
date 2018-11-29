package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class VersionRepositoryTest extends WebAppSpringTestConfig {
    private final static int userId = 1;
    private static int docId;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @BeforeEach
    public void setUpVersions() {
        versionDataInitializer.cleanRepositories();
        documentDataInitializer.cleanRepositories();

        docId = documentDataInitializer.createData().getId();

        versionDataInitializer.createData(1, docId);
        versionDataInitializer.createData(2, docId);
        versionDataInitializer.createData(3, docId);
        versionDataInitializer.createData(4, docId);
        versionDataInitializer.createData(5, docId);
    }

    @AfterEach
    public void tearDown() {
        versionDataInitializer.cleanRepositories();
    }

    @Test
    public void testFindByDocId() {
        assertEquals(6, versionRepository.findByDocId(docId).size());
    }

    @Test
    public void testFindByDocIdAndNo() {
        assertNotNull(versionRepository.findByDocIdAndNo(docId, 0));
        assertNotNull(versionRepository.findByDocIdAndNo(docId, 1));
    }

    @Test
    public void testFindLatestVersion() {
        Version version = versionRepository.findLatest(docId);

        assertNotNull(version);
        assertEquals(docId, version.getDocId().longValue());
        assertEquals(5, version.getNo());
    }

    @Test
    public void testFindDefaultVersion() {
        Version version = versionRepository.findDefault(docId);

        assertNotNull(version);
        assertEquals(docId, version.getDocId().longValue());
        assertEquals(0, version.getNo());
    }

    @Test
    @Transactional
    public void testSetDefault() {
        assertEquals(0, versionRepository.findDefault(docId).getNo());

        versionRepository.updateDefaultNo(docId, 4, userId);

        Version version = versionRepository.findDefault(docId);

        assertNotNull(version);
        assertEquals(docId, version.getDocId().longValue());
        assertEquals(4, version.getNo());
        assertEquals(versionDataInitializer.getUser(), version.getModifiedBy());
    }
}
