package com.imcode.imcms.domain.component;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.LoopDTO;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.LoopJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Transactional
public class LoopDtoToLoopTest extends WebAppSpringTestConfig {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_INDEX = 1;
    private static final int TEST_VERSION_NO = 0;

    @Autowired
    private VersionDataInitializer versionDataInitializer;
    @Autowired
    private VersionRepository versionRepository;

    @BeforeEach
    public void saveData() {
        clearTestData();
        versionDataInitializer.createData(TEST_VERSION_NO, TEST_DOC_ID);
    }

    @AfterEach
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
