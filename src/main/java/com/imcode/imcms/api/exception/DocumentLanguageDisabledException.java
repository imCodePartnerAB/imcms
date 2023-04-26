package com.imcode.imcms.api.exception;

import com.imcode.imcms.api.DocumentLanguage;
import imcode.server.document.DocumentDomainObject;

@Deprecated
public class DocumentLanguageDisabledException extends DocumentLanguageException {

    private DocumentDomainObject document;

    private DocumentLanguage language;

    public DocumentLanguageDisabledException(DocumentDomainObject document, DocumentLanguage language) {
        this.document = document;
        this.language = language;
    }

    public DocumentDomainObject getDocument() {
        return document;
    }

    public void setDocument(DocumentDomainObject document) {
        this.document = document;
    }

    public DocumentLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DocumentLanguage language) {
        this.language = language;
    }
}
