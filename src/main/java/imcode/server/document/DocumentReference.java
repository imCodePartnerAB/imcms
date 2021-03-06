package imcode.server.document;

public abstract class DocumentReference implements Cloneable {

    private final int documentId;

    protected DocumentReference(int documentId) {
        this.documentId = documentId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public abstract DocumentDomainObject getDocument();

    public int hashCode() {
        return documentId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DocumentReference that = (DocumentReference) o;

        return documentId == that.documentId;

    }

    @Override
    public DocumentReference clone() throws CloneNotSupportedException {
        return (DocumentReference) super.clone();
    }
}
