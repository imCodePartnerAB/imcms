package imcode.util;

import java.io.File;
import java.io.FileFilter;

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
