package com.imcode.imcms.api.service;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.dto.LoopDTO;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopRepository;
import com.imcode.imcms.service.LoopService;
import com.imcode.imcms.util.Value;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class LoopServiceTest {

    private static final int TEST_DOC_ID = 1001;
    private static final int TEST_LOOP_ID = 1;

    private static final LoopDTO TEST_LOOP_DTO = new LoopDTO(TEST_DOC_ID, TEST_LOOP_ID, Collections.emptyList());

    @Autowired
    private LoopService loopService;

    @Autowired
    private LoopRepository loopRepository;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private UserRepository userRepository;

    @Before
    public void saveData() {
        clearTestData();

        final User user = userRepository.findById(1);

        final Version testVersion = Value.with(new Version(), version -> {
            version.setNo(0);
            version.setDocId(TEST_DOC_ID);
            version.setCreatedBy(user);
            version.setCreatedDt(new Date());
            version.setModifiedBy(user);
            version.setModifiedDt(new Date());
        });
        versionRepository.saveAndFlush(testVersion);

        final Loop testLoop = Value.with(new Loop(), loop -> {
            loop.setVersion(testVersion);
            loop.setNo(TEST_LOOP_ID);
            loop.setEntries(Collections.emptyList());
            loop.setNextEntryNo(1);// or 0?
        });
        loopRepository.saveAndFlush(testLoop);
    }

    @After
    public void clearTestData() {
        loopRepository.deleteAll();
        loopRepository.flush();

        versionRepository.deleteAll();
        versionRepository.flush();
    }

    @Test
    public void testGetLoop() {
        final LoopDTO loop = loopService.getLoop(TEST_LOOP_ID, TEST_DOC_ID);
        Assert.assertEquals(TEST_LOOP_DTO, loop);
    }
}
