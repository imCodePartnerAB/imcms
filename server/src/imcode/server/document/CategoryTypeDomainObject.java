package imcode.server.document;

import imcode.server.ApplicationServer;

import java.io.Serializable;

/**
 * @author kreiger
 */
public class CategoryTypeDomainObject implements Comparable, Serializable {

    private int id;
    private String name ;
    private int maxChoices ;

    public CategoryTypeDomainObject(int id, String name, int maxChoices) {
        this.id = id;
        this.name = name;
        this.maxChoices = maxChoices;  // 0=singel choice, 1=multi choice
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryTypeDomainObject)) {
            return false;
        }

        final CategoryTypeDomainObject categoryTypeDomainObject = (CategoryTypeDomainObject) o;

        if (!name.equals(categoryTypeDomainObject.name)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return getName() ;
    }

    public int compareTo( Object o ) {
        return name.compareToIgnoreCase( ((CategoryTypeDomainObject)o).name) ;
    }

    public boolean hasImages() {
        CategoryDomainObject[] categories = ApplicationServer.getIMCServiceInterface().getDocumentMapper().getAllCategoriesOfType(this);
        boolean hasImages = false;
        for (int i = 0; i < categories.length; i++) {
            CategoryDomainObject category = categories[i];
            if( !"".equals(category.getImage()) ) {
                hasImages = true;
                break;
            }
        }
        return hasImages;
    }
}
