package com.imcode.imcms.mapping.orm;

import imcode.server.document.textdocument.*;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.server.user.UserDomainObject;
import imcode.util.image.Format;
import imcode.util.image.Resize;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
    private Integer id;

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

    @Column(name = "image_name")
    private volatile String name = "";

    private Integer type;

    @Column(name = "format", nullable = false)
    private int format;

    private volatile CropRegion cropRegion = new CropRegion();

    @Column(name = "rotate_angle", nullable = false)
    private int rotateAngle;

    @Column(name = "gen_file")
    private String generatedFilename;

    private ContentLoopRef contentLoopRef;

    private DocRef docRef;

    /**
     * i18n support
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private DocumentLanguage language;


    @Column(name = "user_id")
    private Integer userId;


    @Column(name = "modified_dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDt;

    private volatile int resize;

    public ImageHistory() {
    }

    public ImageHistory(ImageDomainObject image, UserDomainObject user) {
        setDocRef(image.getDocRef());
        setNo(image.getNo());

        setWidth(image.getWidth());
        setHeight(image.getHeight());
        setBorder(image.getBorder());
        setAlign(image.getAlign());
        setAlternateText(image.getAlternateText());
        setLowResolutionUrl(image.getLowResolutionUrl());
        setVerticalSpace(image.getVerticalSpace());
        setHorizontalSpace(image.getHorizontalSpace());
        setTarget(image.getTarget());
        setLinkUrl(image.getLinkUrl());
        setImageUrl(image.getUrl());
        setType(image.getType());
        setResize(image.getResize());

        setLanguage(image.getLanguage());
        setContentLoopRef(image.getContentLoopRef());
        setUserId(user.getId());
        setModifiedDt(new Date());
        setFormat(image.getFormat());
        setCropRegion(image.getCropRegion());
        setRotateDirection(image.getRotateDirection());
        setGeneratedFilename(image.getGeneratedFilename());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public ContentLoopRef getContentLoopRef() {
        return contentLoopRef;
    }

    public void setContentLoopRef(ContentLoopRef contentLoopRef) {
        this.contentLoopRef = contentLoopRef;
    }

    public DocumentLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DocumentLanguage language) {
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
        this.format = format != null ? format.getOrdinal() : 0;
    }

    public CropRegion getCropRegion() {
        return cropRegion;
    }

    public void setCropRegion(CropRegion cropRegion) {
        this.cropRegion = cropRegion;
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

    public DocRef getDocRef() {
        return docRef;
    }

    public void setDocRef(DocRef docRef) {
        this.docRef = docRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Resize getResize() {
        return Resize.getByOrdinal(resize);
    }

    public void setResize(Resize resize) {
        this.resize = resize == null ? 0 : resize.getOrdinal();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImageHistory)) {
            return false;
        }
        final ImageHistory o = (ImageHistory) obj;
        return new EqualsBuilder()
                .append(id, o.id)
                .append(source.toStorageString(), o.getSource().toStorageString())
                .append(docRef, o.getDocRef())
                .append(contentLoopRef, o.getContentLoopRef())
                .append(no, o.getNo())
                .append(width, o.getWidth())
                .append(height, o.getHeight())
                .append(border, o.getBorder())
                .append(align, o.getAlign())
                .append(alternateText, o.getAlternateText())
                .append(lowResolutionUrl, o.getLowResolutionUrl())
                .append(verticalSpace, o.getVerticalSpace())
                .append(horizontalSpace, o.getHorizontalSpace())
                .append(target, o.getTarget())
                .append(linkUrl, o.getLinkUrl())
                .append(name, o.getName())
                .append(cropRegion, o.getCropRegion())
                .append(language, o.getLanguage())
                .append(getFormat(), o.getFormat())
                .append(getRotateDirection(), o.getRotateDirection())
                .append(getResize(), o.getResize())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(source.toStorageString())
                .append(docRef)
                .append(contentLoopRef)
                .append(no)
                .append(width)
                .append(height)
                .append(border)
                .append(align)
                .append(alternateText)
                .append(lowResolutionUrl)
                .append(verticalSpace)
                .append(horizontalSpace)
                .append(target)
                .append(linkUrl)
                .append(name)
                .append(cropRegion)
                .append(language)
                .append(getFormat())
                .append(getRotateDirection())
                .append(getResize())
                .toHashCode();
    }
}
