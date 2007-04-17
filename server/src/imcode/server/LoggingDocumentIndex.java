package imcode.server;

import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentIndexWrapper;
import imcode.server.document.index.DocumentQuery;
import imcode.server.document.index.IndexException;
import imcode.server.user.UserDomainObject;
import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;

import java.util.List;
import java.util.Date;
import java.util.Collection;
import java.util.HashSet;
import java.sql.Timestamp;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;

public class LoggingDocumentIndex extends DocumentIndexWrapper {

    private final Database database;

    public LoggingDocumentIndex(Database database, DocumentIndex documentIndex) {
        super(documentIndex);
        this.database = database;
    }

    public List search(DocumentQuery documentQuery, UserDomainObject searchingUser) throws IndexException {
        Query query = documentQuery.getQuery();
        logTerms(getTerms(query));
        return super.search(documentQuery, searchingUser);
    }

    private Collection<String> getTerms(Query query) {
        Collection<String> terms = new HashSet<String>();
        getTerms(query, terms);
        return terms;
    }

    private void logTerms(Collection<String> terms) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        for ( String term : terms ) {
            database.execute(new InsertIntoTableDatabaseCommand("document_search_log", new Object[][] {
                    { "datetime", timestamp },
                    { "term", term }
            })) ;
        }
    }

    private void getTerms(Query query, Collection<String> terms) {
        if ( query instanceof BooleanQuery ) {
            BooleanQuery booleanQuery = (BooleanQuery) query ;
            BooleanClause[] clauses = booleanQuery.getClauses();
            for ( BooleanClause clause : clauses ) {
                if (!clause.prohibited) {
                    getTerms(clause.query, terms);
                }
            }
        } else if ( query instanceof TermQuery ) {
            TermQuery termQuery = (TermQuery) query;
            Term term = termQuery.getTerm();
            terms.add(term.text());
        }
    }
}
