package com.imcode.imcms.api;

import imcode.server.document.CategoryDomainObject;

/**
 * @author kreiger
 */
public class Category {

    CategoryDomainObject internalCategory ;

    public Category( CategoryDomainObject internalCategory ) {
        this.internalCategory = internalCategory ;
    }

    CategoryDomainObject getInternal() {
        return internalCategory ;
    }

    public String toString() {
        return internalCategory.toString() ;
    }

    public String getName() {
        return internalCategory.getName() ;
    }

}
