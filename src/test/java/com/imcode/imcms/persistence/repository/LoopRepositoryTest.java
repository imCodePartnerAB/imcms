package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.LoopDataInitializer;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.domain.dto.LoopEntryDTO;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.model.LoopEntry;
import com.imcode.imcms.persistence.entity.LoopJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class LoopRepositoryTest extends WebAppSpringTestConfig {

    private static final int DOC_ID = 1001;
    private static final int VERSION_NO = 0;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private LoopRepository loopRepository;

    @Autowired
    private LoopDataInitializer loopDataInitializer;

    @BeforeEach
    public void recreateLoops() {
        loopDataInitializer.cleanRepositories();

        final List<LoopEntryDTO> oneEntry = Collections.singletonList(LoopEntryDTO.createEnabled(1));
        final List<LoopEntryDTO> twoEntries = Arrays.asList(LoopEntryDTO.createEnabled(1), LoopEntryDTO.createEnabled(2));
        final List<LoopEntryDTO> threeEntries = Arrays.asList(
                LoopEntryDTO.createEnabled(1),
                LoopEntryDTO.createEnabled(2),
                LoopEntryDTO.createEnabled(3)
        );

        loopDataInitializer.createData(new LoopDTO(DOC_ID, 1, oneEntry));
        loopDataInitializer.createData(new LoopDTO(DOC_ID, 2, twoEntries));
        loopDataInitializer.createData(new LoopDTO(DOC_ID, 3, threeEntries));
    }

    @Test
    public void findByDocVersion_Expect_CorrectResultSize() {
        Version version = versionRepository.findByDocIdAndNo(DOC_ID, VERSION_NO);
        List<LoopJPA> loops = loopRepository.findByVersion(version);

        assertThat(loops.size(), is(3));
    }

    @Test
    public void findByDocVersionAndIndex_Expect_NotNullResults() {
        Version version = versionRepository.findByDocIdAndNo(DOC_ID, VERSION_NO);
        LoopJPA loop1 = loopRepository.findByVersionAndIndex(version, 1);
        LoopJPA loop2 = loopRepository.findByVersionAndIndex(version, 2);
        LoopJPA loop3 = loopRepository.findByVersionAndIndex(version, 3);

        assertNotNull(loop1);
        assertNotNull(loop2);
        assertNotNull(loop3);
    }

    @Test
    public void savedData_Expect_EqualIndexAndDocId() {
        Version version = versionRepository.findByDocIdAndNo(DOC_ID, VERSION_NO);
        LoopJPA loop1 = loopRepository.findByVersionAndIndex(version, 1);

        assertEquals(loop1.getIndex(), Integer.valueOf(1));
        assertEquals(loop1.getVersion().getDocId(), Integer.valueOf(DOC_ID));
    }

    @Test
    public void removeOneSavedEntry_Expect_EntriesSizeDecreased() {
        Version version = versionRepository.findByDocIdAndNo(DOC_ID, VERSION_NO);
        LoopJPA loop = loopRepository.findByVersionAndIndex(version, 3);

        final List<LoopEntry> loopEntries = loop.getEntries();

        assertEquals(loopEntries.size(), 3);

        loopEntries.remove(1);
        loop.setEntries(loopEntries);

        loopRepository.save(loop);
        loop = loopRepository.findByVersionAndIndex(version, 3);

        assertEquals(loop.getEntries().size(), 2);
    }

    @Test
    public void findByDocId() {
        loopDataInitializer.cleanRepositories();

        assertTrue(loopRepository.findAll().isEmpty());

        final List<LoopEntryDTO> oneEntry = Collections.singletonList(LoopEntryDTO.createEnabled(1));
        final List<LoopEntryDTO> twoEntries = Arrays.asList(LoopEntryDTO.createEnabled(1), LoopEntryDTO.createEnabled(2));
        final List<LoopEntryDTO> threeEntries = Arrays.asList(
                LoopEntryDTO.createEnabled(1),
                LoopEntryDTO.createEnabled(2),
                LoopEntryDTO.createEnabled(3)
        );

        final LoopDTO loop1 = new LoopDTO(DOC_ID, 1, oneEntry);
        final LoopDTO loop2 = new LoopDTO(DOC_ID, 2, twoEntries);
        final LoopDTO loop3 = new LoopDTO(DOC_ID, 3, threeEntries);

        IntStream.range(0, 10).forEach(versionIndex -> {
            loopDataInitializer.createData(loop1, versionIndex);
            loopDataInitializer.createData(loop2, versionIndex);
            loopDataInitializer.createData(loop3, versionIndex);
        });

        assertFalse(loopRepository.findAll().isEmpty());
        assertEquals(loopRepository.findAll().size(), loopRepository.findByDocId(DOC_ID).size());
    }
}
