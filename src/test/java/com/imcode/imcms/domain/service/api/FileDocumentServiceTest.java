package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.Config;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class FileDocumentServiceTest {

    private static File testSolrFolder;

    private FileDocumentDTO createdDoc;

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
        createdDoc = documentDataInitializer.createFileDocument();
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
        assertNotNull(empty.getFile());
        assertNull(empty.getFile().getId());
    }

    @Test
    public void get() {
    }

    @Test
    public void save() {
    }

    @Test
    public void publishDocument() {
    }

    @Test
    public void deleteByDocId() {
    }
}