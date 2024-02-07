package com.imcode.imcms.controller.api;

import com.imcode.imcms.components.datainitializer.FileDocumentDataInitializer;
import com.imcode.imcms.controller.AbstractControllerTest;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StoragePath;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import static com.imcode.imcms.api.SourceFile.FileType.FILE;
import static org.junit.Assert.*;

@Transactional
public class DocumentFilesControllerTest extends AbstractControllerTest {

    private static File testSolrFolder;

    private int createdDocId;

    @Value("classpath:img1.jpg")
    private File testFile;

    @Autowired
    @Qualifier("fileDocumentStorageClient")
    private StorageClient storageClient;

    @Value("${FilePath}")
    private String filesPath;

    @Autowired
    private FileDocumentDataInitializer documentDataInitializer;

    @Autowired
    private Config config;

    @Value("WEB-INF/solr")
    private Resource defaultSolrFolder;

    @Autowired
    private DocumentService<FileDocumentDTO> fileDocumentService;

    @AfterAll
    public static void shutDownSolr() {
        try {
            FileUtility.forceDelete(testSolrFolder);
        } catch (Exception e) {
            testSolrFolder.deleteOnExit();
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        createdDocId = documentDataInitializer.createFileDocument().getId();
    }

    @PostConstruct
    private void setUpSolrFiles() throws IOException {
        testSolrFolder = new File(config.getSolrHome());

        if (testSolrFolder.mkdirs()) {
            FileUtils.copyDirectory(defaultSolrFolder.getFile(), testSolrFolder);
        }
    }

    @Test
    public void save_When_MultipartFilesAttached_Expect_Saved() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user); // means current user is admin now

        final String filename = "test-file-" + System.currentTimeMillis() + ".jpg";

        final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
        documentFileJPA.setDocId(createdDocId);
        documentFileJPA.setVersionIndex(Version.WORKING_VERSION_INDEX);
        documentFileJPA.setFileId("test_id_" + System.currentTimeMillis());
        documentFileJPA.setFilename("");
        documentFileJPA.setOriginalFilename(filename);
        documentFileJPA.setMimeType("test" + System.currentTimeMillis());

        final FileDocumentDTO fileDocumentDTO = fileDocumentService.get(createdDocId);

        final List<DocumentFileDTO> newFiles = new ArrayList<>();
        newFiles.add(new DocumentFileDTO(documentFileJPA));
        fileDocumentDTO.setFiles(newFiles);

        fileDocumentService.save(fileDocumentDTO);

        final MockMultipartFile mockFile = new MockMultipartFile(
                "files", filename, null, FileUtils.readFileToByteArray(testFile)
        );

        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(controllerPath())
                .file(mockFile)
                .param("docId", "" + createdDocId);

        performRequestBuilderExpectedOk(requestBuilder);

        final List<DocumentFileDTO> savedFiles = fileDocumentService.get(createdDocId).getFiles();

        assertNotNull(savedFiles);
        assertEquals(savedFiles.size(), newFiles.size());

        for (DocumentFileDTO documentFileDTO : savedFiles) {
            assertEquals(documentFileDTO.getDocId().intValue(), createdDocId);
            final StoragePath savedFilePath = StoragePath.get(FILE, filesPath, documentFileDTO.getFilename());
            assertTrue(storageClient.exists(savedFilePath));

            storageClient.delete(savedFilePath, false);
        }
    }

    @Override
    protected String controllerPath() {
        return "/file-documents/files";
    }
}
