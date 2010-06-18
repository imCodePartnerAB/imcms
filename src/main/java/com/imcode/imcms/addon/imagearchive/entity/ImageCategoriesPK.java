package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ImageCategoriesPK implements Serializable {
    private static final long serialVersionUID = 9177391917977478766L;
    
    @Column(name="image_id", nullable=false)
    private long imageId;
    
    @Column(name="category_id", nullable=false)
    private int categoryId;

    
    public ImageCategoriesPK() {
    }

    public ImageCategoriesPK(long imageId, int categoryId) {
        this.imageId = imageId;
        this.categoryId = categoryId;
    }
    
    
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final ImageCategoriesPK other = (ImageCategoriesPK) obj;
        if (this.imageId != other.imageId) {
            return false;
        }
        
        if (this.categoryId != other.categoryId) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (int) (this.imageId ^ (this.imageId >>> 32));
        hash = 79 * hash + this.categoryId;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.ImageCategoriesPK[imageId: %d, categoryId: %d]", 
                imageId, categoryId);
    }
}
