package imcode.server.document.textdocument;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.util.ImageSize;
import imcode.util.image.Resize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageDomainObject extends ImageDTO implements Cloneable {

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

    private static final long serialVersionUID = -2674121677885916016L;

    private volatile int border;
    private volatile String align = "";
    private volatile String alternateText = "";
    private volatile String descriptionText = "";
    private volatile String lowResolutionUrl = "";
    private volatile String target = "";
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

    @Override
    public ImageDomainObject clone() {
        try {
            return (ImageDomainObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
