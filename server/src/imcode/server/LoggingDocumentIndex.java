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
import org.apache.lucene.search.*;

import java.sql.Timestamp;
import java.util.*;

import static org.apache.lucene.search.BooleanClause.Occur;

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
            final BooleanQuery booleanQuery = (BooleanQuery) query;

            final List<BooleanClause> booleanClauses = new ArrayList<>();
            booleanQuery.iterator().forEachRemaining(booleanClauses::add);
            final BooleanClause[] clauses = booleanClauses.toArray(new BooleanClause[0]);

            for (BooleanClause clause : clauses) {
                if (clause.getOccur() != Occur.MUST_NOT) {
                    getTerms(clause.getQuery(), terms);
                }
            }
        } else if (query instanceof TermQuery) {
            final TermQuery termQuery = (TermQuery) query;
            addTerm(terms, termQuery.getTerm());
        } else if (query instanceof PhraseQuery) {
            final PhraseQuery phraseQuery = (PhraseQuery) query;
            for (Term term : phraseQuery.getTerms())
                addTerm(terms, term);
        }
    }

    private void addTerm(Collection<String> terms, Term term) {
        if (LOGGED_FIELDS.contains(term.field())) {
            terms.add(term.text());
        }
    }
}
