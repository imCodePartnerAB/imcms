package com.imcode.imcms.api;

import imcode.server.document.CategoryDomainObject;

/**
 * Represents a category in imcms.
 * @author kreiger
 */
public class Category implements Comparable {

    private final CategoryDomainObject internalCategory ;

    /**
     * Constructs a Category from the given name and {@link CategoryType}
     * @param name a String with name for this category
     * @param categoryType a {@link CategoryType}
     */
    public Category( String name, CategoryType categoryType ) {
        this.internalCategory = new CategoryDomainObject( 0, name, "","", categoryType.getInternal() );
    }

    Category( CategoryDomainObject internalCategory ) {
        this.internalCategory = internalCategory ;
    }

    CategoryDomainObject getInternal() {
        return internalCategory ;
    }

    /**
     * Returns this category's name
     * @return a string with this category's name
     */
    public String getName() {
        return internalCategory.getName() ;
    }

    /**
     * Sets this category's name
     * @param name name for this category
     */
    public void setName(String name) {
        internalCategory.setName( name );
    }

    /**
     * Compares the specified object with this category for equality.
     * @param o the object to be compared for equality with this category
     * @return true if the specified object is a Category and it's id is the same as this category's id, false otherwise
     */
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

    /**
     * Returns this category's hashcode
     * @return hash code value in the form of this category's id
     */
    public int hashCode() {
        return internalCategory.hashCode();
    }

    /**
     * Returns string representation of this category in the form
     * of {@link com.imcode.imcms.api.CategoryType#getName()} + ":" + {@link com.imcode.imcms.api.Category#getName()}
     * @return a string representation of this category
     */
    public String toString() {
        return internalCategory.toString();
    }

    /**
     * Returns this category's description
     * @return this category's description or an empty string
     */
    public String getDescription() {
        return internalCategory.getDescription() ;
    }

    /**
     * Returns this category's id
     * @return this category's id
     */
    public int getId() {
        return internalCategory.getId();
    }

    /**
     * Get image url. The url is not used internally anywhere in the cms, just stored as string.
     * @return image url or an empty string if it's not set
     */
    public String getImage(){
        return internalCategory.getImageUrl();
    }

    /**
     * Sets the image url of this category
     * @param imageUrl url, relative or absolute
     */
    public void setImage( String imageUrl ) {
        internalCategory.setImageUrl( imageUrl );
    }

    /**
     * Sets the description of this category
     * @param description this category's description
     */
    public void setDescription( String description ) {
        internalCategory.setDescription( description );
    }


    /**
     * Compares this Category to the given Object.
     * @param o the Object to be compared with
     * @return 0 if the argument is a Category and it's name is equal ignoring case to this category's name. Value less than 0
     * if the argument's name is lexicographically greater(ignoring case) than this category's name. Value more than 0
     * if the argument's name is lexicographically less(ignoring case) than this category's name.
     */
    public int compareTo( Object o ) {
        return internalCategory.compareTo( ((Category)o).internalCategory ) ;
    }
}
