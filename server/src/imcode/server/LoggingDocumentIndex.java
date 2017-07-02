package imcode.server;

import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentIndexWrapper;
import imcode.server.document.index.DocumentQuery;
import imcode.server.document.index.IndexException;
import imcode.server.user.UserDomainObject;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.util.*;

public class LoggingDocumentIndex extends DocumentIndexWrapper {
    private static final Logger log = Logger.getLogger(LoggingDocumentIndex.class);

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
        try {
            final java.sql.Date correctDateType = new java.sql.Date(new Date().getTime());
            for ( String term : terms ) {
                database.execute(new InsertIntoTableDatabaseCommand("document_search_log", new Object[][] {
                        { "datetime", correctDateType },
                        { "term", term }
                })) ;
            }
        } catch (Exception e) {
            log.error("Error while logging search terms:", e);
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
            addTerm(terms, termQuery.getTerm());
        } else if ( query instanceof MultiTermQuery ) {
            MultiTermQuery multiTermQuery = (MultiTermQuery) query;
            addTerm(terms, multiTermQuery.getTerm());
        } else if ( query instanceof PrefixQuery ) {
            PrefixQuery prefixQuery = (PrefixQuery) query;
            addTerm(terms, prefixQuery.getPrefix());
        }
    }

    private final static Set LOGGED_FIELDS = new HashSet(Arrays.asList(new String[] {
            DocumentIndex.FIELD__META_HEADLINE,
            DocumentIndex.FIELD__META_TEXT,
            DocumentIndex.FIELD__TEXT,
            DocumentIndex.FIELD__ALIAS,
            DocumentIndex.FIELD__KEYWORD,
    } )); 
    
    private void addTerm(Collection<String> terms, Term term) {
        if (LOGGED_FIELDS.contains(term.field())) {
            terms.add(term.text());
        }
    }
}
