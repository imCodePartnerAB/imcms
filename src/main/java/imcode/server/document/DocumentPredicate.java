package imcode.server.document;

import org.apache.commons.collections4.Predicate;

public abstract class DocumentPredicate implements Predicate {

    public boolean evaluate(DocumentDomainObject object) {
        return evaluateDocument(object);
    }

    public abstract boolean evaluateDocument(DocumentDomainObject document);
}
