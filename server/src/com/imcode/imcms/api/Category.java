package com.imcode.imcms.api;

import imcode.server.document.CategoryDomainObject;

/**
 * @author kreiger
 */
public class Category {

    CategoryDomainObject internalCategory ;

    Category( CategoryDomainObject internalCategory ) {
        this.internalCategory = internalCategory ;
    }

    CategoryDomainObject getInternal() {
        return internalCategory ;
    }

    public String getName() {
        return internalCategory.getName() ;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        final Category category = (Category) o;

        if (!internalCategory.equals(category.internalCategory)) return false;

        return true;
    }

    public int hashCode() {
        return internalCategory.hashCode();
    }

    public String toString() {
        return internalCategory.toString();
    }

}
