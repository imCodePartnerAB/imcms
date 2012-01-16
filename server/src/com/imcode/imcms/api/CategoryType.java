package com.imcode.imcms.api;

import imcode.server.document.CategoryTypeDomainObject;

/**
 * Category types help distinguish between Categories with the same name.
 * The also constrol if the categories belonging to them can be inherited or used in image archive.
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

    /**
     * Returns string representation on this category type
     * @return string representation on this category type
     */
    public String toString() {
        return internalCategoryType.toString() ;
    }

    /**
     * Returns category type's name
     * @return category type's name
     */
    public String getName() {
        return internalCategoryType.getName() ;
    }

    /**
     * Returns category type's id
     * @return category type's id
     */
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
