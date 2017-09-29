package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.dto.LoopDTO;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.util.datainitializer.VersionDataInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class LoopDtoToLoopTest {
    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_ID = 1;
    private static final int TEST_VERSION_NO = 0;

    @Autowired
    private BiFunction<LoopDTO, Version, Loop> mapper;
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
    public void expectedEqualsMapResult() throws Exception {
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, TEST_LOOP_ID, Collections.emptyList());
        final Version workingVersion = versionRepository.findByDocIdAndNo(TEST_DOC_ID, TEST_VERSION_NO);
        final Loop expected = new Loop(workingVersion, TEST_LOOP_ID, 1, Collections.emptyList());

        assertEquals(expected, mapper.apply(loopDTO, workingVersion));
    }
}
