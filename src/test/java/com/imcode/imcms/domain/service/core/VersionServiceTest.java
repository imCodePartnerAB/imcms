package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.service.UserService;
import com.imcode.imcms.domain.service.VersionService;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class VersionServiceTest extends WebAppSpringTestConfig {

    private final static int userId = 1;
    private final static int docId = 1001;

    @Autowired
    private VersionService versionService;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    private Version workingVersion;
    private List<Version> versions;

    @BeforeEach
    public void setUpVersions() {
        versionDataInitializer.cleanRepositories();

        workingVersion = versionDataInitializer.createData(0, docId);

        versions = Arrays.asList(
                versionDataInitializer.createData(1, docId),
                versionDataInitializer.createData(2, docId),
                versionDataInitializer.createData(3, docId));
    }

    @AfterEach
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
    public void delete() {
        final List<Version> versions = versionService.findByDocId(docId);
        assertTrue(versions.size() > 0);

        versionService.deleteByDocId(docId);

        final List<Version> emptyList = versionService.findByDocId(docId);
        assertTrue(emptyList.isEmpty());
    }

    @Test
    public void getLatestVersion_When_VersioningIsNotAllowed_Expect_WorkingVersionIsReturned() {
        final DefaultVersionService defaultVersionService = new DefaultVersionService(
                versionRepository, userService, false);

        final Version latestVersion = defaultVersionService.getLatestVersion(docId);

        assertTrue(versionRepository.findAll().size() > 1);
        assertThat(latestVersion, is(workingVersion));
    }

    @Test
    public void findByDocId_When_VersioningIsNotAllowed_Expect_OnlyWorkingVersionIsReturned() {
        final DefaultVersionService defaultVersionService = new DefaultVersionService(
                versionRepository, userService, false);

        final List<Version> byDocId = defaultVersionService.findByDocId(docId);

        assertTrue(versionRepository.findAll().size() > 1);
        assertThat(byDocId, hasSize(1));
        assertThat(byDocId.get(0), is(workingVersion));
    }

    @Test
    public void hasNewerVersion_When_VersioningIsNotAllowed_Expect_FalseIsReturned() {
        final DefaultVersionService defaultVersionService = new DefaultVersionService(
                versionRepository, userService, false);

        assertFalse(defaultVersionService.hasNewerVersion(docId));
    }

    @Test
    public void findDefault_When_VersioningIsNotAllowed_Expect_WorkingVersionIsReturned() {
        final DefaultVersionService defaultVersionService = new DefaultVersionService(
                versionRepository, userService, false);

        final Version defaultVersion = defaultVersionService.findDefault(docId);

        assertThat(defaultVersion, is(workingVersion));
    }

    private Date getTomorrowDate() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }
}
