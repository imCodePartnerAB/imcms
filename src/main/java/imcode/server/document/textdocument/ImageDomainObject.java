package imcode.server.document.textdocument;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.*;

import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocRef;
import imcode.util.image.Resize;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
            imageDomainObject.no = no;
            return this;
        }

        public Builder language(DocumentLanguage language) {
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
    private volatile Long id;

    @Transient
    private volatile ImageSource source = new NullImageSource();

    private volatile DocRef docRef;

    private volatile ContentRef contentRef;

    private volatile Integer no;

    private volatile int width;

    private volatile int height;

    private volatile int border;

    private volatile String align = "";

    @Column(name = "alt_text")
    private volatile String alternateText = "";

    @Column(name = "low_scr")
    private volatile String lowResolutionUrl = "";

    @Column(name = "v_space")
    private volatile int verticalSpace;

    @Column(name = "h_space")
    private volatile int horizontalSpace;
    private volatile String target = "";

    @Column(name = "linkurl")
    private volatile String linkUrl = "";

    @Column(name = "image_name", nullable = false, length = IMAGE_NAME_LENGTH)
    private volatile String name = "";

    // Source type id
    private volatile Integer type = source.getTypeId();

    // Source storage string
    @Column(name = "imgurl")
    private volatile String url = source.toStorageString();

    @Column(name = "archive_image_id")
    private volatile Long archiveImageId;

    @Column(name = "rotate_angle", nullable = false)
    private volatile int rotateAngle = RotateDirection.NORTH.getAngle();

    @Column(name = "gen_file", length = GEN_FILE_LENGTH)
    private volatile String generatedFilename;

    @Column(nullable = false)
    private volatile int resize;

    @Column(nullable = false)
    private volatile int format;

    private volatile CropRegion cropRegion = new CropRegion();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private volatile DocumentLanguage language;

    public String getName() {
        return name;
    }

    public ImageSize getDisplayImageSize() {
        ImageSize realImageSize = getRealImageSize();

        int wantedWidth = getWidth();
        int wantedHeight = getHeight();
        if (0 == wantedWidth && 0 != wantedHeight && 0 != realImageSize.getHeight()) {
            wantedWidth = (int) (realImageSize.getWidth() * ((double) wantedHeight / realImageSize.getHeight()));
        } else if (0 == wantedHeight && 0 != wantedWidth && 0 != realImageSize.getWidth()) {
            wantedHeight = (int) (realImageSize.getHeight() * ((double) wantedWidth / realImageSize.getWidth()));
        } else if (0 == wantedWidth && 0 == wantedHeight) {
            wantedWidth = realImageSize.getWidth();
            wantedHeight = realImageSize.getHeight();
        }
        return new ImageSize(wantedWidth, wantedHeight);
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public DocumentLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DocumentLanguage language) {
        this.language = language;
    }

    public Integer getIndex() {
        return getNo();
    }

    public void setIndex(Integer index) {
        setNo(index);
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

    public RotateDirection getRotateDirection() {
        return RotateDirection.getByAngle(rotateAngle);
    }

    public void setRotateDirection(RotateDirection rotateDirection) {
        this.rotateAngle = rotateDirection.getAngle();
    }

    public void setName(String image_name) {
        this.name = image_name;
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
        this.format = format != null ? format.getOrdinal() : 0;
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
        this.type = source.getTypeId();
        this.url = source.toStorageString();
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

        String[][] specialCharacterReplacements = {
                {"\u00e5", "a"},// å
                {"\u00c5", "A"},
                {"\u00e4", "a"},// ä
                {"\u00c4", "A"},
                {"\u00f6", "o"},// ö
                {"\u00d6", "O"},
                {"\u00e9", "e"},// é
                {"\u00c9", "E"},
                {"\u00f8", "o"},// ø
                {"\u00d8", "O"},
                {"\u00e6", "ae"},// æ
                {"\u00c6", "AE"},
                {"\u0020", "_"} // space
        };
        for (String[] replacement : specialCharacterReplacements) {
            filename = filename.replace(replacement[0], replacement[1]);
        }

        generatedFilename = filename + suffix;
    }

    public Resize getResize() {
        return Resize.getByOrdinal(resize);
    }

    public void setResize(Resize resize) {
        this.resize = resize == null ? 0 : resize.getOrdinal();
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

    public CropRegion getCropRegion() {
        return cropRegion;
    }

    public void setCropRegion(CropRegion cropRegion) {
        this.cropRegion = cropRegion;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImageDomainObject)) {
            return false;
        }
        final ImageDomainObject o = (ImageDomainObject) obj;
        CropRegion otherCropRegion = o.getCropRegion();
        return new EqualsBuilder().append(source.toStorageString(), o.getSource().toStorageString())
                .append(name, o.getName())
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
                .append(format, o.getFormat())
                .append(cropRegion.getCropX1(), otherCropRegion.getCropX1())
                .append(cropRegion.getCropY1(), otherCropRegion.getCropY1())
                .append(cropRegion.getCropX2(), otherCropRegion.getCropX2())
                .append(cropRegion.getCropY2(), otherCropRegion.getCropY2())
                .append(getRotateDirection(), o.getRotateDirection())
                .append(resize, o.getResize())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(source.toStorageString())
                .append(name).append(width).append(height)
                .append(border).append(align).append(alternateText)
                .append(lowResolutionUrl).append(verticalSpace).append(horizontalSpace)
                .append(target).append(linkUrl).append(format)
                .append(cropRegion.getCropX1()).append(cropRegion.getCropY1())
                .append(cropRegion.getCropX2()).append(cropRegion.getCropY2())
                .append(getRotateDirection())
                .append(resize)
                .toHashCode();
    }

    @Override
    public ImageDomainObject clone() {
        try {
            return (ImageDomainObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Embeddable
    public static class CropRegion implements Serializable {
        private static final long serialVersionUID = -586488435877347784L;

        @Column(name = "crop_x1", nullable = false)
        private volatile int cropX1;
        @Column(name = "crop_y1", nullable = false)
        private volatile int cropY1;
        @Column(name = "crop_x2", nullable = false)
        private volatile int cropX2;
        @Column(name = "crop_y2", nullable = false)
        private volatile int cropY2;

        @Transient
        private volatile boolean valid;


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

        @Override
        public String toString() {
            return String.format("%s(%d, %d, %d, %d)", CropRegion.class.getName(), cropX1, cropY1, cropX2, cropY2);
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

        public boolean isDefault() {
            return this == RotateDirection.NORTH;
        }

        public static RotateDirection getByAngleDefaultIfNull(int angle) {
            RotateDirection direction = getByAngle(angle);

            return (direction != null ? direction : RotateDirection.NORTH);
        }
    }
}
