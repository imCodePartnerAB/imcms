package imcode.server.document;

import imcode.server.Imcms;

import java.io.Serializable;

/**
 * @author kreiger
 */
public class CategoryTypeDomainObject implements Comparable, Serializable {

    private int id;
    private String name ;
    private int maxChoices ;
    private boolean inherited;

    public CategoryTypeDomainObject(int id, String name, int maxChoices, boolean inherited) {
        this.id = id;
        this.name = name;
        this.maxChoices = maxChoices;  // 0=single choice, 1=multi choice
        this.inherited = inherited;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxChoices(int maxChoices) {
        this.maxChoices = maxChoices;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited( boolean inherited ) {
        this.inherited = inherited;
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
        CategoryDomainObject[] categories = Imcms.getServices().getDocumentMapper().getAllCategoriesOfType(this);
        boolean hasImages = false;
        for (int i = 0; i < categories.length; i++) {
            CategoryDomainObject category = categories[i];
            if( !"".equals(category.getImageUrl()) ) {
                hasImages = true;
                break;
            }
        }
        return hasImages;
    }

}
