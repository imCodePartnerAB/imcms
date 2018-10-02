package com.imcode.imcms.domain.dto;

import com.imcode.imcms.components.datainitializer.UrlDocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class UrlDocumentDTOTest {

    @Autowired
    private UrlDocumentDataInitializer urlDocumentDataInitializer;

    @Test
    public void cloneUrlDocumentDTO_Expect_Cloned() {
        final UrlDocumentDTO originalUrlDocument = urlDocumentDataInitializer.createUrlDocument();
        final UrlDocumentDTO clonedUrlDocument = originalUrlDocument.clone();

        assertSame(clonedUrlDocument.getClass(), originalUrlDocument.getClass());
        assertNotSame(clonedUrlDocument, originalUrlDocument);

        assertNull(clonedUrlDocument.getId());
        assertThat(clonedUrlDocument.getPublicationStatus(), is(Meta.PublicationStatus.NEW));

        checkAuditDTO(clonedUrlDocument.getPublicationEnd());
        checkAuditDTO(clonedUrlDocument.getArchived());
        checkAuditDTO(clonedUrlDocument.getCreated());
        checkAuditDTO(clonedUrlDocument.getModified());

        clonedUrlDocument.getCommonContents()
                .forEach(commonContent -> {
                    assertNull(commonContent.getId());
                    assertNull(commonContent.getDocId());
                    assertThat(commonContent.getVersionNo(), is(Version.WORKING_VERSION_INDEX));
                });

        final DocumentUrlDTO documentURL = originalUrlDocument.getDocumentURL();
        final DocumentUrlDTO clonedDocumentUrl = clonedUrlDocument.getDocumentURL();

        assertNotSame(documentURL, clonedDocumentUrl);

        assertNull(clonedDocumentUrl.getId());
        assertNull(clonedDocumentUrl.getDocId());
    }

    private void checkAuditDTO(AuditDTO auditDTO) {
        assertNull(auditDTO.getId());
        assertNull(auditDTO.getTime());
        assertNull(auditDTO.getDate());
    }
}