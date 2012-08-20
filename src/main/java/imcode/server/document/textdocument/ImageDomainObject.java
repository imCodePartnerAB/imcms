package imcode.server.document.textdocument;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.util.ImageSize;
import imcode.server.Imcms;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity(name = "Image")
@Table(name = "imcms_text_doc_images")
public class ImageDomainObject implements Serializable, Cloneable {

    public static final class Builder {
        private ImageDomainObject imageDomainObject;

        public Builder() {
            imageDomainObject = new ImageDomainObject();
        }

        public Builder(ImageDomainObject imageDomainObject) {
            this.imageDomainObject = imageDomainObject.clone();
        }

        public ImageDomainObject build() {
            return imageDomainObject.clone();
        }

        public Builder id(Long id) {
            imageDomainObject.id = id;
            return this;
        }

        public Builder docRef(DocRef docRef) {
            imageDomainObject.docRef = docRef;
            return this;
        }

        public Builder no(Integer no) {
            imageDomainObject.no = no.toString();
            return this;
        }

        public Builder language(I18nLanguage language) {
            imageDomainObject.language = language;
            return this;
        }

        public Builder contentRef(ContentRef contentRef) {
            imageDomainObject.contentRef = contentRef;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ImageDomainObject imageDomainObject) {
        return new Builder(imageDomainObject);
    }

    public static final int IMAGE_NAME_LENGTH = 40;

    private static final int GEN_FILE_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private ImageSource source = new NullImageSource();

    private DocRef docRef;

    private ContentRef contentRef;

    /**
     * Image order no in a text doc.
     */
    private String no = "";


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

    @Column(name = "image_name", nullable = false, length = IMAGE_NAME_LENGTH)
    private String imageName = "";

    private Integer type;

    @Column(name = "archive_image_id")
    private Long archiveImageId;

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

    @Column(name = "gen_file", length = GEN_FILE_LENGTH)
    private String generatedFilename;

