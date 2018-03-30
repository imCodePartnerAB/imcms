package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.LoopService;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LoopRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.imcode.imcms.components.datainitializer.LoopDataInitializer.TEST_VERSION_NO;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class LoopServiceTest {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_INDEX = 1;
    private static final int TEST_LOOP_COUNT = 10;

    private static final Loop TEST_LOOP_DTO = new LoopDTO(TEST_DOC_ID, TEST_LOOP_INDEX, Collections.emptyList());
    private static final Loop TEST_LOOP_DTO_LATEST_VERSION = new LoopDTO(TEST_DOC_ID, TEST_LOOP_INDEX, Arrays.asList(LoopEntryDTO.createEnabled(1),
            LoopEntryDTO.createEnabled(2),
            LoopEntryDTO.createEnabled(3)
    ));

    @Autowired
    private LoopDataInitializer loopDataInitializer;

    @Autowired
    private LoopRepository loopRepository;

    @Autowired
    private LoopService loopService;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    private int lastVersionIndex;

    @Before
    public void saveData() {
        loopDataInitializer.createData(TEST_LOOP_DTO);
        for (int i = 1; i < TEST_LOOP_COUNT; i++) {
            final boolean isLast = i == (TEST_LOOP_COUNT - 1);
            if (isLast) {
                loopDataInitializer.createData(TEST_LOOP_DTO_LATEST_VERSION, i);
                lastVersionIndex = i;
            } else {
                final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, TEST_LOOP_INDEX, Collections.singletonList(LoopEntryDTO.createEnabled(1)));
                loopDataInitializer.createData(loopDTO, i);
            }
        }
    }

    @Test
    public void getLoop_Expect_correctFieldsData() {
        final Loop loop = loopService.getLoop(TEST_LOOP_INDEX, TEST_DOC_ID);
        assertEquals(TEST_LOOP_DTO, loop);
    }

    @Test
    public void getLoopPublic_Expect_correctFieldsData() {
        final Loop loop = loopService.getLoopPublic(TEST_LOOP_INDEX, TEST_DOC_ID);
        assertEquals(TEST_LOOP_DTO_LATEST_VERSION, loop);
    }

    @Test(expected = DocumentNotExistException.class)
    public void getLoop_When_DocNotExist_Expect_Exception() {
        final int nonExistingDocId = 42;
        loopService.getLoop(TEST_LOOP_INDEX, nonExistingDocId); // should threw exception
    }

    @Test(expected = DocumentNotExistException.class)
    public void getLoopPublic_When_DocNotExist_Expect_Exception() {
        final int nonExistingDocId = 42;
        loopService.getLoopPublic(TEST_LOOP_INDEX, nonExistingDocId); // should threw exception
    }

    @Test
    public void getLoop_When_NotExist_ExpectEmptyLoop() {
        final int nonExistingLoopIndex = 42;
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, nonExistingLoopIndex, Collections.emptyList());
        final Loop loop = loopService.getLoop(nonExistingLoopIndex, TEST_DOC_ID);

        assertNotNull(loop);
        assertEquals(loop, loopDTO);
    }

    @Test
    public void getLoopPublic_When_NotExist_ExpectEmptyLoop() {
        final int nonExistingLoopIndex = 42;
        final LoopDTO loopDTO = new LoopDTO(TEST_DOC_ID, nonExistingLoopIndex, Collections.emptyList());
        final Loop loop = loopService.getLoopPublic(nonExistingLoopIndex, TEST_DOC_ID);

        assertNotNull(loop);
        assertEquals(loop, loopDTO);
    }

    @Test
    public void saveLoop_Expect_NotNullAndCorrectFieldsData() {
        final int testLoopIndex = 23;
        final Loop loopDTO = new LoopDTO(TEST_DOC_ID, testLoopIndex, Collections.emptyList());

        loopService.saveLoop(loopDTO);
        final Loop savedLoop = loopService.getLoop(loopDTO.getIndex(), loopDTO.getDocId());

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
        final Loop loopDTO = new LoopDTO(TEST_DOC_ID, 42, entries);

        loopService.saveLoop(loopDTO);
        final Loop savedLoop = loopService.getLoop(loopDTO.getIndex(), loopDTO.getDocId());

        assertEquals(savedLoop, loopDTO);
    }

    @Test
    public void getLoopsByVersion() {
        final Loop loopDTO1 = new LoopDTO(TEST_DOC_ID, 10, Collections.emptyList());
        loopService.saveLoop(loopDTO1);
        final Loop loopDTO2 = new LoopDTO(TEST_DOC_ID, 20, Collections.emptyList());
        loopService.saveLoop(loopDTO2);
        final Loop loopDTO3 = new LoopDTO(TEST_DOC_ID, 30, Collections.emptyList());
        loopService.saveLoop(loopDTO3);

        final Collection<Loop> loopDTOS = Arrays.asList(TEST_LOOP_DTO, loopDTO1, loopDTO2, loopDTO3);
        final Version version = versionRepository.findByDocIdAndNo(TEST_DOC_ID, TEST_VERSION_NO);
        final Collection<Loop> allByVersion = loopService.getByVersion(version);

        assertEquals(loopDTOS.size(), allByVersion.size());
        assertTrue(allByVersion.containsAll(loopDTOS));
    }

    @Test
    public void createVersionedContent() {

        final Version workingVersion = versionRepository.findByDocIdAndNo(TEST_DOC_ID, TEST_VERSION_NO);
        final int newVersionIndex = lastVersionIndex + 1;
        final Version newVersion = versionDataInitializer.createData(newVersionIndex, TEST_DOC_ID);

        loopService.createVersionedContent(workingVersion, newVersion);

        final Loop loopLatest = loopService.getLoopPublic(TEST_LOOP_DTO_LATEST_VERSION.getIndex(), TEST_LOOP_DTO_LATEST_VERSION.getDocId());
        assertNotNull(loopLatest);
        assertEquals(TEST_LOOP_DTO, loopLatest);
    }

    @Test
    public void deleteByDocId() {
        loopDataInitializer.cleanRepositories();

        assertTrue(loopRepository.findAll().isEmpty());

        final List<LoopEntryDTO> oneEntry = Collections.singletonList(LoopEntryDTO.createEnabled(1));
        final List<LoopEntryDTO> twoEntries = Arrays.asList(LoopEntryDTO.createEnabled(1), LoopEntryDTO.createEnabled(2));
        final List<LoopEntryDTO> threeEntries = Arrays.asList(
                LoopEntryDTO.createEnabled(1),
                LoopEntryDTO.createEnabled(2),
                LoopEntryDTO.createEnabled(3)
        );

        final LoopDTO loop1 = new LoopDTO(TEST_DOC_ID, 1, oneEntry);
        final LoopDTO loop2 = new LoopDTO(TEST_DOC_ID, 2, twoEntries);
        final LoopDTO loop3 = new LoopDTO(TEST_DOC_ID, 3, threeEntries);

        IntStream.range(0, 10).forEach(versionIndex -> {
            loopDataInitializer.createData(loop1, versionIndex);
            loopDataInitializer.createData(loop2, versionIndex);
            loopDataInitializer.createData(loop3, versionIndex);
        });

        assertFalse(loopRepository.findAll().isEmpty());

        loopService.deleteByDocId(TEST_DOC_ID);

        assertTrue(loopRepository.findAll().isEmpty());
    }
}
