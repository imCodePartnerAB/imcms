package imcode.anttasks;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.FileSet;

import java.util.Vector;
import java.util.Properties;
import java.util.Locale;
import java.util.Iterator;
import java.io.*;

/**
 * @author kreiger
 */
public class Translate extends Task {

    private File toDir ;

    private String startToken ;
    private String endToken ;

    private String bundle ;
    private String bundleLanguage;
    private Vector fileSets = new Vector() ;

    private FileUtils fileUtils = FileUtils.newFileUtils() ;
    private Properties bundleProperties = new Properties() ;
    private long bundleFileLastModified ;

    public void addFileSet(FileSet fileSet) {
        fileSets.add( fileSet ) ;
    }

    public void setToDir(File toDir) {
        this.toDir = toDir ;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle ;
    }

    public void setBundleLanguage( String bundleLanguage ) {
        this.bundleLanguage = bundleLanguage;
    }

    public void setStartToken( String startToken ) {
        this.startToken = startToken;
    }

    public void setEndToken( String endToken ) {
        this.endToken = endToken;
    }


    public void execute() {
        validateAttributes();
        loadBundle();
        translateFiles();
    }

    private void translateFiles() {
        for (Iterator it = fileSets.iterator(); it.hasNext() ;) {
            FileSet fileSet = (FileSet)it.next() ;
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner( getProject() ) ;
            String[] sourceFiles = directoryScanner.getIncludedFiles() ;
            for ( int i = 0; i < sourceFiles.length; i++ ) {
                String sourcePath = sourceFiles[i];
                File destFile = fileUtils.resolveFile(toDir,sourcePath) ;
                File destDir = destFile.getParentFile() ;
                if (!destDir.exists()) {
                    if (!destDir.mkdirs()) {
                        throw new BuildException( "Failed to create directory "+destDir) ;
                    }
                }
                File sourceFile = fileUtils.resolveFile( directoryScanner.getBasedir(), sourcePath) ;
                long destFileLastModified = destFile.lastModified() ;
                boolean needsWork = destFileLastModified < sourceFile.lastModified() || destFileLastModified < bundleFileLastModified ;
                if (needsWork) {
                    translateFile(sourceFile, destFile) ;
                }
            }
        }
    }

    private void translateFile( File sourceFile, File destFile ) {
        try {
            log("Translating file "+sourceFile+" to file "+destFile) ;
            BufferedReader sourceReader = new BufferedReader( new InputStreamReader( new FileInputStream ( sourceFile ))) ;
            BufferedWriter destWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream ( destFile ))) ;
            for (String line ; null != (line = readLine(sourceReader));) {
                for (int startTokenIndex = 0; -1 != (startTokenIndex = line.indexOf( startToken, startTokenIndex )) ; ) {
                    int endTokenIndex = line.indexOf( endToken, startTokenIndex + startToken.length() ) ;
                    if (-1 != endTokenIndex) {
                        String bundleKey = line.substring(startTokenIndex+startToken.length(), endTokenIndex) ;
                        String bundleValue = bundleProperties.getProperty( bundleKey ) ;
                        if (null != bundleValue) {
                            line = line.substring(0,startTokenIndex)+bundleValue+line.substring(endTokenIndex+endToken.length()) ;
                            startTokenIndex += bundleValue.length() ;
                        }
                    } else {
                        break ;
                    }
                }
                destWriter.write( line ) ;
            }
            destWriter.flush();
            destWriter.close();
        } catch ( IOException e ) {
            throw new BuildException( e ) ;
        }
    }

    private char lastChar ;

    private synchronized String readLine(Reader reader) throws IOException {
        StringBuffer line = new StringBuffer() ;
        int c ;
        if (0 != lastChar) {
            lastChar = 0 ;
            c = lastChar ;
        } else {
            c = reader.read() ;
        }
        boolean lastWasCR = false ;
        for ( ; -1 != c; c = reader.read()) {
            if (lastWasCR) {
                lastWasCR = false ;
                if (-1 != c && '\n' != c) {
                    lastChar = (char)c ;
                    return line.toString() ;
                }
            }
            line.append((char)c) ;
            if ('\r' == c) {
                lastWasCR = true ;
                continue ;
            } else if ('\n' == c) {
                break ;
            }
        }
        if (line.length() > 0) {
            return line.toString() ;
        } else {
            return null ;
        }
    }

    private void loadBundle() {
        File bundleFile = new File(bundle + '_' + bundleLanguage + ".properties") ;
        bundleFileLastModified = bundleFile.lastModified() ;
        try {
            bundleProperties.load( new FileInputStream( bundleFile ));
        } catch ( IOException e ) {
            throw new BuildException( e.getMessage(), e ) ;
        }
    }

    private void validateAttributes() {
        if ( fileSets.size() == 0 ) {
            throw new BuildException("Specify at least one fileset.");
        }

        if (null == toDir) {
            throw new BuildException("The todir attribute must be set.");
        }

        if (null == bundle) {
            throw new BuildException("The bundle attribute must be set.");
        }

        if (null == bundleLanguage) {
            bundleLanguage = Locale.getDefault().getLanguage() ;
        }

    }

}
