package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import com.imcode.imcms.domain.service.SearchDocumentService;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.index.DocumentIndex;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.PostConstruct;
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

    private static final int DOC_ID = 1001;

    private static Imcms imcmsStatic;

    private static VersionDataInitializer versionDataInitializerStatic;

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

    @Autowired
    private Config config;

    @AfterClass
    public static void shutDownSolr() {
        imcmsStatic.stop();
        Imcms.removeUser();
        versionDataInitializerStatic.cleanRepositories();
    }

    @PostConstruct
    public void initSolr() throws Exception {
        imcmsStatic = imcms;
        versionDataInitializerStatic = versionDataInitializer;

        final File testSolrFolder = new File(config.getSolrHome());

        if (!testSolrFolder.mkdirs()) {
            assertTrue(FileUtility.forceDelete(testSolrFolder));
        }

        FileUtils.copyDirectory(defaultSolrFolder, testSolrFolder);

        versionDataInitializer.cleanRepositories();

        versionDataInitializer.createData(0, DOC_ID);
        versionDataInitializer.createData(1, DOC_ID);
        versionDataInitializer.createData(2, DOC_ID);

        Imcms.invokeStart();
        Thread.sleep(TimeUnit.SECONDS.toMillis(2)); // to let solr init, not sure 2 sec is exact time
        Imcms.setUser(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper().getDefaultUser());
    }

    @Test
    public void searchDocuments_When_DocId1001Requested_Expect_Found() {
        PageRequest pageRequest = new PageRequest(0, 10, new Sort(new Sort.Order(DocumentIndex.FIELD__META_ID)));

        SearchQueryDTO searchQueryDTO = new SearchQueryDTO();

        searchQueryDTO.setTerm(String.valueOf(DOC_ID));
        searchQueryDTO.setPage(pageRequest);

        assertEquals(1, searchDocumentService.searchDocuments(searchQueryDTO).size());
    }

}
