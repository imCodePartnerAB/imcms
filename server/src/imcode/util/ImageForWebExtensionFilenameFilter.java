package imcode.util;

import java.io.File;
import java.io.FilenameFilter;

public class ImageForWebExtensionFilenameFilter implements FilenameFilter {

    public boolean accept( File file, String filename ) {
        String name = filename.toLowerCase();
        boolean jpeg = name.endsWith( ".jpg" ) || name.endsWith( ".jpeg" );
        boolean gif = name.endsWith( ".gif" );
        boolean png = name.endsWith( ".png" );
        
        return jpeg || gif || png;
    }

}

