package com.imcode.imcms.api;

import imcode.server.document.CategoryDomainObject;

/**
 * @author kreiger
 */
public class Category implements Comparable {

    private CategoryDomainObject internalCategory ;

    public Category( String name, CategoryType categoryType ) {
        this.internalCategory = new CategoryDomainObject( 0, name, "","", categoryType.getInternal() );
    }

    Category( CategoryDomainObject internalCategory ) {
        this.internalCategory = internalCategory ;
    }

    CategoryDomainObject getInternal() {
        return internalCategory ;
    }

    public String getName() {
        return internalCategory.getName() ;
    }

    public void setName(String name) {
        internalCategory.setName( name );
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
        return internalCategory.getImageUrl();
    }

    public void setImage( String imageUrl ) {
        internalCategory.setImageUrl( imageUrl );
    }

    public void setDescription( String description ) {
        internalCategory.setDescription( description );
    }

    public int compareTo( Object o ) {
        return internalCategory.compareTo( ((Category)o).internalCategory ) ;
    }
}