    /**
     * i18n support
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private I18nLanguage language;

    public String getName() {
        return no;
    }

    public ImageSize getDisplayImageSize() {
        ImageSize realImageSize = getRealImageSize();
        CropRegion region = getCropRegion();

        int realWidth;
        int realHeight;

        if (region.isValid()) {
            realWidth = region.getWidth();
            realHeight = region.getHeight();
        } else {
            realWidth = realImageSize.getWidth();
            realHeight = realImageSize.getHeight();
        }

        int wantedWidth = getWidth();
        int wantedHeight = getHeight();
        int displayWidth;
        int displayHeight;

        if (wantedWidth > 0 || wantedHeight > 0) {
            float ratio = realWidth / (float) realHeight;

            if (wantedWidth > 0 && wantedHeight > 0) {
                displayWidth = wantedWidth;
                displayHeight = wantedHeight;
            } else if (wantedWidth > 0) {
                displayWidth = wantedWidth;
                displayHeight = Math.round(wantedWidth / ratio);
            } else {
                displayHeight = wantedHeight;
                displayWidth = Math.round(wantedHeight * ratio);
            }

        } else {
            displayWidth = realWidth;
            displayHeight = realHeight;
        }

        return new ImageSize(displayWidth, displayHeight);
    }

    public ImageSize getRealImageSize() {
        ImageSize imageSize = new ImageSize(0, 0);
        if (!isEmpty()) {
            try {
                imageSize = source.getImageSize();
            } catch (IOException ignored) {
            }
        }
        return imageSize;
    }

    public ImageInfo getImageInfo() {
        if (!isEmpty()) {
            try {
                return source.getImageInfo();
            } catch (IOException ex) {
            }
        }

        return null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBorder() {
        return border;
    }

    public String getAlign() {
        return align;
    }

    public String getAlternateText() {
        return alternateText;
    }

    public String getLowResolutionUrl() {
        return lowResolutionUrl;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public int getHorizontalSpace() {
        return horizontalSpace;
    }

    public String getTarget() {
        return target;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setName(String image_name) {
        this.no = image_name;
    }

    public void setWidth(int image_width) {
        this.width = image_width;
    }

    public void setHeight(int image_height) {
        this.height = image_height;
    }

    public void setBorder(int image_border) {
        this.border = image_border;
    }

    public void setAlign(String image_align) {
        this.align = image_align;
    }

    public void setAlternateText(String alt_text) {
        this.alternateText = alt_text;
    }

    public void setLowResolutionUrl(String low_scr) {
        this.lowResolutionUrl = low_scr;
    }

    public void setVerticalSpace(int v_space) {
        this.verticalSpace = v_space;
    }

    public void setHorizontalSpace(int h_space) {
        this.horizontalSpace = h_space;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setLinkUrl(String image_ref_link) {
        this.linkUrl = image_ref_link;
    }

    public void setSourceAndClearSize(ImageSource source) {
        setSource(source);
        setWidth(0);
        setHeight(0);
    }

    public void setSource(ImageSource source) {
        if (null == source) {
            throw new NullArgumentException("source");
        }
        this.source = source;
    }

    public boolean isEmpty() {
        return source.isEmpty();
    }

    public String getUrlPath(String contextPath) {
        String urlPathRelativeToContextPath = getUrlPathRelativeToContextPath();
        if (StringUtils.isBlank(urlPathRelativeToContextPath)) {
            return "";
        }
        return contextPath + urlPathRelativeToContextPath;
    }

    public String getUrlPathRelativeToContextPath() {
        return source.getUrlPathRelativeToContextPath();
    }

    public File getGeneratedFile() {
        File basePath = Imcms.getServices().getConfig().getImagePath();

        return new File(basePath, "generated/" + getGeneratedFilename());
    }

    public String getGeneratedUrlPath(String contextPath) {
        return contextPath + getGeneratedUrlPathRelativeToContextPath();
    }

    public String getGeneratedUrlPathRelativeToContextPath() {
        String imagesUrl = Imcms.getServices().getConfig().getImageUrl();

        return imagesUrl + "generated/" + getGeneratedFilename();
    }

    public String getGeneratedFilename() {
        return generatedFilename;
    }

    public void setGeneratedFilename(String generatedFilename) {
        this.generatedFilename = generatedFilename;
    }

    public void generateFilename() {
        String suffix = "_" + UUID.randomUUID().toString();

        Format fmt = getFormat();
        if (fmt != null) {
            suffix += "." + fmt.getExtension();
        }

        int maxlength = GEN_FILE_LENGTH - suffix.length();

        String filename = source.getNameWithoutExt();

        if (filename.length() > maxlength) {
            filename = filename.substring(0, maxlength);
        }

        generatedFilename = filename + suffix;
    }

    public long getSize() {
        if (isEmpty()) {
            return 0;
        }
        try {
            return source.getInputStreamSource().getSize();
        } catch (IOException e) {
            return 0;
        }
    }

    public ImageSource getSource() {
        if (isEmpty()) {
            return new NullImageSource();
        }
        return source;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImageDomainObject)) {
            return false;
        }

        final ImageDomainObject o = (ImageDomainObject) obj;
        return new EqualsBuilder().append(source.toStorageString(), o.getSource().toStorageString())
                .append(no, o.getName())
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
                .append(language, o.getLanguage())
                .append(format, o.format)
                .append(cropX1, o.cropX1)
                .append(cropY1, o.cropY1)
                .append(cropX2, o.cropX2)
                .append(cropY2, o.cropY2)
                .append(rotateAngle, o.rotateAngle)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(source.toStorageString())
                .append(no).append(width).append(height)
                .append(border).append(align).append(alternateText)
                .append(lowResolutionUrl).append(verticalSpace).append(horizontalSpace)
                .append(target).append(linkUrl)
                .append(language).append(format).append(rotateAngle)
                .append(cropX1).append(cropY1).append(cropX2).append(cropY2)
                .toHashCode();
    }

    public I18nLanguage getLanguage() {
        return language;
    }

    public void setLanguage(I18nLanguage language) {
        this.language = language;
    }

    public ImageDomainObject clone() {
        try {
            return (ImageDomainObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return image source type
     * @see ImageSource
     */
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Deprecated
    public Integer getIndex() {
        return no == null ? null : new Integer(no);
    }

    @Deprecated
    public void setIndex(Integer index) {
        if (index == null) {
            no = null;
        } else {
            no = index.toString();
        }
    }


    public Integer getNo() {
        return getIndex();
    }

