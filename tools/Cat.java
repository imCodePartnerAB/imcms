import java.io.* ;
import java.util.* ;
import org.apache.tools.ant.* ;
import org.apache.tools.ant.types.* ;

public class Cat extends Task {

    protected File destFile = null ; // the destination file
    protected String orderBy = null ;
    protected boolean append = false ;

    protected Vector filesets = new Vector() ;

    private String ORDER_BY_PATH    = "path" ;

    private final static int BUFFER_SIZE = 32768 ;

    /**
     * Set the value of append.
     * @param v  Value to assign to append.
     */
    public void setAppend(boolean  v) {
	this.append = v;
    }
    
    /**
     * Sets the value of destFile
     *
     * @param argDestFile Value to assign to this.destFile
     */
    public void setDestFile(File argDestFile) {
	this.destFile = argDestFile;
    }

    /**
     * Sets the value of orderBy
     *
     * @param argOrderBy Value to assign to this.orderBy
     */
    public void setOrderBy(String argOrderBy) {
	this.orderBy = argOrderBy;
    }

    /**
     * Adds a set of files (nested fileset attribute).
     */
    public void addFileset(FileSet set) {
        filesets.addElement(set);
    }

    public void execute() throws BuildException {
        if ( filesets.size() == 0 ) {
            throw new BuildException("Specify at least one fileset.");
        }
	if ( destFile == null ) {
	    throw new BuildException("Specify a destfile.") ;
	}

	if ( orderBy != null && !ORDER_BY_PATH.equalsIgnoreCase(orderBy)) {
	    throw new BuildException("Specify a valid orderby, or none.") ;
	}

	try {
	    OutputStream out = new FileOutputStream(destFile,append) ;
    
	    Vector srcFiles = new Vector() ;

	    // deal with the filesets
	    for (int i=0; i<filesets.size(); i++) {
		FileSet fs = (FileSet) filesets.elementAt(i);
		DirectoryScanner ds = fs.getDirectoryScanner(project);
		File baseDir = fs.getDir(project);

		String[] srcFilesArray = ds.getIncludedFiles() ;
		if (ORDER_BY_PATH.equalsIgnoreCase(orderBy)) {
		    Arrays.sort(srcFilesArray) ;
		}
		for ( int j = 0; j < srcFilesArray.length; ++j ) {
		    srcFiles.add(new File(baseDir,srcFilesArray[j])) ;
		}
	    }
	    log("Concatenating "+srcFiles.size()+(srcFiles.size() == 1 ? " file to " : " files to ") + destFile.getAbsolutePath()) ;
	    for (int j = 0; j<srcFiles.size(); ++j ) {
		int read ;
		byte[] buffer = new byte[BUFFER_SIZE] ;
		FileInputStream in = new FileInputStream((File)srcFiles.elementAt(j)) ;
		while (-1 != (read = in.read(buffer,0,BUFFER_SIZE)) ) {
		    out.write(buffer,0,read) ;
		}
	    }
	} catch (IOException ex) {
	    throw new BuildException(ex) ;
	}
    }
}
