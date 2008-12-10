package imcode.server.document;

import com.imcode.util.ChainableReversibleNullComparator;
import org.apache.commons.lang.NullArgumentException;

public abstract class DocumentComparator extends ChainableReversibleNullComparator {

    private final String name ;

    protected DocumentComparator( String name ) {
        this.name = name;
    }

    public String toString() {
        return name ;
    }

    public int compare( Object o1, Object o2 ) {
        if (null == o1 || null == o2) {
            throw new NullArgumentException("o1 and o2") ;
        }
        final DocumentDomainObject d1 = (DocumentDomainObject)o1;
        final DocumentDomainObject d2 = (DocumentDomainObject)o2;
        try {
            return compareDocuments( d1, d2 );
        } catch ( NullPointerException npe ) {
            NullPointerException nullPointerException = new NullPointerException("Tried sorting on null fields! You need to call .nullsFirst() or .nullsLast() on your Comparator.");
            nullPointerException.initCause(npe) ;
            throw nullPointerException ;
        }
    }

    protected abstract int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) ;

    public final static DocumentComparator ID = new DocumentComparator("ID") {
        protected int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) {
            return d1.getId() - d2.getId();
        }
    };

    public final static DocumentComparator HEADLINE = new DocumentComparator("HEADLINE") {
        protected int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) {
            return d1.getHeadline().compareToIgnoreCase( d2.getHeadline() ) ;
        }
    };

    public final static DocumentComparator MODIFIED_DATETIME = new DocumentComparator("MODIFIED_DATETIME") {
        protected int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) {
            return d1.getModifiedDatetime().compareTo( d2.getModifiedDatetime() ) ;
        }
    };

    public final static DocumentComparator ARCHIVED_DATETIME = new DocumentComparator("ARCHIVED_DATETIME") {
        protected int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) {
            return d1.getArchivedDatetime().compareTo( d2.getArchivedDatetime() ) ;
        }
    };

    public final static DocumentComparator PUBLICATION_START_DATETIME = new DocumentComparator("PUBLICATION_START_DATETIME") {
        protected int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) {
            return d1.getPublicationStartDatetime().compareTo( d2.getPublicationStartDatetime() ) ;
        }
    };

    public final static DocumentComparator PUBLICATION_END_DATETIME = new DocumentComparator("PUBLICATION_END_DATETIME") {
        protected int compareDocuments( DocumentDomainObject d1, DocumentDomainObject d2 ) {
            return d1.getPublicationEndDatetime().compareTo( d2.getPublicationEndDatetime() ) ;
        }
    };

}
