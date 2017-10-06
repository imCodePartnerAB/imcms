package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.domain.service.exception.DocumentNotExistException;
import com.imcode.imcms.util.datainitializer.LoopDataInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
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
        loopDataInitializer.createData(TEST_LOOP_DTO);
    }

    @Test
    public void getLoop_Expect_correctFieldsData() {
        final LoopDTO loop = loopService.getLoop(TEST_LOOP_INDEX, TEST_DOC_ID);
        assertEquals(TEST_LOOP_DTO, loop);
    }

    @Test(expected = DocumentNotExistException.class)
    public void getLoop_When_DocNotExist_Expect_Exception() {
        final int nonExistingDocId = 42;
        loopService.getLoop(TEST_LOOP_INDEX, nonExistingDocId); // should threw exception
    }

    @Test
    public void getLoop_When_NotExist_ExpectEmptyLoop() {
        final int nonExistingLoopIndex = 42;
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, nonExistingLoopIndex, Collections.emptyList());
        final LoopDTO loop = loopService.getLoop(nonExistingLoopIndex, TEST_DOC_ID);

        assertNotNull(loop);
        assertEquals(loop, loopDTO);
    }

    @Test
    public void saveLoop_Expect_NotNullAndCorrectFieldsData() {
        final int testLoopIndex = 23;
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, testLoopIndex, Collections.emptyList());

        loopService.saveLoop(loopDTO);
        final LoopDTO savedLoop = loopService.getLoop(loopDTO.getIndex(), loopDTO.getDocId());

        assertNotNull(savedLoop);
        assertEquals(savedLoop, loopDTO);
    }

    @Test
    public void saveLoop_With_Entries_Expect_NotNullCorrectSizeAndValues() {
        final List<LoopEntryDTO> entries = Arrays.asList(
                new LoopEntryDTO(1, true),
                new LoopEntryDTO(2, false),
                new LoopEntryDTO(3, true)
        );
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, 42, entries);

        loopService.saveLoop(loopDTO);
        final LoopDTO savedLoop = loopService.getLoop(loopDTO.getIndex(), loopDTO.getDocId());

        assertEquals(savedLoop, loopDTO);
    }
}
