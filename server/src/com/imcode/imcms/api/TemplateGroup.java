package com.imcode.imcms.api;

import imcode.server.document.TemplateGroupDomainObject;

public class TemplateGroup {

    private TemplateGroupDomainObject internalTemplateGroup;

    public TemplateGroup( TemplateGroupDomainObject internal ) {
        internalTemplateGroup = internal;
    }

    public String getName() {
        return internalTemplateGroup.getName();
    }

    int getId() {
        return internalTemplateGroup.getId();
    }

    TemplateGroupDomainObject getInternal() {
        return internalTemplateGroup ;
    }

    public boolean equals( Object o ) {
        if( this == o )
            return true;
        if( !(o instanceof TemplateGroup) )
            return false;

        final TemplateGroup templateGroup = (TemplateGroup)o;

        if( internalTemplateGroup != null ? !internalTemplateGroup.equals( templateGroup.internalTemplateGroup ) : templateGroup.internalTemplateGroup != null )
            return false;

        return true;
    }

    public int hashCode() {
        return (internalTemplateGroup != null ? internalTemplateGroup.hashCode() : 0);
    }
}
