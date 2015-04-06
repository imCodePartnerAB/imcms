package imcode.server.document.textdocument;

import com.imcode.util.ImageSize;

/**
 * Created by Shadowgun on 02.04.2015.
 */
public class DocumentImageSource extends ImagesPathRelativePathImageSource {
    private ImageSize displayImageSize;

    public DocumentImageSource(String path, ImageSize displayImageSize) {
        super(path);
        this.displayImageSize = displayImageSize;
    }

    public ImageSize getDisplaySize() {
        return displayImageSize;
    }
}
