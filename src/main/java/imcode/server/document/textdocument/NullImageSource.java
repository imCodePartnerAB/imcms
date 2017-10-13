package imcode.server.document.textdocument;

import com.fasterxml.jackson.annotation.JsonIgnore;
import imcode.util.image.ImageInfo;
import imcode.util.io.EmptyInputStreamSource;
import imcode.util.io.InputStreamSource;

import java.util.Date;

public class NullImageSource extends ImageSource {
    @JsonIgnore
    public InputStreamSource getInputStreamSource() {
        return new EmptyInputStreamSource();
    }

    public String getUrlPathRelativeToContextPath() {
        return "";
    }

    public String toStorageString() {
        return "";
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    public int getTypeId() {
        return ImageSource.IMAGE_TYPE_ID__NULL;
    }

    public Date getModifiedDatetime() {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public ImageInfo getImageInfo() {
        return new ImageInfo();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof NullImageSource);
    }
}
