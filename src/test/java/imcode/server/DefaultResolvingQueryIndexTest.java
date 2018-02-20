package imcode.server;

import com.imcode.imcms.domain.component.DocumentSearchQueryConverter;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.IndexSearchResult;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultResolvingQueryIndexTest {

    @Mock
    private DocumentSearchQueryConverter documentSearchQueryConverter;

    @Mock
    private DocumentIndex documentIndex;

    @Mock
    private IndexSearchResult indexSearchResult;

    @InjectMocks
    private DefaultResolvingQueryIndex defaultResolvingQueryIndex;

    @Test
    public void search_When_SearchQueryAndUserAreSet_Expect_IndexSearchResultIsNotNull() {

        final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
        final UserDomainObject user = new UserDomainObject(1);
        final SolrQuery solrQuery = new SolrQuery();

        when(documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO)).thenReturn(solrQuery);
        when(documentIndex.search(solrQuery, user)).thenReturn(indexSearchResult);

        final IndexSearchResult indexSearchResult = defaultResolvingQueryIndex.search(searchQueryDTO, user);

        assertNotNull(indexSearchResult);
    }
}