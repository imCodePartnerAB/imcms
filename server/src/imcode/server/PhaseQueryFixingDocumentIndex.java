package imcode.server;

import imcode.server.document.LifeCyclePhase;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentIndexWrapper;
import imcode.server.document.index.DocumentQuery;
import imcode.server.document.index.IndexException;
import imcode.server.document.index.SimpleDocumentQuery;
import imcode.server.user.UserDomainObject;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.Date;
import java.util.List;

public class PhaseQueryFixingDocumentIndex extends DocumentIndexWrapper {

    public PhaseQueryFixingDocumentIndex(DocumentIndex index) {
        super(index);
    }

    public List search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return super.search(fixQuery(query), searchingUser);
    }

    DocumentQuery fixQuery(DocumentQuery documentQuery) {
        Query query = documentQuery.getQuery();
        return new SimpleDocumentQuery(fixQuery(query), documentQuery.getSort(), documentQuery.isLogged());
    }

    Query fixQuery(Query query) {
        if (query instanceof BooleanQuery ) {
            BooleanQuery booleanQuery = (BooleanQuery) query ;
            BooleanClause[] clauses = booleanQuery.getClauses();
            for ( BooleanClause clause : clauses ) {
                clause.query = fixQuery(clause.query);
            }
        } else if ( query instanceof TermQuery ) {
            TermQuery termQuery = (TermQuery) query;
            Term term = termQuery.getTerm();
            if ( DocumentIndex.FIELD__PHASE.equals(term.field())) {
                LifeCyclePhase[] allPhases = LifeCyclePhase.ALL ;
                Date now = new Date();
                for ( LifeCyclePhase phase : allPhases ) {
                    if ( phase.toString().equals(term.text()) ) {
                        return phase.asQuery(now);
                    }
                }
            }
        }
        return query ;
    }
}
