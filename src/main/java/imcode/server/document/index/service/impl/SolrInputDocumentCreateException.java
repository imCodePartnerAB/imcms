package imcode.server.document.index.service.impl;

public class SolrInputDocumentCreateException extends RuntimeException {

    public SolrInputDocumentCreateException() {
    }

    public SolrInputDocumentCreateException(String message) {
        super(message);
    }

    public SolrInputDocumentCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SolrInputDocumentCreateException(Throwable cause) {
        super(cause);
    }

    public SolrInputDocumentCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}