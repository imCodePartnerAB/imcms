package imcode.server.document.textdocument;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import imcode.server.Imcms;
import imcode.util.ImcmsImageUtils;
import imcode.util.image.ImageInfo;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.InputStreamSource;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImagesPathRelativePathImageSource extends ImageSource {
    private String path;

    @JsonCreator
    public ImagesPathRelativePathImageSource(@JsonProperty("urlPathRelativeToContextPath") String path) {
        this.path = path.replace('\\', '/');
    }

    public static String getImagesUrlPath() {
        return Imcms.getServices().getConfig().getImageUrl();
    }

    @JsonIgnore
    public InputStreamSource getInputStreamSource() {
        return new FileInputStreamSource(getFile());
    }

    public File getFile() {
        return new File(ImcmsImageUtils.imagesPath, path);
    }

    public String getUrlPathRelativeToContextPath() {
        if (!isAbsolute()) {
            return getFile().exists() ? getImagesUrlPath() + path : "";
        }
        return getFile().exists() ? path : "";
    }

    private boolean isAbsolute() {
        return path.startsWith("/");
    }

    public String toStorageString() {
        return path;
    }

    public int getTypeId() {
        return ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH;
    }

    public Date getModifiedDatetime() {
        return new Date(getFile().lastModified());
    }

    @Override
    public String getName() {
        return getFile().getName();
    }

    @Override
    public ImageInfo getImageInfo() {
        try {
            return Optional.ofNullable(super.getImageInfo()).orElse(new ImageInfo());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
