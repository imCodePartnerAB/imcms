import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
/**
  Display archived docs.
  */
public class ShowArchivedDocs extends HttpServlet {

	/**
	init()
	*/
	public void init(ServletConfig config) throws ServletException {
		super.init(config) ;
	}

	/**
	doGet()
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		int start_doc 				= IMCServiceRMI.getDefaultHomePage(imcserver) ; ;

		imcode.server.User user ; 
		String htmlStr = "" ;     
		int meta_id ;

		res.setContentType("text/html");
		ServletOutputStream out = res.getOutputStream();
		String metaStr = req.getParameter("meta_id") ;
		if (metaStr != null)
			meta_id = Integer.parseInt(metaStr) ;
		else
			meta_id = start_doc ;

		// Get the session
		HttpSession session = req.getSession(true);

		// Does the session indicate this user already logged in?
		Object done = session.getValue("logon.isDone");  // marker object
		user = (imcode.server.User)done ;

		if (done == null) {
			// No logon.isDone means he hasn't logged in.
			// Save the request URL as the true target and redirect to the login page.
			session.putValue("login.target",
				HttpUtils.getRequestURL(req).toString());
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

		user.archiveOn() ;

		// interpret
		// htmlStr = imc.listArchive(meta_id,user) ;
//		htmlStr = IMCServiceRMI.interpretTemplate(imcserver, meta_id,user) ;
		byte[] tempbytes = AdminDoc.adminDoc(meta_id,meta_id,host,user,req,res) ;
		if ( tempbytes != null ) {
			out.write(tempbytes) ;
		}
	}
}
