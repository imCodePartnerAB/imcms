package imcode.server.document.textdocument;

import imcode.util.io.InputStreamSource;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Shadowgun on 30.03.2015.
 */
public abstract class AbstractFileSource implements Serializable {
    public abstract InputStreamSource getInputStreamSource();

    public abstract String getUrlPathRelativeToContextPath();

    public abstract String toStorageString();

    public abstract int getTypeId();

    public abstract Date getModifiedDatetime();

    public boolean isEmpty() {
        try {
            return getInputStreamSource().getSize() <= 0;
        } catch (IOException e) {
            return true;
        }
    }

    public String getNameWithoutExt() {
        String name = getName();

        int periodIndex = name.lastIndexOf('.');
        if (periodIndex != -1) {
            name = name.substring(0, periodIndex);
        }

        return name;
    }

    public abstract String getName();

    public String getExtension() {
        return FilenameUtils.getExtension(getName());
    }

}
