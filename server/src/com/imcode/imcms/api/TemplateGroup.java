package com.imcode.imcms.api;

import imcode.server.document.TemplateGroupDomainObject;

/**
 * Class describing a group of templates
 */
public class TemplateGroup {

    private final TemplateGroupDomainObject internalTemplateGroup;

    /**
     * Constructs TemplateGroup with TemplateGroupDomainObject backing it
     * @param internal TemplateGroupDomainObject to be used internally
     */
    public TemplateGroup(TemplateGroupDomainObject internal) {
        internalTemplateGroup = internal;
    }

    /**
     * Returns template group's name
     * @return template group's name
     */
    public String getName() {
        return internalTemplateGroup.getName();
    }

    /**
     * Returns template group's id
     * @return template group's id
     */
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
