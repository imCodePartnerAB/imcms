package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="category_types")
public class CategoryTypes implements Serializable {
    private static final long serialVersionUID = -7036783245514725184L;
    
    @Id
    @Column(name="category_type_id", nullable=false)
    @GeneratedValue
    private int id;
    
    @Column(name="name", length=128, nullable=false)
    private String name;
    
    @Column(name="max_choices", nullable=false)
    private int maxChoices;
    
    @Column(name="inherited", nullable=false)
    private boolean inherited;
    
    @Column(name="is_image_archive", nullable=false)
    private boolean imageArchive;

    
    public CategoryTypes() {
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public void setMaxChoices(int maxChoices) {
        this.maxChoices = maxChoices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isImageArchive() {
        return imageArchive;
    }

    public void setImageArchive(boolean imageArchive) {
        this.imageArchive = imageArchive;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final CategoryTypes other = (CategoryTypes) obj;
        if (this.id != other.id) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.id;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.CategoryTypes[id: %d, name: %s]", id, name);
    }
}
