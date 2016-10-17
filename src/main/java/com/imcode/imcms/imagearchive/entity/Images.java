package com.imcode.imcms.imagearchive.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "archive_images")

@NamedQueries({
        @NamedQuery(name = "updateImageData",
                query = " UPDATE\n" +
                        "          Images im \n" +
                        "        SET\n" +
                        "          im.imageNm = :imageNm, \n" +
                        "          im.uploadedBy = :uploadedBy, \n" +
                        "          im.licenseDt = :licenseDt, \n" +
                        "          im.licenseEndDt = :licenseEndDt,\n" +
                        "          im.status = :statusActive, \n" +
                        "          im.updatedDt = current_timestamp(),\n" +
                        "          im.altText = :altText\n" +
                        "        WHERE\n" +
                        "          im.id = :id"),
        @NamedQuery(name = "updateFullImageData",
                query = " UPDATE\n" +
                        "          Images im\n" +
                        "        SET\n" +
                        "          im.imageNm = :imageNm, \n" +
                        "          im.width = :width, \n" +
                        "          im.height = :height, \n" +
                        "          im.fileSize = :fileSize, \n" +
                        "          im.format = :format, \n" +
                        "          im.uploadedBy = :uploadedBy, \n" +
                        "          im.licenseDt = :licenseDt, \n" +
                        "          im.licenseEndDt = :licenseEndDt,\n" +
                        "          im.status = :statusActive, \n" +
                        "          im.updatedDt = current_timestamp() ,\n" +
                        "          im.altText = :altText\n" +
                        "        WHERE\n" +
                        "          im.id = :id"),
        @NamedQuery(name = "updateImageExif",
                query = " UPDATE \n" +
                        "          Exif e \n" +
                        "        SET \n" +
                        "          e.artist = :artist, \n" +
                        "          e.description = :description, \n" +
                        "          e.copyright = :copyright, \n" +
                        "          e.updatedDt = current_timestamp() \n" +
                        "        WHERE \n" +
                        "              e.imageId = :imageId \n" +
                        "          AND e.type = :changedType"),
        @NamedQuery(name = "updateImageExifFull",
        query = " UPDATE \n" +
                "          Exif e \n" +
                "        SET \n" +
                "          e.artist = :artist, \n" +
                "          e.description = :description, \n" +
                "          e.copyright = :copyright, \n" +
                "          e.xResolution = :xResolution,\n" +
                "           e.yResolution = :yResolution,\n" +
                "           e.manufacturer = :manufacturer,\n" +
                "           e.model = :model,\n" +
                "           e.compression = :compression,\n" +
                "           e.exposure = :exposure,\n" +
                "           e.exposureProgram = :exposureProgram,\n" +
                "           e.fStop = :fStop,\n" +
                "           e.flash = :flash,\n" +
                "           e.focalLength = :focalLength,\n" +
                "           e.colorSpace = :colorSpace,\n" +
                "           e.resolutionUnit = :resolutionUnit,\n" +
                "           e.pixelXDimension = :pixelXDimension,\n" +
                "           e.pixelYDimension = :pixelYDimension,\n" +
                "           e.dateOriginal = :dateOriginal,\n" +
                "           e.dateDigitized = :dateDigitized,\n" +
                "           e.ISO = :ISO,\n" +
                "          e.updatedDt = current_timestamp()\n" +
                "        WHERE \n" +
                "              e.imageId = :imageId \n" +
                "          AND e.type = :exifType"),
        @NamedQuery(name = "availableImageCategoriesAdmin",
        query = "SELECT \n" +
                "          c.id AS id, \n" +
                "          c.name AS name \n" +
                "        FROM \n" +
                "          Categories c \n" +
                "        WHERE \n" +
                "              c.type.name = 'Images'\n" +
                "          AND NOT EXISTS (FROM \n" +
                "                            ImageCategories ic \n" +
                "                          WHERE \n" +
                "                                ic.imageId = :imageId\n" +
                "                            AND ic.categoryId = c.id)"),
        @NamedQuery(name = "availableImageCategories",
        query = " SELECT \n" +
                "          c.id AS id, \n" +
                "          c.name AS name \n" +
                "        FROM \n" +
                "          CategoryRoles cr \n" +
                "        INNER JOIN \n" +
                "          cr.category c \n" +
                "        WHERE \n" +
                "              cr.roleId IN (:roleIds) AND cr.canChange = 1 \n" +
                "          AND NOT EXISTS (FROM\n" +
                "                            ImageCategories ic \n" +
                "                          WHERE\n" +
                "                                ic.imageId = :imageId \n" +
                "                            AND ic.categoryId = cr.categoryId) \n" +
                "          AND c.type.name = 'Images'"),
        @NamedQuery(name = "availableKeywords",
        query = "SELECT \n" +
                "          k.keywordNm \n" +
                "        FROM \n" +
                "          Keywords k \n" +
                "        WHERE  \n" +
                "              NOT EXISTS (FROM \n" +
                "                            ImageKeywords ik \n" +
                "                          WHERE \n" +
                "                                ik.imageId = :imageId \n" +
                "                            AND ik.keywordId = k.id) \n" +
                "        ORDER BY \n" +
                "          k.keywordNm"),
        @NamedQuery(name = "keywordsUsedByImages",
        query = " SELECT DISTINCT\n" +
                "          k.id AS id, \n" +
                "          k.keywordNm AS keywordNm\n" +
                "        FROM\n" +
                "          ImageKeywords ik\n" +
                "        INNER JOIN\n" +
                "          ik.keyword k\n" +
                "        ORDER BY\n" +
                "          k.keywordNm"),

})
public class Images implements Serializable {
    public static final short STATUS_UPLOADED = 0;
    public static final short STATUS_ACTIVE = 1;
    public static final short STATUS_ARCHIVED = 2;
    private static final long serialVersionUID = -1831641612874730366L;
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "image_nm", length = 255, nullable = false)
    private String imageNm = "";

    @Column(name = "format", nullable = false)
    private int format;

    @Column(name = "width", nullable = false)
    private int width;

    @Column(name = "height", nullable = false)
    private int height;

    @Column(name = "file_size", nullable = false)
    private int fileSize;

    @Column(name = "uploaded_by", length = 130, nullable = false)
    private String uploadedBy = "";

    @Column(name = "users_id", nullable = false)
    private int usersId;

    @Column(name = "status", nullable = false)
    private short status = STATUS_UPLOADED;

    @Column(name = "created_dt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt = new Date();

    @Column(name = "updated_dt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDt = new Date();

    @Column(name = "license_dt")
    @Temporal(TemporalType.DATE)
    private Date licenseDt;

    @Column(name = "license_end_dt")
    @Temporal(TemporalType.DATE)
    private Date licenseEndDt;

    @Column(name = "alt_text")
    private String altText;

    @OneToMany
    @JoinTable(
            name = "archive_image_categories",
            joinColumns = @JoinColumn(name = "image_id", nullable = false, insertable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "category_id", nullable = false, insertable = false, updatable = false)
    )
    @OrderBy("name")
    private List<Categories> categories;

    @OneToMany
    @JoinTable(
            name = "archive_image_keywords",
            joinColumns = @JoinColumn(name = "image_id", nullable = false, insertable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "keyword_id", nullable = false, insertable = false, updatable = false)
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
        return String.format("Images[id: %d, imageNm: %s, uploadedBy: %s]",
                id, imageNm, uploadedBy);
    }
}
