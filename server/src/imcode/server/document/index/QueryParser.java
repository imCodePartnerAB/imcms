package imcode.server.document.index;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

import java.io.Serializable;

public interface QueryParser extends Serializable {

    Query parse(String queryString) throws ParseException;

}