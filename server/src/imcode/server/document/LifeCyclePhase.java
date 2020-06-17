package imcode.server.document;

import com.imcode.imcms.api.Document;
import imcode.server.document.index.DocumentIndex;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

import java.util.Date;

import static org.apache.lucene.search.BooleanClause.Occur;

public abstract class LifeCyclePhase {

    public static final LifeCyclePhase NEW = new LifeCyclePhase("new") {
        public Query asQuery(Date time) {
            return getStatusQuery(Document.PublicationStatus.NEW);
        }
    };
    public static final LifeCyclePhase DISAPPROVED = new LifeCyclePhase("disapproved") {
        public Query asQuery(Date time) {
            return getStatusQuery(Document.PublicationStatus.DISAPPROVED);
        }
    };
    public static final LifeCyclePhase UNPUBLISHED = new LifeCyclePhase("unpublished") {
        public Query asQuery(Date time) {
            return add(getApprovedBooleanQuery(), getPublicationEndRangeQuery(time)).build();
        }
    };
    public static final LifeCyclePhase APPROVED = new LifeCyclePhase("approved") {
        public Query asQuery(Date time) {
            return subtract(getApprovedNonUnpublishedQuery(time), getPublicationStartRangeQuery(time)).build();
        }
    };
    public static final LifeCyclePhase ARCHIVED = new LifeCyclePhase("archived") {
        public Query asQuery(Date time) {
            return add(getPublishedQuery(time), getArchivedRangeQuery(time)).build();
        }
    };
    public static final LifeCyclePhase PUBLISHED = new LifeCyclePhase("published") {
        public Query asQuery(Date time) {
            return subtract(getPublishedQuery(time), getArchivedRangeQuery(time)).build();
        }
    };

    public static final LifeCyclePhase[] ALL = new LifeCyclePhase[]{
            NEW, APPROVED, DISAPPROVED,
            PUBLISHED, ARCHIVED, UNPUBLISHED
    };
    private final String name;

    private LifeCyclePhase(String name) {
        this.name = name;
    }

    private static BooleanQuery.Builder add(BooleanQuery.Builder booleanQueryBuilder, Query otherQuery) {
        return booleanQueryBuilder.add(otherQuery, Occur.MUST);
    }

    private static BooleanQuery.Builder subtract(BooleanQuery.Builder booleanQueryBuilder, Query subtrahend) {
        return booleanQueryBuilder.add(subtrahend, Occur.MUST_NOT);
    }

    private static TermRangeQuery getPublicationStartRangeQuery(Date now) {
        return getDateRangeQuery(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, now);
    }

    private static TermRangeQuery getPublicationEndRangeQuery(Date now) {
        return getDateRangeQuery(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, now);
    }

    private static TermRangeQuery getArchivedRangeQuery(Date now) {
        return getDateRangeQuery(DocumentIndex.FIELD__ARCHIVED_DATETIME, now);
    }

    /**
     * @param field range field name.
     * @param now   range upper bound.
     * @return a TermRangeQuery in an interval from minimum to now.
     */
    private static TermRangeQuery getDateRangeQuery(String field, Date now) {
        return new TermRangeQuery(field,
                new BytesRef("0"),
                new BytesRef(DateTools.dateToString(now, DateTools.Resolution.MINUTE)),
                true, true);
    }

    private static BooleanQuery.Builder getPublishedQuery(Date now) {
        return add(getApprovedNonUnpublishedQuery(now), getPublicationStartRangeQuery(now));
    }

    private static BooleanQuery.Builder getApprovedNonUnpublishedQuery(Date now) {
        return subtract(getApprovedBooleanQuery(), getPublicationEndRangeQuery(now));
    }

    private static BooleanQuery.Builder getApprovedBooleanQuery() {
        return add(new BooleanQuery.Builder(), getStatusQuery(Document.PublicationStatus.APPROVED));
    }

    private static TermQuery getStatusQuery(Document.PublicationStatus publicationStatus) {
        return new TermQuery(new Term(DocumentIndex.FIELD__STATUS, publicationStatus.toString()));
    }

    public String toString() {
        return name;
    }

    public abstract Query asQuery(Date time);

}
