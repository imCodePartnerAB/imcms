package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.model.DocumentURL;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
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
public class UrlDocumentServiceTest {

    @Autowired
    private DocumentService<UrlDocumentDTO> urlDocumentService;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private DocumentUrlRepository documentUrlRepository;

    @Autowired
    private DocumentUrlService documentUrlService;

    private UrlDocumentDTO emptyUrlDocumentDTO;

    @Before
    public void setUp() {
        Imcms.setUser(new UserDomainObject(1));
        this.emptyUrlDocumentDTO = urlDocumentService.createEmpty();
    }

    @Test
    public void createEmpty() {
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

    @Test
    public void saveUrlDocument_When_documentDoesNotExist_Expect_Saved() {
        emptyUrlDocumentDTO.setDocumentUrlDTO(null);
        urlDocumentService.save(emptyUrlDocumentDTO);

        assertEquals(2, metaRepository.findAll().size()); // one doc with 1001 id
    }

    @Test
    public void saveUrlDocument_When_DocumentUrlDTOIsNotSet_Expect_DocumentUrlDtoIsNotSaved() {
        emptyUrlDocumentDTO.setDocumentUrlDTO(null);
        urlDocumentService.save(emptyUrlDocumentDTO);

        assertEquals(0, documentUrlRepository.findAll().size());
    }

    @Test
    public void saveUrlDocument_When_DocumentUrlDtoIsSet_Expect_DocumentUrlDtoIsSaved() {
        final int savedDocId = urlDocumentService.save(emptyUrlDocumentDTO);
        final List<DocumentUrlJPA> all = documentUrlRepository.findAll();

        assertEquals(1, all.size());

        final DocumentURL actualDocumentUrl = documentUrlService.getByDocId(savedDocId);

        final DocumentUrlDTO expectedDocumentUrl = emptyUrlDocumentDTO.getDocumentUrlDTO();
        expectedDocumentUrl.setId(actualDocumentUrl.getId());

        assertEquals(expectedDocumentUrl, expectedDocumentUrl);
    }

    @Test
    public void getDocumentUrlByDocId_When_DocumentExists_Expect_Found() {
        final int savedDocId = urlDocumentService.save(emptyUrlDocumentDTO);
        final UrlDocumentDTO actualUrlDocumentDTO = urlDocumentService.get(savedDocId);

        assertNotNull(actualUrlDocumentDTO);
    }

    @Test(expected = DocumentNotExistException.class)
    public void deleteDocumentUrl_When_Expect_Deleted() {
        final int savedDocId = urlDocumentService.save(emptyUrlDocumentDTO);

        assertNotNull(urlDocumentService.get(savedDocId));

        urlDocumentService.deleteByDocId(savedDocId);
        urlDocumentService.get(savedDocId);
    }
}