package imcode.server.document.index;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import com.imcode.imcms.api.DocumentVersionTag;

public interface DocumentQuery {

    Query getQuery();
    Sort getSort();
    boolean isLogged();

}