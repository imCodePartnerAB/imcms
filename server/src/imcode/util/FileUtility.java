package imcode.util;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.FileFilter;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;

public class FileUtility {

    private FileUtility() {}

    /**
     * Takes a path-string and returns a file. The path is prepended with the webapp dir if the path is relative.
     */
    public static File getFileFromWebappRelativePath( String pathString ) {
        File path = new File( pathString );
        if ( !path.isAbsolute() ) {
            path = new File( imcode.server.WebAppGlobalConstants.getInstance().getAbsoluteWebAppPath(), pathString );
        }
        return path;
    }

    public static File relativizeFile( File ancestorDirectory, File file ) {
        LinkedList fileParents = new LinkedList();
        File currentParent = file;
        while ( !currentParent.equals( ancestorDirectory ) ) {
            fileParents.addFirst( currentParent.getName() );
            currentParent = currentParent.getParentFile();
        }
        File relativeFile = new File( (String)fileParents.removeFirst() );
        for ( Iterator iterator = fileParents.iterator(); iterator.hasNext(); ) {
            relativeFile = new File( relativeFile, (String)iterator.next() );
        }
        return relativeFile;
    }

    public static boolean directoryIsAncestorOfOrEqualTo( File dir, File file ) {
        for ( File currentFile = file; null != currentFile; currentFile = currentFile.getParentFile() ) {
            if ( currentFile.equals( dir ) ) {
                return true;
            }
        }
        return false;
    }

    public static String[] splitFile(File file) {
        LinkedList list = new LinkedList() ;
        for (File parent = file; null != parent; parent = parent.getParentFile()) {
            list.addFirst( parent.getName() );
        }
        return (String[])list.toArray( new String[list.size()] );
    }

    public static String relativeFileToString(File file) {
        return StringUtils.join( splitFile( file ), '/') ;
    }

    public static Collection collectRelativeSubdirectoriesStartingWith( final File directory ) {
        Collection directories = CollectionUtils.transformedCollection( new ArrayList(), new FileRelativizingTransformer( directory.getParentFile() ) );
        FileTreeTraverser fileTreeTraverser = new FileTreeTraverser( new DirectoryCollectingFileFilter( directories ) );
        fileTreeTraverser.traverse( new File[]{directory} );
        return directories;
    }

    private static class DirectoryCollectingFileFilter implements FileFilter {

        private final Collection directories;

        private DirectoryCollectingFileFilter( Collection directories ) {
            this.directories = directories;
        }

        public boolean accept( File file ) {
            if ( file.isDirectory() ) {
                directories.add( file );
                return true;
            } else {
                return false;
            }
        }
    }

    private static class FileRelativizingTransformer implements Transformer {

        private final File directory;

        public FileRelativizingTransformer( File directory ) {
            this.directory = directory;
        }

        public Object transform( Object input ) {
            return relativizeFile( directory, (File)input );
        }
    }
}
