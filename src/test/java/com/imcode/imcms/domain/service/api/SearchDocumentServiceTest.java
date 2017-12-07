package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.SearchDocumentService;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.index.DocumentIndex;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by dmizem from Ubrainians for imCode on 20.10.17.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class SearchDocumentServiceTest {
    private File testSolrFolder;

    @Value("WEB-INF/solr")
    private File defaultSolrFolder;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private SearchDocumentService searchDocumentService;

    @Autowired
    private ImcmsServices imcmsServices;

    @Autowired
    private Imcms imcms;

    @Before
    public void setUp() throws Exception {
        versionDataInitializer.cleanRepositories();

        testSolrFolder = new File(defaultSolrFolder.getParentFile().getAbsolutePath() + "/test-solr");
        testSolrFolder.mkdirs();
        FileUtils.copyDirectory(defaultSolrFolder, testSolrFolder);
        imcmsServices.getConfig().setSolrHome(testSolrFolder.getAbsolutePath());

        versionDataInitializer.createData(0, 1001);
        versionDataInitializer.createData(1, 1001);
        versionDataInitializer.createData(2, 1001);

        Imcms.invokeStart();

        Imcms.setUser(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper().getDefaultUser());
    }

    @After
    public void tearDown() throws Exception {
        imcms.stop();
        Thread.sleep(TimeUnit.SECONDS.toMillis(2)); // to let solr shut down, not sure 2 sec is exact time
        versionDataInitializer.cleanRepositories();
        assertTrue(FileUtility.forceDelete(testSolrFolder));
    }

    @Test
    public void searchDocuments() throws Exception {
        Thread.sleep(TimeUnit.SECONDS.toMillis(2)); // to let solr init, not sure 2 sec is exact time
        PageRequest pageRequest = new PageRequest(0, 10, new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID)));

        SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

        searchQueryDTO.setUserId(1);
        searchQueryDTO.setTerm("1001");
        searchQueryDTO.setPage(pageRequest);

        assertEquals(1, searchDocumentService.searchDocuments(searchQueryDTO).size());
    }

}
