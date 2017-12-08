package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.service.VersionService;
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

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class VersionServiceTest {

    private final static int userId = 1;
    private final static int docId = 1001;

    @Autowired
    private VersionService versionService;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Before
    public void setUpVersions() {
        versionDataInitializer.cleanRepositories();

        versionDataInitializer.createData(0, docId);
        versionDataInitializer.createData(1, docId);
        versionDataInitializer.createData(2, docId);
        versionDataInitializer.createData(3, docId);
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
    @Transactional
    public void delete() {
        final List<Version> versions = versionService.findByDocId(docId);
        assertTrue(versions.size() > 0);

        versionService.deleteByDocId(docId);

        final List<Version> emptyList = versionService.findByDocId(docId);
        assertTrue(emptyList.isEmpty());
    }
}
