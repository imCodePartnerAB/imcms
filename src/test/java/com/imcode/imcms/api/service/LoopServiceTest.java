package com.imcode.imcms.api.service;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.domain.service.exception.DocumentNotExistException;
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
        loopDataInitializer.createData(TEST_LOOP_DTO);
    }

    @After
    public void clearTestData() {
        loopDataInitializer.cleanRepositories();
    }

    @Test
    public void getLoop_Expect_correctFieldsData() throws DocumentNotExistException {
        final LoopDTO loop = loopService.getLoop(TEST_LOOP_INDEX, TEST_DOC_ID);
        Assert.assertEquals(TEST_LOOP_DTO, loop);
    }

    @Test
    public void getLoop_When_DocNotExist_Expect_Exception() {
        final int nonExistingDocId = 42;
        try {
            loopService.getLoop(TEST_LOOP_INDEX, nonExistingDocId); // should threw exception
            Assert.fail("Expected exception wasn't thrown!");

        } catch (DocumentNotExistException e) {
            // all fine, this is expected behavior
            return;
        }

        Assert.fail("Expected exception was not caught!");
    }

    @Test
    public void getLoop_When_NotExist_ExpectEmptyLoop() throws DocumentNotExistException {
        final int nonExistingLoopIndex = 42;
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, nonExistingLoopIndex, Collections.emptyList());
        final LoopDTO loop = loopService.getLoop(nonExistingLoopIndex, TEST_DOC_ID);

        Assert.assertNotNull(loop);
        Assert.assertEquals(loop, loopDTO);
    }

    @Test
    public void saveLoop_Expect_NotNullAndCorrectFieldsData() throws DocumentNotExistException {
        final int testLoopIndex = 23;
        final List<LoopEntryDTO> entries = Collections.emptyList();
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, testLoopIndex, entries);

        loopService.saveLoop(loopDTO);
        final LoopDTO savedLoop = loopService.getLoop(loopDTO.getLoopIndex(), loopDTO.getDocId());

        Assert.assertNotNull(savedLoop);
        Assert.assertEquals(savedLoop, loopDTO);
    }

    @Test
    public void saveLoop_With_Entries_Expect_NotNullCorrectSizeAndValues() throws DocumentNotExistException {
        final int entryNo_0 = 1, entryNo_1 = 2, entryNo_2 = 3;
        final boolean entryIsEnabled_0 = true, entryIsEnabled_1 = false, entryIsEnabled_2 = true;

        final LoopEntryDTO loopEntryDto0 = new LoopEntryDTO(entryNo_0, entryIsEnabled_0);
        final LoopEntryDTO loopEntryDto1 = new LoopEntryDTO(entryNo_1, entryIsEnabled_1);
        final LoopEntryDTO loopEntryDto2 = new LoopEntryDTO(entryNo_2, entryIsEnabled_2);
        final List<LoopEntryDTO> entries = Arrays.asList(loopEntryDto0, loopEntryDto1, loopEntryDto2);

        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, 42, entries);
        loopService.saveLoop(loopDTO);
        final LoopDTO savedLoop = loopService.getLoop(loopDTO.getLoopIndex(), loopDTO.getDocId());

        Assert.assertEquals(savedLoop, loopDTO);
    }
}
