package com.imcode.imcms.api.service;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.dto.LoopDTO;
import com.imcode.imcms.mapping.dto.LoopEntryDTO;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopRepository;
import com.imcode.imcms.service.LoopService;
import com.imcode.imcms.util.RepositoryTestDataCleaner;
import com.imcode.imcms.util.Value;
import com.imcode.imcms.util.datainitializer.VersionDataInitializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class LoopServiceTest {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_ID = 1;
    private static final int TEST_VERSION_NO = 0;

    private static final LoopDTO TEST_LOOP_DTO = new LoopDTO(TEST_DOC_ID, TEST_LOOP_ID, Collections.emptyList());

    private RepositoryTestDataCleaner testDataCleaner;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private LoopService loopService;

    @Autowired
    private LoopRepository loopRepository;

    @PostConstruct
    public void init() {
        testDataCleaner = new RepositoryTestDataCleaner(loopRepository);
    }

    @Before
    public void saveData() {
        clearTestData();

        final Version testVersion = versionDataInitializer.createData(TEST_VERSION_NO, TEST_DOC_ID);
        final Loop testLoop = Value.with(new Loop(), loop -> {
            loop.setVersion(testVersion);
            loop.setNo(TEST_LOOP_ID);
            loop.setEntries(Collections.emptyList());
            loop.setNextEntryNo(1);
        });
        loopRepository.saveAndFlush(testLoop);
    }

    @After
    public void clearTestData() {
        testDataCleaner.cleanRepositories();
        versionDataInitializer.cleanRepositories();
    }

    @Test
    public void testGetLoopExpectCorrectDTO() {
        final LoopDTO loop = loopService.getLoop(TEST_LOOP_ID, TEST_DOC_ID);
        Assert.assertEquals(TEST_LOOP_DTO, loop);
    }

    @Test
    public void testSaveLoopExpectNotNullAndCorrectFieldsData() {
        final int testLoopId = 23;
        final List<LoopEntryDTO> entries = Collections.emptyList();
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, testLoopId, entries);

        loopService.saveLoop(loopDTO);
        final LoopDTO savedLoop = loopService.getLoop(loopDTO.getLoopId(), loopDTO.getDocId());

        Assert.assertNotNull(savedLoop);
        Assert.assertThat(savedLoop.getDocId(), is(TEST_DOC_ID));
        Assert.assertThat(savedLoop.getLoopId(), is(testLoopId));
        Assert.assertThat(savedLoop.getEntries().size(), is(entries.size()));
    }

    @Test
    public void testSaveLoopWithEntriesExpectNotNullCorrectSizeAndValues() {
        final int entryNo_0 = 1, entryNo_1 = 2, entryNo_2 = 3;
        final boolean entryIsEnabled_0 = true, entryIsEnabled_1 = false, entryIsEnabled_2 = true;

        final List<LoopEntryDTO> entries = Arrays.asList(
                new LoopEntryDTO(entryNo_0, entryIsEnabled_0),
                new LoopEntryDTO(entryNo_1, entryIsEnabled_1),
                new LoopEntryDTO(entryNo_2, entryIsEnabled_2)
        );

        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, 42, entries);
        loopService.saveLoop(loopDTO);
        final List<LoopEntryDTO> resultEntries = loopService.getLoop(loopDTO.getLoopId(), loopDTO.getDocId())
                .getEntries();

        Assert.assertNotNull(resultEntries);
        Assert.assertThat(resultEntries.size(), is(entries.size()));

        Assert.assertThat(resultEntries.get(0).getNo(), is(entryNo_0));
        Assert.assertThat(resultEntries.get(0).isEnabled(), is(entryIsEnabled_0));

        Assert.assertThat(resultEntries.get(1).getNo(), is(entryNo_1));
        Assert.assertThat(resultEntries.get(1).isEnabled(), is(entryIsEnabled_1));

        Assert.assertThat(resultEntries.get(2).getNo(), is(entryNo_2));
        Assert.assertThat(resultEntries.get(2).isEnabled(), is(entryIsEnabled_2));
    }
}
