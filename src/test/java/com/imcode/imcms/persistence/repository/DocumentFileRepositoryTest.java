package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class DocumentFileRepositoryTest {

    @Autowired
    private DocumentFileRepository documentFileRepository;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    private DocumentFileJPA createdDocumentFile;

    @Before
    public void setUp() throws Exception {
        final DocumentDTO documentDTO = documentDataInitializer.createData();

        final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
        documentFileJPA.setDocId(documentDTO.getId());
        documentFileJPA.setFileId("test_id");
        documentFileJPA.setFilename("test_name");
        documentFileJPA.setMimeType("test");

        createdDocumentFile = documentFileRepository.save(documentFileJPA);
    }

    @Test
    public void findByDocId() {
        final List<DocumentFileJPA> foundFiles = documentFileRepository.findByDocId(createdDocumentFile.getDocId());
        assertEquals(foundFiles.size(), 1);
        assertTrue(foundFiles.contains(createdDocumentFile));
    }

}
