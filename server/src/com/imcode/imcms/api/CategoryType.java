package com.imcode.imcms.api;

import imcode.server.document.CategoryTypeDomainObject;

/**
 * @author kreiger
 */
public class CategoryType {
    private CategoryTypeDomainObject internalCategoryType;

    CategoryType(CategoryTypeDomainObject internalCategoryType) {
        this.internalCategoryType = internalCategoryType;
    }

    CategoryTypeDomainObject getInternal() {
        return internalCategoryType;
    }

    public String toString() {
        return internalCategoryType.toString() ;
    }  

    public String getName() {
        return internalCategoryType.getName() ;
    }

    public int getId() {
        return internalCategoryType.getId();
    }
}
