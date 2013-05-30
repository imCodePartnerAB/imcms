package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="archive_images")
public class Images implements Serializable {
    private static final long serialVersionUID = -1831641612874730366L;
    
    public static final short STATUS_UPLOADED = 0;
    public static final short STATUS_ACTIVE = 1;
    public static final short STATUS_ARCHIVED = 2;
    
    
    @Id
    @GeneratedValue
    private long id;
    
    @Column(name="image_nm", length=255, nullable=false)
    private String imageNm = "";
    
    @Column(name="format", nullable=false)
    private int format;
    
    @Column(name="width", nullable=false)
    private int width;
    
    @Column(name="height", nullable=false)
    private int height;
    
    @Column(name="file_size", nullable=false)
    private int fileSize;
    
    @Column(name="uploaded_by", length=130, nullable=false)
    private String uploadedBy = "";
    
    @Column(name="users_id", nullable=false)
    private int usersId;
    
    @Column(name="status", nullable=false)
    private short status = STATUS_UPLOADED;
    
    @Column(name="created_dt", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt = new Date();
    
    @Column(name="updated_dt", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDt = new Date();
    
    @Column(name="license_dt")
    @Temporal(TemporalType.DATE)
    private Date licenseDt;
    
    @Column(name="license_end_dt")
    @Temporal(TemporalType.DATE)
    private Date licenseEndDt;

    @Column(name="alt_text")
    private String altText;
    
    @OneToMany
    @JoinTable(
        name="archive_image_categories",
        joinColumns=@JoinColumn(name="image_id", nullable=false, insertable=false, updatable=false), 
        inverseJoinColumns=@JoinColumn(name="category_id", nullable=false, insertable=false, updatable=false)
    )
    @OrderBy("name")
    private List<Categories> categories;
    
    @OneToMany
    @JoinTable(
        name="archive_image_keywords", 
        joinColumns=@JoinColumn(name="image_id", nullable=false, insertable=false, updatable=false), 
        inverseJoinColumns=@JoinColumn(name="keyword_id", nullable=false, insertable=false, updatable=false)
    )
    @OrderBy("keywordNm")
    private List<Keywords> keywords;
    
    @Transient
    private Exif changedExif;
    
    @Transient
    private Exif originalExif;
    
    @Transient
    private List<Integer> metaIds;
    
    @Transient
    private boolean usedInImcms;
    
    @Transient
    private boolean canChange;

    
    public Images() {
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageNm() {
        return imageNm;
    }

    public void setImageNm(String imageNm) {
        this.imageNm = imageNm;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public Date getLicenseDt() {
        return licenseDt;
    }

    public void setLicenseDt(Date licenseDt) {
        this.licenseDt = licenseDt;
    }

    public Date getLicenseEndDt() {
        return licenseEndDt;
    }

    public void setLicenseEndDt(Date licenseEndDt) {
        this.licenseEndDt = licenseEndDt;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public Date getUpdatedDt() {
        return updatedDt;
    }

    public void setUpdatedDt(Date updatedDt) {
        this.updatedDt = updatedDt;
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public void setCategories(List<Categories> categories) {
        this.categories = categories;
    }

    public List<Keywords> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keywords> keywords) {
        this.keywords = keywords;
    }

    public List<Integer> getMetaIds() {
        return metaIds;
    }

    public void setMetaIds(List<Integer> metaIds) {
        this.metaIds = metaIds;
    }

    public boolean isUsedInImcms() {
        return usedInImcms;
    }

    public void setUsedInImcms(boolean usedInImcms) {
        this.usedInImcms = usedInImcms;
    }

    public Exif getChangedExif() {
        return changedExif;
    }

    public void setChangedExif(Exif changedExif) {
        this.changedExif = changedExif;
    }

    public Exif getOriginalExif() {
        return originalExif;
    }

    public void setOriginalExif(Exif originalExif) {
        this.originalExif = originalExif;
    }
    
    public void setArtist(String artist) {
        changedExif = new Exif();
        changedExif.setArtist(artist);
    }
    
    public boolean isArchived() {
        return status == STATUS_ARCHIVED;
    }

    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    public boolean isCanChange() {
        return canChange;
    }

    public void setCanChange(boolean canChange) {
        this.canChange = canChange;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Images other = (Images) obj;
        if (this.id != other.id) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (this.id ^ (this.id >>> 32));
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.Images[id: %d, imageNm: %s, uploadedBy: %s]", 
                id, imageNm, uploadedBy);
    }
}
