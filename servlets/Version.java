import java.io.* ;
import java.util.zip.* ;
import javax.servlet.* ;
import javax.servlet.http.* ;

public class Version extends HttpServlet implements FilenameFilter {

    private final static String CVS_NAME = "$Name$" ;
    private final static String CVS_TAG = CVS_NAME.substring(CVS_NAME.indexOf(' ')+1,CVS_NAME.lastIndexOf(' ')) ;

    private final static int BUFFER_LEN = 16384 ;

    private class DirectoryFilter implements FileFilter {
	
	public boolean accept(File file) {
	    return file.isDirectory() ;
	}

    }

    private String classes_path ;

    public void init (ServletConfig config) throws ServletException {
	super.init(config) ;
	classes_path = new File(this.getServletContext().getRealPath("/"),"/WEB-INF/classes").getPath()+'/' ;
    }

    public void doGet (HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {

	res.setContentType("text/plain") ;
	ServletOutputStream out = res.getOutputStream() ;

	// Print out the tag this file was checked out with.
	out.println(CVS_TAG.replace('_','.')) ;

	// Find /WEB-INF/classes

	checksumDirectory(new File(classes_path), out) ;

    }

    public void checksumDirectory(File dir, ServletOutputStream out) throws java.io.IOException {
	// Get all .class-files in /WEB-INF/classes
	File[] classes = dir.listFiles(this) ;

	// Loop through the classfiles and get a checksum for each.
	for (int i = 0; i < classes.length; ++i) {
	    out.print(classes[i].getPath().substring(classes_path.length())+' ') ;

	    Checksum checksum = new CRC32() ;
	    FileInputStream in = new FileInputStream(classes[i]) ;
	    byte[] buffer = new byte[BUFFER_LEN] ;
	    for (int read; -1 != (read = in.read(buffer,0,BUFFER_LEN)); ) {
		checksum.update(buffer,0,read) ;
	    }
	    out.println(checksum.getValue()) ;
	}

	File[] subdirs = dir.listFiles(new DirectoryFilter()) ;

	for (int i = 0; i < subdirs.length; ++i) {
	    checksumDirectory(subdirs[i],out) ;
	}

    }

    public boolean accept(File dir, String name) {
	return name.endsWith(".class") ;
    }

}
