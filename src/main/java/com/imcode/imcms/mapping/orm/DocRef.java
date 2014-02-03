package com.imcode.imcms.mapping.orm;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Min;

@Embeddable
public class DocRef {

    @Column(name="doc_id")
    private int docId;

    @Min(1)
    @Column(name="doc_version_no")
    private int versionNo;

    public DocRef() {
    }

    public DocRef(int docId, int versionNo) {
        this.docId = docId;
        this.versionNo = versionNo;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("docId", docId).add("versionNo", versionNo).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(docId, versionNo);
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof DocRef && equals((DocRef) o));
    }

    private boolean equals(DocRef that) {
        return this.docId == that.docId && this.versionNo == that.versionNo;

    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }
}
