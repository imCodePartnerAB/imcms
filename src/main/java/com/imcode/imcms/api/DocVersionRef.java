package com.imcode.imcms.api;

import java.util.Objects;

public final class DocVersionRef {

    public static Builder buillder() {
        return new Builder();
    }

    public static Builder buillder(DocVersionRef docRef) {
        return new Builder(docRef);
    }

    public static class Builder {
        private int docId;
        private int docVersionNo;

        public Builder() {}

        public Builder(DocVersionRef docRef) {
            this.docId = docRef.docId;
            this.docVersionNo = docRef.docVersionNo;
        }

        public Builder docId(int docId) {
            this.docId = docId;
            return this;
        }
        public Builder docVersionNo(int docVersionNo) {
            this.docVersionNo = docVersionNo;
            return this;
        }

        public DocVersionRef build() {
            return DocVersionRef.of(docId, docVersionNo);
        }
    }

    public static DocVersionRef of(int docId, int docVersionNo) {
        return new DocVersionRef(docId, docVersionNo);
    }

    private final int docId;
    private final int docVersionNo;
    private final int cachedHashCode;

    public DocVersionRef(int docId, int docVersionNo) {
        this.docId = docId;
        this.docVersionNo = docVersionNo;
        this.cachedHashCode = Objects.hash(docId, docVersionNo);
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof DocVersionRef && equals((DocVersionRef) o));
    }

    private boolean equals(DocVersionRef that) {
        return docId == that.docId && docVersionNo == that.docVersionNo;
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("id", docId)
                .add("docVersionNo", docVersionNo)
                .toString();
    }

    public int getDocId() {
        return docId;
    }

    public int getDocVersionNo() {
        return docVersionNo;
    }
}

