import java.io.* ;
import java.util.zip.* ;
import javax.servlet.* ;
import javax.servlet.http.* ;

import org.apache.oro.text.perl.* ;

public class Version extends HttpServlet {

    private final static String CVS_REV =  "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;
    private final static String CVS_NAME = "$Name$" ;
    private final static String CVS_TAG = CVS_NAME.substring(CVS_NAME.indexOf(' ')+1,CVS_NAME.lastIndexOf(' ')) ;

    private final static int BUFFERLENGTH = 32768 ;

    private class DirectoryFilter implements FileFilter {
	
	public boolean accept(File file) {
	    return file.isDirectory() ;
	}

    }

    private class NotDirectoryFilter implements FileFilter {
	
	public boolean accept(File file) {
	    return !file.isDirectory() ;
	}

    }

    public void init (ServletConfig config) throws ServletException {
	super.init(config) ;
    }

    public void doGet (HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {

	res.setContentType("text/plain") ;
	ServletOutputStream out = res.getOutputStream() ;

	// Print out the tag this file was checked out with.
	out.println(CVS_TAG.length() > 0 ? CVS_TAG : "Unknown" ) ;

	checksumDirectory(new File(this.getServletContext().getRealPath("/")), "", out) ;
    }

    public void checksumDirectory(File parent_dir, String sub_dir, ServletOutputStream out) throws java.io.IOException {
	Perl5Util perl = new Perl5Util() ;

	File dir = new File(parent_dir,sub_dir) ;
	File[] files = dir.listFiles(new NotDirectoryFilter()) ;

	// Loop through the files and get a checksum for each.
	for (int i = 0; i < files.length; ++i) {
	    out.print(files[i].getPath().substring(parent_dir.getPath().length()+1)+' ') ;

	    Checksum checksum = new CRC32() ;
	    int class_length = (int)files[i].length() ;
	    char[] buffer = new char[BUFFERLENGTH] ;
	    Reader in = new InputStreamReader(new CheckedInputStream(new FileInputStream(files[i]), checksum), "8859_1") ;
	    StringBuffer file_buffer = new StringBuffer() ;
	    // Read the classfile, and have the inputstream compute the checksum as we go.
	    for (int read; -1 != (read = in.read(buffer,0,BUFFERLENGTH));) {
		file_buffer.append(buffer, 0, read) ;
	    } ;

	    // Find and print the revision.
	    if (perl.match("/\\$"+"Revision: (\\d(?:\\.\\d)+) "+"\\$/",file_buffer.toString())) {
		String revision = perl.group(1) ;
		out.print(revision+' ') ;
	    } else {
		out.print("Unknown ") ;
	    }

	    // Find and print the date.
	    if (perl.match("/\\$"+"Date: (\\S+)\\s+(\\S+) "+"\\$/",file_buffer.toString())) {
		String date = perl.group(1) ;
		String time = perl.group(2) ;
		out.print(date+' '+time+' ') ;
	    } else {
		out.print("Unknown ") ;
	    }

	    // Print the checksum.
	    out.println(checksum.getValue()) ;
	}

	File[] subdirs = dir.listFiles(new DirectoryFilter()) ;

	for (int i = 0; i < subdirs.length; ++i) {
	    checksumDirectory(parent_dir, subdirs[i].getPath().substring(parent_dir.getPath().length()), out) ;
	}

    }
}
