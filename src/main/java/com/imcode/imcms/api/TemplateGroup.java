package com.imcode.imcms.api;

import imcode.server.document.TemplateGroupDomainObject;

public class TemplateGroup {

    private final TemplateGroupDomainObject internalTemplateGroup;

    public TemplateGroup(TemplateGroupDomainObject internal) {
        internalTemplateGroup = internal;
    }

    public String getName() {
        return internalTemplateGroup.getName();
    }

    public int getId() {
        return internalTemplateGroup.getId();
    }

    TemplateGroupDomainObject getInternal() {
        return internalTemplateGroup;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof TemplateGroup ) ) {
            return false;
        }

        final TemplateGroup templateGroup = (TemplateGroup) o;

        return internalTemplateGroup.equals(templateGroup.internalTemplateGroup) ;

    }

    public int hashCode() {
        return internalTemplateGroup.hashCode() ;

    }
}
