package com.imcode.imcms.api;

import imcode.server.document.textdocument.*;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.server.user.UserDomainObject;
import imcode.util.image.Format;

import javax.persistence.*;
import java.util.Date;

/**
 *
 */
@Entity
@Table(name = "imcms_text_doc_images_history")
public class ImageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private ImageSource source = new NullImageSource();

    private Integer no;

    private int width;
    private int height;
    private int border;
    private String align = "";

    @Column(name = "alt_text")
    private String alternateText = "";

    @Column(name = "low_scr")
    private String lowResolutionUrl = "";

    @Column(name = "v_space")
    private int verticalSpace;

    @Column(name = "h_space")
    private int horizontalSpace;
    private String target = "";

    @Column(name = "linkurl")
    private String linkUrl = "";

    @Column(name = "imgurl")
    private String imageUrl = "";

    private Integer type;

    @Column(name = "format", nullable = false)
    private short format;

    @Column(name = "crop_x1", nullable = false)
    private int cropX1;

    @Column(name = "crop_y1", nullable = false)
    private int cropY1;

    @Column(name = "crop_x2", nullable = false)
    private int cropX2;

    @Column(name = "crop_y2", nullable = false)
    private int cropY2;

    @Column(name = "rotate_angle", nullable = false)
    private short rotateAngle;

    @Column(name = "gen_file")
    private String generatedFilename;

    private ContentLoopIdentity contentLoopIdentity;

    private DocIdentity docIdentity;

    /**
     * i18n support
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private I18nLanguage language;


    @Column(name = "user_id")
    private Integer userId;


    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    public ImageHistory() {
    }

    public ImageHistory(ImageDomainObject imageDO, UserDomainObject user) {
        setDocIdentity(imageDO.getDocIdentity());
        setNo(imageDO.getNo());

        setWidth(imageDO.getWidth());
        setHeight(imageDO.getHeight());
        setBorder(imageDO.getBorder());
        setAlign(imageDO.getAlign());
        setAlternateText(imageDO.getAlternateText());
        setLowResolutionUrl(imageDO.getLowResolutionUrl());
        setVerticalSpace(imageDO.getVerticalSpace());
        setHorizontalSpace(imageDO.getHorizontalSpace());
        setTarget(imageDO.getTarget());
        setLinkUrl(imageDO.getLinkUrl());
        setImageUrl(imageDO.getImageUrl());
        setType(imageDO.getType());

        setLanguage(imageDO.getLanguage());
        setContentRef(imageDO.getContentLoopIdentity());
        setUserId(user.getId());
        setModifiedDt(new Date());
        setFormat(imageDO.getFormat());
        setCropRegion(imageDO.getCropRegion());
        setRotateDirection(imageDO.getRotateDirection());
        setGeneratedFilename(imageDO.getGeneratedFilename());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ImageSource getSource() {
        return source;
    }

    public void setSource(ImageSource source) {
        this.source = source;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getAlternateText() {
        return alternateText;
    }

    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

    public String getLowResolutionUrl() {
        return lowResolutionUrl;
    }

    public void setLowResolutionUrl(String lowResolutionUrl) {
        this.lowResolutionUrl = lowResolutionUrl;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
    }

    public int getHorizontalSpace() {
        return horizontalSpace;
    }

    public void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public ContentLoopIdentity getContentRef() {
        return contentLoopIdentity;
    }

    public void setContentRef(ContentLoopIdentity contentRef) {
        this.contentLoopIdentity = contentRef;
    }

    public I18nLanguage getLanguage() {
        return language;
    }

    public void setLanguage(I18nLanguage language) {
        this.language = language;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getModifiedDt() {
        return modifiedDt;
    }

    public void setModifiedDt(Date modifiedDt) {
        this.modifiedDt = modifiedDt;
    }

    public Format getFormat() {
        return Format.findFormat(format);
    }

    public void setFormat(Format format) {
        this.format = (short) (format != null ? format.getOrdinal() : 0);
    }

    public CropRegion getCropRegion() {
        return new CropRegion(cropX1, cropY1, cropX2, cropY2);
    }

    public void setCropRegion(CropRegion region) {
        if (region.isValid()) {
            cropX1 = region.getCropX1();
            cropY1 = region.getCropY1();
            cropX2 = region.getCropX2();
            cropY2 = region.getCropY2();
        } else {
            cropX1 = -1;
            cropY1 = -1;
            cropX2 = -1;
            cropY2 = -1;
        }
    }

    public RotateDirection getRotateDirection() {
        return RotateDirection.getByAngleDefaultIfNull(rotateAngle);
    }

    public void setRotateDirection(RotateDirection dir) {
        this.rotateAngle = (short) (dir != null ? dir.getAngle() : 0);
    }

    public String getGeneratedFilename() {
        return generatedFilename;
    }

    public void setGeneratedFilename(String generatedFilename) {
        this.generatedFilename = generatedFilename;
    }

    public DocIdentity getDocIdentity() {
        return docIdentity;
    }

    public void setDocIdentity(DocIdentity docIdentity) {
        this.docIdentity = docIdentity;
    }
}
