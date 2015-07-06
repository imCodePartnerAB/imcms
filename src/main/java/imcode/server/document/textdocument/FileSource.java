package imcode.server.document.textdocument;

import com.fasterxml.jackson.annotation.JsonIgnore;
import imcode.server.Imcms;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.InputStreamSource;

import java.io.File;
import java.util.Date;

/**
 * Created by Shadowgun on 30.03.2015.
 */
public class FileSource extends AbstractFileSource {
    public final File file;

    public FileSource(File file) {
        this.file = file;
    }

    @JsonIgnore
    @Override
    public InputStreamSource getInputStreamSource() {
        return new FileInputStreamSource(file);
    }

    @Override
    public String getUrlPathRelativeToContextPath() {
        return file.getAbsolutePath().replace(Imcms.getPath().getAbsolutePath(), "");
    }

    @Override
    public String toStorageString() {
        return null;
    }

    @Override
    public int getTypeId() {
        return 0;
    }

    @Override
    public Date getModifiedDatetime() {
        return new Date(file.lastModified());
    }

    @Override
    public String getName() {
        return file.getName();
    }
}
