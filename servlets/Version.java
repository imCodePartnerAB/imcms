import java.io.* ;
import java.util.zip.* ;
import java.util.Date ;
import java.text.DateFormat ;
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
	    long file_length = files[i].length() ;
	    // Read the classfile, and have the inputstream compute the checksum as we go.
	    String line ;
	    String revision = null ;
	    String date_time = null ;
	    try {
		BufferedReader in = new BufferedReader(new InputStreamReader(new CheckedInputStream(new FileInputStream(files[i]), checksum), "8859_1")) ;
		while (null != (line = in.readLine())) {
		    // Find the revision.
		    if (null == revision && perl.match("/\\$"+"Revision: (\\d+(?:\\.\\d+)+) "+"\\$/",line)) {
			revision = perl.group(1) ;
		    }
		    // Find the date
		    if (null == date_time && perl.match("/\\$"+"Date: (\\S+)\\s+(\\S+) "+"\\$/",line)) {
			date_time = perl.group(1) + ' ' + perl.group(2) ;
		    }
		}
	    }
	    catch (IOException ignored) { } 
	    catch (OutOfMemoryError ignored) { }

	    // Print the revision.
	    out.print( ( revision != null ? revision : "Unknown" ) + ' ') ;
	    // Print the date.
	    out.print( ( date_time != null ? date_time : "Unknown" ) + ' ') ;
	    // Print the checksum.
	    out.print(checksum.getValue()+" ") ;
	    
	    // Find and print the last-modified date.
	    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT) ;
	    out.print(df.format(new Date(files[i].lastModified()))+" ") ;

	    // Print the file-length.
	    out.println(file_length) ;
	}

	File[] subdirs = dir.listFiles(new DirectoryFilter()) ;

	for (int i = 0; i < subdirs.length; ++i) {
	    checksumDirectory(parent_dir, subdirs[i].getPath().substring(parent_dir.getPath().length()), out) ;
	}

    }
}
