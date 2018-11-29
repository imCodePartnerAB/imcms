package imcode.server.document.index.service.impl;

import imcode.server.document.index.IndexException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class ManagedDocumentIndexServiceTest {

    @Mock
    private DocumentIndexServiceOps serviceOps;

    @Mock
    private SolrClient solrClient;

    @Mock
    private QueryResponse queryResponse;

    @InjectMocks
    private ManagedDocumentIndexService managedDocumentIndexService;

    private SolrQuery solrQuery;

    @BeforeEach
    void setUp() {
        solrQuery = new SolrQuery();
    }

    @Test
    void query_When_SolrServerIsCorrect_Expect_QueryResponseIsReturned() throws SolrServerException, IOException {
        when(serviceOps.query(solrClient, solrQuery)).thenReturn(queryResponse);

        final QueryResponse actualResponse = managedDocumentIndexService.query(solrQuery);

        assertNotNull(actualResponse);
    }

    @Test()
    void query_When_SolrServerExceptionIsThrown_Expect_IndexException() throws SolrServerException, IOException {
        when(serviceOps.query(solrClient, solrQuery)).thenThrow(new SolrServerException("test_message"));

        assertThrows(IndexException.class, () -> managedDocumentIndexService.query(solrQuery));
    }

    @AfterEach
    void verifyMethodExecution() throws SolrServerException, IOException {
        verify(serviceOps, times(1)).query(solrClient, solrQuery);
    }
}