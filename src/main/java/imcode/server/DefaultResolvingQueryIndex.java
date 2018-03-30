package imcode.server;

import com.imcode.imcms.domain.component.DocumentSearchQueryConverter;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import imcode.server.document.index.*;
import imcode.server.user.UserDomainObject;
import org.apache.solr.client.solrj.SolrQuery;

public class DefaultResolvingQueryIndex extends DocumentIndexWrapper implements ResolvingQueryIndex {

    private final DocumentSearchQueryConverter documentSearchQueryConverter;

    public DefaultResolvingQueryIndex(DocumentIndex index, DocumentSearchQueryConverter documentSearchQueryConverter) {
        super(index);

        this.documentSearchQueryConverter = documentSearchQueryConverter;
    }

    @Override
    public IndexSearchResult search(SearchQueryDTO searchQuery, UserDomainObject searchingUser) throws IndexException {
        return super.search(fixQuery(searchQuery), searchingUser);
    }

    private SolrQuery fixQuery(SearchQueryDTO searchQuery) {
        return documentSearchQueryConverter.convertToSolrQuery(searchQuery);
    }
}
