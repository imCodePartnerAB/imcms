import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.* ;
import java.rmi.registry.* ;
import imcode.util.* ;
import imcode.server.* ;
/**
   Save an urldocument.
   Shows a change_meta.html which calls SaveMeta
*/
public class SaveUrlDoc extends HttpServlet {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /**
       init()
    */
    public void init( ServletConfig config ) throws ServletException {
	super.init( config ) ;
    }

    /**
       doPost()
    */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
	String host				= req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	String start_url	= imcref.getStartUrl() ;

	imcode.server.user.UserDomainObject user ;
	String htmlStr = "" ;
	String submit_name = "" ;
	String url_ref = "" ;
	String url_txt = "" ;
	String values[] ;
	int meta_id ;
	int parent_meta_id ;

	String ok = "" ;
	String cancel = "" ;
	String reset = "" ;
	String show_meta = "" ;

	res.setContentType( "text/html" );
	Writer out = res.getWriter( );

	// get meta_id
	meta_id = Integer.parseInt( req.getParameter( "meta_id" ) ) ;

	// get parent_meta_id
	parent_meta_id = Integer.parseInt( req.getParameter( "parent_meta_id" ) ) ;

	// get urlref
	values = req.getParameterValues( "url_ref" ) ;
	if( values != null )
	    url_ref = values[0] ;

	String target = req.getParameter("target") ;
	if ( "_other".equals(target) ) {
	    target = req.getParameter("frame_name") ;
	}

	// Get the session
	HttpSession session = req.getSession( true );

	// Does the session indicate this user already logged in?
	Object done = session.getAttribute( "logon.isDone" );  // marker object
	user = (imcode.server.user.UserDomainObject)done ;

	if( done == null ) {
	    // No logon.isDone means he hasn't logged in.
	    // Save the request URL as the true target and redirect to the login page.
	    String scheme = req.getScheme( );
	    String serverName = req.getServerName( );
	    int p = req.getServerPort( );
	    String port = (p == 80) ? "" : ":" + p;
	    res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
	    return ;
	}
	// Check if user has write rights
	if ( !imcref.checkDocAdminRights( meta_id, user, 65536) ) {
	    log("User "+user.getUserId()+" was denied access to meta_id "+meta_id+" and was sent to "+start_url) ;
	    String scheme = req.getScheme() ;
	    String serverName = req.getServerName() ;
	    int p = req.getServerPort() ;
	    String port = ( p == 80 ) ? "" : ":" + p ;
	    res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
	    return ;
	}

	// FIXME: Move to SProc
	String sqlStr = "update url_docs set url_ref = '"+url_ref+"' where meta_id = "+meta_id ;
	imcref.sqlUpdateQuery(sqlStr) ;
	sqlStr = "update meta set target = '"+target+"' where meta_id = "+meta_id ;
	imcref.sqlUpdateQuery(sqlStr) ;

	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd") ;
	Date dt = imcref.getCurrentDate() ;
	sqlStr = "update meta set date_modified = '"+dateformat.format(dt)+"' where meta_id = "+meta_id ;
	imcref.sqlUpdateQuery(sqlStr) ;

	String output = AdminDoc.adminDoc(meta_id,meta_id,user,req,res) ;
	if ( output != null ) {
	    out.write(output) ;
	}
	return ;
    }
}
