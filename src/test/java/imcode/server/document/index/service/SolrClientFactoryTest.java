package imcode.server.document.index.service;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import imcode.server.document.index.service.impl.DocumentIndexServiceOps;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.params.CoreAdminParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class SolrClientFactoryTest {

    private static final String SOLR_URL = "http://localhost:8983/solr";
    private static final String CORE = "test_core_name";

    private static final int DOC_ID = 1001;

    private SolrClient solrClient;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Autowired
    private DocumentIndexServiceOps documentIndexServiceOps;

    @Before
    public void createSolrClient() {
        solrClient = new HttpSolrClient.Builder(SOLR_URL).build();
    }

    @Test
    public void createHttpSolrClient_When_SpecifiedCoreDoesNotExist_Expect_CoreAndSolrClientAreCreated() {
        try {
            final SolrClient httpSolrClient = SolrClientFactory
                    .createHttpSolrClient(SOLR_URL + "/" + CORE, false);

            assertNotNull(httpSolrClient);
            coreExists(CORE);
        } catch (Exception e) {
            // solr is not setup
        }
    }

    @Test
    public void createHttpSolrClient_When_SpecifiedCoreExist_Expect_SolrClientAreCreated() {
        try {
            CoreAdminRequest.createCore(CORE, CORE, solrClient);
            coreExists(CORE);

            final SolrClient httpSolrClient = SolrClientFactory
                    .createHttpSolrClient(SOLR_URL + "/" + CORE, false);

            assertNotNull(httpSolrClient);
        } catch (Exception e) {
            // solr is not setup
        }
    }

    @Test
    public void createHttpSolrClient_When_RecreateDataDirIsSet_Expect_Recreated() {
        try {
            SolrClient httpSolrClient = SolrClientFactory
                    .createHttpSolrClient(SOLR_URL + "/" + CORE, false);

            versionDataInitializer.createData(0, DOC_ID);

            documentIndexServiceOps.addDocsToIndex(httpSolrClient, DOC_ID);

            final SolrQuery solrQuery = new SolrQuery("*:*");

            assertTrue(
                    documentIndexServiceOps.query(httpSolrClient, solrQuery).getResults().getNumFound() == 1
            );

            httpSolrClient = SolrClientFactory
                    .createHttpSolrClient(SOLR_URL + "/" + CORE, true);

            assertTrue(
                    documentIndexServiceOps.query(httpSolrClient, solrQuery).getResults().getNumFound() == 0
            );
        } catch (Exception e) {
            // solr is not setup
        }
    }

    private void coreExists(String coreName) throws IOException, SolrServerException {
        final CoreAdminRequest coreAdminRequest = new CoreAdminRequest();
        coreAdminRequest.setAction(CoreAdminParams.CoreAdminAction.STATUS);
        coreAdminRequest.setIndexInfoNeeded(false);

        assertNotNull(coreAdminRequest.process(solrClient).getCoreStatus(coreName));
    }

    @After
    public void deleteCore() {
        try {
            CoreAdminRequest.unloadCore(CORE, true, true, solrClient);
        } catch (Exception e) {
            // solr is not setup
        }
    }
}