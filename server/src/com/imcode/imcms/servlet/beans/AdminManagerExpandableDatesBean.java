package com.imcode.imcms.servlet.beans;

import imcode.server.document.DocumentDomainObject;

public class AdminManagerExpandableDatesBean {

    private DocumentDomainObject document;
    private boolean expanded;

    public DocumentDomainObject getDocument() {
        return document;
    }

    public void setDocument(DocumentDomainObject document) {
        this.document = document;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
