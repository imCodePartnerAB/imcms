package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.jpa.doc.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
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

}
