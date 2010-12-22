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

@Entity(name="Category")
@Table(name="categories")
public class CategoryDomainObject implements Comparable, Serializable, Cloneable {
	
    private String name;
    
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="category_id")	    
    private int id;
	    
    private String description = "";
    
    @Column(name="image")	 
    private String imageUrl = "";
    
    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.REFRESH)
    @JoinColumn(name="category_type_id", nullable=false)
    private CategoryTypeDomainObject type;


    public CategoryDomainObject() {} 
    
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

    @Override
    public CategoryDomainObject clone() {
        try {
            CategoryDomainObject clone = (CategoryDomainObject)super.clone();
            if (type != null) clone.setType(type.clone());

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
