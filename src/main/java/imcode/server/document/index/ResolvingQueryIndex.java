package imcode.server.document.index;

import com.imcode.imcms.domain.dto.PageRequestDTO;
import com.imcode.imcms.domain.dto.SearchQueryDTO;

public interface ResolvingQueryIndex extends DocumentIndex {

    IndexSearchResult search(SearchQueryDTO searchQuery, boolean limitSearch) throws IndexException;

    IndexSearchResult search(String searchQuery, boolean limitSearch) throws IndexException;

    IndexSearchResult search(String searchQuery, PageRequestDTO page, boolean limitSearch) throws IndexException;
}
