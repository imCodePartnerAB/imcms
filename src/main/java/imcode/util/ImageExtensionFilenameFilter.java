package imcode.util;

import java.io.File;
import java.io.FilenameFilter;

public class ImageExtensionFilenameFilter implements FilenameFilter {
    private static final String[] EXTENSIONS = {
        ".jpg",
        ".jpeg",
        ".gif",
        ".png",
        ".psd",
        ".svg",
        ".tif",
        ".tiff",
        ".xcf",
        ".pct"
    };

    public boolean accept( File file, String filename ) {
        String name = filename.toLowerCase();

        for (String ext : EXTENSIONS) {
            if (name.endsWith(ext)) {
                return true;
            }
        }

        return false;
    }

}
