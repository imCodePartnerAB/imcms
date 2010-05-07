package imcode.server.document.index;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.MultiFieldQueryParser;

public class DefaultQueryParser implements QueryParser {

    public Query parse( String queryString ) throws ParseException {
        return MultiFieldQueryParser.parse( queryString,
                                            new String[]{
                                                DocumentIndex.FIELD__META_ID,
                                                DocumentIndex.FIELD__META_HEADLINE,
                                                DocumentIndex.FIELD__META_TEXT,
                                                DocumentIndex.FIELD__TEXT,
                                                DocumentIndex.FIELD__KEYWORD,
                                                DocumentIndex.FIELD__ALIAS
                                            },
                                            new AnalyzerImpl() );
    }
}
