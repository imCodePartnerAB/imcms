package imcode.server;

import com.imcode.imcms.api.SearchResult;
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
import java.util.function.Predicate;

public class PhaseQueryFixingDocumentIndex extends DocumentIndexWrapper {

    public PhaseQueryFixingDocumentIndex(DocumentIndex index) {
        super(index);
    }

    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return super.search(fixQuery(query), searchingUser);
    }

    public SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults) throws IndexException {
        return super.search(fixQuery(query), searchingUser, startPosition, maxResults);
    }

    @Override
    public SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults, Predicate<DocumentDomainObject> filterPredicate) throws IndexException {
        return super.search(fixQuery(query), searchingUser, startPosition, maxResults, filterPredicate);
    }

    private DocumentQuery fixQuery(DocumentQuery documentQuery) {
        Query query = documentQuery.getQuery();
        return new SimpleDocumentQuery(fixQuery(query), documentQuery.getSort(), documentQuery.isLogged());
    }

    Query fixQuery(Query query) {
        if (query instanceof BooleanQuery) {
            BooleanQuery booleanQuery = (BooleanQuery) query;

            final BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
            final List<BooleanClause> booleanClauses = booleanQuery.clauses();

            for (final BooleanClause booleanClause : booleanClauses) {
                final BooleanClause fixedBooleanClause = new BooleanClause(
                        fixQuery(booleanClause.getQuery()), booleanClause.getOccur()
                );

                queryBuilder.add(fixedBooleanClause);
            }

            return queryBuilder.build();

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
