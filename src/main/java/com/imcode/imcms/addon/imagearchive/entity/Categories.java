package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="categories")
public class Categories implements Serializable {
    private static final long serialVersionUID = 7533187253952781894L;
    
    @Id
    @Column(name="category_id", nullable=false)
    @GeneratedValue
    private int id;
    
    @Column(name="category_type_id", nullable=false)
    private int typeId;
    
    @ManyToOne
    @JoinColumn(name="category_type_id", referencedColumnName="category_type_id", insertable=false, updatable=false)
    private CategoryTypes type;
    
    @Column(name="name", length=128, nullable=false)
    private String name;
    
    @Column(name="description", length=500)
    private String description;
    
    @Column(name="image", length=255, nullable=false)
    private String image;

    
    public Categories() {
    }

    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryTypes getType() {
        return type;
    }

    public void setType(CategoryTypes type) {
        this.type = type;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Categories other = (Categories) obj;
        if (this.id != other.id) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.id;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.Categories[id: %d, typeId: %d, name: %s]", 
                id, typeId, name);
    }
}
