package imcode.anttasks;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class Cat extends Task {

    private File destFile = null; // the destination file
    private String orderBy = null;
    private boolean filtering = false;
    private boolean append = false;
    private int verbosity = Project.MSG_VERBOSE;
    private boolean forceOverwrite = false;

    private Vector filesets = new Vector();
    private Vector filterSets = new Vector();

    private String ORDER_BY_PATH = "path";

    private final static int BUFFER_SIZE = 32768;

    /**
     * Set the value of append.
     * 
     * @param v Value to assign to append.
     */
    public void setAppend( boolean v ) {
        this.append = v;
    }

    /**
     * Sets the value of destFile
     * 
     * @param argDestFile Value to assign to this.destFile
     */
    public void setDestFile( File argDestFile ) {
        this.destFile = argDestFile;
    }

    /**
     * Sets the value of orderBy
     * 
     * @param argOrderBy Value to assign to this.orderBy
     */
    public void setOrderBy( String argOrderBy ) {
        this.orderBy = argOrderBy;
    }

    /**
     * Adds a set of files (nested fileset attribute).
     */
    public void addFileset( FileSet set ) {
        filesets.addElement( set );
    }

    /**
     * Used to force listing of all names of copied files.
     */
    public void setVerbose( boolean verbose ) {
        if ( verbose ) {
            this.verbosity = Project.MSG_INFO;
        } else {
            this.verbosity = Project.MSG_VERBOSE;
        }
    }

    /**
     * Sets filtering.
     */
    public void setFiltering( boolean filtering ) {
        this.filtering = filtering;
    }

    /**
     * Overwrite any existing destination file(s).
     */
    public void setOverwrite( boolean overwrite ) {
        this.forceOverwrite = overwrite;
    }

    /**
     * Create a nested filterset
     */
    public FilterSet createFilterSet() {
        FilterSet filterSet = new FilterSet();
        filterSets.addElement( filterSet );
        return filterSet;
    }

    public void execute() throws BuildException {
        validateAttributes();
        doFileOperations();
    }

    /**
     * Make sure all attributes are correct. *
     */
    private void validateAttributes() {
        if ( filesets.size() == 0 ) {
            throw new BuildException( "Specify at least one fileset." );
        }

        if ( destFile == null ) {
            throw new BuildException( "Specify a destfile." );
        }

        if ( orderBy != null && !ORDER_BY_PATH.equalsIgnoreCase( orderBy ) ) {
            throw new BuildException( "Specify a valid orderby ('path'), or none." );
        }
    }

    /**
     * Do whatever is necessary to open the destination file. *
     */
    private OutputStream openOutputStream() throws IOException {
        File parent = new File( destFile.getParent() );
        if ( !parent.exists() ) {
            if ( !parent.mkdirs() ) {
                log( "Unable to create directory " + parent.getAbsolutePath(), Project.MSG_ERR );
            } else {
                log( "Created directory " + parent.getAbsolutePath(), verbosity );
            }
        }

        return new FileOutputStream( destFile.getPath(), append );

    }

    /**
     * Do all fileoperations. *
     */
    private void doFileOperations() {
        try {

            List srcFiles = handleFileSets();

            if ( srcFiles == null ) {
                return;
            }

            FilterSetCollection filters = handleFilterSets();

            log( "Concatenating " + srcFiles.size() + ( srcFiles.size() == 1
                                                        ? " file to "
                                                        : " files to " ) + destFile.getAbsolutePath() );

            catFiles( srcFiles, filters );

        } catch ( IOException ex ) {
            throw new BuildException( ex );
        }
    }

    /**
     * Deal with the filesets *
     */
    private List handleFileSets() {
        List srcFiles = new Vector();
        boolean foundNewer = false;

        for ( Iterator filesetsIterator = filesets.iterator(); filesetsIterator.hasNext(); ) {  // for each fileset
            FileSet fs = (FileSet)filesetsIterator.next();
            DirectoryScanner ds = fs.getDirectoryScanner( project );
            File baseDir = fs.getDir( project );

            // get all files in this fileset
            String[] srcFilesArray = ds.getIncludedFiles();

            // sort by path if requested
            if ( ORDER_BY_PATH.equalsIgnoreCase( orderBy ) ) {
                Arrays.sort( srcFilesArray );
            }

            long destModified = destFile.lastModified();

            // add all source files to vector
            for ( int j = 0; j < srcFilesArray.length; ++j ) {
                File srcFile = new File( baseDir, srcFilesArray[j] );
                if ( srcFile.equals( destFile ) ) {
                    log( "Skipping self-concatenation of " + srcFile, verbosity );
                } else {
                    // See if we can find a sourcefile that is newer than the destination
                    if ( !foundNewer && srcFile.lastModified() > destModified ) {
                        foundNewer = true;
                    }
                    srcFiles.add( srcFile );
                }
            }
        }

        // If we didn't find any newer sourcefiles, then forget it.
        if ( !forceOverwrite && !foundNewer ) {
            srcFiles = null;
        }

        return srcFiles;
    }

    /**
     * Handle the filtersets. *
     */
    private FilterSetCollection handleFilterSets() {
        // handle filters
        FilterSetCollection executionFilters = new FilterSetCollection();
        if ( filtering ) {
            executionFilters.addFilterSet( project.getGlobalFilterSet() );
        }
        for ( Enumeration filterEnum = filterSets.elements(); filterEnum.hasMoreElements(); ) {
            executionFilters.addFilterSet( (FilterSet)filterEnum.nextElement() );
        }
        return executionFilters;
    }

    /**
     * Cat through all files  *
     */
    private void catFiles( List srcFiles, FilterSetCollection filters ) throws IOException {
        OutputStream out = openOutputStream();

        // do actual concatenation
        for ( Iterator srcFilesIterator = srcFiles.iterator(); srcFilesIterator.hasNext(); ) {

            File srcFile = (File)srcFilesIterator.next();
            log( "Concatenating " + srcFile + " to " + destFile, verbosity );

            FileInputStream in = new FileInputStream( srcFile );
            catStream( in, out, filters );
            in.close();
        }
        out.close();
    }

    /**
     * Cat one file/stream *
     */
    private void catStream( InputStream in, OutputStream out, FilterSetCollection filters ) throws IOException {
        if ( filters != null && filters.hasFilters() ) {

            BufferedReader input = new BufferedReader( new InputStreamReader( in ) );
            BufferedWriter output = new BufferedWriter( new OutputStreamWriter( out ) );

            for ( String newline, line = input.readLine(); line != null; line = input.readLine() ) {
                if ( line.length() == 0 ) {
                    output.newLine();
                } else {
                    newline = filters.replaceTokens( line );
                    output.write( newline );
                    output.newLine();
                }
            }
            output.flush();

        } else {

            int read;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ( -1 != ( read = in.read( buffer, 0, BUFFER_SIZE ) ) ) {
                out.write( buffer, 0, read );
            }

        }
    }
}
