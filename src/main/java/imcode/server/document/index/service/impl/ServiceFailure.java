package imcode.server.document.index.service.impl;

public class ServiceFailure {

    private final ManagedDocumentIndexService service;
    private final Throwable exception;
    private final Type type;
    public ServiceFailure(ManagedDocumentIndexService service, Throwable exception, Type type) {
        this.service = service;
        this.exception = exception;
        this.type = type;
    }

    public ManagedDocumentIndexService getService() {
        return service;
    }

    public Throwable getException() {
        return exception;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SEARCH, UPDATE, REBUILD
    }
}
