package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class FileDocumentServiceTest {

    private static File testSolrFolder;

    private int createdDocId;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @Autowired
    private Config config;

    @Value("WEB-INF/solr")
    private File defaultSolrFolder;

    @Autowired
    private DocumentService<FileDocumentDTO> fileDocumentService;

    @AfterClass
    public static void shutDownSolr() throws Exception {
        FileUtility.forceDelete(testSolrFolder);
    }

    @Before
    public void setUp() throws Exception {
        createdDocId = documentDataInitializer.createFileDocument().getId();
    }

    @PostConstruct
    private void setUpSolrFiles() throws IOException {
        testSolrFolder = new File(config.getSolrHome());

        if (testSolrFolder.mkdirs()) {
            FileUtils.copyDirectory(defaultSolrFolder, testSolrFolder);
        }
    }

    @Test
    public void createEmpty() {
        final FileDocumentDTO empty = fileDocumentService.createEmpty(Meta.DocumentType.FILE);

        assertNull(empty.getId());
        assertNotNull(empty.getFiles());
        assertTrue(empty.getFiles().isEmpty());
    }

    @Test
    public void get_When_NoFileSavedYet_Expect_Found() {
        final FileDocumentDTO fileDocumentDTO = fileDocumentService.get(createdDocId);

        assertNotNull(fileDocumentDTO);
    }

    @Test
    public void save_When_CustomFileSet_Expect_SavedWithSameDocId() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final FileDocumentDTO fileDocumentDTO = fileDocumentService.get(createdDocId);

        final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
        documentFileJPA.setDocId(createdDocId);
        documentFileJPA.setVersionIndex(Version.WORKING_VERSION_INDEX);
        documentFileJPA.setFileId("test_id_" + System.currentTimeMillis());
        documentFileJPA.setFilename("test_name" + System.currentTimeMillis());
        documentFileJPA.setMimeType("test" + System.currentTimeMillis());

        final List<DocumentFileDTO> documentFileDTOS = new ArrayList<>();
        documentFileDTOS.add(new DocumentFileDTO(documentFileJPA));
        fileDocumentDTO.setFiles(documentFileDTOS);

        final int savedDocId = fileDocumentService.save(fileDocumentDTO);

        assertEquals(savedDocId, createdDocId);
    }

    @Test
    public void save_When_CustomFileSet_Expect_CustomFileSaved() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final FileDocumentDTO fileDocumentDTO = fileDocumentService.get(createdDocId);

        final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
        documentFileJPA.setDocId(createdDocId);
        documentFileJPA.setVersionIndex(Version.WORKING_VERSION_INDEX);
        documentFileJPA.setFileId("test_id_" + System.currentTimeMillis());
        documentFileJPA.setFilename("test_name" + System.currentTimeMillis());
        documentFileJPA.setMimeType("test" + System.currentTimeMillis());

        final List<DocumentFileDTO> documentFileDTOS = new ArrayList<>();
        documentFileDTOS.add(new DocumentFileDTO(documentFileJPA));
        fileDocumentDTO.setFiles(documentFileDTOS);

        fileDocumentService.save(fileDocumentDTO);

        final List<DocumentFileDTO> savedFiles = fileDocumentService.get(createdDocId).getFiles();

        assertNotNull(savedFiles);
        assertEquals(savedFiles.size(), documentFileDTOS.size());

        final DocumentFileDTO savedFile = documentFileDTOS.get(0);

        assertEquals(savedFile.getDocId(), documentFileJPA.getDocId());
        assertEquals(savedFile.getFileId(), documentFileJPA.getFileId());
        assertEquals(savedFile.getFilename(), documentFileJPA.getFilename());
        assertEquals(savedFile.getMimeType(), documentFileJPA.getMimeType());
    }

    @Test
    public void publishDocument() {
    }

    @Test
    public void deleteByDocId() {
    }
}