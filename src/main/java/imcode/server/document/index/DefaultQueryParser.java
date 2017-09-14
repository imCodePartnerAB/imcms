package imcode.server.document.index;

import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

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
                DocumentIndex.FIELD__ALIAS
        };

        BooleanClause.Occur[] flags = new BooleanClause.Occur[fields.length];
        Arrays.fill(flags, BooleanClause.Occur.SHOULD);

        return MultiFieldQueryParser.parse(Version.LUCENE_43, queryString, fields, flags, new AnalyzerImpl());
    }
}
