import java.io.* ;

import javax.servlet.* ;
import javax.servlet.http.* ;

public class Version extends HttpServlet {

    private final static String CVS_REV =  "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private final static String VERSION_FILE = "version.txt" ;
    private final static int BUFFER_LENGTH = 32768 ;

    public void init (ServletConfig config) throws ServletException {
	super.init(config) ;
    }

    public void doGet (HttpServletRequest req, HttpServletResponse res) throws IOException {
	InputStream in = this.getServletContext().getResourceAsStream("/WEB-INF/"+VERSION_FILE) ;

	OutputStream out = res.getOutputStream() ;

	byte[] buffer = new byte[BUFFER_LENGTH] ;
	int length ;
	while (-1 != (length = in.read(buffer,0,BUFFER_LENGTH))) {
	    out.write(buffer,0,length) ;
	}
    }
}
