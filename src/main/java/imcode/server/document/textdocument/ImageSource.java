package imcode.server.document.textdocument;

import com.imcode.util.ImageSize;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;

import java.io.IOException;
import java.util.Date;

public abstract class ImageSource extends AbstractFileSource {
    public static final int IMAGE_TYPE_ID__NULL = -1;
    public static final int IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH = 0;
    public static final int IMAGE_TYPE_ID__FILE_DOCUMENT = 1;
    public static final int IMAGE_TYPE_ID__IMAGE_ARCHIVE = 2;
    private static final long serialVersionUID = -8968488500648667313L;

    private ImageInfo cachedImageInfo;
    private Date cachedImageInfoTime;

    ImageSize getImageSize() throws IOException {
        ImageInfo imageInfo = getImageInfo();
        if (imageInfo != null) {
            return new ImageSize(imageInfo.getWidth(), imageInfo.getHeight());
        }

        return new ImageSize(0, 0);
    }


    ImageInfo getImageInfo() throws IOException {
        if (getInputStreamSource().getSize() > 0) {
            Date modifiedDatetime = getModifiedDatetime();
            if (cachedImageInfoTime == null || modifiedDatetime.after(cachedImageInfoTime)) {
                cachedImageInfo = getNonCachedImageInfo();
                cachedImageInfoTime = modifiedDatetime;
            }

            return cachedImageInfo;
        }

        return null;
    }

    ImageInfo getNonCachedImageInfo() throws IOException {
        return ImageOp.getImageInfo(ImcmsImageUtils.imageMagickPath, getInputStreamSource().getInputStream());
    }
}

