package com.imcode.imcms.mapping;

import java.util.Objects;

public final class DocRef {

    public static Builder buillder() {
        return new Builder();
    }

    public static Builder buillder(DocRef docRef) {
        return new Builder(docRef);
    }

    public static class Builder {
        private int docId;
        private int docVersionNo;
        private String docLanguageCode;

        public Builder() {
        }

        public Builder(DocRef docRef) {
            this.docId = docRef.docId;
            this.docVersionNo = docRef.docVersionNo;
            this.docLanguageCode = docRef.docLanguageCode;
        }

        public Builder docId(int docId) {
            this.docId = docId;
            return this;
        }

        public Builder docVersionNo(int docVersionNo) {
            this.docVersionNo = docVersionNo;
            return this;
        }

        public Builder docLanguageCode(String docLanguageCode) {
            this.docLanguageCode = docLanguageCode;
            return this;
        }

        public DocRef build() {
            return DocRef.of(docId, docVersionNo, docLanguageCode);
        }
    }

    public static DocRef of(int docId, int docVersionNo) {
        return new DocRef(docId, docVersionNo, null);
    }

    public static DocRef of(int docId, int docVersionNo, String docLanguageCode) {
        return new DocRef(docId, docVersionNo, docLanguageCode);
    }

    private final int docId;

    private final int docVersionNo;

    private final String docLanguageCode;

    private final int cachedHashCode;

    private final String cachedToString;

    public DocRef(int docId, int docVersionNo, String docLanguageCode) {
        this.docId = docId;
        this.docVersionNo = docVersionNo;
        this.docLanguageCode = docLanguageCode;
        this.cachedHashCode = Objects.hash(docId, docVersionNo);
        this.cachedToString = com.google.common.base.Objects.toStringHelper(this)
                .add("id", docId)
                .add("docVersionNo", docVersionNo)
                .add("docLanguageCode", docLanguageCode)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof DocRef && equals((DocRef) o));
    }

    private boolean equals(DocRef that) {
        return docId == that.docId
                && docVersionNo == that.docVersionNo
                && Objects.equals(docLanguageCode, that.docLanguageCode);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return cachedToString;
    }

    public int getDocId() {
        return docId;
    }

    public int getDocVersionNo() {
        return docVersionNo;
    }

    public String getDocLanguageCode() {
        return docLanguageCode;
    }

    public DocVersionRef getDocVersionRef() {
        return DocVersionRef.of(docId, docVersionNo);
    }
}

