package imcode.anttasks;

import imcode.util.LineReader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @author kreiger
 */
public class Translate extends Task {

    private File toDir;

    private String startToken;
    private String endToken;

    private File bundle;
    private String bundleLanguage;
    private List fileSets = new ArrayList();

    private int verbosity = Project.MSG_VERBOSE;

    private boolean filtering;
    private List filterSets = new ArrayList();

    private FileUtils fileUtils = FileUtils.newFileUtils();
    private Properties bundleProperties = new Properties();
    private long bundleFileLastModified;
    private File bundleFile;

    public void addFileSet( FileSet fileSet ) {
        fileSets.add( fileSet );
    }

    public void setToDir( File toDir ) {
        this.toDir = toDir;
    }

    public void setBundle( File bundle ) {
        this.bundle = bundle;
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

    public void setFiltering( boolean filtering ) {
        this.filtering = filtering;
    }

    /**
     * Create a nested filterset
     */
    public FilterSet createFilterSet() {
        FilterSet filterSet = new FilterSet();
        filterSets.add( filterSet );
        return filterSet;
    }

    /**
     * Used to force listing of all names of copied files.
     */
    public void setVerbose( boolean verbose ) {
        if ( verbose ) {
            verbosity = Project.MSG_INFO;
        } else {
            verbosity = Project.MSG_VERBOSE;
        }
    }

    public void execute() {
        validateAttributes();
        loadBundle();
        FilterSetCollection filters = getFilterSetCollection();
        translateFiles( filters );
    }

    private void translateFiles( FilterSetCollection filters ) {
        for ( Iterator it = fileSets.iterator(); it.hasNext(); ) {
            FileSet fileSet = (FileSet)it.next();
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner( getProject() );
            String[] sourceFiles = directoryScanner.getIncludedFiles();
            log( "Translating " + sourceFiles.length + ( sourceFiles.length == 1
                                                         ? " file to "
                                                         : " files to " ) + toDir, verbosity );
            for ( int i = 0; i < sourceFiles.length; i++ ) {
                String sourcePath = sourceFiles[i];
                File destFile = fileUtils.resolveFile( toDir, sourcePath );
                File destDir = destFile.getParentFile();
                if ( !destDir.exists() ) {
                    if ( !destDir.mkdirs() ) {
                        throw new BuildException( "Failed to create directory " + destDir );
                    }
                }
                File sourceFile = fileUtils.resolveFile( directoryScanner.getBasedir(), sourcePath );
                long destFileLastModified = destFile.lastModified();
                boolean needsWork = destFileLastModified < sourceFile.lastModified()
                                    || destFileLastModified < bundleFileLastModified;
                if ( needsWork ) {
                    log( "Translating file " + sourceFile + " to file " + destFile, verbosity );
                    translateFile( sourceFile, destFile, filters );
                } else {
                    log( "Omitting translation of " + sourceFile + " as " + destFile + " is up-to-date.", verbosity );
                }
            }
        }
    }

    private void translateFile( File sourceFile, File destFile, FilterSetCollection filters ) {
        try {
            InputStream sourceStream = new FileInputStream( sourceFile );
            FileOutputStream destStream = new FileOutputStream( destFile );
            translateStream( sourceStream, destStream, filters );
        } catch ( IOException e ) {
            destFile.delete();
            throw new BuildException( e );
        } catch ( PropertiesNotFoundException e ) {
            destFile.delete();
            String[] missingPropertyKeys = e.getPropertyKeys();
            String missingPropertyKeysString = StringUtils.join(missingPropertyKeys, ", ") ;
            String message = "\n"+sourceFile+":0: "+(missingPropertyKeys.length > 1 ? "Keys " : "Key ")+missingPropertyKeysString+" not found in " + bundleFile;
            throw new BuildException( message );
        }
    }

    private void translateStream( InputStream sourceStream, FileOutputStream destStream, FilterSetCollection filters ) throws IOException, PropertiesNotFoundException {
        LineReader lineReader = new LineReader( new BufferedReader( new InputStreamReader( sourceStream ) ) );
        BufferedWriter destWriter = new BufferedWriter( new OutputStreamWriter( destStream ) );
        List propertiesNotFound = new ArrayList();
        for ( String line; null != ( line = lineReader.readLine() ); ) {
            try {
                String translatedLine = translateLine( line );
                String translatedAndFilteredLine = filters.replaceTokens( translatedLine );
                destWriter.write( translatedAndFilteredLine );
            } catch ( PropertiesNotFoundException e ) {
                propertiesNotFound.addAll( Arrays.asList(e.getPropertyKeys()) );
            }
        }
        destWriter.flush();
        destWriter.close();
        if ( !propertiesNotFound.isEmpty() ) {
            throw new PropertiesNotFoundException( (String[])propertiesNotFound.toArray( new String[propertiesNotFound.size()] ) );
        }
    }

    private String translateLine( String line ) throws PropertiesNotFoundException {
        String translatedLine = line;
        List propertiesNotFound = new ArrayList();
        for ( int startTokenIndex = 0; -1
                                       != ( startTokenIndex = translatedLine.indexOf( startToken, startTokenIndex ) ); ) {
            int endTokenIndex = translatedLine.indexOf( endToken, startTokenIndex + startToken.length() );
            if ( -1 != endTokenIndex ) {
                String bundleKey = translatedLine.substring( startTokenIndex + startToken.length(), endTokenIndex );
                String bundleValue = bundleProperties.getProperty( bundleKey );
                if ( null == bundleValue ) {
                    bundleValue = bundleKey;
                    propertiesNotFound.add( bundleKey );
                } else {
                    log( "Replacing key " + bundleKey + " with value " + bundleValue, Project.MSG_DEBUG );
                    translatedLine = translatedLine.substring( 0, startTokenIndex ) + bundleValue
                                     + translatedLine.substring( endTokenIndex + endToken.length() );
                }
                startTokenIndex += bundleValue.length();
            } else {
                break;
            }
        }
        if (!propertiesNotFound.isEmpty()) {
            throw new PropertiesNotFoundException( (String[])propertiesNotFound.toArray( new String[propertiesNotFound.size()] ) );
        }
        return translatedLine;
    }

    private void loadBundle() {
        bundleFile = new File( bundle.getPath() + '_' + bundleLanguage + ".properties" );
        bundleFileLastModified = bundleFile.lastModified();
        try {
            bundleProperties.load( new FileInputStream( bundleFile ) );
        } catch ( IOException e ) {
            throw new BuildException( e.getMessage(), e );
        }
    }

    private void validateAttributes() {
        if ( fileSets.size() == 0 ) {
            throw new BuildException( "Specify at least one fileset." );
        }

        if ( null == toDir ) {
            throw new BuildException( "The todir attribute must be set." );
        }

        if ( null == bundle ) {
            throw new BuildException( "The bundle attribute must be set." );
        }

        if ( null == bundleLanguage ) {
            bundleLanguage = Locale.getDefault().getLanguage();
        }

    }

    /**
     * Handle the filtersets. *
     */
    private FilterSetCollection getFilterSetCollection() {
        // handle filters
        FilterSetCollection executionFilters = new FilterSetCollection();
        if ( filtering ) {
            executionFilters.addFilterSet( project.getGlobalFilterSet() );
        }
        for ( Iterator filterEnum = filterSets.iterator(); filterEnum.hasNext(); ) {
            executionFilters.addFilterSet( (FilterSet)filterEnum.next() );
        }
        return executionFilters;
    }

    private static class PropertiesNotFoundException extends Exception {

        private String[] propertyKeys;

        public PropertiesNotFoundException( String[] propertyKeys ) {
            this.propertyKeys = propertyKeys;
        }

        public String[] getPropertyKeys() {
            return propertyKeys;
        }

    }
}
