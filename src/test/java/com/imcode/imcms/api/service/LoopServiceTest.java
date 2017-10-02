package com.imcode.imcms.api.service;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.util.datainitializer.LoopDataInitializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class LoopServiceTest {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_INDEX = 1;

    private static final LoopDTO TEST_LOOP_DTO = new LoopDTO(TEST_DOC_ID, TEST_LOOP_INDEX, Collections.emptyList());

    @Autowired
    private LoopDataInitializer loopDataInitializer;

    @Autowired
    private LoopService loopService;

    @Before
    public void saveData() {
        clearTestData();
        loopDataInitializer.createData(TEST_DOC_ID, TEST_LOOP_INDEX);
    }

    @After
    public void clearTestData() {
        loopDataInitializer.cleanRepositories();
    }

    @Test
    public void testGetLoopExpectCorrectDTO() {
        final LoopDTO loop = loopService.getLoop(TEST_LOOP_INDEX, TEST_DOC_ID);
        Assert.assertEquals(TEST_LOOP_DTO, loop);
    }

    @Test
    public void testSaveLoopExpectNotNullAndCorrectFieldsData() {
        final int testLoopId = 23;
        final List<LoopEntryDTO> entries = Collections.emptyList();
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, testLoopId, entries);

        loopService.saveLoop(loopDTO);
        final LoopDTO savedLoop = loopService.getLoop(loopDTO.getLoopIndex(), loopDTO.getDocId());

        Assert.assertNotNull(savedLoop);
        Assert.assertThat(savedLoop.getDocId(), is(TEST_DOC_ID));
        Assert.assertThat(savedLoop.getLoopIndex(), is(testLoopId));
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
        final List<LoopEntryDTO> resultEntries = loopService.getLoop(loopDTO.getLoopIndex(), loopDTO.getDocId())
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
