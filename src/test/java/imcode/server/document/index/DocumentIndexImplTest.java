package imcode.server.document.index;

import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentIndexImplTest {

    @Mock
    private DocumentIndexService service;

    @Mock
    private QueryResponse queryResponse;

    @Mock
    private SolrDocumentList solrDocumentList;

    @InjectMocks
    private DocumentIndexImpl documentIndex;

    @Test
    public void search_When_SolrQueryAndUserAreSet_Expect_CorrectIndexSearchResult() {
        final SolrQuery solrQuery = new SolrQuery();
        final int solrDocumentListSize = 10;

        when(service.query(solrQuery)).thenReturn(queryResponse);
        when(queryResponse.getResults()).thenReturn(solrDocumentList);
        when(solrDocumentList.size()).thenReturn(solrDocumentListSize);

        final IndexSearchResult indexSearchResult = documentIndex.search(solrQuery, new UserDomainObject(1));

        assertNotNull(indexSearchResult);
        assertThat(indexSearchResult.size(), is(solrDocumentListSize));

        verify(service, times(1)).query(solrQuery);
        verify(queryResponse, times(1)).getResults();
        verify(solrDocumentList, times(1)).size();
    }
}