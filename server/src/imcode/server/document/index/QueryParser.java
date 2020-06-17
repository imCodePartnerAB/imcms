package imcode.server.document.index;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

import java.io.Serializable;

public interface QueryParser extends Serializable {

    Query parse(String queryString) throws ParseException;

}