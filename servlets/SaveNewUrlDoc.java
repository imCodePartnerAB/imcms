import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
import imcode.server.* ;
/**
   Save a new urldocument.
*/
public class SaveNewUrlDoc extends HttpServlet {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /**
       init()
    */
    public void init(ServletConfig config) throws ServletException {
	super.init(config) ;
    }

    /**
       doPost()
    */
    public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException {
	String host				= req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	String start_url	= imcref.getStartUrl() ;

	imcode.server.user.UserDomainObject user ;

	res.setContentType("text/html");
	Writer out = res.getWriter();

	// get meta_id
	String meta_id = req.getParameter("meta_id") ;
	// get new_meta_id
	String new_meta_id = req.getParameter("new_meta_id") ;
	// get url_ref
	String url_ref = req.getParameter("url_ref") ;

	// Get the session
	HttpSession session = req.getSession(true);

	// Does the session indicate this user already logged in?
	Object done = session.getAttribute("logon.isDone");  // marker object
	user = (imcode.server.user.UserDomainObject)done ;

	if (done == null) {
	    // No logon.isDone means he hasn't logged in.
	    // Save the request URL as the true target and redirect to the login page.
	    String scheme = req.getScheme();
	    String serverName = req.getServerName();
	    int p = req.getServerPort();
	    String port = (p == 80) ? "" : ":" + p;
	    res.sendRedirect(scheme + "://" + serverName + port + start_url) ;
	    return ;
	}

	String target = req.getParameter("target") ;
	if ( "_other".equals(target) ) {
	    target = req.getParameter("frame_name") ;
	}

	if (req.getParameter("cancel")!=null) {
	    String output = AdminDoc.adminDoc(Integer.parseInt(meta_id),Integer.parseInt(meta_id),user,req,res) ;
	    if ( output != null ) {
		out.write(output) ;
	    }
	    return ;
	}

	// FIXME: Move to a SProc.
	String sqlStr = "insert into url_docs (meta_id, frame_name,target,url_ref,url_txt,lang_prefix)\n"+
	    "values ("+new_meta_id+",'','','"+url_ref+"','','se')\n"+
	    "update meta set activate = 1, target = '"+target+"' where meta_id = "+new_meta_id ;
	log (sqlStr) ;
	imcref.sqlUpdateQuery(sqlStr) ;

	String output = AdminDoc.adminDoc(Integer.parseInt(new_meta_id),Integer.parseInt(new_meta_id),user,req,res) ;
	if ( output != null ) {
	    out.write(output) ;
	}
    }
}
