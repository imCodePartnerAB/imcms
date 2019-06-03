package imcode.server.document.index;

import com.imcode.imcms.domain.dto.SearchQueryDTO;
import imcode.server.user.UserDomainObject;

public interface ResolvingQueryIndex extends DocumentIndex {

    IndexSearchResult search(SearchQueryDTO searchQuery, UserDomainObject searchingUser) throws IndexException;

    IndexSearchResult search(String searchQuery, UserDomainObject searchingUser) throws IndexException;
}
