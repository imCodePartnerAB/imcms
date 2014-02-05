package com.imcode.imcms.mapping.orm;

import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.NullImageSource;
import imcode.util.image.Format;
import imcode.util.image.Resize;

import javax.persistence.*;

@MappedSuperclass
public class TextDocImageBase extends DocVersionedI18nContent {

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

    private TextDocLoopItemRef contentLoopRef;

    @OneToOne
    @JoinColumn(name = "language_id")
    private DocLanguage language;

    private volatile int resize;


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

    public TextDocLoopItemRef getContentLoopRef() {
        return contentLoopRef;
    }

    public void setContentLoopRef(TextDocLoopItemRef contentLoopRef) {
        this.contentLoopRef = contentLoopRef;
    }

    public DocLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DocLanguage language) {
        this.language = language;
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
}
