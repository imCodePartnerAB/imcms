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
public class DocVersionRepositoryTest {

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

    public List<Version> recreateVersions() {
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
            docVersionRepository.saveAndFlush(new Version(docId, 0, user, now, user, now)),
            docVersionRepository.saveAndFlush(new Version(docId, 1, user, now, user, now)),
            docVersionRepository.saveAndFlush(new Version(docId, 2, user, now, user, now)),
            docVersionRepository.saveAndFlush(new Version(docId, 3, user, now, user, now)),
            docVersionRepository.saveAndFlush(new Version(docId, 4, user, now, user, now)),
            docVersionRepository.saveAndFlush(new Version(docId, 5, user, now, user, now))
        );
    }

    @Test
    public void testFindByDocId() throws Exception {
        List<Version> versions = recreateVersions();

        assertThat(docVersionRepository.findByDocId(docId).size(), is(6));
    }

    @Test
    public void testFindByDocIdAndNo() throws Exception {
        List<Version> versions = recreateVersions();

        assertThat(docVersionRepository.findByDocIdAndNo(docId, 0), equalTo(versions.get(0)));
        assertThat(docVersionRepository.findByDocIdAndNo(docId, 1), equalTo(versions.get(1)));
    }

    @Test
    public void testFindLatestVersion() {
        recreateVersions();

        Version version = docVersionRepository.findLatest(docId);

        assertNotNull(version);
        assertThat(version.getDocId(), is(docId));
        assertThat(version.getNo(), is(5));
    }

    @Test
    public void testFindDefaultVersion() {
        recreateVersions();

        Version version = docVersionRepository.findDefault(docId);

        assertNotNull(version);
        assertThat(version.getDocId(), is(docId));
        assertThat(version.getNo(), is(3));
    }

    @Test
    public void testCreate() {
        recreateVersions();

        Version version = docVersionRepository.create(docId, userId);

        assertNotNull(version);
        assertThat(version.getDocId(), is(docId));
        assertThat(version.getNo(), is(6));
        assertThat(version.getCreatedBy(), equalTo(userRepository.findOne(userId)));
    }

    @Test
    public void testSetDefault() {
        recreateVersions();

        assertThat(docVersionRepository.findDefault(docId).getNo(), is(3));

        docVersionRepository.setDefault(docId, 4, userId);

        Version version = docVersionRepository.findDefault(docId);

        assertNotNull(version);
        assertThat(version.getDocId(), is(docId));
        assertThat(version.getNo(), is(4));
        assertThat(version.getModifiedBy(), equalTo(userRepository.findOne(userId)));
    }
}
