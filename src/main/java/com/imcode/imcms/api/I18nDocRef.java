package com.imcode.imcms.api;

import com.google.common.base.Objects;

public class I18nDocRef {

    private final DocRef docRef;
    private final DocumentLanguage language;

    public I18nDocRef(DocRef docRef, DocumentLanguage language) {
        this.docRef = docRef;
        this.language = language;
    }

    public int metaId() {
        return docRef.metaId();
    }

    public int versionNo() {
        return docRef.versionNo();
    }

    public static I18nDocRef of(int metaId, int versionNo, DocumentLanguage language) {
        return of(DocRef.of(metaId, versionNo), language);
    }

    public static I18nDocRef of(DocRef docRef, DocumentLanguage language) {
        return new I18nDocRef(docRef, language);
    }

    public DocRef docRef() {
        return docRef;
    }

    public DocumentLanguage language() {
        return language;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("docRef", docRef).add("language", language).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(docRef, language);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof I18nDocRef && equals((I18nDocRef) o));
    }

    private boolean equals(I18nDocRef that) {
        return this.docRef.equals(that.docRef) && this.language.equals(that.language);
    }
}
