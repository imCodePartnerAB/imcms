package com.imcode.imcms.api;

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
        private DocumentLanguage docLanguage;

        public Builder() {}

        public Builder(DocRef docRef) {
            this.docId = docRef.docId;
            this.docVersionNo = docRef.docVersionNo;
            this.docLanguage = docRef.docLanguage;
        }

        public Builder docId(int docId) {
            this.docId = docId;
            return this;
        }
        public Builder docVersionNo(int docVersionNo) {
            this.docVersionNo = docVersionNo;
            return this;
        }

        public Builder docLanguage(DocumentLanguage docLanguage) {
            this.docLanguage = docLanguage;
            return this;
        }

        public DocRef build() {
            return DocRef.of(docId, docVersionNo, docLanguage);
        }
    }

    public static DocRef of(int docId, int docVersionNo) {
        return new DocRef(docId, docVersionNo, null);
    }

    public static DocRef of(int docId, int docVersionNo, DocumentLanguage docLanguage) {
        return new DocRef(docId, docVersionNo, docLanguage);
    }

    private final int docId;

    private final int docVersionNo;

    private final DocumentLanguage docLanguage;

    private final int cachedHashCode;

    private final String cachedToString;

    public DocRef(int docId, int docVersionNo, DocumentLanguage docLanguage) {
        this.docId = docId;
        this.docVersionNo = docVersionNo;
        this.docLanguage = docLanguage;
        this.cachedHashCode = Objects.hash(docId, docVersionNo);
        this.cachedToString = com.google.common.base.Objects.toStringHelper(this)
                .add("id", docId)
                .add("docVersionNo", docVersionNo)
                .add("docLanguage", docLanguage)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof DocRef && equals((DocRef) o));
    }

    private boolean equals(DocRef that) {
        return docId == that.docId
                && docVersionNo == that.docVersionNo
                && Objects.equals(docLanguage, that.docLanguage);
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

    public DocumentLanguage getDocLanguage() {
        return docLanguage;
    }
}

