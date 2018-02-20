package imcode.server.document.index.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentIndexServiceOpsTest {

    @Mock
    private QueryResponse queryResponse;

    @Mock
    private SolrServer solrServer;

    @Mock
    private SolrQuery solrQuery;

    @InjectMocks
    private DocumentIndexServiceOps documentIndexServiceOps;

    @Test
    public void query_When_SolrServerAndSolrQuerySpecified_Expect_QueryResponseReturned() throws SolrServerException {
        when(solrServer.query(solrQuery)).thenReturn(queryResponse);

        final QueryResponse response = documentIndexServiceOps.query(solrServer, solrQuery);

        assertNotNull(response);
    }

    @Test(expected = SolrServerException.class)
    public void query_Expect_SolrServerException() throws SolrServerException {
        when(solrServer.query(solrQuery)).thenThrow(new SolrServerException("test_message"));

        documentIndexServiceOps.query(solrServer, solrQuery);
    }

    @After
    public void verifyMethodCall() throws SolrServerException {
        verify(solrServer, times(1)).query(solrQuery);
    }
}