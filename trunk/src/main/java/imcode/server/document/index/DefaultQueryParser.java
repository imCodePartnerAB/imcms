package imcode.server.document.index;

import java.util.Arrays;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

public class DefaultQueryParser implements QueryParser {

    public Query parse(String queryString) throws ParseException {
        String[] fields = new String[]{
            DocumentIndex.FIELD__META_ID,
            DocumentIndex.FIELD__META_HEADLINE,
            DocumentIndex.FIELD__META_TEXT,
            DocumentIndex.FIELD__TEXT,
            DocumentIndex.FIELD__KEYWORD,
            DocumentIndex.FIELD__ALIAS
        };

        BooleanClause.Occur[] flags = new BooleanClause.Occur[fields.length];
        Arrays.fill(flags, BooleanClause.Occur.SHOULD);

        return MultiFieldQueryParser.parse(queryString, fields, flags, new AnalyzerImpl());
    }
}
