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
        private int versionNo;
        private DocumentLanguage docLanguage;

        public Builder() {}

        public Builder(DocRef docRef) {
            this.docId = docRef.docId;
            this.versionNo = docRef.versionNo;
            this.docLanguage = docRef.docLanguage;
        }

        public Builder docId(int docId) {
            this.docId = docId;
            return this;
        }
        public Builder versionNo(int versionNo) {
            this.versionNo = versionNo;
            return this;
        }

        public Builder docLanguage(DocumentLanguage docLanguage) {
            this.docLanguage = docLanguage;
            return this;
        }

        public DocRef build() {
            return DocRef.of(docId, versionNo, docLanguage);
        }
    }

    public static DocRef of(int docId, int docVersionNo) {
        return new DocRef(docId, docVersionNo, null);
    }

    public static DocRef of(int docId, int docVersionNo, DocumentLanguage docLanguage) {
        return new DocRef(docId, docVersionNo, docLanguage);
    }

    private final int docId;

    private final int versionNo;

    private final DocumentLanguage docLanguage;

    private final int cachedHashCode;

    private final String cachedToString;

    public DocRef(int docId, int versionNo, DocumentLanguage docLanguage) {
        this.docId = docId;
        this.versionNo = versionNo;
        this.docLanguage = docLanguage;
        this.cachedHashCode = Objects.hash(docId, versionNo);
        this.cachedToString = com.google.common.base.Objects.toStringHelper(this)
                .add("id", docId)
                .add("docVersionNo", versionNo)
                .add("docLanguage", docLanguage)
                .toString();
    }

    public int getDocId() {
        return docId;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public DocumentLanguage getDocLanguage() {
        return docLanguage;
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof DocRef && equals((DocRef) o));
    }

    private boolean equals(DocRef that) {
        return docId == that.docId
                && versionNo == that.versionNo
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
}

