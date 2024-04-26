package imcode.server;

import com.imcode.imcms.domain.component.DocumentSearchQueryConverter;
import com.imcode.imcms.domain.dto.DocumentPageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.IndexSearchResult;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
    public void search_When_SearchQueryIsSearchQueryDTO_Expect_IndexSearchResultIsNotNull() {
        final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
        final SolrQuery solrQuery = new SolrQuery();

        when(documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, true)).thenReturn(solrQuery);
        when(documentSearchQueryConverter.convertToSolrQuery(searchQueryDTO, false)).thenReturn(solrQuery);
        when(documentIndex.search(solrQuery)).thenReturn(indexSearchResult);

        IndexSearchResult indexSearchResult = defaultResolvingQueryIndex.search(searchQueryDTO, true);
        assertNotNull(indexSearchResult);
        indexSearchResult = defaultResolvingQueryIndex.search(searchQueryDTO, false);
        assertNotNull(indexSearchResult);
    }

    @Test
    public void search_When_SearchQueryIsString_Expect_IndexSearchResultIsNotNull2() {
        final SolrQuery solrQuery = new SolrQuery();
        final String strSolrQuery = new String();

        when(documentSearchQueryConverter.convertToSolrQuery(strSolrQuery, true)).thenReturn(solrQuery);
        when(documentSearchQueryConverter.convertToSolrQuery(strSolrQuery, false)).thenReturn(solrQuery);
        when(documentIndex.search(solrQuery)).thenReturn(indexSearchResult);

        IndexSearchResult indexSearchResult = defaultResolvingQueryIndex.search(strSolrQuery, true);
        assertNotNull(indexSearchResult);
        indexSearchResult = defaultResolvingQueryIndex.search(strSolrQuery, false);
        assertNotNull(indexSearchResult);
    }

    @Test
    public void search_When_SearchQueryIsString_And_PageRequestIsNotNull_Expect_IndexSearchResultIsNotNull3() {
        final SolrQuery solrQuery = new SolrQuery();
        final String strSolrQuery = new String();
        final DocumentPageRequestDTO page = new DocumentPageRequestDTO();

        when(documentSearchQueryConverter.convertToSolrQuery(strSolrQuery, page,true)).thenReturn(solrQuery);
        when(documentSearchQueryConverter.convertToSolrQuery(strSolrQuery, page, false)).thenReturn(solrQuery);
        when(documentIndex.search(solrQuery)).thenReturn(indexSearchResult);

        IndexSearchResult indexSearchResult = defaultResolvingQueryIndex.search(strSolrQuery, page, true);
        assertNotNull(indexSearchResult);
        indexSearchResult = defaultResolvingQueryIndex.search(strSolrQuery, page, false);
        assertNotNull(indexSearchResult);
    }
}
