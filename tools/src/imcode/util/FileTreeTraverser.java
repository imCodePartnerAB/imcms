package imcode.util;

import java.io.File;
import java.io.FileFilter;

public class FileTreeTraverser {

    private FileFilter filter;

    public FileTreeTraverser( FileFilter filter ) {
        this.filter = filter;
    }

    public void traverse( File[] files ) {
        for ( int i = 0; i < files.length; i++ ) {
            traverseDirectory( files[i] );
        }
    }

    public void traverseDirectory( File dir ) {
        if ( filter.accept( dir ) && dir.isDirectory() ) {
            File[] files = dir.listFiles();
            if ( null != files ) {
                traverse( files );
            }
        }
    }

}
