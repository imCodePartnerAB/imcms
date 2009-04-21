package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ExifPK implements Serializable {
    private static final long serialVersionUID = -343809179983130412L;
    
    @Column(name="image_id", nullable=false)
    private long imageId;
    
    @Column(name="exif_type", nullable=false)
    private short type = Exif.TYPE_ORIGINAL;
    
    
    public ExifPK() {
    }
    
    public ExifPK(long imageId, short type) {
        this.imageId = imageId;
        this.type = type;
    }
    
    
    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
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
        
        final ExifPK other = (ExifPK) obj;
        if (this.imageId != other.imageId) {
            return false;
        }
        
        if (this.type != other.type) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (int) (this.imageId ^ (this.imageId >>> 32));
        hash = 17 * hash + this.type;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.ExifPK[imageId: %d, type: %d]", 
                imageId, type);
    }
}
