package com.imcode.imcms.api;

import com.google.common.base.Objects;

// scala
public final class DocumentLanguages {

    private final DocumentLanguage preferredLanguage;
    private final DocumentLanguage defaultLanguage;


    public DocumentLanguages(DocumentLanguage preferredLanguage, DocumentLanguage defaultLanguage) {
        this.preferredLanguage = preferredLanguage;
        this.defaultLanguage = defaultLanguage;
    }

    public boolean preferredIsDefault() {
        return Objects.equal(preferredLanguage, defaultLanguage);
    }

    public DocumentLanguage getPreferred() {
        return preferredLanguage;
    }

    public DocumentLanguage getDefault() {
        return defaultLanguage;
    }
}
