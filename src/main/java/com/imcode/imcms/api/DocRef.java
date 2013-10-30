package com.imcode.imcms.api;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DocRef {

    // doc_meta_id
    @Column(name="doc_id")
    private int metaId;

    @Column(name="doc_version_no")
    private int versionNo;

    protected DocRef() {
    }

    public DocRef(int metaId, int versionNo) {
        this.metaId = metaId;
        this.versionNo = versionNo;
    }

    public static DocRef of(int metaId, int docVersionNo) {
        return new DocRef(metaId, docVersionNo);
    }

    public int metaId() {
        return metaId;
    }

    public int versionNo() {
        return versionNo;
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof DocRef && equals((DocRef) o));
    }

    private boolean equals(DocRef that) {
        return this.metaId == that.metaId && this.versionNo == that.versionNo;

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(metaId, versionNo);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("docId", metaId).add("docVersionNo", versionNo()).toString();
    }
}
