import java.io.* ;
import java.util.zip.* ;
import java.util.Date ;
import java.text.DateFormat ;
import javax.servlet.* ;
import javax.servlet.http.* ;

import org.apache.oro.text.perl.* ;

import imcode.util.version.* ;

import org.apache.xml.serialize.XMLSerializer ;

public class Version extends HttpServlet {

    private final static String CVS_REV =  "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;
    private final static String CVS_NAME = "$Name$" ;
    private final static String CVS_TAG ;

    private final static int BUFFERLENGTH = 32768 ;

    static {
	if (CVS_NAME.indexOf(' ') != -1) {
	    CVS_TAG = CVS_NAME.substring(CVS_NAME.indexOf(' ')+1,CVS_NAME.lastIndexOf(' ')) ;
	} else {
	    CVS_TAG = "" ;
	}
    }

    public void init (ServletConfig config) throws ServletException {
	super.init(config) ;
    }

    public void doGet (HttpServletRequest req, HttpServletResponse res) throws IOException {

	res.setContentType("text/xml; charset=UTF-8") ;
	ServletOutputStream out = res.getOutputStream() ;

	try {
	    if (req.getParameter("diff")!=null) {
		diff(out) ;
	    } else if (req.getParameter("version")!=null) {
		version(out) ;
	    } else {
		now(out) ;
	    }
	} catch (Exception ex) {
	    ex.printStackTrace(new PrintWriter(out)) ;
	}
    }


    private void diff(OutputStream out) throws Exception {
	File webapproot = new File(this.getServletContext().getRealPath("/")) ;
	File version    = new File(webapproot,"WEB-INF/version.xml") ;
	XmlVersion v1 = new XmlVersion(version) ;
	XmlVersion v2 = new XmlVersion(webapproot, CVS_TAG) ;
	XmlVersionDiff d = new XmlVersionDiff(v1.getDocument(),v2.getDocument()) ;
	XMLSerializer xmlSerializer = new XMLSerializer() ;
	xmlSerializer.writeNode(out,d.getDiffDocument()) ;
    }

    private void version(OutputStream out) throws Exception {
	File webapproot = new File(this.getServletContext().getRealPath("/")) ;
	File version    = new File(webapproot,"WEB-INF/version.xml") ;
	XmlVersion v = new XmlVersion(version) ;
	XMLSerializer xmlSerializer = new XMLSerializer() ;
	xmlSerializer.writeNode(out,v.getDocument()) ;
    }

    private void now(OutputStream out) throws Exception {
	File webapproot = new File(this.getServletContext().getRealPath("/")) ;
	XmlVersion v = new XmlVersion(webapproot) ;
	XMLSerializer xmlSerializer = new XMLSerializer() ;
	xmlSerializer.writeNode(out,v.getDocument()) ;
    }

}
