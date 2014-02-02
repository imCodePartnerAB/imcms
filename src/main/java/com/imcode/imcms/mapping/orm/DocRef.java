package com.imcode.imcms.mapping.orm;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DocRef {

    @Column(name="doc_id")
    private volatile int docId;

    @Column(name="doc_version_no")
    private volatile int versionNo;

    protected DocRef() {
    }

    public DocRef(int docId, int versionNo) {
        this.docId = docId;
        this.versionNo = versionNo;
    }

    public static DocRef of(int docId, int docVersionNo) {
        return new DocRef(docId, docVersionNo);
    }

    public int getDocId() {
        return docId;
    }

    public int getVersionNo() {
        return versionNo;
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof DocRef && equals((DocRef) o));
    }

    private boolean equals(DocRef that) {
        return this.docId == that.docId && this.versionNo == that.versionNo;

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(docId, versionNo);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("docId", docId).add("docVersionNo", getVersionNo()).toString();
    }
}
