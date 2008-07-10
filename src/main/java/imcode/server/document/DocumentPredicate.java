package imcode.server.document;

import org.apache.commons.collections.Predicate;

public abstract class DocumentPredicate implements Predicate {

    public boolean evaluate( Object object ) {
        return evaluateDocument((DocumentDomainObject)object) ;
    }

    public abstract boolean evaluateDocument( DocumentDomainObject document ) ;
}
