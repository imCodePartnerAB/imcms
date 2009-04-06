package imcode.util;

import java.io.File;
import java.io.FilenameFilter;

public class ImageExtensionFilenameFilter implements FilenameFilter {

    public boolean accept( File file, String filename ) {
        String name = filename.toLowerCase();
        boolean jpeg = name.endsWith( ".jpg" ) || name.endsWith( ".jpeg" );
        boolean gif = name.endsWith( ".gif" );
        boolean png = name.endsWith( ".png" );
        boolean psd = name.endsWith(".psd");
        boolean svg = name.endsWith(".svg");
        boolean tiff = name.endsWith(".tif");
        boolean xcf = name.endsWith(".xcf");
        boolean pict = name.endsWith(".pct");
        
        return jpeg || gif || png || psd || svg || tiff || xcf || pict;
    }

}

