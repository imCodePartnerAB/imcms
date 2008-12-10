package com.imcode.imcms.servlet.beans;

import com.imcode.imcms.servlet.superadmin.AdminManager;
import com.imcode.imcms.util.l10n.LocalizedMessage;

import java.util.List;

public class AdminManagerSubreport {

    private String name;
    private String sortorder = "MOD";
    private List documents;
    private LocalizedMessage heading;
    private boolean expanded;
    private String searchQueryString ;
    private int maxDocumentCount = AdminManager.DEFAULT_DOCUMENTS_PER_LIST ;


    public AdminManagerSubreport() {
    }

    public String getName() {
        return name;
    }

    public String getSortorder() {
        return sortorder;
    }

    public LocalizedMessage getHeading() {
        return heading;
    }

    public List getDocuments() {
        return documents;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setDocuments( List documents ) {
        this.documents = documents;
    }

    public void setExpanded( boolean expanded ) {
        this.expanded = expanded;
    }

    public void setHeading( LocalizedMessage heading ) {
        this.heading = heading;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setSortorder( String sortorder ) {
        this.sortorder = sortorder;
    }

    public void setMaxDocumentCount( int maxDocumentCount ) {
        this.maxDocumentCount = maxDocumentCount;
    }

    public boolean isBelowMaxDocumentCount( int i ) {
        return 0 == maxDocumentCount || i < maxDocumentCount ;
    }

    public boolean isAboveMaxDocumentCount( int i ) {
        return 0 != maxDocumentCount && i > maxDocumentCount;
    }

    public void setSearchQueryString( String searchQueryString ) {
        this.searchQueryString = searchQueryString;
    }

    public String getSearchQueryString() {
        return searchQueryString;
    }
}
