package imcode.server;

import imcode.server.document.index.*;
import imcode.server.user.UserDomainObject;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.imcode.db.Database;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;

public class LoggingDocumentIndex extends DocumentIndexWrapper {

    private final Database database;

    public LoggingDocumentIndex(Database database, DocumentIndexService documentIndex) {
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
                if (clause.getOccur() != BooleanClause.Occur.MUST_NOT) {
                    getTerms(clause.getQuery(), terms);
                }
            }
        } else if ( query instanceof TermQuery ) {
            TermQuery termQuery = (TermQuery) query;
            addTerm(terms, termQuery.getTerm());
        } else if ( query instanceof MultiTermQuery ) {
            MultiTermQuery multiTermQuery = (MultiTermQuery) query;
            //todo: check - method is removed - how to WA
            //addTerm(terms, multiTermQuery.getTerm());
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
