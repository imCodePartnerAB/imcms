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
    private final static String CVS_NAME = "$Name$" ;
    private final static String CVS_TAG ;

    static {
	if (CVS_NAME.indexOf(' ') != -1) {
	    CVS_TAG = CVS_NAME.substring(CVS_NAME.indexOf(' ')+1,CVS_NAME.lastIndexOf(' ')) ;
	} else {
	    CVS_TAG = "" ;
	}
    }

    protected File dir ;
    protected File destFile ;

    public void setDestFile(File destFile) {
	this.destFile = destFile ;
    }

    public void setDir(File dir) {
	this.dir = dir ;
    }

    public void execute() throws BuildException {
	try {
	    if (findFileNewerThan(dir,destFile.lastModified())) {
		XmlVersion v = new XmlVersion(dir,CVS_TAG) ;
		destFile.delete() ;
		// Ugly kludge that requires Apache Crimson, which is the XML-parser used by Ant.
		((org.apache.crimson.tree.XmlDocument)v.getDocument()).write(new FileOutputStream(destFile)) ;
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
