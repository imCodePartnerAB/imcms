package imcode.server;

import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import com.imcode.imcms.api.SearchResult;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentIndexWrapper;
import imcode.server.document.index.DocumentQuery;
import imcode.server.document.index.IndexException;
import imcode.server.user.UserDomainObject;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoggingDocumentIndex extends DocumentIndexWrapper {

    private final static Set<String> LOGGED_FIELDS = new HashSet<>(Arrays.asList(
            DocumentIndex.FIELD__META_HEADLINE,
            DocumentIndex.FIELD__META_TEXT,
            DocumentIndex.FIELD__TEXT,
            DocumentIndex.FIELD__ALIAS,
            DocumentIndex.FIELD__KEYWORD
    ));
    private final Database database;

    public LoggingDocumentIndex(Database database, DocumentIndex documentIndex) {
        super(documentIndex);
        this.database = database;
    }

    public List<DocumentDomainObject> search(DocumentQuery documentQuery, UserDomainObject searchingUser) throws IndexException {
        Query query = documentQuery.getQuery();
        logTerms(getTerms(query));
        return super.search(documentQuery, searchingUser);
    }

    @Override
    public SearchResult<DocumentDomainObject> search(DocumentQuery documentQuery, UserDomainObject searchingUser, int startPosition,
                                                     int maxResults) throws IndexException {

        Query query = documentQuery.getQuery();
        logTerms(getTerms(query));
        return super.search(documentQuery, searchingUser, startPosition, maxResults);
    }

    private Collection<String> getTerms(Query query) {
        Collection<String> terms = new HashSet<>();
        getTerms(query, terms);
        return terms;
    }

    private void logTerms(Collection<String> terms) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        for (String term : terms) {
            database.execute(new InsertIntoTableDatabaseCommand("document_search_log", new Object[][]{
                    {"datetime", timestamp},
                    {"term", term}
            }));
        }
    }

    private void getTerms(Query query, Collection<String> terms) {
        if (query instanceof BooleanQuery) {
            BooleanQuery booleanQuery = (BooleanQuery) query;
            BooleanClause[] clauses = booleanQuery.getClauses();
            for (BooleanClause clause : clauses) {
                if (!clause.prohibited) {
                    getTerms(clause.query, terms);
                }
            }
        } else if (query instanceof TermQuery) {
            TermQuery termQuery = (TermQuery) query;
            addTerm(terms, termQuery.getTerm());
        } else if (query instanceof MultiTermQuery) {
            MultiTermQuery multiTermQuery = (MultiTermQuery) query;
            addTerm(terms, multiTermQuery.getTerm());
        } else if (query instanceof PrefixQuery) {
            PrefixQuery prefixQuery = (PrefixQuery) query;
            addTerm(terms, prefixQuery.getPrefix());
        }
    }

    private void addTerm(Collection<String> terms, Term term) {
        if (LOGGED_FIELDS.contains(term.field())) {
            terms.add(term.text());
        }
    }
}
