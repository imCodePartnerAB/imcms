package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class})
public class VersionServiceTest {

    private final static int userId = 1;
    private final static int docId = 1001;

    @Autowired
    private VersionService versionService;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private VersionDataInitializer versionDataInitializer;
    private List<Version> versions;

    @Before
    public void setUpVersions() {
        versionDataInitializer.cleanRepositories();

        versionDataInitializer.createData(0, docId);
        versions = Arrays.asList(
                versionDataInitializer.createData(1, docId),
                versionDataInitializer.createData(2, docId),
                versionDataInitializer.createData(3, docId));
    }

    @After
    public void tearDown() {
        versionDataInitializer.cleanRepositories();
    }

    @Test
    public void testCreate() {
        Version version = versionService.create(docId, userId);

        assertNotNull(version);
        assertEquals(docId, version.getDocId().longValue());
        assertEquals(4, version.getNo());
    }

    @Test
    public void hasNewerVersion_When_VersionCreatedAfterDocModification() {
        assertFalse(versionService.hasNewerVersion(docId));
    }

    @Test
    public void hasNewerVersion_When_VersionCreatedBeforeDocModification() {
        final Version documentWorkingVersion = versionService.getDocumentWorkingVersion(docId);
        documentWorkingVersion.setModifiedDt(getTomorrowDate());
        versionRepository.save(documentWorkingVersion);

        assertTrue(versionService.hasNewerVersion(docId));
    }

    @Test
    public void hasNewerVersion_When_OnlyWorkingVersion() {
        versionRepository.delete(versions);
        assertTrue(versionService.hasNewerVersion(docId));
    }

    @Test
    @Transactional
    public void delete() {
        final List<Version> versions = versionService.findByDocId(docId);
        assertTrue(versions.size() > 0);

        versionService.deleteByDocId(docId);

        final List<Version> emptyList = versionService.findByDocId(docId);
        assertTrue(emptyList.isEmpty());
    }

    private Date getTomorrowDate() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
}
