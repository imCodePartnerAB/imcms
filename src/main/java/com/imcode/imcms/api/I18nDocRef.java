package com.imcode.imcms.api;

import com.google.common.base.Objects;

// todo: include part of meta - title, etc ?
public class I18nDocRef {
    private final DocRef docRef;
    private final DocumentLanguage language;

    public I18nDocRef(DocRef docRef, DocumentLanguage language) {
        this.docRef = docRef;
        this.language = language;
    }

    public static I18nDocRef of(DocRef docRef, DocumentLanguage language) {
        return new I18nDocRef(docRef, language);
    }

    DocRef docRef() {
        return docRef;
    }

    DocumentLanguage language() {
        return language;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("docRef", docRef).add("language", language).toString();
    }
}
