package com.imcode.imcms.servlet.beans;

import com.imcode.imcms.servlet.superadmin.AdminManager;
import imcode.util.LocalizedMessage;

import java.util.List;

public class AdminManagerSubreport {

    private String name;
    private String sortorder;
    private List documents;
    private LocalizedMessage heading;
    private boolean expanded;
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

    public int getMaxDocumentCount() {
        return maxDocumentCount;
    }

    public void setMaxDocumentCount( int maxDocumentCount ) {
        this.maxDocumentCount = maxDocumentCount;
    }

    public boolean isBelowMaxDocumentCount( int i ) {
        return 0 == maxDocumentCount || i < maxDocumentCount ;
    }
}
