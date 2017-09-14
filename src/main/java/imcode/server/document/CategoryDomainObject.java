package imcode.server.document;

import java.io.Serializable;

public class CategoryDomainObject implements Comparable<CategoryDomainObject>, Serializable, Cloneable {

    private int id;
    private String name;
    private String description = "";
    private String imageUrl = "";
    private CategoryTypeDomainObject type;

    public CategoryDomainObject() {
    }

    public CategoryDomainObject(int id, String name, String description, String imageUrl, CategoryTypeDomainObject type) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CategoryTypeDomainObject getType() {
        return type;
    }

    public void setType(CategoryTypeDomainObject type) {
        this.type = type;
    }

    public String toString() {
        return type + ": " + name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryDomainObject)) {
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int compareTo(CategoryDomainObject category) {
        return name.compareToIgnoreCase(category.name);
    }

    @Override
    public CategoryDomainObject clone() {
        try {
            CategoryDomainObject clone = (CategoryDomainObject) super.clone();
            if (type != null) clone.setType(type.clone());

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
