package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.api.Document.PublicationStatus;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.mapping.jpa.User;
import com.imcode.imcms.mapping.jpa.UserRepository;
import com.imcode.imcms.mapping.jpa.doc.Meta;
import com.imcode.imcms.mapping.jpa.doc.MetaRepository;
import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class VersionRepositoryTest {

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private UserRepository userRepository;

    private int userId;

    private int docId;

    private List<Version> versions;

    private User user;

    @Before
    public void setUpVersions() {
        versionRepository.deleteAll();
        versionRepository.flush();

        userId = 1; // super admin
        user = userRepository.findById(userId);
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
        meta.setPublicationStatusInt(PublicationStatus.APPROVED.asInt());

        docId = metaRepository.save(meta).getId();


        versions = Arrays.asList(
                versionRepository.saveAndFlush(new Version(docId, 0, user, now, user, now)),
                versionRepository.saveAndFlush(new Version(docId, 1, user, now, user, now)),
                versionRepository.saveAndFlush(new Version(docId, 2, user, now, user, now)),
                versionRepository.saveAndFlush(new Version(docId, 3, user, now, user, now)),
                versionRepository.saveAndFlush(new Version(docId, 4, user, now, user, now)),
                versionRepository.saveAndFlush(new Version(docId, 5, user, now, user, now))
        );
    }

    @After
    public void tearDown() {
        versionRepository.deleteAll();
        versionRepository.flush();
        metaRepository.delete(docId);
        metaRepository.flush();
    }

    @Test
    public void testFindByDocId() throws Exception {
        assertEquals(6, versionRepository.findByDocId(docId).size());
    }

    @Test
    public void testFindByDocIdAndNo() throws Exception {
        assertNotNull(versionRepository.findByDocIdAndNo(docId, 0));
        assertNotNull(versionRepository.findByDocIdAndNo(docId, 1));
    }

    @Test
    public void testFindLatestVersion() {
        Version version = versionRepository.findLatest(docId);

        assertNotNull(version);
        assertEquals(docId, version.getDocId().longValue());
        assertEquals(5, version.getNo());
    }

    @Test
    public void testFindDefaultVersion() {
        Version version = versionRepository.findDefault(docId);

        assertNotNull(version);
        assertEquals(docId, version.getDocId().longValue());
        assertEquals(3, version.getNo());
    }

    @Test
    @Ignore("Move method to service")
    public void testCreate() {
        Version version = versionRepository.create(docId, userId);

        assertNotNull(version);
        assertEquals(docId, version.getDocId().longValue());
        assertEquals(6, version.getNo());
        assertEquals(user, version.getCreatedBy());
    }

    @Test
    @Transactional
    public void testSetDefault() {
        assertEquals(3, versionRepository.findDefault(docId).getNo());

        versionRepository.updateDefaultNo(docId, 4, userId);

        Version version = versionRepository.findDefault(docId);

        assertNotNull(version);
        assertEquals(docId, version.getDocId().longValue());
        assertEquals(4, version.getNo());
        assertEquals(user, version.getModifiedBy());
    }
}
