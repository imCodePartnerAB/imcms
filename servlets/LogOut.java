import java.io.* ;
import java.util.* ;
import javax.servlet.* ;
import javax.servlet.http.* ;

import imcode.util.* ;
import imcode.server.* ;

public class LogOut extends HttpServlet {

    /**
	doGet()
	*/
	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
		String login_url        	= Utility.getDomainPref( "admin_url" ) ;
		String start_url        	= imcref.getStartUrl() ;
		res.setContentType("text/html") ;
		ServletOutputStream out = res.getOutputStream () ;
		HttpSession session = req.getSession (true) ;
		Object done = session.getAttribute("logon.isDone");  // marker object
		imcode.server.user.UserDomainObject user = (imcode.server.user.UserDomainObject)done ;
		if ( user == null ) {
			res.sendRedirect(start_url) ;
			return ;
		}
		Vector vec = new Vector() ;
		vec.add("#start#") ;
		vec.add(start_url) ;
		vec.add("#login#") ;
		vec.add(login_url) ;
		String htmlStr = imcref.parseDoc(vec,"logged_out.html", user) ;
		session.invalidate() ;
		out.print(htmlStr) ;
	}
}
