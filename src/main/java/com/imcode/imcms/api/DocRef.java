package com.imcode.imcms.api;

import java.util.Objects;

public final class DocRef {

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

