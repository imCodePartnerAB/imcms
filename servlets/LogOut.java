import java.io.* ;
import java.util.* ;
import javax.servlet.* ;
import javax.servlet.http.* ;

import imcode.util.* ;

public class LogOut extends HttpServlet {

	/**
	 init()
	*/
	public void init ( ServletConfig config ) throws ServletException {
		super.init ( config ) ;
	}

	/**
	doGet()
	*/
	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("userserver",host) ;
		String login_url        	= Utility.getDomainPref( "admin_url",host ) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		res.setContentType("text/html") ;
		ServletOutputStream out = res.getOutputStream () ;
		HttpSession session = req.getSession (true) ;
		Object done = session.getValue("logon.isDone");  // marker object
		imcode.server.User user = (imcode.server.User)done ;
		if ( user == null ) {
			res.sendRedirect(start_url) ;              
			return ;
		}
		String lang_pf = IMCServiceRMI.sqlQueryStr(imcserver, "select lang_prefix from lang_prefixes where lang_id = "+user.getInt("lang_id")) ;
		Vector vec = new Vector() ;
		vec.add("#start#") ;
		vec.add(start_url) ;
		vec.add("#login#") ;
		vec.add(login_url) ;
		String htmlStr = IMCServiceRMI.parseDoc(imcserver,vec,"logged_out.html",lang_pf) ;
		session.invalidate() ;
		out.print(htmlStr) ;
	}
}