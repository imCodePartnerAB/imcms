package imcode.server.document.index;

import java.io.Serializable;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

public interface QueryParser extends Serializable {
    Query parse(String queryString) throws ParseException;
}