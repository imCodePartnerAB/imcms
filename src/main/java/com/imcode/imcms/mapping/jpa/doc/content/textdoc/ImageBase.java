package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.content.VersionedDocI18nContent;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.util.image.Format;
import imcode.util.image.Resize;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class ImageBase extends VersionedDocI18nContent {

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
    private String name = "";

    private Integer type;

    @Column(name = "format", nullable = false)
    private int format;

    private ImageCropRegion cropRegion = new ImageCropRegion();

    @Column(name = "rotate_angle", nullable = false)
    private int rotateAngle;

    @Column(name = "gen_file")
    private String generatedFilename;

    private LoopEntryRef loopEntryRef;

    private int resize;

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

    public LoopEntryRef getLoopEntryRef() {
        return loopEntryRef;
    }

    public void setLoopEntryRef(LoopEntryRef loopEntryRef) {
        this.loopEntryRef = loopEntryRef;
    }

    public Format getFormat() {
        return Format.findFormat(format);
    }

    public void setFormat(Format format) {
        this.format = format != null ? format.getOrdinal() : 0;
    }

    public ImageCropRegion getCropRegion() {
        return cropRegion;
    }

    public void setCropRegion(ImageCropRegion cropRegion) {
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
