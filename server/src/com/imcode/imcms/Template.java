package com.imcode.imcms;

import imcode.server.document.TemplateDomainObject;

public class Template {
    private TemplateDomainObject internalTemplate;

    public Template( TemplateDomainObject internalTemplate ) {
        this.internalTemplate = internalTemplate;
    }

    public String getName() {
        return internalTemplate.getSimple_name();
    }

    public int getId() {
        return internalTemplate.getId();
    }

    public boolean equals( Object o ) {
        if( this == o )
            return true;
        if( !(o instanceof Template) )
            return false;

        final Template template = (Template)o;

        if( internalTemplate != null ? !internalTemplate.equals( template.internalTemplate ) : template.internalTemplate != null )
            return false;

        return true;
    }

    public int hashCode() {
        return (internalTemplate != null ? internalTemplate.hashCode() : 0);
    }

    TemplateDomainObject getInternal() {
        return internalTemplate;
    }

    public String toString() {
        return getName() + "(id = " + getId() + ")";
    }
}
