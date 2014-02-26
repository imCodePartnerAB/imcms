package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.container.DocVersionRef;
import com.imcode.imcms.mapping.orm.DocVersion;
import com.imcode.imcms.mapping.orm.TextDocLoop;
import com.imcode.imcms.mapping.orm.User;
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
public class TextDocLoopDaoTest {

    static final DocVersionRef DOC_VERSION_REF = new DocVersionRef(1001, 0);

    @Inject
    UserDao userDao;

    @Inject
    DocVersionDao docVersionDao;

    @Inject
    TextDocLoopDao textDocLoopDao;

    @PersistenceContext
    EntityManager entityManager;

    public List<TextDocLoop> recreateLoops() {
        textDocLoopDao.deleteAll();
        docVersionDao.deleteAll();
        userDao.deleteAll();

        User user = userDao.saveAndFlush(new User("admin", "admin", "admin@imcode.com"));
        DocVersion docVersion = docVersionDao.saveAndFlush(
                new DocVersion(
                        DOC_VERSION_REF.getDocId(),
                        DOC_VERSION_REF.getDocVersionNo(),
                        user,
                        new Date(),
                        user, new Date()
                )
        );

        return Arrays.asList(
                textDocLoopDao.saveAndFlush(
                    new TextDocLoop(
                            docVersion,
                            1,
                            2,
                            Arrays.asList(
                                new TextDocLoop.Entry(1)
                            )
                    )
                ),

                textDocLoopDao.saveAndFlush(
                    new TextDocLoop(
                            docVersion,
                            2,
                            3,
                            Arrays.asList(
                                    new TextDocLoop.Entry(1),
                                    new TextDocLoop.Entry(2)
                            )
                    )
                ),

                textDocLoopDao.saveAndFlush(
                    new TextDocLoop(
                            docVersion,
                            3,
                            4,
                            Arrays.asList(
                                    new TextDocLoop.Entry(1),
                                    new TextDocLoop.Entry(2),
                                    new TextDocLoop.Entry(3)
                            )
                    )
                )
        );
    }

    @Test
    public void textFindByDocVersion() {
        recreateLoops();

        DocVersion docVersion = docVersionDao.findByDocIdAndNo(DOC_VERSION_REF.getDocId(), DOC_VERSION_REF.getDocVersionNo());
        List<TextDocLoop> loops = textDocLoopDao.findByDocVersion(docVersion);

        assertThat(loops.size(), is(3));
    }

    @Test
    public void textFindByDocVersionAndNo() {
        recreateLoops();

        DocVersion docVersion = docVersionDao.findByDocIdAndNo(DOC_VERSION_REF.getDocId(), DOC_VERSION_REF.getDocVersionNo());
        TextDocLoop loop1 = textDocLoopDao.findByDocVersionAndNo(docVersion, 1);
        TextDocLoop loop2 = textDocLoopDao.findByDocVersionAndNo(docVersion, 2);
        TextDocLoop loop3 = textDocLoopDao.findByDocVersionAndNo(docVersion, 3);

        assertNotNull(loop1);
        assertNotNull(loop2);
        assertNotNull(loop3);
    }
}
