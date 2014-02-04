package com.imcode.imcms.api;

import java.util.Objects;

public class I18nDocRef {

    public static I18nDocRef of(DocRef docRef, DocumentLanguage language) {
        return new I18nDocRef(docRef, language);
    }

//    public static I18nDocRef of(int docId, int versionNo, DocumentLanguage language) {
//        return of(com.imcode.imcms.mapping.orm.DocRef.of(docId, versionNo), language);
//    }
//
//    public static I18nDocRef of(com.imcode.imcms.mapping.orm.DocRef docRef, DocumentLanguage language) {
//        return new I18nDocRef(docRef, language);
//    }


    private final DocRef docRef;

    private final DocumentLanguage language;

    public I18nDocRef(DocRef docRef, DocumentLanguage language) {
        this.docRef = docRef;
        this.language = language;
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this).add("docRef", docRef).add("language", language).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(docRef, language);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof I18nDocRef && equals((I18nDocRef) o));
    }

    private boolean equals(I18nDocRef that) {
        return Objects.equals(docRef, that.docRef) && Objects.equals(language, that.language);
    }

    public int docId() {
        return docRef.getDocId();
    }

    public int versionNo() {
        return docRef.getVersionNo();
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public DocumentLanguage getLanguage() {
        return language;
    }
}
