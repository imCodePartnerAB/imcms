package imcode.server.document.index;

import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

import java.util.Arrays;

@Deprecated
public class DefaultQueryParser implements QueryParser {

    public Query parse(String queryString) throws ParseException {
        String[] fields = new String[]{
                DocumentIndex.FIELD__META_ID,
                DocumentIndex.FIELD__META_HEADLINE,
                DocumentIndex.FIELD__META_TEXT,
                DocumentIndex.FIELD__TEXT,
                DocumentIndex.FIELD__KEYWORD,
                DocumentIndex.FIELD__META_ALIAS
        };

        BooleanClause.Occur[] flags = new BooleanClause.Occur[fields.length];
        Arrays.fill(flags, BooleanClause.Occur.SHOULD);

        return MultiFieldQueryParser.parse(queryString, fields, flags, new AnalyzerImpl());
    }
}
