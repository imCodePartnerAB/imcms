package com.imcode.imcms.servlet.beans;

import java.util.List;

public class AdminManagerSubreportBean {

    private String name;
    private String sortorder;
    private List documents;
    private String heading;
    private boolean expanded;

    public AdminManagerSubreportBean() {
    }

    public String getName() {
        return name;
    }

    public String getSortorder() {
        return sortorder;
    }

    public String getHeading() {
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

    public void setHeading( String heading ) {
        this.heading = heading;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setSortorder( String sortorder ) {
        this.sortorder = sortorder;
    }

}
