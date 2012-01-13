package com.imcode.imcms.api;

import imcode.server.document.TemplateDomainObject;

/**
 * Represents a template in imcms
 */
public class Template {

    private final TemplateDomainObject internalTemplate;

    /**
     * Constructs Template with the given TemplateDomainObject backing it.
     * @param internalTemplate TemplateDomainObject to be used internally.
     */
    public Template(TemplateDomainObject internalTemplate) {
        this.internalTemplate = internalTemplate;
    }

    /**
     * Returns the name of this template
     * @return name of this template
     */
    public String getName() {
        return internalTemplate.getName();
    }

    /** @deprecated Returns 0, use {@link #getName()} instead. **/
    public int getId() {
        return 0;
    }

    /**
     * Compares two templates, uses {@link com.imcode.imcms.api.Template#getName()} as a criteria of equality.
     * @param o template to compare with
     * @return true if both templates are the same
     */
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Template ) ) {
            return false;
        }

        final Template template = (Template) o;

        return internalTemplate.equals(template.internalTemplate);

    }

    /**
     * Hash code of this Template, calls hashCode() of Template's name.
     * @return
     */
    public int hashCode() {
        return internalTemplate.hashCode() ;
    }

    TemplateDomainObject getInternal() {
        return internalTemplate;
    }

    /**
     * String representation of this template in the form of a name
     * @return same as {@link com.imcode.imcms.api.Template#getName()}
     */
    public String toString() {
        return getName();
    }
}
