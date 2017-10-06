package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.Loop;
import com.imcode.imcms.util.datainitializer.LoopDataInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class LoopRepositoryTest {

    private static final int DOC_ID = 1001;
    private static final int VERSION_NO = 0;

    @Inject
    private VersionRepository versionRepository;

    @Inject
    private LoopRepository loopRepository;

    @Autowired
    private LoopDataInitializer loopDataInitializer;

    @Before
    public void recreateLoops() {
        loopDataInitializer.cleanRepositories();

        final List<LoopEntryDTO> oneEntry = Collections.singletonList(new LoopEntryDTO(1));
        final List<LoopEntryDTO> twoEntries = Arrays.asList(new LoopEntryDTO(1), new LoopEntryDTO(2));
        final List<LoopEntryDTO> threeEntries = Arrays.asList(
                new LoopEntryDTO(1),
                new LoopEntryDTO(2),
                new LoopEntryDTO(3)
        );

        loopDataInitializer.createData(new LoopDTO(DOC_ID, 1, oneEntry));
        loopDataInitializer.createData(new LoopDTO(DOC_ID, 2, twoEntries));
        loopDataInitializer.createData(new LoopDTO(DOC_ID, 3, threeEntries));
    }

    @Test
    public void testFindByDocVersionExpectCorrectResultSize() {
        Version version = versionRepository.findByDocIdAndNo(DOC_ID, VERSION_NO);
        List<Loop> loops = loopRepository.findByVersion(version);

        assertThat(loops.size(), is(3));
    }

    @Test
    public void testFindByDocVersionAndNoExpectNotNullResults() {
        Version version = versionRepository.findByDocIdAndNo(DOC_ID, VERSION_NO);
        Loop loop1 = loopRepository.findByVersionAndIndex(version, 1);
        Loop loop2 = loopRepository.findByVersionAndIndex(version, 2);
        Loop loop3 = loopRepository.findByVersionAndIndex(version, 3);

        assertNotNull(loop1);
        assertNotNull(loop2);
        assertNotNull(loop3);
    }

    @Test
    public void testSavedDataExpectEqualNoAndDocId() {
        Version version = versionRepository.findByDocIdAndNo(DOC_ID, VERSION_NO);
        Loop loop1 = loopRepository.findByVersionAndIndex(version, 1);

        assertEquals(loop1.getIndex(), Integer.valueOf(1));
        assertEquals(loop1.getVersion().getDocId(), Integer.valueOf(DOC_ID));
    }
}
