package imcode.server.document.index;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryParser.ParseException;

public interface QueryParser {

    Query parse(String queryString) throws ParseException ;

}