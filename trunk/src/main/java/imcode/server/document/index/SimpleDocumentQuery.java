package imcode.server.document.index;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

public class SimpleDocumentQuery implements DocumentQuery {

    private final Query query;
    private final Sort sort;
    private final boolean logged;

    public SimpleDocumentQuery(Query query, Sort sort, boolean logged) {
        this.query = query;
        this.sort = sort;
        this.logged = logged;
    }

    public SimpleDocumentQuery(Query query) {
        this(query,null,false);
    }

    public Query getQuery() {
        return query;
    }

    public Sort getSort() {
        return sort;
    }

    public boolean isLogged() {
        return logged;
    }
}
