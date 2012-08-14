package imcode.server.document.textdocument;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DocIdentity {

    @Column(name="doc_id")
    private int docId;

    @Column(name="doc_version_no")
    private int docVersionNo;

    protected DocIdentity() {
    }

    public DocIdentity(int docId, int docVersionNo) {
        this.docId = docId;
        this.docVersionNo = docVersionNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocIdentity docRef = (DocIdentity) o;

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
}
