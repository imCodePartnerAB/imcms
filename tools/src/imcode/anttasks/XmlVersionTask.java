package imcode.anttasks ;

import java.io.* ;
import java.util.Date ;

import org.apache.tools.ant.* ;

import imcode.util.version.* ;

/**
   An Ant-task that uses imcode.util.version.XmlVersion
   to write an XML-file describing the files of a release.
**/
public class XmlVersionTask extends Task {

    private final static String CVS_REV =  "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    protected File dir ;
    protected File destFile ;
    protected String version = "" ;

    public void setDestFile(File destFile) {
	this.destFile = destFile ;
    }

    public void setDir(File dir) {
	this.dir = dir ;
    }

    public void setVersion(String version) {
	this.version = version ;
    }

    public void execute() throws BuildException {
	try {
	    if (findFileNewerThan(dir,destFile.lastModified())) {
		XmlVersion v = new XmlVersion(dir,version) ;
		destFile.delete() ;
		// Ugly kludge that requires Apache Crimson, which is the XML-parser used by Ant.
		// FIXME: Use xerces instead? This cast is very fragile, to say the least.
		((org.apache.crimson.tree.XmlDocument)v.getDocument()).write(new FileOutputStream(destFile)) ;
		log("Created versionfile: "+destFile) ;
	    }
	} catch (Exception ex) {
	    throw new BuildException(ex) ;
	}
    }

    private boolean findFileNewerThan(File dir, long lastModified) throws BuildException {
	DirectoryScanner ds = new DirectoryScanner() ;
	ds.setBasedir(dir) ;
	ds.scan() ;
	String[] files = ds.getIncludedFiles() ;
	for (int i = 0; i < files.length; ++i) {
	    long fileLastModified = (new File(dir,files[i])).lastModified() ;
	    if (fileLastModified > lastModified) {
		return true ;
	    }
	}
	return false ;
    }
}
