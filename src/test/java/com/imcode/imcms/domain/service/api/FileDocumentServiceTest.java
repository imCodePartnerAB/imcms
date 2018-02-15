package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.FileDocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.DocumentFile;
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
import org.springframework.mock.web.MockMultipartFile;
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
@ContextConfiguration(classes = {TestConfig.class})
public class FileDocumentServiceTest {

    private static File testSolrFolder;
    private static File testFilesFolder;

    private FileDocumentDTO createdDoc;
    private int createdDocId;

    @Value("classpath:img1.jpg")
    private File testFile;

    @Value("${FilePath}")
    private File filesPath;

    @Autowired
    private FileDocumentDataInitializer documentDataInitializer;

    @Autowired
    private Config config;

    @Value("WEB-INF/solr")
    private File defaultSolrFolder;

    @Autowired
    private DocumentService<FileDocumentDTO> fileDocumentService;

    @Autowired
    private DocumentFileService documentFileService;

    @AfterClass
    public static void shutDownSolr() {
        try {
            FileUtility.forceDelete(testSolrFolder);

        } catch (Exception e) {
            testSolrFolder.deleteOnExit();

        } finally {
            try {
                FileUtility.forceDelete(testFilesFolder);

            } catch (Exception e) {
                testFilesFolder.deleteOnExit();
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        createdDoc = documentDataInitializer.createFileDocument();
        createdDocId = createdDoc.getId();
        filesPath.mkdirs();
    }

    @PostConstruct
    private void setUpSolrFiles() throws IOException {
        testSolrFolder = new File(config.getSolrHome());
        testFilesFolder = filesPath;

        if (testSolrFolder.mkdirs()) {
            FileUtils.copyDirectory(defaultSolrFolder, testSolrFolder);
        }
    }

    @Test
    public void createEmpty() {
        final FileDocumentDTO empty = fileDocumentService.createEmpty();

        assertNull(empty.getId());
        assertEquals(empty.getType(), Meta.DocumentType.FILE);
        assertNotNull(empty.getFiles());
        assertTrue(empty.getFiles().isEmpty());
    }

    @Test
    public void get_When_NoFileSavedYet_Expect_Found() {
        documentFileService.saveAll(createdDoc.getFiles(), createdDocId);
        final FileDocumentDTO fileDocumentDTO = fileDocumentService.get(createdDocId);

        assertEquals(fileDocumentDTO, createdDoc);
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

        final int savedDocId = fileDocumentService.save(fileDocumentDTO).getId();

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

        final List<DocumentFileDTO> documentFileDTOS = fileDocumentDTO.getFiles();
        documentFileDTOS.add(new DocumentFileDTO(documentFileJPA));

        fileDocumentService.save(fileDocumentDTO);

        final List<DocumentFileDTO> savedFiles = fileDocumentService.get(createdDocId).getFiles();

        assertNotNull(savedFiles);
        assertEquals(savedFiles.size(), documentFileDTOS.size());

        for (DocumentFileDTO documentFileDTO : documentFileDTOS) {
            assertEquals(documentFileDTO.getDocId().intValue(), createdDocId);
        }
    }

    @Test
    public void publishDocument() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final FileDocumentDTO fileDocumentDTO = fileDocumentService.get(createdDocId);

        final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
        documentFileJPA.setDocId(createdDocId);
        documentFileJPA.setVersionIndex(Version.WORKING_VERSION_INDEX);
        documentFileJPA.setFileId("test_id_" + System.currentTimeMillis());
        documentFileJPA.setFilename("test_name" + System.currentTimeMillis());
        documentFileJPA.setDefaultFile(true);
        documentFileJPA.setMimeType("test" + System.currentTimeMillis());

        final List<DocumentFileDTO> documentFileDTOS = new ArrayList<>();
        documentFileDTOS.add(new DocumentFileDTO(documentFileJPA));
        fileDocumentDTO.setFiles(documentFileDTOS);

        fileDocumentService.save(fileDocumentDTO);

        final boolean published = fileDocumentService.publishDocument(createdDocId, user.getId());

        assertTrue(published);

        final DocumentFile publicByDocId = documentFileService.getPublicByDocId(createdDocId);

        assertNotNull(publicByDocId);
        assertEquals(publicByDocId.getDocId().intValue(), createdDocId);
        assertEquals(publicByDocId.getFilename(), documentFileJPA.getFilename());
    }

    @Test
    public void save_When_FileIsSetAndNewIsSaved_Expect_OldRemovedAndNewSaved() {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final FileDocumentDTO fileDocumentDTO = fileDocumentService.get(createdDocId);
        final List<DocumentFileDTO> oldFiles = fileDocumentDTO.getFiles();

        final DocumentFileDTO documentFile = new DocumentFileDTO();
        documentFile.setDocId(createdDocId);
        documentFile.setFileId("test_id_" + System.currentTimeMillis());
        documentFile.setFilename("test_name" + System.currentTimeMillis());
        documentFile.setMimeType("test" + System.currentTimeMillis());

        final List<DocumentFileDTO> newFiles = new ArrayList<>();
        newFiles.add(documentFile);
        fileDocumentDTO.setFiles(newFiles);

        fileDocumentService.save(fileDocumentDTO);

        final List<DocumentFileDTO> savedFiles = fileDocumentService.get(createdDocId).getFiles();

        assertNotNull(savedFiles);
        assertEquals(savedFiles.size(), newFiles.size());
        assertFalse(savedFiles.containsAll(oldFiles));

        for (DocumentFileDTO documentFileDTO : newFiles) {
            assertEquals(documentFileDTO.getDocId().intValue(), createdDocId);
        }
    }

    @Test
    public void save_When_MultipartFilesAttached_Expect_Saved() throws IOException {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final FileDocumentDTO fileDocumentDTO = fileDocumentService.get(createdDocId);
        final List<DocumentFileDTO> oldFiles = fileDocumentDTO.getFiles();
        final String filename = "test-file-" + System.currentTimeMillis() + ".jpg";
        final MockMultipartFile mockFile = new MockMultipartFile(
                "file", filename, null, FileUtils.readFileToByteArray(testFile)
        );

        final DocumentFileDTO documentFile = new DocumentFileDTO();
        documentFile.setDocId(createdDocId);
        documentFile.setFilename(filename);
        documentFile.setMimeType("test" + System.currentTimeMillis());
        documentFile.setMultipartFile(mockFile);

        final List<DocumentFileDTO> newFiles = new ArrayList<>();
        newFiles.add(documentFile);
        fileDocumentDTO.setFiles(newFiles);

        fileDocumentService.save(fileDocumentDTO);

        final List<DocumentFileDTO> savedFiles = fileDocumentService.get(createdDocId).getFiles();

        assertNotNull(savedFiles);
        assertEquals(savedFiles.size(), newFiles.size());
        assertFalse(savedFiles.containsAll(oldFiles));

        for (DocumentFileDTO documentFileDTO : newFiles) {
            assertEquals(documentFileDTO.getDocId().intValue(), createdDocId);
            final File savedFile = new File(filesPath, documentFileDTO.getFilename());
            assertTrue(savedFile.exists());

            try {
                FileUtility.forceDelete(savedFile);
            } catch (Exception e) {
                savedFile.deleteOnExit();
            }
        }
    }

    @Test
    public void deleteByDocId() {
    }
}