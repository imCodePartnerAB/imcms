package imcode.server.document.textdocument;

import com.fasterxml.jackson.annotation.JsonIgnore;
import imcode.util.image.ImageInfo;
import imcode.util.io.EmptyInputStreamSource;
import imcode.util.io.InputStreamSource;
import lombok.EqualsAndHashCode;

import java.util.Date;

public class NullImageSource extends ImageSource {
    private static final long serialVersionUID = -7330157356707491656L;

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

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
