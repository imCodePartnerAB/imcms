package imcode.server.document.index;

import com.imcode.imcms.domain.dto.DocumentPageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;

public interface ResolvingQueryIndex extends DocumentIndex {

    IndexSearchResult<DocumentStoredFields> search(SearchQueryDTO searchQuery, boolean limitSearch) throws IndexException;

    IndexSearchResult<DocumentStoredFields> search(String searchQuery, boolean limitSearch) throws IndexException;

    IndexSearchResult<DocumentStoredFields> search(String searchQuery, DocumentPageRequestDTO page, boolean limitSearch) throws IndexException;
}
