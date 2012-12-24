package com.imcode.imcms.api;

import com.google.common.base.Objects;

public class I18nDocRef {
    private final DocRef docRef;
    private final I18nLanguage language;

    public I18nDocRef(DocRef docRef, I18nLanguage language) {
        this.docRef = docRef;
        this.language = language;
    }

    public static I18nDocRef of(DocRef docRef, I18nLanguage language) {
        return new I18nDocRef(docRef, language);
    }

    DocRef docRef() {
        return docRef;
    }

    I18nLanguage language() {
        return language;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("docRef", docRef).add("language", language).toString();
    }
}
