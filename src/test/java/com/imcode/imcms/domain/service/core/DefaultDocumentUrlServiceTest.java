package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.domain.service.VersionedContentService;
import com.imcode.imcms.model.DocumentURL;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private VersionedContentService<DocumentURL> defaultDocumentUrlService;

    @Test
    public void saveDocumentUrl_Expect_Saved() {
        final DocumentUrlDTO urlDocument = DocumentUrlDTO.createDefault();
        urlDocument.setDocId(1001);

        versionDataInitializer.createData(Version.WORKING_VERSION_INDEX, DEFAULT_DOC_ID);
        documentUrlService.save(urlDocument);

        assertEquals(1, documentUrlRepository.findAll().size());
    }

    @Test
    public void getDocumentUrlByDocId_When_DocumentExists_Expect_Found() {
        final DocumentUrlDTO expectedDocument = documentDataInitializer.createUrlDocument().getDocumentUrlDTO();
        final int docID = expectedDocument.getDocId();

        assertEquals(expectedDocument, documentUrlService.getByDocId(docID));
    }

    @Test
    public void getDocumentUrlByVersion_When_SpecifiedVersionExists_Expect_Found() {
        versionDataInitializer.createData(1, DEFAULT_DOC_ID);
        final Version version = versionDataInitializer.createData(2, DEFAULT_DOC_ID);
        versionDataInitializer.createData(3, DEFAULT_DOC_ID);

        final DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA();
        documentUrlJPA.setUrlFrameName("test");
        documentUrlJPA.setUrl("test");
        documentUrlJPA.setUrlLanguagePrefix("t");
        documentUrlJPA.setUrlTarget("test");
        documentUrlJPA.setUrlText("test");
        documentUrlJPA.setVersion(version);

        final int savedId = documentUrlRepository.saveAndFlush(documentUrlJPA).getId();
        documentUrlJPA.setId(savedId);

        final Set<DocumentURL> byVersion = defaultDocumentUrlService.getByVersion(version);

        assertEquals(1, byVersion.size());
        assertTrue(byVersion.contains(new DocumentUrlDTO(documentUrlJPA)));
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