package imcode.server.document;

import java.io.Serializable;

/** @author kreiger */
public class CategoryDomainObject implements Comparable, Serializable {

    private String name;
    private int id;
    private CategoryTypeDomainObject type;
    private String description = "";
    private String imageUrl = "";

    public CategoryDomainObject(int id, String name, String description, String imageUrl, CategoryTypeDomainObject type) {
        this.description = description;
        this.type = type;
        this.name = name;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public CategoryTypeDomainObject getType() {
        return type;
    }

    public String toString() {
        return type + ": " + name;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof CategoryDomainObject ) ) {
            return false;
        }

        final CategoryDomainObject categoryDomainObject = (CategoryDomainObject) o;

        return id == categoryDomainObject.id;

    }

    public int hashCode() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int compareTo(Object o) {
        return name.compareToIgnoreCase(( (CategoryDomainObject) o ).name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setType(CategoryTypeDomainObject type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }
}
