package com.imcode.imcms;

import imcode.server.TemplateDomainObject;

public class Template {
    private TemplateDomainObject internalTemplate;

    public Template( TemplateDomainObject internalTemplate ) {
        this.internalTemplate = internalTemplate;
    }

    public String getName() {
        return internalTemplate.getName();
    }

    public int getId() {
        return internalTemplate.getId();
    }

    public int hashCode() {
        return internalTemplate.hashCode();
    }

    public boolean equals( Object obj ) {
        return internalTemplate.equals( obj );
    }
}
