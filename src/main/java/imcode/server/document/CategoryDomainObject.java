package imcode.server.document;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

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

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public int compareTo(CategoryDomainObject category) {
        return name.compareToIgnoreCase(category.name);
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
