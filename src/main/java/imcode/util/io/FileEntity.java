package imcode.util.io;

import java.io.File;

/**
 * Created by Shadowgun on 30.03.2015.
 */
public class FileEntity {
    private String previewImageUrl;
    private File file;

    public FileEntity(File file) {
        this.file = file;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    public File getFile() {
        return file;
    }
}
