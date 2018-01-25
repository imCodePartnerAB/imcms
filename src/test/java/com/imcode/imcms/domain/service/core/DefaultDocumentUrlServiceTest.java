package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class DefaultDocumentUrlServiceTest {

    @Autowired
    public DocumentUrlService documentUrlService;

    @Autowired
    public DocumentUrlRepository documentUrlRepository;

    @Autowired
    public DocumentDataInitializer documentDataInitializer;

    @Autowired
    public VersionDataInitializer versionDataInitializer;

    @Test
    public void saveDocumentUrl_Expect_Saved() {
        final DocumentUrlDTO urlDocument = DocumentUrlDTO.createDefault();
        urlDocument.setDocId(1001);

        versionDataInitializer.createData(Version.WORKING_VERSION_INDEX, 1001);
        documentUrlService.save(urlDocument);

        assertEquals(1, documentUrlRepository.findAll().size());
    }

    @Test
    public void getDocumentUrlByDocId_When_DocumentExists_Expect_Found() {
        final DocumentUrlDTO expectedDocument = documentDataInitializer.createUrlDocument().getDocumentUrlDTO();
        final int docID = expectedDocument.getDocId();

        assertEquals(expectedDocument, documentUrlService.getByDocId(docID));
    }
}