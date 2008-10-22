package com.imcode.imcms.servlet.beans;

import imcode.server.document.DocumentDomainObject;

public class AdminManagerSubReportListItemBean {
    DocumentDomainObject document ;
    int index ;
    boolean expanded ;

    public DocumentDomainObject getDocument() {
        return document;
    }

    public void setDocument( DocumentDomainObject document ) {
        this.document = document;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded( boolean expanded ) {
        this.expanded = expanded;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex( int index ) {
        this.index = index;
    }
}
