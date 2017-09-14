package imcode.server;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.LifeCyclePhase;
import imcode.server.document.index.*;
import imcode.server.user.UserDomainObject;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.Date;
import java.util.List;

/**
 * Rewrites queries containing {@link DocumentIndex#FIELD__PHASE} fields with
 * combination of {@link DocumentIndex#FIELD__STATUS} and date ranges.
 *
 * @see imcode.server.document.LifeCyclePhase#asQuery(java.util.Date)
 */
public class PhaseQueryFixingDocumentIndex extends DocumentIndexWrapper {

    public PhaseQueryFixingDocumentIndex(DocumentIndex index) {
        super(index);
    }


    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return super.search(fixQuery(query), searchingUser);
    }

    DocumentQuery fixQuery(DocumentQuery documentQuery) {
        Query query = documentQuery.getQuery();
        return new SimpleDocumentQuery(fixQuery(query), documentQuery.getSort(), documentQuery.isLogged());
    }

    Query fixQuery(Query query) {
        if (query instanceof BooleanQuery) {
            BooleanQuery booleanQuery = (BooleanQuery) query;
            BooleanClause[] clauses = booleanQuery.getClauses();
            for (BooleanClause clause : clauses) {
                clause.setQuery(fixQuery(clause.getQuery()));
            }
        } else if (query instanceof TermQuery) {
            TermQuery termQuery = (TermQuery) query;
            Term term = termQuery.getTerm();
            if (DocumentIndex.FIELD__PHASE.equals(term.field())) {
                LifeCyclePhase[] allPhases = LifeCyclePhase.ALL;
                Date now = new Date();
                for (LifeCyclePhase phase : allPhases) {
                    if (phase.toString().equals(term.text())) {
                        return phase.asQuery(now);
                    }
                }
            }
        }

        return query;
    }
}
