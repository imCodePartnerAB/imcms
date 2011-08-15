package com.imcode.imcms.addon.imagearchive.entity;

import com.imcode.imcms.addon.imagearchive.util.exif.Flash;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

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
    
    @Column(name="x_resolution")
    private Integer xResolution;

    @Column(name="y_resolution")
    private Integer yResolution;
    
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

    @Column(name="manufacturer")
    private String manufacturer;

    @Column(name="model")
    private String model;

    @Column(name="compression")
    private String compression;

    @Column(name = "exposure")
    private Double exposure;

    @Column(name = "exposure_program")
    private String exposureProgram;

    @Column(name = "fstop")
    private Float fStop;

    @Column(name = "flash")
    @Enumerated(EnumType.ORDINAL)
    private Flash flash;

    @Column(name = "focal_length")
    private Float focalLength;

    @Column(name = "color_space")
    private String colorSpace;

    @Column(name = "resolution_unit")
    private Integer resolutionUnit;

    @Column(name = "pixel_x_dimension")
    private Integer pixelXDimension;

    @Column(name = "pixel_y_dimension")
    private Integer pixelYDimension;

    @Column(name = "date_original")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOriginal;

    @Column(name = "date_digitized")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDigitized;

    @Column(name = "ISO")
    private Integer ISO;
    
    
    public Exif() {
    }
    
    public Exif(Integer xResolution, Integer yResolution, String description, String artist, String copyright, short type,
                String manufacturer, String model, String compression, Double exposure, String exposureProgram, Float fStop,
                Flash flash, Float focalLength, String colorSpace, Integer resolutionUnit,
                Integer pixelXDimension, Integer pixelYDimension, Date dateOriginal, Date dateDigitized, Integer ISO) {
        this.xResolution = xResolution;
        this.yResolution = yResolution;
        this.description = description;
        this.artist = artist;
        this.copyright = copyright;
        this.type = type;
        this.manufacturer = manufacturer;
        this.model = model;
        this.compression = compression;
        this.exposure = exposure;
        this.exposureProgram = exposureProgram;
        this.fStop = fStop;
        this.flash = flash;
        this.focalLength = focalLength;
        this.colorSpace = colorSpace;
        this.resolutionUnit = resolutionUnit;
        this.pixelXDimension = pixelXDimension;
        this.pixelYDimension = pixelYDimension;
        this.dateOriginal = dateOriginal;
        this.dateDigitized = dateDigitized;
        this.ISO = ISO;
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

    public Integer getxResolution() {
        return xResolution;
    }

    public void setxResolution(Integer xResolution) {
        this.xResolution = xResolution;
    }

    public Integer getyResolution() {
        return yResolution;
    }

    public void setyResolution(Integer yResolution) {
        this.yResolution = yResolution;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public Double getExposure() {
        return exposure;
    }

    public void setExposure(Double exposure) {
        this.exposure = exposure;
    }

    public String getExposureProgram() {
        return exposureProgram;
    }

    public void setExposureProgram(String exposureProgram) {
        this.exposureProgram = exposureProgram;
    }

    public Float getfStop() {
        return fStop;
    }

    public void setfStop(Float fStop) {
        this.fStop = fStop;
    }

    public Flash getFlash() {
        return flash;
    }

    public void setFlash(Flash flash) {
        this.flash = flash;
    }

    public Float getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(Float focalLength) {
        this.focalLength = focalLength;
    }

    public String getColorSpace() {
        return colorSpace;
    }

    public void setColorSpace(String colorSpace) {
        this.colorSpace = colorSpace;
    }

    public Integer getResolutionUnit() {
        return resolutionUnit;
    }

    public void setResolutionUnit(Integer resolutionUnit) {
        this.resolutionUnit = resolutionUnit;
    }

    public Integer getPixelXDimension() {
        return pixelXDimension;
    }

    public void setPixelXDimension(Integer pixelXDimension) {
        this.pixelXDimension = pixelXDimension;
    }

    public Integer getPixelYDimension() {
        return pixelYDimension;
    }

    public void setPixelYDimension(Integer pixelYDimension) {
        this.pixelYDimension = pixelYDimension;
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

    public Date getDateOriginal() {
        return dateOriginal;
    }

    public void setDateOriginal(Date dateOriginal) {
        this.dateOriginal = dateOriginal;
    }

    public Date getDateDigitized() {
        return dateDigitized;
    }

    public void setDateDigitized(Date dateDigitized) {
        this.dateDigitized = dateDigitized;
    }

    public Integer getISO() {
        return ISO;
    }

    public void setISO(Integer ISO) {
        this.ISO = ISO;
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
