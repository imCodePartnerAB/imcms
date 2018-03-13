package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;

import java.util.Set;

public interface DocumentRepository {

    Set<DocumentDomainObject> getDocs();

}
