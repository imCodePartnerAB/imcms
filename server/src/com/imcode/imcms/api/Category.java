package com.imcode.imcms.api;

import imcode.server.document.CategoryDomainObject;

/**
 * @author kreiger
 */
public class Category {

    private CategoryDomainObject internalCategory ;

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

    public String getDescription() {
        return internalCategory.getDescription() ;
    }

    public int getId() {
        return internalCategory.getId();
    }

    public String getImage(){
        return internalCategory.getImage();
    }
    

}
