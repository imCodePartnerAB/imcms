package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.config.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class LoopRepositoryTest {

    private static final VersionRef DOC_VERSION_REF = new VersionRef(1001, 0);

    @Inject
    private UserRepository userRepository;

    @Inject
    private VersionRepository versionRepository;

    @Inject
    private LoopRepository loopRepository;

    private void recreateLoops() {
        loopRepository.deleteAll();
        loopRepository.flush();

        versionRepository.deleteAll();
        versionRepository.flush();

        final User user = userRepository.findById(1);
        final Version version = versionRepository.saveAndFlush(
                new Version(DOC_VERSION_REF.getDocId(), DOC_VERSION_REF.getNo(), user, new Date(), user, new Date())
        );

        loopRepository.saveAndFlush(
                new Loop(version, 1, 2, Collections.singletonList(new Loop.Entry(1)))
        );
        loopRepository.saveAndFlush(
                new Loop(version, 2, 3, Arrays.asList(new Loop.Entry(1), new Loop.Entry(2)))
        );
        loopRepository.saveAndFlush(
                new Loop(version, 3, 4, Arrays.asList(
                        new Loop.Entry(1),
                        new Loop.Entry(2),
                        new Loop.Entry(3)
                ))
        );
    }

    @Test
    public void testFindByDocVersion() {
        recreateLoops();

        Version version = versionRepository.findByDocIdAndNo(DOC_VERSION_REF.getDocId(), DOC_VERSION_REF.getNo());
        List<Loop> loops = loopRepository.findByVersion(version);

        assertThat(loops.size(), is(3));
    }

    @Test
    public void testFindByDocVersionAndNo() {
        recreateLoops();

        Version version = versionRepository.findByDocIdAndNo(DOC_VERSION_REF.getDocId(), DOC_VERSION_REF.getNo());
        Loop loop1 = loopRepository.findByVersionAndNo(version, 1);
        Loop loop2 = loopRepository.findByVersionAndNo(version, 2);
        Loop loop3 = loopRepository.findByVersionAndNo(version, 3);

        assertNotNull(loop1);
        assertNotNull(loop2);
        assertNotNull(loop3);
    }
}
