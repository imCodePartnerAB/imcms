import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.* ;
import imcode.server.* ;
/**
  Return to last page.
  */
public class LastRequest extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

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
		IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		String start_url        	= imcref.getStartUrl() ;

		imcode.server.User user ; 
		String htmlStr = "" ;     
		int meta_id ;
		int parent_meta_id ;


		res.setContentType("text/html");
		PrintWriter out = res.getWriter();


		// redirect data 
		String scheme = req.getScheme();
		String serverName = req.getServerName();
		int p = req.getServerPort();
		String port = (p == 80) ? "" : ":" + p;



		// Get the session
		HttpSession session = req.getSession(true);

		// Does the session indicate this user already logged in?
		Object done = session.getAttribute("logon.isDone");  // marker object
		user = (imcode.server.User)done ;

		if (done == null) {
			// No logon.isDone means he hasn't logged in.
			// Save the request URL as the true target and redirect to the login page.      
			session.setAttribute("login.target",
				HttpUtils.getRequestURL(req).toString());
			res.sendRedirect(scheme + "://" + serverName + port + start_url) ;              
			return ;
		}

		res.sendRedirect(scheme + "://" + serverName + port + user.getLastRequest()) ;                         	
		out.println(htmlStr) ;



	}
}
