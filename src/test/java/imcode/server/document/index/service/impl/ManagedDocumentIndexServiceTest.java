package imcode.server.document.index.service.impl;

import imcode.server.document.index.IndexException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ManagedDocumentIndexServiceTest {

    @Mock
    private DocumentIndexServiceOps serviceOps;

    @Mock
    private SolrClient solrServer;

    @Mock
    private QueryResponse queryResponse;

    @InjectMocks
    private ManagedDocumentIndexService managedDocumentIndexService;

    private SolrQuery solrQuery;

    @Before
    public void setUp() {
        solrQuery = new SolrQuery();
    }

    @Test
    public void query_When_SolrServerIsCorrect_Expect_QueryResponseIsReturned() throws SolrServerException, IOException {
        when(serviceOps.query(solrServer, solrQuery)).thenReturn(queryResponse);

        final QueryResponse actualResponse = managedDocumentIndexService.query(solrQuery);

        assertNotNull(actualResponse);
    }

    @Test(expected = IndexException.class)
    public void query_When_SolrServerExceptionIsThrown_Expect_IndexException() throws SolrServerException, IOException {
        when(serviceOps.query(solrServer, solrQuery)).thenThrow(new SolrServerException("test_message"));

        managedDocumentIndexService.query(solrQuery);
    }

    @After
    public void verifyMethodExecution() throws SolrServerException, IOException {
        verify(serviceOps, times(1)).query(solrServer, solrQuery);
    }
}