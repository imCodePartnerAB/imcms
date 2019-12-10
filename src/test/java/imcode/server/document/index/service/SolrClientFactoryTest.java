package imcode.server.document.index.service;

import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.config.TestConfig;
import imcode.server.document.index.service.impl.DocumentIndexServiceOps;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.solr.client.solrj.SolrClient;
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
import java.net.ConnectException;

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

    @After
    public void deleteCore() throws IOException {
        try {
            CoreAdminRequest.unloadCore(CORE, true, true, solrClient);

        } catch (SolrServerException e) {
            // solr is not set up, ok.
            checkException(e);
        }
    }

    @Test
    public void createHttpSolrClient_When_SpecifiedCoreDoesNotExist_Expect_CoreAndSolrClientAreCreated() throws IOException, SolrServerException {
        try {
            final SolrClient httpSolrClient = SolrClientFactory.createHttpSolrClient(
                    SOLR_URL + "/" + CORE, false
            );

            assertNotNull(httpSolrClient);
            coreExists(CORE);

        } catch (IllegalArgumentException e) {
            // solr is not set up, ok.
            checkException(e);
        }
    }

    @Test
    public void createHttpSolrClient_When_SpecifiedCoreExist_Expect_SolrClientAreCreated() throws IOException {
        try {
            CoreAdminRequest.createCore(CORE, CORE, solrClient);
            coreExists(CORE);

            final SolrClient httpSolrClient = SolrClientFactory.createHttpSolrClient(
                    SOLR_URL + "/" + CORE, false
            );

            assertNotNull(httpSolrClient);

        } catch (SolrServerException e) {
            // solr is not set up, ok.
            checkException(e);
        }
    }

    //todo cache in CI problem..
    @Test
    public void createHttpSolrClient_When_RecreateDataDirIsSet_Expect_Recreated() throws IOException, SolrServerException {
//        try {
//            SolrClient httpSolrClient = SolrClientFactory.createHttpSolrClient(
//                    SOLR_URL + "/" + CORE, false
//            );
//
//            versionDataInitializer.createData(0, DOC_ID);
//
//            documentIndexServiceOps.addDocsToIndex(httpSolrClient, DOC_ID);
//
//            final SolrQuery query = new SolrQuery("*:*");
//
//            assertEquals(1, documentIndexServiceOps.query(httpSolrClient, query).getResults().getNumFound());
//
//            httpSolrClient = SolrClientFactory.createHttpSolrClient(
//                    SOLR_URL + "/" + CORE, true
//            );
//
//            assertEquals(0, documentIndexServiceOps.query(httpSolrClient, query).getResults().getNumFound());
//
//        } catch (IllegalArgumentException e) {
//            // solr is not set up, ok.
//            checkException(e);
//        }
    }

    private void coreExists(String coreName) throws IOException, SolrServerException {
        final CoreAdminRequest coreAdminRequest = new CoreAdminRequest();
        coreAdminRequest.setAction(CoreAdminParams.CoreAdminAction.STATUS);
        coreAdminRequest.setIndexInfoNeeded(false);

        assertNotNull(coreAdminRequest.process(solrClient).getCoreStatus(coreName));
    }

    private void checkException(IllegalArgumentException e) {
        final Throwable cause = e.getCause();
        assertTrue(cause instanceof SolrServerException);
        checkException(((SolrServerException) cause));
    }

    private void checkException(SolrServerException e) {
        Throwable cause = e.getCause();
        assertTrue(cause instanceof HttpHostConnectException);

        cause = cause.getCause();
        assertTrue(cause instanceof ConnectException);
    }
}
