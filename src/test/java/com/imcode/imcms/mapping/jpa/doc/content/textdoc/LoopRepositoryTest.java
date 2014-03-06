package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.container.DocVersionRef;
import com.imcode.imcms.mapping.jpa.JpaConfiguration;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfiguration.class})
@Transactional
public class LoopRepositoryTest {

    static final DocVersionRef DOC_VERSION_REF = new DocVersionRef(1001, 0);

    @Inject
    UserRepository userRepository;

    @Inject
    VersionRepository versionRepository;

    @Inject
    LoopRepository loopRepository;

    @PersistenceContext
    EntityManager entityManager;

    public List<Loop> recreateLoops() {
        loopRepository.deleteAll();
        versionRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.saveAndFlush(new User("admin", "admin", "admin@imcode.com"));
        Version version = versionRepository.saveAndFlush(
                new Version(
                        DOC_VERSION_REF.getDocId(),
                        DOC_VERSION_REF.getDocVersionNo(),
                        user,
                        new Date(),
                        user, new Date()
                )
        );

        return Arrays.asList(
                loopRepository.saveAndFlush(
                    new Loop(
                            version,
                            1,
                            2,
                            Arrays.asList(
                                new Loop.Entry(1)
                            )
                    )
                ),

                loopRepository.saveAndFlush(
                    new Loop(
                            version,
                            2,
                            3,
                            Arrays.asList(
                                    new Loop.Entry(1),
                                    new Loop.Entry(2)
                            )
                    )
                ),

                loopRepository.saveAndFlush(
                    new Loop(
                            version,
                            3,
                            4,
                            Arrays.asList(
                                    new Loop.Entry(1),
                                    new Loop.Entry(2),
                                    new Loop.Entry(3)
                            )
                    )
                )
        );
    }

    @Test
    public void textFindByDocVersion() {
        recreateLoops();

        Version version = versionRepository.findByDocIdAndNo(DOC_VERSION_REF.getDocId(), DOC_VERSION_REF.getDocVersionNo());
        List<Loop> loops = loopRepository.findByDocVersion(version);

        assertThat(loops.size(), is(3));
    }

    @Test
    public void textFindByDocVersionAndNo() {
        recreateLoops();

        Version version = versionRepository.findByDocIdAndNo(DOC_VERSION_REF.getDocId(), DOC_VERSION_REF.getDocVersionNo());
        Loop loop1 = loopRepository.findByDocVersionAndNo(version, 1);
        Loop loop2 = loopRepository.findByDocVersionAndNo(version, 2);
        Loop loop3 = loopRepository.findByDocVersionAndNo(version, 3);

        assertNotNull(loop1);
        assertNotNull(loop2);
        assertNotNull(loop3);
    }
}
