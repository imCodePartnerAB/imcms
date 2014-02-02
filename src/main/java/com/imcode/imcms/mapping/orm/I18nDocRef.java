package com.imcode.imcms.mapping.orm;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Objects;

@Embeddable
public class I18nDocRef {

    private volatile DocRef docRef;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private volatile DocumentLanguage language;

    protected I18nDocRef() {
    }

    public I18nDocRef(DocRef docRef, DocumentLanguage language) {
        this.docRef = docRef;
        this.language = language;
    }

    public int docId() {
        return docRef.getDocId();
    }

    public int versionNo() {
        return docRef.getVersionNo();
    }

    public static I18nDocRef of(int docId, int versionNo, DocumentLanguage language) {
        return of(DocRef.of(docId, versionNo), language);
    }

    public static I18nDocRef of(DocRef docRef, DocumentLanguage language) {
        return new I18nDocRef(docRef, language);
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public DocumentLanguage getLanguage() {
        return language;
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
}
