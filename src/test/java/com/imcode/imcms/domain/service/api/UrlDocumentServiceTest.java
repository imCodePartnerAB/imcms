package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.persistence.entity.Meta;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class UrlDocumentServiceTest {

    @Autowired
    private DocumentService<UrlDocumentDTO> urlDocumentService;

    @Test
    public void createEmpty() {
        final UrlDocumentDTO emptyUrlDocumentDTO = urlDocumentService.createEmpty();

        final DocumentUrlDTO expectedDefaultDocumentUrlDTO = new DocumentUrlDTO();
        expectedDefaultDocumentUrlDTO.setId(null);
        expectedDefaultDocumentUrlDTO.setDocId(null);
        expectedDefaultDocumentUrlDTO.setUrlFrameName("");
        expectedDefaultDocumentUrlDTO.setUrlTarget("");
        expectedDefaultDocumentUrlDTO.setUrl("");
        expectedDefaultDocumentUrlDTO.setUrlText("");
        expectedDefaultDocumentUrlDTO.setUrlLanguagePrefix("");

        final DocumentUrlDTO actualDefaultDocumentUrlDTO = emptyUrlDocumentDTO.getDocumentUrlDTO();

        assertNull(emptyUrlDocumentDTO.getId());
        assertEquals(emptyUrlDocumentDTO.getType(), Meta.DocumentType.URL);
        assertNotNull(actualDefaultDocumentUrlDTO);
        assertEquals(expectedDefaultDocumentUrlDTO, actualDefaultDocumentUrlDTO);
    }
}