package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.UrlDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.domain.service.VersionedContentService;
import com.imcode.imcms.model.DocumentURL;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
public class DefaultDocumentUrlServiceTest extends WebAppSpringTestConfig {

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

    @BeforeEach
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

    @Test
    public void getByDocIdAndVersionNo_Expected_DocumentUrlOfSpecificVersion() {
        final String url = "url";

        final UrlDocumentDTO urlDocumentWorkingVersion = documentDataInitializer.createUrlDocument(url);
        final DocumentUrlDTO documentUrlWorkingVersion = urlDocumentWorkingVersion.getDocumentURL();

        final int docId = urlDocumentWorkingVersion.getId();

        final Version version1 = versionDataInitializer.createData(1, docId);
        final DocumentUrlJPA documentUrlVersion1 = new DocumentUrlJPA(documentUrlWorkingVersion, version1);
        documentUrlVersion1.setId(null);
        documentUrlVersion1.setUrl(url + version1.getNo());

        final Version version2 = versionDataInitializer.createData(2, docId);
        final DocumentUrlJPA documentUrlVersion2 = new DocumentUrlJPA(documentUrlWorkingVersion, version2);
        documentUrlVersion2.setId(null);
        documentUrlVersion2.setUrl(url + version2.getNo());

        documentUrlRepository.saveAll(List.of(documentUrlVersion1, documentUrlVersion2));

        final DocumentURL expectedUrlVersion1 = new DocumentUrlDTO(documentUrlVersion1);
        final DocumentURL receivedUrlVersion1 = documentUrlService.getByDocIdAndVersionNo(docId, version1.getNo());

        assertEquals(expectedUrlVersion1, receivedUrlVersion1);
    }

    @Test
    public void setAsWorkingVersion_Expected_CopyDocumentUrlFromSpecificVersionToWorkingVersion(){
        final String url = "url";

        final UrlDocumentDTO urlDocumentWorkingVersion = documentDataInitializer.createUrlDocument(url);
        final DocumentUrlDTO documentUrlWorkingVersion = urlDocumentWorkingVersion.getDocumentURL();

        final int docId = urlDocumentWorkingVersion.getId();

        final Version version1 = versionDataInitializer.createData(1, docId);
        final DocumentUrlJPA documentUrlVersion1 = new DocumentUrlJPA(documentUrlWorkingVersion, version1);
        documentUrlVersion1.setId(null);
        documentUrlVersion1.setUrl(url + version1.getNo());

        final Version version2 = versionDataInitializer.createData(2, docId);
        final DocumentUrlJPA documentUrlVersion2 = new DocumentUrlJPA(documentUrlWorkingVersion, version2);
        documentUrlVersion2.setId(null);
        documentUrlVersion2.setUrl(url + version2.getNo());

        documentUrlRepository.saveAll(List.of(documentUrlVersion1, documentUrlVersion2));

        final DocumentUrlDTO urlWorkingVersionBeforeReset = documentUrlWorkingVersion;
        urlWorkingVersionBeforeReset.setId(null);
        final DocumentUrlDTO urlVersion1BeforeReset = new DocumentUrlDTO(documentUrlVersion1);
        urlVersion1BeforeReset.setId(null);
        final DocumentUrlDTO urlVersion2BeforeReset = new DocumentUrlDTO(documentUrlVersion2);
        urlVersion2BeforeReset.setId(null);

        documentUrlService.setAsWorkingVersion(version1);

        final DocumentUrlDTO urlWorkingVersionAfterReset = new DocumentUrlDTO(documentUrlRepository.findByDocIdAndVersionNo(docId, Version.WORKING_VERSION_INDEX));
        urlWorkingVersionAfterReset.setId(null);
        final DocumentUrlDTO urlVersion1AfterReset = new DocumentUrlDTO(documentUrlRepository.findByDocIdAndVersionNo(docId, version1.getNo()));
        urlVersion1AfterReset.setId(null);
        final DocumentUrlDTO urlVersion2AfterReset = new DocumentUrlDTO(documentUrlRepository.findByDocIdAndVersionNo(docId, version2.getNo()));
        urlVersion2AfterReset.setId(null);

        assertNotEquals(urlWorkingVersionBeforeReset, urlWorkingVersionAfterReset);
        assertEquals(urlVersion1BeforeReset, urlWorkingVersionAfterReset);
        assertEquals(urlVersion1BeforeReset, urlVersion1AfterReset);
        assertEquals(urlVersion2BeforeReset, urlVersion2AfterReset);
    }
}