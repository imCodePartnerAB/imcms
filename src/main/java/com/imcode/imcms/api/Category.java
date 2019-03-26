package com.imcode.imcms.api;

import imcode.server.document.CategoryDomainObject;

/**
 * @author kreiger
 */
public class Category implements Comparable<Category> {

    private final CategoryDomainObject internalCategory;

    public Category(String name, CategoryType categoryType) {
        this.internalCategory = new CategoryDomainObject(0, name, "", categoryType.getInternal());
    }

    Category(CategoryDomainObject internalCategory) {
        this.internalCategory = internalCategory;
    }

    CategoryDomainObject getInternal() {
        return internalCategory;
    }

    public String getName() {
        return internalCategory.getName();
    }

    public void setName(String name) {
        internalCategory.setName(name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }

        final Category category = (Category) o;

        return internalCategory.equals(category.internalCategory);

    }

    public int hashCode() {
        return internalCategory.hashCode();
    }

    public String toString() {
        return internalCategory.toString();
    }

    public String getDescription() {
        return internalCategory.getDescription();
    }

    public void setDescription(String description) {
        internalCategory.setDescription(description);
    }

    public int getId() {
        return internalCategory.getId();
    }

    public com.imcode.imcms.model.CategoryType getType() {
        return internalCategory.getType();
    }

    @Override
    public int compareTo(Category category) {
        return internalCategory.compareTo(category.internalCategory);
    }
}
