package imcode.server;

import com.imcode.imcms.domain.component.DocumentSearchQueryConverter;
import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentIndexWrapper;
import imcode.server.document.index.IndexException;
import imcode.server.document.index.IndexSearchResult;
import imcode.server.document.index.ResolvingQueryIndex;
import imcode.server.user.UserDomainObject;

public class DefaultResolvingQueryIndex extends DocumentIndexWrapper implements ResolvingQueryIndex {

    private final DocumentSearchQueryConverter documentSearchQueryConverter;

    public DefaultResolvingQueryIndex(DocumentIndex index, DocumentSearchQueryConverter documentSearchQueryConverter) {
        super(index);

        this.documentSearchQueryConverter = documentSearchQueryConverter;
    }

    @Override
    public IndexSearchResult search(SearchQueryDTO searchQuery, UserDomainObject searchingUser) throws IndexException {
        return super.search(documentSearchQueryConverter.convertToSolrQuery(searchQuery), searchingUser);
    }

    @Override
    public IndexSearchResult search(String searchQuery, UserDomainObject searchingUser) throws IndexException {
        return super.search(documentSearchQueryConverter.convertToSolrQuery(searchQuery), searchingUser);
    }

    @Override
    public IndexSearchResult search(String searchQuery, PageRequestDTO page, UserDomainObject searchingUser) throws IndexException {
        return super.search(documentSearchQueryConverter.convertToSolrQuery(searchQuery, page), searchingUser);
    }
}
