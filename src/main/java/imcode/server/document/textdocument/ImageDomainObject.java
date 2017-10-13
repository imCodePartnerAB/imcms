package imcode.server.document.textdocument;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.imcode.imcms.domain.dto.ImageData;
import com.imcode.util.ImageSize;
import imcode.server.Imcms;
import imcode.util.image.Format;
import imcode.util.image.ImageInfo;
import imcode.util.image.Resize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDomainObject extends ImageData implements Cloneable {

    public static final int IMAGE_NAME_LENGTH = 40;
    public static final String ALIGN_NONE = "";
    public static final String ALIGN_TOP = "top";
    public static final String ALIGN_MIDDLE = "middle";
    public static final String ALIGN_BOTTOM = "bottom";
    public static final String ALIGN_LEFT = "left";
    public static final String ALIGN_RIGHT = "right";
    public static final String TARGET_TOP = "_top";
    public static final String TARGET_BLANK = "_blank";
    public static final String TARGET_PARENT = "_parent";
    public static final String TARGET_SELF = "_self";

    private static final int GEN_FILE_LENGTH = 255;
    private static final long serialVersionUID = -2674121677885916016L;
    private volatile ImageSource source = new NullImageSource();

    private volatile int border;

    private volatile String align = "";

    private volatile String alternateText = "";

    private volatile String lowResolutionUrl = "";

    private volatile int verticalSpace;

    private volatile int horizontalSpace;
    private volatile String target = "";

    private volatile String linkUrl = "";

    private volatile String name = "";

    private volatile Long archiveImageId;


    public ImageSize getDisplayImageSize() {
        ImageSize realImageSize = getRealImageSize();

        int w = realImageSize.getWidth();
        int h = realImageSize.getHeight();

        if (w == 0 && h == 0) {
            return realImageSize;
        }

        RotateDirection rotateDirection = getRotateDirection();
        Resize resize = getResize();

        if (rotateDirection != null && (rotateDirection == RotateDirection.EAST || rotateDirection == RotateDirection.WEST)) {
            int temp = h;
            h = w;
            w = temp;
        }

        if (cropRegion != null && cropRegion.isValid()) {
            w = cropRegion.getWidth();
            h = cropRegion.getHeight();
        }

        if (width > 0 || height > 0) {
            Resize res = resize;
            if (res == null) {
                res = (width > 0 && height > 0 ? Resize.FORCE : Resize.DEFAULT);
            }

            double ratio = w / (double) h;
            double targetRatio;

            int finalWidth;
            int finalHeight;

            switch (res) {
                case DEFAULT:
                    if (width > 0 && height > 0) {
                        targetRatio = width / (double) height;

                        if (ratio > targetRatio) {
                            finalWidth = width;
                            finalHeight = (int) Math.round(width / ratio);
                        } else {
                            finalWidth = (int) Math.round(height * ratio);
                            finalHeight = height;
                        }

                    } else if (width > 0) {
                        finalWidth = width;
                        finalHeight = (int) Math.round(width / ratio);
                    } else {
                        finalWidth = (int) Math.round(height * ratio);
                        finalHeight = height;
                    }
                    break;
                case FORCE:
                    if (width > 0 && height > 0) {
                        finalWidth = width;
                        finalHeight = height;
                    } else if (width > 0) {
                        finalWidth = width;
                        finalHeight = h;
                    } else {
                        finalWidth = w;
                        finalHeight = height;
                    }
                    break;
                case GREATER_THAN:
                    if (width > 0 && height > 0) {
                        if (w > width && h > height) {
                            targetRatio = width / (double) height;

                            if (ratio > targetRatio) {
                                finalWidth = width;
                                finalHeight = (int) Math.round(width / ratio);
                            } else {
                                finalWidth = (int) Math.round(height * ratio);
                                finalHeight = height;
                            }
                        } else if (w > width) {
                            finalWidth = width;
                            finalHeight = (int) Math.round(width / ratio);
                        } else if (h > height) {
                            finalWidth = (int) Math.round(height * ratio);
                            finalHeight = height;
                        } else {
                            finalWidth = w;
                            finalHeight = h;
                        }
                    } else if (width > 0) {
                        if (w > width) {
                            finalWidth = width;
                            finalHeight = (int) Math.round(width / ratio);
                        } else {
                            finalWidth = w;
                            finalHeight = h;
                        }
                    } else {
                        if (h > height) {
                            finalWidth = (int) Math.round(height * ratio);
                            finalHeight = height;
                        } else {
                            finalWidth = w;
                            finalHeight = h;
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Unhandled resize = " + res);
            }

            return new ImageSize(finalWidth, finalHeight);
        }

        return new ImageSize(w, h);
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

    public void setSourceAndClearSize(ImageSource source) {
        setSource(source);
        setWidth(0);
        setHeight(0);
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

    public String getGeneratedUrlPath(String contextPath) {
        return contextPath + getGeneratedUrlPathRelativeToContextPath();
    }

    private String getGeneratedUrlPathRelativeToContextPath() {
        String imagesUrl = Imcms.getServices().getConfig().getImageUrl();

        return imagesUrl + "generated/" + getGeneratedFilename();
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

        filename = Normalizer.normalize(filename, Normalizer.Form.NFC);

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

    @JsonDeserialize(as = ImagesPathRelativePathImageSource.class)
    public void setSource(ImageSource source) {
        this.source = Objects.requireNonNull(source, "image source can not be null");
    }

    @Override
    public ImageDomainObject clone() {
        try {
            return (ImageDomainObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
