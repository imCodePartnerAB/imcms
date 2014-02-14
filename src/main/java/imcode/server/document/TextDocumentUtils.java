package imcode.server.document;

import imcode.server.document.textdocument.*;

import java.util.List;
import java.util.Objects;

public final class TextDocumentUtils {

    private TextDocumentUtils() {}

    public static TextDomainObject createDefaultText() {
        return new TextDomainObject();
    }

    public static ImageDomainObject createDefaultImage() {
        return new ImageDomainObject();
    }

    /** Inits text docs images sources. */
    public static List<ImageDomainObject> initImagesSources(List<ImageDomainObject> images) {
        for (ImageDomainObject image : images) {
            initImageSource(image);
        }

        return images;
    }

    /** Inits text doc's image source. */
    public static ImageDomainObject initImageSource(ImageDomainObject image) {
        String url = image.getUrl();
        Integer type = image.getType();

        Objects.requireNonNull(url);
        Objects.requireNonNull(type);

        image.setSource(createImageSource(image, url.trim(), type));

        return image;
    }

    private static ImageSource createImageSource(ImageDomainObject image, String url, int type) {
        switch (type) {
            case ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT:
                throw new IllegalStateException(
                        String.format("Illegal image source type - IMAGE_TYPE_ID__FILE_DOCUMENT. Image: %s", image)
                );

            case ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH:
                return new ImagesPathRelativePathImageSource(url);

            case ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE:
                return new ImageArchiveImageSource(url);

            default:
                return new NullImageSource();
        }
    }
}
