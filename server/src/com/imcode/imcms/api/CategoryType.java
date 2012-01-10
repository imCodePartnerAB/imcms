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

    /**
     * Indicates whether the categories in this category type will be inherited by a new document when a document that
     * has categories of this type is used as parent.
     * @return true if this category type's categories are inherited, false otherwise
     */
    public boolean isInherited() {
        return internalCategoryType.isInherited();
    }

    /**
     * Indicates whether this category type can be used in image archive
     * @return true if this category type can be used in image archive
     */
    public boolean isImageArchive() {
    	return internalCategoryType.isImageArchive();
    }
}
