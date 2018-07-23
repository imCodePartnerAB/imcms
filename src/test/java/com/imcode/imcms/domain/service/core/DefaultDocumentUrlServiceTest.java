package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.UrlDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.domain.service.VersionedContentService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class DefaultDocumentUrlServiceTest {

    private static final int DEFAULT_DOC_ID = 1001;

    @Autowired
    private DocumentUrlService documentUrlService;

    @Autowired
    private DocumentUrlRepository documentUrlRepository;

    @Autowired
    private UrlDocumentDataInitializer documentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private VersionedContentService defaultDocumentUrlService;

    @Before
    public void setUp() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);
    }

    @Test
    public void saveDocumentUrl_Expect_Saved() {
        final DocumentUrlDTO urlDocument = DocumentUrlDTO.createDefault();
        urlDocument.setDocId(DEFAULT_DOC_ID);

        versionDataInitializer.createData(Version.WORKING_VERSION_INDEX, DEFAULT_DOC_ID);
        documentUrlService.save(urlDocument);

        assertEquals(1, documentUrlRepository.findAll().size());
    }

    @Test
    public void getDocumentUrlByDocId_When_DocumentExists_Expect_Found() {
        final DocumentUrlDTO expectedDocument = documentDataInitializer.createUrlDocument().getDocumentURL();
        final int docID = expectedDocument.getDocId();

        assertEquals(expectedDocument, documentUrlService.getByDocId(docID));
    }

    @Test
    public void createVersionedContent_Expect_Created() {
        final Version workingVersion = versionDataInitializer.createData(0, DEFAULT_DOC_ID);
        final Version newVersion = versionDataInitializer.createData(1, DEFAULT_DOC_ID);

        final DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA();
        documentUrlJPA.setUrlFrameName("test");
        documentUrlJPA.setUrl("test");
        documentUrlJPA.setUrlLanguagePrefix("t");
        documentUrlJPA.setUrlTarget("test");
        documentUrlJPA.setUrlText("test");
        documentUrlJPA.setVersion(workingVersion);

        documentUrlRepository.saveAndFlush(documentUrlJPA);
        defaultDocumentUrlService.createVersionedContent(workingVersion, newVersion);

        final List<DocumentUrlJPA> byDocId = documentUrlRepository.findByDocId(DEFAULT_DOC_ID);

        assertNotNull(documentUrlRepository.findByDocIdAndVersionNo(DEFAULT_DOC_ID, 1));
        assertEquals(2, byDocId.size());
    }
}