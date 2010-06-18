package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="archive_image_categories")
@IdClass(ImageCategoriesPK.class)
public class ImageCategories implements Serializable {
    private static final long serialVersionUID = -5295771796587859459L;
    
    @Id
    @Column(name="image_id", nullable=false)
    private long imageId;
    
    @ManyToOne
    @JoinColumn(name="image_id", referencedColumnName="id", insertable=false, updatable=false)
    private Images image;
    
    @Id
    @Column(name="category_id", nullable=false)
    private int categoryId;
    
    @ManyToOne
    @JoinColumn(name="category_id", referencedColumnName="category_id", insertable=false, updatable=false)
    private Categories category;
    
    @Column(name="created_dt", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt = new Date();
    
    
    public ImageCategories() {
    }

    public ImageCategories(long imageId, int categoryId) {
        this.imageId = imageId;
        this.categoryId = categoryId;
    }

    
    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Images getImage() {
        return image;
    }

    public void setImage(Images image) {
        this.image = image;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final ImageCategories other = (ImageCategories) obj;
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
        int hash = 3;
        hash = 37 * hash + (int) (this.imageId ^ (this.imageId >>> 32));
        hash = 37 * hash + this.categoryId;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.ImageCategories[imageId: %d, categoryId: %d]", 
                imageId, categoryId);
    }
}
