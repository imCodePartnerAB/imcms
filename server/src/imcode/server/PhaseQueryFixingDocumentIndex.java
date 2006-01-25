package imcode.server;

import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentIndexWrapper;
import imcode.server.document.index.IndexException;
import imcode.server.document.LifeCyclePhase;
import imcode.server.user.UserDomainObject;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.index.Term;

import java.util.Date;
import java.util.List;

public class PhaseQueryFixingDocumentIndex extends DocumentIndexWrapper {

    public PhaseQueryFixingDocumentIndex(DocumentIndex index) {
        super(index);
    }

    public List search(Query query, Sort sort, UserDomainObject searchingUser) throws IndexException {
        return super.search(fixQuery(query), sort, searchingUser);
    }

    Query fixQuery(Query query) {
        if (query instanceof BooleanQuery ) {
            BooleanQuery booleanQuery = (BooleanQuery) query ;
            BooleanClause[] clauses = booleanQuery.getClauses();
            for ( int i = 0; i < clauses.length; i++ ) {
                clauses[i].query = fixQuery(clauses[i].query);
            }
        } else if ( query instanceof TermQuery ) {
            TermQuery termQuery = (TermQuery) query;
            Term term = termQuery.getTerm();
            if (DocumentIndex.FIELD__PHASE.equals(term.field())) {
                LifeCyclePhase[] allPhases = LifeCyclePhase.ALL ;
                Date now = new Date();
                for ( int i = 0; i < allPhases.length; i++ ) {
                    LifeCyclePhase phase = allPhases[i];
                    if ( phase.toString().equals(term.text())) {
                        return phase.asQuery(now) ;
                    }
                }
            }
        }
        return query ;
    }
}
