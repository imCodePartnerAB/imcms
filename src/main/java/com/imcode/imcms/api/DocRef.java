package com.imcode.imcms.api;

import java.util.Objects;

public class DocRef {

    private final int docId;

    private final int versionNo;

    private final int cachedHashCode;

    private final String cachedToString;

    public DocRef(int docId, int versionNo) {
        this.docId = docId;
        this.versionNo = versionNo;
        this.cachedHashCode = Objects.hash(docId, versionNo);
        this.cachedToString = com.google.common.base.Objects.toStringHelper(this)
                .add("docId", docId).add("docVersionNo", getVersionNo()).toString();
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
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return cachedToString;
    }
}

