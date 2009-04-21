package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ImageKeywordsPK implements Serializable {
    private static final long serialVersionUID = -5964411262470839415L;
    
    @Column(name="image_id", nullable=false)
    private long imageId;
    
    @Column(name="keyword_id", nullable=false)
    private long keywordId;

    
    public ImageKeywordsPK() {
    }

    public ImageKeywordsPK(long imageId, long keywordId) {
        this.imageId = imageId;
        this.keywordId = keywordId;
    }

    
    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public long getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(long keywordId) {
        this.keywordId = keywordId;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final ImageKeywordsPK other = (ImageKeywordsPK) obj;
        if (this.imageId != other.imageId) {
            return false;
        }
        
        if (this.keywordId != other.keywordId) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (this.imageId ^ (this.imageId >>> 32));
        hash = 53 * hash + (int) (this.keywordId ^ (this.keywordId >>> 32));
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.ImageKeywordsPK[imageId: %d, keywordId: %d]", 
                imageId, keywordId);
    }
}
