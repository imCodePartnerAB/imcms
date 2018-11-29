package com.imcode.imcms.domain.dto;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TextDocumentDataInitializer;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class TextDocumentDTOTest extends WebAppSpringTestConfig {

    @Autowired
    private TextDocumentDataInitializer documentDataInitializer;

    @Test
    public void cloneTextDocumentDTO_Expect_Cloned() {
        final TextDocumentDTO textDocumentDTO = documentDataInitializer.createTextDocument();
        final TextDocumentDTO cloneTextDocumentDTO = textDocumentDTO.clone();

        assertSame(cloneTextDocumentDTO.getClass(), textDocumentDTO.getClass());
        assertNotSame(cloneTextDocumentDTO, textDocumentDTO);

        assertNull(cloneTextDocumentDTO.getId());
        assertThat(cloneTextDocumentDTO.getPublicationStatus(), is(Meta.PublicationStatus.NEW));

        checkAuditDTO(cloneTextDocumentDTO.getPublicationEnd());
        checkAuditDTO(cloneTextDocumentDTO.getArchived());
        checkAuditDTO(cloneTextDocumentDTO.getCreated());
        checkAuditDTO(cloneTextDocumentDTO.getModified());

        cloneTextDocumentDTO.getCommonContents()
                .forEach(commonContent -> {
                    assertNull(commonContent.getId());
                    assertNull(commonContent.getDocId());
                    assertThat(commonContent.getVersionNo(), is(Version.WORKING_VERSION_INDEX));
                });

        final TextDocumentTemplateDTO originalTemplate = textDocumentDTO.getTemplate();
        final TextDocumentTemplateDTO clonedTemplate = cloneTextDocumentDTO.getTemplate();

        assertNotSame(originalTemplate, clonedTemplate);
        assertNull(clonedTemplate.getDocId());
    }

    private void checkAuditDTO(AuditDTO auditDTO) {
        assertNull(auditDTO.getId());
        assertNull(auditDTO.getTime());
        assertNull(auditDTO.getDate());
    }
}