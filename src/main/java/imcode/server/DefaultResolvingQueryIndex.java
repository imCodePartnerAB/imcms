package imcode.server;

import com.imcode.imcms.domain.component.DocumentSearchQueryConverter;
import com.imcode.imcms.domain.dto.DocumentPageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;
import imcode.server.document.index.*;

public class DefaultResolvingQueryIndex extends DocumentIndexWrapper implements ResolvingQueryIndex {

    private final DocumentSearchQueryConverter documentSearchQueryConverter;

    public DefaultResolvingQueryIndex(DocumentIndex index, DocumentSearchQueryConverter documentSearchQueryConverter) {
        super(index);

        this.documentSearchQueryConverter = documentSearchQueryConverter;
    }

    @Override
    public IndexSearchResult<DocumentStoredFields> search(SearchQueryDTO searchQuery, boolean limitSearch) throws IndexException {
        return super.search(documentSearchQueryConverter.convertToSolrQuery(searchQuery, limitSearch));
    }

    @Override
    public IndexSearchResult<DocumentStoredFields> search(String searchQuery, boolean limitSearch) throws IndexException {
        return super.search(documentSearchQueryConverter.convertToSolrQuery(searchQuery, limitSearch));
    }

    @Override
    public IndexSearchResult<DocumentStoredFields> search(String searchQuery, DocumentPageRequestDTO page, boolean limitSearch) throws IndexException {
        return super.search(documentSearchQueryConverter.convertToSolrQuery(searchQuery, page, limitSearch));
    }
}
