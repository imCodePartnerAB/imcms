package imcode.util.io;

import imcode.server.Imcms;
import imcode.util.image.ImageInfo;
import imcode.util.image.ImageOp;

import java.io.File;
import java.io.IOException;

/**
 * Created by Shadowgun on 30.03.2015.
 */
public class ImageFileEntity extends FileEntity {
    private final ImageInfo imageInfo;

    public ImageFileEntity(File file) {
        super(file);
        ImageInfo imageInfoTemp;
        try {
            imageInfoTemp = ImageOp.getImageInfo(Imcms.getServices().getConfig(), new FileInputStreamSource(file).getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            imageInfoTemp = null;
        }
        imageInfo = imageInfoTemp;
    }

    public ImageInfo getImageInfo() throws IOException {
        return imageInfo;
    }
}
