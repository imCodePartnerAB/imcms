package imcode.server.document.index;

import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

import java.util.Arrays;

import static org.apache.lucene.search.BooleanClause.Occur;

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

        final Occur[] flags = new Occur[fields.length];
        Arrays.fill(flags, Occur.SHOULD);

        return MultiFieldQueryParser.parse(queryString, fields, flags, new AnalyzerImpl());
    }
}
