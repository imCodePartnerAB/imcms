package imcode.server.document;

import java.util.ConcurrentModificationException;

/**
    The document changed while we were not expecting it to. Probably a bug.
**/
public class ConcurrentDocumentModificationException extends ConcurrentModificationException {
    public ConcurrentDocumentModificationException(Throwable throwable) {
        initCause(throwable);
    }
}
