package com.imcode.imcms.domain.dto;

import com.imcode.imcms.components.datainitializer.FileDocumentDataInitializer;
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

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class FileDocumentDTOTest {

    @Autowired
    private FileDocumentDataInitializer fileDocumentDataInitializer;

    @Test
    public void cloneFileDocumentDTO_Expect_Cloned() {
        final FileDocumentDTO originalFileDocument = fileDocumentDataInitializer.createFileDocument();
        final FileDocumentDTO cloneFileDocument = originalFileDocument.clone();

        assertSame(cloneFileDocument.getClass(), originalFileDocument.getClass());
        assertNotSame(cloneFileDocument, originalFileDocument);

        assertNull(cloneFileDocument.getId());
        assertThat(cloneFileDocument.getPublicationStatus(), is(Meta.PublicationStatus.NEW));

        checkAuditDTO(cloneFileDocument.getPublicationEnd());
        checkAuditDTO(cloneFileDocument.getArchived());
        checkAuditDTO(cloneFileDocument.getCreated());
        checkAuditDTO(cloneFileDocument.getModified());

        cloneFileDocument.getCommonContents()
                .forEach(commonContent -> {
                    assertNull(commonContent.getId());
                    assertNull(commonContent.getDocId());
                    assertThat(commonContent.getVersionNo(), is(Version.WORKING_VERSION_INDEX));
                });

        final List<DocumentFileDTO> originalFiles = originalFileDocument.getFiles();
        final List<DocumentFileDTO> cloneFiles = cloneFileDocument.getFiles();

        assertNotSame(originalFiles, cloneFiles);

        IntStream.range(0, originalFiles.size())
                .forEach(i -> {
                    final DocumentFileDTO originalDocumentFileDTO = originalFiles.get(i);
                    final DocumentFileDTO cloneDocumentFileDTO = cloneFiles.get(i);

                    assertNotSame(originalDocumentFileDTO, cloneDocumentFileDTO);

                    assertNotNull(originalDocumentFileDTO.getId());
                    assertNotNull(originalDocumentFileDTO.getDocId());

                    assertNull(cloneDocumentFileDTO.getId());
                    assertNull(cloneDocumentFileDTO.getDocId());
                });
    }

    private void checkAuditDTO(AuditDTO auditDTO) {
        assertNull(auditDTO.getId());
        assertNull(auditDTO.getTime());
        assertNull(auditDTO.getDate());
    }
}