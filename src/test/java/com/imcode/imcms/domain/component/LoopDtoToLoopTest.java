package com.imcode.imcms.domain.component;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.LoopJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class LoopDtoToLoopTest {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_INDEX = 1;
    private static final int TEST_VERSION_NO = 0;

    @Autowired
    private VersionDataInitializer versionDataInitializer;
    @Autowired
    private VersionRepository versionRepository;

    @Before
    public void saveData() {
        clearTestData();
        versionDataInitializer.createData(TEST_VERSION_NO, TEST_DOC_ID);
    }

    @After
    public void clearTestData() {
        versionDataInitializer.cleanRepositories();
    }

    @Test
    public void expectedEqualsMapResult() {
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, TEST_LOOP_INDEX, Collections.emptyList());
        final Version workingVersion = versionRepository.findByDocIdAndNo(TEST_DOC_ID, TEST_VERSION_NO);
        final LoopJPA expected = LoopJPA.emptyLoop(workingVersion, TEST_LOOP_INDEX);

        assertEquals(expected, new LoopJPA(loopDTO, workingVersion));
    }
}
