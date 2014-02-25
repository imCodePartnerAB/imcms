package com.imcode.imcms.mapping.dao;

import com.imcode.imcms.mapping.orm.DocMeta;
import com.imcode.imcms.mapping.orm.DocVersion;
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
public class DocVersionDaoTest {

    @Inject
    DocVersionDao docVersionDao;

    @Inject
    DocDao docDao;

    @Inject
    UserDao userDao;

    @PersistenceContext
    EntityManager entityManager;

    int userId;

    int docId;

    public List<DocVersion> recreateVersions() {
        docVersionDao.deleteAll();
        userDao.deleteAll();

        User user = userDao.saveAndFlush(new User("admin", "admin", "admin@imcode.com"));
        Date now = new Date();

        DocMeta docMeta = new DocMeta();

        docMeta.setDefaultVersionNo(3);
        docMeta.setDisabledLanguageShowSetting(DocMeta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE);
        docMeta.setDocumentType(1);
        docMeta.setCreatorId(1);
        docMeta.setRestrictedOneMorePrivilegedThanRestrictedTwo(true);
        docMeta.setLinkableByOtherUsers(true);
        docMeta.setLinkedForUnauthorizedUsers(true);
        docMeta.setCreatedDatetime(new Date());
        docMeta.setModifiedDatetime(new Date());
        docMeta.setTarget("_blank");

        userId = user.getId();
        docId = docDao.saveMeta(docMeta).getId();


        return Arrays.asList(
            docVersionDao.saveAndFlush(new DocVersion(docId, 0, user, now, user, now)),
            docVersionDao.saveAndFlush(new DocVersion(docId, 1, user, now, user, now)),
            docVersionDao.saveAndFlush(new DocVersion(docId, 2, user, now, user, now)),
            docVersionDao.saveAndFlush(new DocVersion(docId, 3, user, now, user, now)),
            docVersionDao.saveAndFlush(new DocVersion(docId, 4, user, now, user, now)),
            docVersionDao.saveAndFlush(new DocVersion(docId, 5, user, now, user, now))
        );
    }

    @Test
    public void testFindByDocId() throws Exception {
        List<DocVersion> versions = recreateVersions();

        assertThat(docVersionDao.findByDocId(docId).size(), is(6));
    }

    @Test
    public void testFindByDocIdAndNo() throws Exception {
        List<DocVersion> versions = recreateVersions();

        assertThat(docVersionDao.findByDocIdAndNo(docId, 0), equalTo(versions.get(0)));
        assertThat(docVersionDao.findByDocIdAndNo(docId, 1), equalTo(versions.get(1)));
    }

    @Test
    public void testFindLatestVersion() {
        recreateVersions();

        DocVersion docVersion = docVersionDao.findLatest(docId);

        assertNotNull(docVersion);
        assertThat(docVersion.getDocId(), is(docId));
        assertThat(docVersion.getNo(), is(5));
    }

    @Test
    public void testFindDefaultVersion() {
        recreateVersions();

        DocVersion docVersion = docVersionDao.findDefault(docId);

        assertNotNull(docVersion);
        assertThat(docVersion.getDocId(), is(docId));
        assertThat(docVersion.getNo(), is(3));
    }

    @Test
    public void testCreate() {
        recreateVersions();

        DocVersion docVersion = docVersionDao.create(docId, userId);

        assertNotNull(docVersion);
        assertThat(docVersion.getDocId(), is(docId));
        assertThat(docVersion.getNo(), is(6));
        assertThat(docVersion.getCreatedBy(), equalTo(userDao.findOne(userId)));
    }

    @Test
    public void testSetDefault() {
        recreateVersions();

        assertThat(docVersionDao.findDefault(docId).getNo(), is(3));

        docVersionDao.setDefault(docId, 4, userId);

        DocVersion docVersion = docVersionDao.findDefault(docId);

        assertNotNull(docVersion);
        assertThat(docVersion.getDocId(), is(docId));
        assertThat(docVersion.getNo(), is(4));
        assertThat(docVersion.getModifiedBy(), equalTo(userDao.findOne(userId)));
    }
}