    public void setNo(Integer no) {
        setIndex(no);
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Long getArchiveImageId() {
        return archiveImageId;
    }

    public void setArchiveImageId(Long archiveImageId) {
        this.archiveImageId = archiveImageId;
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


    public static class CropRegion implements Serializable {
        private static final long serialVersionUID = -586488435877347784L;

        private int cropX1;
        private int cropY1;
        private int cropX2;
        private int cropY2;

        private boolean valid;


        public CropRegion() {
            cropX1 = -1;
            cropY1 = -1;
            cropX2 = -1;
            cropY2 = -1;
        }

        public CropRegion(int cropX1, int cropY1, int cropX2, int cropY2) {
            if (cropX1 > cropX2) {
                this.cropX1 = cropX2;
                this.cropX2 = cropX1;
            } else {
                this.cropX1 = cropX1;
                this.cropX2 = cropX2;
            }

            if (cropY1 > cropY2) {
                this.cropY1 = cropY2;
                this.cropY2 = cropY1;
            } else {
                this.cropY1 = cropY1;
                this.cropY2 = cropY2;
            }

            updateValid();
        }

        public void updateValid() {
            valid = (cropX1 >= 0 && cropY1 >= 0 && cropX2 >= 0 && cropY2 >= 0
                    && cropX1 != cropX2 && cropY1 != cropY2);
        }

        public boolean isValid() {
            return valid;
        }

        public int getCropX1() {
            return cropX1;
        }

        public void setCropX1(int cropX1) {
            this.cropX1 = cropX1;
        }

        public int getCropY1() {
            return cropY1;
        }

        public void setCropY1(int cropY1) {
            this.cropY1 = cropY1;
        }

        public int getCropX2() {
            return cropX2;
        }

        public void setCropX2(int cropX2) {
            this.cropX2 = cropX2;
        }

        public int getCropY2() {
            return cropY2;
        }

        public void setCropY2(int cropY2) {
            this.cropY2 = cropY2;
        }

        public int getWidth() {
            return isValid() ? cropX2 - cropX1 : 0;
        }

        public int getHeight() {
            return isValid() ? cropY2 - cropY1 : 0;
        }


        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + cropX1;
            result = prime * result + cropY1;
            result = prime * result + cropX2;
            result = prime * result + cropY2;

            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            CropRegion other = (CropRegion) obj;
            if (cropX1 != other.cropX1 || cropY1 != other.cropY1 ||
                    cropX2 != other.cropX2 || cropY2 != other.cropY2) {
                return false;
            }

            return true;
        }

        public boolean isSame(CropRegion other) {
            return (!valid && !other.valid) ||
                    valid && other.valid && equals(other);
        }
    }

    public enum RotateDirection {
        NORTH(0, -90, 90),
        EAST(90, 0, 180),
        SOUTH(180, 90, -90),
        WEST(-90, 180, 0);

        private static final Map<Integer, RotateDirection> ANGLE_MAP =
                new HashMap<Integer, RotateDirection>(RotateDirection.values().length);

        static {
            for (RotateDirection direction : RotateDirection.values()) {
                ANGLE_MAP.put(direction.getAngle(), direction);
            }
        }

        private final int angle;
        private final int leftAngle;
        private final int rightAngle;

        private RotateDirection(int angle, int leftAngle, int rightAngle) {
            this.angle = angle;
            this.leftAngle = leftAngle;
            this.rightAngle = rightAngle;
        }

        public int getAngle() {
            return angle;
        }

        public RotateDirection getLeftDirection() {
            return getByAngle(leftAngle);
        }

        public RotateDirection getRightDirection() {
            return getByAngle(rightAngle);
        }

        public static RotateDirection getByAngle(int angle) {
            return ANGLE_MAP.get(angle);
        }

        public static RotateDirection getByAngleDefaultIfNull(int angle) {
            RotateDirection direction = getByAngle(angle);

            return (direction != null ? direction : RotateDirection.NORTH);
        }
    }

    public DocRef getDocRef() {
        return docRef;
    }

    public void setDocRef(DocRef docRef) {
        this.docRef = docRef;
    }

    public ContentRef getContentRef() {
        return contentRef;
    }

    public void setContentRef(ContentRef contentRef) {
        this.contentRef = contentRef;
    }

    @Override
    public String toString() {
        return "ImageDomainObject{" +
                "no='" + no + '\'' +
                ", id=" + id +
                ", contentRef=" + contentRef +
                ", docRef=" + docRef +
                '}';
    }
}
