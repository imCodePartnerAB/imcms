package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.FileDocumentDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Transactional
public class DocumentFilesControllerTest extends AbstractControllerTest {

    private static File testSolrFolder;
    private static File testFilesFolder;

    private int createdDocId;

    @Value("classpath:img1.jpg")
    private File testFile;

    @Value("${FilePath}")
    private Resource filesPath;

    @Autowired
    private FileDocumentDataInitializer documentDataInitializer;

    @Autowired
    private Config config;

    @Value("WEB-INF/solr")
    private Resource defaultSolrFolder;

    @Autowired
    private DocumentService<FileDocumentDTO> fileDocumentService;

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
        createdDocId = documentDataInitializer.createFileDocument().getId();
        filesPath.getFile().mkdirs();
    }

    @PostConstruct
    private void setUpSolrFiles() throws IOException {
        testSolrFolder = new File(config.getSolrHome());
        testFilesFolder = filesPath.getFile();

        if (testSolrFolder.mkdirs()) {
            FileUtils.copyDirectory(defaultSolrFolder.getFile(), testSolrFolder);
        }
    }

    @Test
    public void save_When_MultipartFilesAttached_Expect_Saved() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(RoleId.SUPERADMIN);
        Imcms.setUser(user); // means current user is admin now

        final String filename = "test-file-" + System.currentTimeMillis() + ".jpg";

        final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
        documentFileJPA.setDocId(createdDocId);
        documentFileJPA.setVersionIndex(Version.WORKING_VERSION_INDEX);
        documentFileJPA.setFileId("test_id_" + System.currentTimeMillis());
        documentFileJPA.setFilename(filename);
        documentFileJPA.setMimeType("test" + System.currentTimeMillis());

        final FileDocumentDTO fileDocumentDTO = fileDocumentService.get(createdDocId);

        final List<DocumentFileDTO> newFiles = new ArrayList<>();
        newFiles.add(new DocumentFileDTO(documentFileJPA));
        fileDocumentDTO.setFiles(newFiles);

        fileDocumentService.save(fileDocumentDTO);

        final MockMultipartFile mockFile = new MockMultipartFile(
                "files", filename, null, FileUtils.readFileToByteArray(testFile)
        );

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.fileUpload(controllerPath())
                .file(mockFile)
                .param("docId", "" + createdDocId);

        performRequestBuilderExpectedOk(requestBuilder);

        final List<DocumentFileDTO> savedFiles = fileDocumentService.get(createdDocId).getFiles();

        assertNotNull(savedFiles);
        assertEquals(savedFiles.size(), newFiles.size());

        for (DocumentFileDTO documentFileDTO : newFiles) {
            assertEquals(documentFileDTO.getDocId().intValue(), createdDocId);
            final File savedFile = new File(filesPath.getFile(), documentFileDTO.getFilename());
            assertTrue(savedFile.exists());

            try {
                FileUtility.forceDelete(savedFile);
            } catch (Exception e) {
                savedFile.deleteOnExit();
            }
        }
    }

    @Override
    protected String controllerPath() {
        return "/file-documents/files";
    }
}