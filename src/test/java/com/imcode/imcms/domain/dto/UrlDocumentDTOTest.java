package com.imcode.imcms.domain.dto;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.UrlDocumentDataInitializer;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class UrlDocumentDTOTest extends WebAppSpringTestConfig {

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