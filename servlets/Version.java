import java.io.* ;
import java.util.zip.* ;
import java.util.Date ;
import java.text.DateFormat ;
import javax.servlet.* ;
import javax.servlet.http.* ;

import org.apache.oro.text.perl.* ;

import imcode.util.version.* ;

import org.w3c.dom.Document ;

import org.apache.xml.serialize.* ;

public class Version extends HttpServlet {

    private final static String CVS_REV =  "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

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
	File installedVersionFile    = new File(webapproot,"WEB-INF/version.xml") ;
	XmlVersion installedVersion = new XmlVersion(installedVersionFile) ;
	XmlVersion v2 = new XmlVersion(webapproot, installedVersion.getVersion()) ;
	XmlVersionDiff d = new XmlVersionDiff(installedVersion.getDocument(),v2.getDocument()) ;
	writeNode(out,d.getDiffDocument()) ;
    }

    private void version(OutputStream out) throws Exception {
	File webapproot = new File(this.getServletContext().getRealPath("/")) ;
	File version    = new File(webapproot,"WEB-INF/version.xml") ;
	XmlVersion v = new XmlVersion(version) ;
	writeNode(out,v.getDocument()) ;
    }

    private void now(OutputStream out) throws Exception {
	File webapproot = new File(this.getServletContext().getRealPath("/")) ;
	File installedVersionFile    = new File(webapproot,"WEB-INF/version.xml") ;
	XmlVersion installedVersion = new XmlVersion(installedVersionFile) ;
	XmlVersion v = new XmlVersion(webapproot,installedVersion.getVersion()) ;
	writeNode(out,v.getDocument()) ;
    }

    private void writeNode(OutputStream out, Document document) throws Exception {
	XMLSerializer xmlSerializer = new XMLSerializer() ;
	xmlSerializer.writeNode(out,document) ;
    }

}
