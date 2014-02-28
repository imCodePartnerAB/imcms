package com.imcode.imcms.mapping.jpa.doc;

import com.imcode.imcms.mapping.jpa.JpaConfiguration;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
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
public class DocDocVersionRepositoryTest {

    @Inject
    DocVersionRepository docVersionRepository;

    @Inject
    MetaRepository metaRepository;

    @Inject
    UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    int userId;

    int docId;

    public List<DocVersion> recreateVersions() {
        docVersionRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.saveAndFlush(new User("admin", "admin", "admin@imcode.com"));
        Date now = new Date();

        Meta meta = new Meta();

        meta.setDefaultVersionNo(3);
        meta.setDisabledLanguageShowMode(Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE);
        meta.setDocumentType(1);
        meta.setCreatorId(1);
        meta.setRestrictedOneMorePrivilegedThanRestrictedTwo(true);
        meta.setLinkableByOtherUsers(true);
        meta.setLinkedForUnauthorizedUsers(true);
        meta.setCreatedDatetime(new Date());
        meta.setModifiedDatetime(new Date());
        meta.setTarget("_blank");

        userId = user.getId();
        docId = metaRepository.save(meta).getId();


        return Arrays.asList(
            docVersionRepository.saveAndFlush(new DocVersion(docId, 0, user, now, user, now)),
            docVersionRepository.saveAndFlush(new DocVersion(docId, 1, user, now, user, now)),
            docVersionRepository.saveAndFlush(new DocVersion(docId, 2, user, now, user, now)),
            docVersionRepository.saveAndFlush(new DocVersion(docId, 3, user, now, user, now)),
            docVersionRepository.saveAndFlush(new DocVersion(docId, 4, user, now, user, now)),
            docVersionRepository.saveAndFlush(new DocVersion(docId, 5, user, now, user, now))
        );
    }

    @Test
    public void testFindByDocId() throws Exception {
        List<DocVersion> docVersions = recreateVersions();

        assertThat(docVersionRepository.findByDocId(docId).size(), is(6));
    }

    @Test
    public void testFindByDocIdAndNo() throws Exception {
        List<DocVersion> docVersions = recreateVersions();

        assertThat(docVersionRepository.findByDocIdAndNo(docId, 0), equalTo(docVersions.get(0)));
        assertThat(docVersionRepository.findByDocIdAndNo(docId, 1), equalTo(docVersions.get(1)));
    }

    @Test
    public void testFindLatestVersion() {
        recreateVersions();

        DocVersion docVersion = docVersionRepository.findLatest(docId);

        assertNotNull(docVersion);
        assertThat(docVersion.getDocId(), is(docId));
        assertThat(docVersion.getNo(), is(5));
    }

    @Test
    public void testFindDefaultVersion() {
        recreateVersions();

        DocVersion docVersion = docVersionRepository.findDefault(docId);

        assertNotNull(docVersion);
        assertThat(docVersion.getDocId(), is(docId));
        assertThat(docVersion.getNo(), is(3));
    }

    @Test
    public void testCreate() {
        recreateVersions();

        DocVersion docVersion = docVersionRepository.create(docId, userId);

        assertNotNull(docVersion);
        assertThat(docVersion.getDocId(), is(docId));
        assertThat(docVersion.getNo(), is(6));
        assertThat(docVersion.getCreatedBy(), equalTo(userRepository.findOne(userId)));
    }

    @Test
    public void testSetDefault() {
        recreateVersions();

        assertThat(docVersionRepository.findDefault(docId).getNo(), is(3));

        docVersionRepository.setDefault(docId, 4, userId);

        DocVersion docVersion = docVersionRepository.findDefault(docId);

        assertNotNull(docVersion);
        assertThat(docVersion.getDocId(), is(docId));
        assertThat(docVersion.getNo(), is(4));
        assertThat(docVersion.getModifiedBy(), equalTo(userRepository.findOne(userId)));
    }
}
