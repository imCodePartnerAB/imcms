package com.imcode.imcms.api;

import com.google.common.base.Objects;

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

    public int docId() {
        return docId;
    }

    public int docVersionNo() {
        return docVersionNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocRef)) return false;

        DocRef docRef = (DocRef) o;

        if (docId != docRef.docId) return false;
        if (docVersionNo != docRef.docVersionNo) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(docId, docVersionNo);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("docId", docId).add("docVersionNo", docVersionNo()).toString();
    }
}
