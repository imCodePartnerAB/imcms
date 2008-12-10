package imcode.util.io;

import com.imcode.util.FileTreeTraverser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.oro.text.regex.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

import imcode.server.Imcms;

public class FileUtility {

    private FileUtility() {}

    /**
     * Takes a path-string and returns a file. The path is prepended with the webapp dir if the path is relative.
     */
    public static File getFileFromWebappRelativePath( String pathString ) {
        if ( null == pathString ) {
            return null ;
        }
        File path = new File( pathString );
        if ( !path.isAbsolute() ) {
            path = new File( Imcms.getPath(), pathString );
        }
        return path;
    }

    public static File relativizeFile( File ancestorDirectory, File file ) throws IOException {
        File currentParent = file.getCanonicalFile();
        ancestorDirectory = ancestorDirectory.getCanonicalFile();
        LinkedList fileParents = new LinkedList();
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

    public static boolean directoryIsAncestorOfOrEqualTo( File dir, File file ) throws IOException {
        dir = dir.getCanonicalFile() ;
        for ( File currentFile = file.getCanonicalFile(); null != currentFile; currentFile = currentFile.getParentFile() ) {
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

    public static String escapeFilename( String filename ) {
        StringBuffer escapedFilename = new StringBuffer() ;
        for (int i = 0; i < filename.length(); ++i) {
            char c = filename.charAt( i ) ;
            if ( c > 127 || !Character.isLetterOrDigit( c ) ) {
                escapedFilename.append( '_' ).append( StringUtils.leftPad( Integer.toHexString( c ), 4, '0' ) ) ;
            } else {
                escapedFilename.append( c ) ;
            }
        }
        return escapedFilename.toString() ;
    }

    public static String unescapeFilename( String escapedFilename ) {
        try {
            return Util.substitute(new Perl5Matcher(), new Perl5Compiler().compile( "_([A-Fa-f0-9]{4})"), new UnescapeFilenameSubstitution(), escapedFilename,Util.SUBSTITUTE_ALL) ;
        } catch ( MalformedPatternException e ) {
            throw new UnhandledException( e );
        }
    }

    public static void backupRename(File source, File destination) throws IOException {
        if (!source.equals(destination)) {
            File oldDestination = new File(destination.getParentFile(), destination.getName() + ".old");
            if (oldDestination.exists()) {
                FileUtils.forceDelete(oldDestination);
            }
            if (destination.exists()) {
                fatalRename(destination, oldDestination);
            }
            fatalRename(source, destination);
            if (oldDestination.exists()) {
                FileUtils.deleteDirectory(oldDestination);
            }
        }
    }

    private static void fatalRename(File source, File destination) throws IOException {
        if ( !source.renameTo(destination) ) {
            throw new IOException("Failed to rename \"" + source + "\" to \""
                                  + destination
                                  + "\".");
        }
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
            try {
                return relativizeFile( directory, (File)input );
            } catch ( IOException e ) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class UnescapeFilenameSubstitution implements Substitution {

        public void appendSubstitution( StringBuffer stringBuffer, MatchResult matchResult, int i,
                                        PatternMatcherInput patternMatcherInput, PatternMatcher patternMatcher,
                                        Pattern pattern ) {
            String hex = matchResult.group( 1 ) ;
            char c = (char)Integer.parseInt( hex, 16 ) ;
            stringBuffer.append( c ) ;
        }
    }
}
