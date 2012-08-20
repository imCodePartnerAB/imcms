package imcode.server.document.textdocument;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DocRef {

    @Column(name="doc_id")
    private int docId;

    @Column(name="doc_version_no")
    private int docVersionNo;

    protected DocRef() {
    }

    public DocRef(int docId, int docVersionNo) {
        this.docId = docId;
        this.docVersionNo = docVersionNo;
    }

    public static DocRef of(int docId, int docVersionNo) {
        return new DocRef(docId, docVersionNo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocRef docRef = (DocRef) o;

        if (docId != docRef.docId) return false;
        if (docVersionNo != docRef.docVersionNo) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = docId;
        result = 31 * result + docVersionNo;
        return result;
    }

    public int getDocId() {
        return docId;
    }

    public int getDocVersionNo() {
        return docVersionNo;
    }

    @Override
    public String toString() {
        return "DocRef{" +
                "docId=" + docId +
                ", docVersionNo=" + docVersionNo +
                '}';
    }
}
