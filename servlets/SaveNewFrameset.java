import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
/**
  Save a new framesetdocument.
  */
public class SaveNewFrameset extends HttpServlet {
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
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		imcode.server.User user ; 
		String htmlStr = "" ;  
		String submit_name = "" ;                          	
		String values[] ;                           	
		int meta_id ;
		int new_meta_id ;

		res.setContentType("text/html");
		Writer out = res.getWriter();

		// get meta_id
		meta_id = Integer.parseInt(req.getParameter("meta_id")) ;

		// get new_meta_id
		new_meta_id = Integer.parseInt(req.getParameter("new_meta_id")) ;                           	

		// save form data
		imcode.server.Table doc = new imcode.server.Table() ;
		String frame_set =  req.getParameter("frame_set") ;                          	
		String[] tmpary = {
			"'",	"''"
		} ;
		frame_set = Parser.parseDoc(frame_set,tmpary) ;
		doc.addField("frame_set",frame_set) ;

		// Get the session
		HttpSession session = req.getSession(true);

		// Does the session indicate this user already logged in?
		Object done = session.getValue("logon.isDone");  // marker object
		user = (imcode.server.User)done ;

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
		// Check if user has write rights
		if ( !IMCServiceRMI.checkDocAdminRights(imcserver, meta_id, user) ) {
			log("User "+user.getInt("user_id")+" was denied access to meta_id "+meta_id+" and was sent to "+start_url) ;			
			String scheme = req.getScheme() ;
			String serverName = req.getServerName() ;
			int p = req.getServerPort() ;
			String port = ( p == 80 ) ? "" : ":" + p ;
			res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
			return ;
		}

		if (req.getParameter("cancel")!=null) {
			String output = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
			if ( output != null ) {
			    out.write(output) ;
			}
		} else {
			IMCServiceRMI.saveNewFrameset(imcserver,new_meta_id,user,doc) ;
			String output = AdminDoc.adminDoc(new_meta_id,new_meta_id,host,user,req,res) ;
			if ( output != null ) {
			    out.write(output) ;
			}
		}
	}
}
