package imcode.util;

import java.io.FileFilter;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * @author kreiger
 */
public class FileFinder {

    public void find(FileFilter wanted, File dir) {
        File[] files = dir.listFiles(wanted);
        for ( int i = 0; i < files.length; i++ ) {
            File file = files[i];
            if ( file.isDirectory() ) {
                find( wanted, file );
            }
        }
    }
}
