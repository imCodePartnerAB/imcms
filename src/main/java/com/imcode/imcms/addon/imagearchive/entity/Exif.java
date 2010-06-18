package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="archive_exif")
@IdClass(ExifPK.class)
public class Exif implements Serializable {
    private static final long serialVersionUID = 8753082342920008037L;
    
    public static final short TYPE_ORIGINAL = 0;
    public static final short TYPE_CHANGED = 1;
    
    @Id
    @Column(name="image_id", nullable=false)
    private long imageId;
    
    @Id
    @Column(name="exif_type", nullable=false)
    private short type = TYPE_ORIGINAL;
    
    @Column(name="resolution", nullable=false)
    private int resolution;
    
    @Column(name="description", length=255, nullable=false)
    private String description = "";
    
    @Column(name="artist", length=255, nullable=false)
    private String artist = "";
    
    @Column(name="copyright", length=255, nullable=false)
    private String copyright = "";
    
    @Column(name="created_dt", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt = new Date();
    
    @Column(name="updated_dt", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDt = new Date();

    
    public Exif() {
    }
    
    public Exif(int resolution, String description, String artist, String copyright, short type) {
        this.resolution = resolution;
        this.description = description;
        this.artist = artist;
        this.copyright = copyright;
        this.type = type;
    }

    
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public Date getUpdatedDt() {
        return updatedDt;
    }

    public void setUpdatedDt(Date updatedDt) {
        this.updatedDt = updatedDt;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Exif other = (Exif) obj;
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
        int hash = 7;
        hash = 17 * hash + (int) (this.imageId ^ (this.imageId >>> 32));
        hash = 17 * hash + this.type;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.Exif[imageId: %d, type: %d]", 
                imageId, type);
    }
}
